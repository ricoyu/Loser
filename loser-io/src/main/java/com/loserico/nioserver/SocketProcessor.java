package com.loserico.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jjenkov on 16-10-2015.
 */
public class SocketProcessor implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SocketProcessor.class);

	private Queue<Socket> inboundSocketQueue = null;

	// todo Not used now - but perhaps will be later - to check for space in the
	// buffer before reading from sockets
	private MessageBuffer readMessageBuffer = null;
	// todo Not used now - but perhaps will be later - to check for space in the
	// buffer before reading from sockets (space for more to write?)
	private MessageBuffer writeMessageBuffer = null;

	private MessageReaderFactory messageReaderFactory = null;

	// todo use a better / faster queue.
	private Queue<Message> outboundMessageQueue = new LinkedList<>();

	private Map<Long, Socket> socketMap = new HashMap<>();

	private ByteBuffer readByteBuffer = ByteBuffer.allocate(1024 * 1024);
	private ByteBuffer writeByteBuffer = ByteBuffer.allocate(1024 * 1024);
	private Selector readSelector = null;
	private Selector writeSelector = null;

	private MessageProcessor messageProcessor = null;
	private WriteProxy writeProxy = null;

	// start incoming socket ids from 16K - reserve bottom ids for pre-defined
	// sockets (servers).
	private long nextSocketId = 16 * 1024;

	private Set<Socket> emptyToNonEmptySockets = new HashSet<>();
	private Set<Socket> nonEmptyToEmptySockets = new HashSet<>();

	public SocketProcessor(Queue<Socket> inboundSocketQueue, MessageBuffer readMessageBuffer,
			MessageBuffer writeMessageBuffer, MessageReaderFactory messageReaderFactory,
			MessageProcessor messageProcessor) throws IOException {
		this.inboundSocketQueue = inboundSocketQueue;

		this.readMessageBuffer = readMessageBuffer;
		this.writeMessageBuffer = writeMessageBuffer;
		this.writeProxy = new WriteProxy(writeMessageBuffer, this.outboundMessageQueue);

		this.messageReaderFactory = messageReaderFactory;

		this.messageProcessor = messageProcessor;

		this.readSelector = Selector.open();
		this.writeSelector = Selector.open();
	}

	public void run() {
		while (true) {
			try {
				executeCycle();
			} catch (IOException e) {
				logger.error("", e);
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.error("", e);
			}
		}
	}

	public void executeCycle() throws IOException {
		takeNewSockets();
		readFromSockets();
		writeToSockets();
	}

	public void takeNewSockets() throws IOException {
		Socket newSocket = this.inboundSocketQueue.poll();

		while (newSocket != null) {
			newSocket.setSocketId(this.nextSocketId++);
			newSocket.getSocketChannel().configureBlocking(false);

			newSocket.setMessageReader(this.messageReaderFactory.createMessageReader());
			newSocket.getMessageReader().init(this.readMessageBuffer);

			newSocket.setMessageWriter(new MessageWriter());

			this.socketMap.put(newSocket.getSocketId(), newSocket);

			SelectionKey key = newSocket.getSocketChannel().register(this.readSelector,
					SelectionKey.OP_READ);
			key.attach(newSocket);

			newSocket = this.inboundSocketQueue.poll();
		}
	}

	public void readFromSockets() throws IOException {
		int readReady = this.readSelector.selectNow();

		if (readReady > 0) {
			Set<SelectionKey> selectedKeys = this.readSelector.selectedKeys();
			Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();

				readFromSocket(key);

				keyIterator.remove();
			}
			selectedKeys.clear();
		}
	}

	private void readFromSocket(SelectionKey key) throws IOException {
		Socket socket = (Socket) key.attachment();
		socket.getMessageReader().read(socket, this.readByteBuffer);

		List<Message> fullMessages = socket.getMessageReader().getMessages();
		if (fullMessages.size() > 0) {
			for (Message message : fullMessages) {
				message.socketId = socket.getSocketId();
				// the message processor will eventually push outgoing messages
				// into an IMessageWriter for this socket.
				this.messageProcessor.process(message, this.writeProxy);
			}
			fullMessages.clear();
		}

		if (socket.isEndOfStreamReached()) {
			logger.info("Socket closed: " + socket.getSocketId());
			this.socketMap.remove(socket.getSocketId());
			key.attach(null);
			key.cancel();
			key.channel().close();
		}
	}

	public void writeToSockets() throws IOException {

		// Take all new messages from outboundMessageQueue
		takeNewOutboundMessages();

		// Cancel all sockets which have no more data to write.
		cancelEmptySockets();

		// Register all sockets that *have* data and which are not yet
		// registered.
		registerNonEmptySockets();

		// Select from the Selector.
		int writeReady = this.writeSelector.selectNow();

		if (writeReady > 0) {
			Set<SelectionKey> selectionKeys = this.writeSelector.selectedKeys();
			Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();

				Socket socket = (Socket) key.attachment();

				socket.getMessageWriter().write(socket, this.writeByteBuffer);

				if (socket.getMessageWriter().isEmpty()) {
					this.nonEmptyToEmptySockets.add(socket);
				}

				keyIterator.remove();
			}

			selectionKeys.clear();

		}
	}

	private void registerNonEmptySockets() throws ClosedChannelException {
		for (Socket socket : emptyToNonEmptySockets) {
			socket.getSocketChannel().register(this.writeSelector, SelectionKey.OP_WRITE, socket);
		}
		emptyToNonEmptySockets.clear();
	}

	private void cancelEmptySockets() {
		for (Socket socket : nonEmptyToEmptySockets) {
			SelectionKey key = socket.getSocketChannel().keyFor(this.writeSelector);

			key.cancel();
		}
		nonEmptyToEmptySockets.clear();
	}

	private void takeNewOutboundMessages() {
		Message outMessage = this.outboundMessageQueue.poll();
		while (outMessage != null) {
			Socket socket = this.socketMap.get(outMessage.socketId);

			if (socket != null) {
				MessageWriter messageWriter = socket.getMessageWriter();
				if (messageWriter.isEmpty()) {
					messageWriter.enqueue(outMessage);
					nonEmptyToEmptySockets.remove(socket);
					// not necessary if removed from nonEmptyToEmptySockets in
					// prev. statement.
					emptyToNonEmptySockets.add(socket);
				} else {
					messageWriter.enqueue(outMessage);
				}
			}

			outMessage = this.outboundMessageQueue.poll();
		}
	}

}
