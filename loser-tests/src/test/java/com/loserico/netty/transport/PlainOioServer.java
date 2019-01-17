package com.loserico.netty.transport;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @formatter:off
 * 这段代码足够处理中等数量的并发客户端。但是随着这个应用变得受欢迎，你会注意到它不能很好地支持上万的并发连接。于是你决定转换到异步网络编程，但是很快就发现异步的API是完全不同的，因此你不得不重写你的应用。
 * 
 * @author Loser
 * @since Jul 6, 2016
 * @version
 *
 */
public class PlainOioServer {

	public void serve(int port) throws IOException {
		// Binds the server to the specified port
		final ServerSocket socket = new ServerSocket(port);
		try {
			for (;;) {
				// Accepts a connection
				final Socket clientSocket = socket.accept();
				System.out.println("Accepted connection from " + clientSocket);
				// Creates a new thread to handle the connection
				new Thread(new Runnable() {
					@Override
					public void run() {
						OutputStream out;
						try {
							out = clientSocket.getOutputStream();
							// Writes message to the connected client
							out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
							out.flush();
							// Closes the connection
							clientSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							try {
								clientSocket.close();
							} catch (IOException ex) {
								// ignore on close
							}
						}
					}
				}).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}