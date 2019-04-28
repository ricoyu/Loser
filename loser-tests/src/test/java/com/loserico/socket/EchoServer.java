package com.loserico.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import lombok.SneakyThrows;

public class EchoServer {

	private ServerSocket serverSocket;
	
	private Socket clientSocket;
	
	private PrintWriter out;
	
	private BufferedReader in;
	
	public static void main(String[] args) {
		EchoServer echoServer = new EchoServer();
		echoServer.start(6666);
	}
	
	@SneakyThrows
	public void start(int port) {
		serverSocket = new ServerSocket(port);
		System.out.println("Waitting for connections");
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
			if (".".equals(inputLine)) {
				out.println("good bye");
				break;
			}
			out.print(inputLine);
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
