package com.loserico.network.echo;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * There are several different types of sockets. These include datagram sockets; stream sockets,
 * which frequently use TCP; and raw sockets, which normally work at the IP level. We will focus
 * on TCP sockets for our client/server application.
 * 
 * @author Rico Yu
 * @since 2016-12-04 11:50
 * @version 1.0
 *
 */
public class SimpleEchoServer {

	public static void main(String[] args) {
		System.out.println("Simple Echo Server");

		InetAddress address = null;
		try {
			address = InetAddress.getByName("192.168.1.7");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		/*
		 * The ServerSocket class is a specialized socket that is used by a server to listen for
		 * client requests. Its argument is its port number. The IP of the machine on which the
		 * server is located is not necessarily of interest to the server, but the client will
		 * ultimately need to know this IP address.
		 */
		try (ServerSocket serverSocket = new ServerSocket(6000, 0, address)) {
			System.out.println("Waiting for connection.....");
			/*
			 * In the next code sequence, an instance of the ServerSocket class is created and
			 * its accept method is called. The ServerSocket will block this call until it
			 * receives a request from a client. Blocking means that the program is suspended
			 * until the method returns. When a request is received, the accept method will
			 * return a Socket class instance, which represents the connection between that
			 * client and the server.
			 */
			Socket clientSocket = serverSocket.accept();
			System.out.println("Connected to client");

			/*
			 * After this client socket has been created, we can process the message sent to the
			 * server. As we are dealing with text, we will use a BufferedReader instance to
			 * read the message from the client. This is created using the client socket's
			 * getInputStream method. We will use a PrintWriter instance to reply to the client.
			 * This is created using the client socket's getOutputStream method
			 */
			try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), UTF_8))) {
				/*
				 * The second argument to the PrintWriter constructor is set to true. This means
				 * that text sent using the out object will automatically be flushed after each
				 * use.
				 * 
				 * When text is written to a socket, it will sit in a buffer until either the
				 * buffer is full or a flush method is called. Performing automatic flushing
				 * saves us from having to remember to flush the buffer, but it can result in
				 * excessive flushing, whereas a single flush issued after the last write is
				 * performed, will also do.
				 */
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

				/*
				 * The readLine method reads a line at a time from the client.
				 */
				String inputLine = null;
				while ((inputLine = br.readLine()) != null) {
					System.out.println("Server: " + inputLine);
					out.println(inputLine);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}