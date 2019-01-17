package com.loserico.nioserver;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by jjenkov on 24-10-2015.
 */
public class Server {

	private SocketAccepter socketAccepter = null;
	private SocketProcessor socketProcessor = null;

	private int tcpPort = 0;
	private MessageReaderFactory messageReaderFactory = null;
	private MessageProcessor messageProcessor = null;

	public Server(int tcpPort, MessageReaderFactory messageReaderFactory,
			MessageProcessor messageProcessor) {
		this.tcpPort = tcpPort;
		this.messageReaderFactory = messageReaderFactory;
		this.messageProcessor = messageProcessor;
	}

	public void start() throws IOException {

		Queue socketQueue = new ArrayBlockingQueue(1024); // move 1024 to
															// ServerConfig

		this.socketAccepter = new SocketAccepter(tcpPort, socketQueue);

		MessageBuffer readBuffer = new MessageBuffer();
		MessageBuffer writeBuffer = new MessageBuffer();

		this.socketProcessor = new SocketProcessor(socketQueue, readBuffer, writeBuffer, this.messageReaderFactory,
				this.messageProcessor);

		Thread accepterThread = new Thread(this.socketAccepter);
		Thread processorThread = new Thread(this.socketProcessor);

		accepterThread.start();
		processorThread.start();
	}

}
