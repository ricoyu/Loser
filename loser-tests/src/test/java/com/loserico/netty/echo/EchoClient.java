package com.loserico.netty.echo;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * The Echo client will 1 Connect to the server 2 Send one or more messages 3 For each
 * message, wait for and receive the same message back from the server 4 Close the
 * connection
 * 
 * @author Rico Yu
 * @since 2016-12-20 17:14
 * @version 1.0
 *
 */
public class EchoClient {
	private final String host;
	private final int port;

	public EchoClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/*
	 * @of
	 * Let’s review the important points introduced in this section: 
	 * ■ A Bootstrap instance is created to initialize the client. 
	 * ■ An NioEventLoopGroup instance is assigned to handle the event processing, which includes creating new
	 * connections and processing inbound and outbound data. 
	 * ■ An InetSocketAddress is created for the connection to the server. 
	 * ■ An EchoClientHandler will be installed in the pipeline when the connection is established. 
	 * ■ After everything is set up, Bootstrap.connect() is called to connect to the remote peer.
	 * @on
	 */
	public void start() throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			//Creates Bootstrap
			Bootstrap bootstrap = new Bootstrap();
			//Specifies EventLoopGroup to handle client events; 
			//NIO implementation is needed.
			bootstrap.group(group)
					//Channel type is the one for NIO transport.
					/*
					 * As before, the NIO transport is used. Note that you could use
					 * different transports in the client and server; for example, NIO
					 * transport on the server side and OIO transport on the client
					 * side.
					 */
					.channel(NioSocketChannel.class)
//					.channel(NioServerSocketChannel.class)
					//Sets the server’s InetSocketAddress
					/*
					 * bootstrapping a client is similar to bootstrapping a server,
					 * with the difference that instead of binding to a listening port
					 * the client uses host and port parameters to connect to a remote
					 * address, here that of the Echo server.
					 */
					.remoteAddress(new InetSocketAddress(host, port))
					//Adds an EchoClientHandler to the pipeline when a Channel is created
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new EchoClientHandler());
						}

					});
			//Connects to the remote peer; waits until the connect completes
			ChannelFuture future = bootstrap.connect().sync();
			//Blocks until the Channel closes
			future.channel().closeFuture().sync();
		} finally {
			//Shuts down the thread pools and the release of all resources
			group.shutdownGracefully().sync();
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: " + EchoClient.class.getSimpleName() + " <host> <port>");
			return;
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		new EchoClient(host, port).start();
	}
}
