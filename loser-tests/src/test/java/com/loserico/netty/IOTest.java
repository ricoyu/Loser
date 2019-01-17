package com.loserico.netty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * @formatter:off
 * @author Rico Yu
 * @since 2016-11-28 20:21
 * @version 1.0
 *
 */
public class IOTest {

	public static void main(String[] args) {
		System.out.println(System.getProperty("java.io.tmpdir"));
	}
	@Test
	public void testBlockingIO() throws IOException {
		ServerSocket serverSocket = new ServerSocket(412);
		Socket clientSocket = serverSocket.accept();
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		String request, response = null;
		while ((request = in.readLine()) != null) {
			if ("Done".equals(request)) {
				break;
			}
			//		response = processRequest(request);
			out.println(response);
		}
	}

	@Test
	public void testChannelFuture() {
		System.out.println(System.getProperty("java.io.tmpdir)"));
		Channel channel = null;
		// Does not block
		ChannelFuture future = channel.connect(new InetSocketAddress("192.168.0.1", 25));
	}

	@Test
	public void testChannelFutureListener() {
		Channel channel = null;
		// Does not block
		ChannelFuture future = channel.connect(new InetSocketAddress("192.168.0.1", 25));
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (future.isSuccess()) {
					ByteBuf buffer = Unpooled.copiedBuffer("Hello", Charset.defaultCharset());
					ChannelFuture wf = future.channel().writeAndFlush(buffer);
					//....
				} else {
					Throwable cause = future.cause();
					cause.printStackTrace();
				}
			}
		});
	}
}
