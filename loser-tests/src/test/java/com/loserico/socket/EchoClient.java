package com.loserico.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import lombok.SneakyThrows;

public class EchoClient {

	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;

	public void startConnection(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
