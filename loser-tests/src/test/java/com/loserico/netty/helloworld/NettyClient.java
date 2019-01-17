package com.loserico.netty.helloworld;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * The main class for client, NettyClient.java, shows below.
 * 
 * @author Rico Yu
 * @since 2016-12-20 11:36
 * @version 1.0
 *
 */
public class NettyClient {

	public static void main(String[] args) {
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);

		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new TimeStampEncoder(), new TimeStampDecoder(), new ClientHandler());
			}
		});

		String serverIp = "192.168.1.7";
		b.connect(serverIp, 19000);
	}
}