package com.loserico.netty.transport;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyNioServer {
	public void server(int port) throws Exception {
		final ByteBuf buf = Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8"));
		// Uses NioEventLoopGroup for non-blocking mode
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			// Creates ServerBootstrap
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(group)
					.channel(NioServerSocketChannel.class)
					.localAddress(new InetSocketAddress(port))
					// Specifies ChannelInitializer to be called for each accepted connection
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							// Adds ChannelInboundHandlerAdapter to receive events and process
							// them
							ch.pipeline().addLast(
									new ChannelInboundHandlerAdapter() {
										@Override
										public void channelActive(ChannelHandlerContext ctx) throws Exception {
											/*
											 * Writes message to client and adds
											 * ChannelFutureListener to close the connection
											 * once the message is written
											 */
											ctx.writeAndFlush(buf.duplicate())
													.addListener(ChannelFutureListener.CLOSE);
										}
									});
						}
					});
			// Binds server to accept connections
			ChannelFuture f = serverBootstrap.bind().sync();
			f.channel().closeFuture().sync();
		} finally {
			// Releases all resources
			group.shutdownGracefully().sync();
		}
	}
}