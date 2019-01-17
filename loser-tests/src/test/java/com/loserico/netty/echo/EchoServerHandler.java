package com.loserico.netty.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

// Indicates that a ChannelHandler can be safely shared by multiple channels
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

	/**
	 * Called for each incoming message
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf in = (ByteBuf) msg;
		//Logs the message to the console
		System.out.println("Server received: " + in.toString(CharsetUtil.UTF_8));
		//Writes the received message to the sender without flushing the outbound messages
		ctx.write(in);
	}

	/**
	 * Notifies the handler that the last call made to channelRead() was the last
	 * message in the current batch
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		//Flushes pending messages to the remote peer and closes the channel
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * Called if an exception is thrown during the read operation
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		//Closes the channel
		ctx.close();
	}

}
