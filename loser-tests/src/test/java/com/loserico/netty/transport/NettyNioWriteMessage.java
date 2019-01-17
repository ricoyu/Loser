package com.loserico.netty.transport;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyNioWriteMessage {

	public void write(String message) {
		Channel channel = new NioSocketChannel();
		// Creates ByteBuf that holds the data to write
		ByteBuf buf = Unpooled.copiedBuffer("your data", CharsetUtil.UTF_8);
		ChannelFuture future = channel.bind(new InetSocketAddress(9999));
		// Writes the data and flushes it
		ChannelFuture channelFuture = channel.writeAndFlush(buf);
		// Adds ChannelFutureListener to receive notification after write completes
		channelFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				// Write operation completes without error
				if (future.isSuccess()) {
					System.out.println("Write successful");
				} else {
					System.err.println("Write error");
					future.cause().getCause().printStackTrace();
				}
			}
		});
	}

}
