package com.loserico.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import lombok.SneakyThrows;

public class GreetServer {

	private ServerSocket serverSocket;
	
	private Socket clientSocket;
	
	private PrintWriter out;
	
	private BufferedReader in;
	
	public static void main(String[] args) {
		GreetServer greetServer = new GreetServer();
		greetServer.start(6666);
	}
	
	@SneakyThrows
	public void start(int port) {
		serverSocket = new ServerSocket(port);
		clientSocket = serverSocket.accept();
		
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String greeting = in.readLine();
		System.out.println("receive message: " + greeting);
		if ("hello server".equalsIgnoreCase(greeting)) {
			out.println("hello client");
		} else {
			out.println("unrecognised greeting");
		}
	}
	
	@SneakyThrows
	public void stop() {
		in.close();
		out.close();
		clientSocket.close();
		serverSocket.close();
	}
}
