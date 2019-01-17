package com.loserico.network.echo;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SimpleEchoClient {

	public static void main(String args[]) {
		System.out.println("Simple Echo Client");

		try {
			InetAddress address = InetAddress.getByName("192.168.1.12");
			try (Socket clientSocket = new Socket(address, 6000)) {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), UTF_8));

				System.out.println("Connected to server");

				Scanner scanner = new Scanner(System.in);
				while (true) {
					System.out.print("Enter text: ");
					String inputLine = scanner.nextLine();
					if ("quit".equalsIgnoreCase(inputLine)) {
						break;
					}
					out.println(inputLine);

					String response = br.readLine();
					System.out.println("Server response: " + response);
				}
			} catch (IOException e) {
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}