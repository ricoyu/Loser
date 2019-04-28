package com.loserico.socket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import lombok.SneakyThrows;

public class GreetClient {

	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;

	@SneakyThrows
	public void startConnection(String ip, int port) {
		clientSocket = new Socket(ip, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	@SneakyThrows
	public String sendMessage(String message) {
		out.println(message);
		String response = in.readLine();
		return response;
	}

	@SneakyThrows
	public void stopConnection() {
		in.close();
		out.close();
		clientSocket.close();
	}
}
