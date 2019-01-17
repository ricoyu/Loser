package com.loserico.netty.transport;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.buffer.Unpooled.unreleasableBuffer;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

public class NettyOioServer {

	public void server(int port) throws Exception {
		final ByteBuf buf = unreleasableBuffer(copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")));
		EventLoopGroup group = new OioEventLoopGroup();
		try {
			//Creates a ServerBootstrap
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(group)
					//Uses OioEventLoopGroup to allow blocking mode (old I/O)
					.channel(OioServerSocketChannel.class)
					.localAddress(new InetSocketAddress(port))
					//Specifies ChannelInitializer that will be called for each accepted connection
					.childHandler(new ChannelInitializer<SocketChannel>() {//Adds a ChannelInboundHandlerAdapter to intercept and handle events
						@Override
						public void initChannel(SocketChannel socketChannel) throws Exception {
							socketChannel.pipeline().addLast(
									new ChannelInboundHandlerAdapter() {
										@Override
										public void channelActive(ChannelHandlerContext ctx) throws Exception {
											/*
											 * Writes message to client and adds ChannelFutureListener 
											 * to close connection once message is written
											 */
											ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
										}
									});
						}
					});
			//Binds server to accept connections
			ChannelFuture channelFuture = serverBootstrap.bind().sync();
			channelFuture.channel().closeFuture().sync();
		} finally {
			//Releases all resources
			group.shutdownGracefully().sync();
		}
	}
}