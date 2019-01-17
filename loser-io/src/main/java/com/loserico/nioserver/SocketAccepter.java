package com.loserico.nioserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jjenkov on 19-10-2015.
 */
public class SocketAccepter implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SocketAccepter.class);

	private int tcpPort = 0;
	private ServerSocketChannel serverSocketChannel = null;

	private Queue<Socket> socketQueue = null;

	public SocketAccepter(int tcpPort, Queue<Socket> socketQueue) {
		this.tcpPort = tcpPort;
		this.socketQueue = socketQueue;
	}

	public void run() {
		try {
			this.serverSocketChannel = ServerSocketChannel.open();
			this.serverSocketChannel.bind(new InetSocketAddress(tcpPort));
		} catch (IOException e) {
			logger.error("ServerSocketChannel bind to port " + tcpPort + " failed!", e);
			return;
		}

		while (true) {
			try {
				SocketChannel socketChannel = this.serverSocketChannel.accept();

				logger.info("Socket accepted: " + socketChannel);

				// todo check if the queue can even accept more sockets.
				this.socketQueue.add(new Socket(socketChannel));

			} catch (IOException e) {
				logger.error("Something wrong with serverSocketchannel", e);
			}

		}

	}
}
