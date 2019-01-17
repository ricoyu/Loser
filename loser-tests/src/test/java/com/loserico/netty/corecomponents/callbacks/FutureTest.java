package com.loserico.netty.corecomponents.callbacks;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.net.InetSocketAddress;

import org.junit.Test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class FutureTest {

	@Test
	public void testChannelFutureListener() {
		Channel channel = null;
		ChannelFuture channelFuture = channel.connect(new InetSocketAddress("localhost", 9090));
		ChannelFutureListener listener = (future) -> {
			/*
			 * Checks the status of the operation.
			 */
			if (future.isSuccess()) {
				/*
				 * If the operation is successful, creates a ByteBuf to hold the data.
				 */
				ByteBuf byteBuf = Unpooled.copiedBuffer("Hello", UTF_8);
				future.channel().writeAndFlush(byteBuf);
			} else {
				future.cause().printStackTrace();
			}
		};
		channelFuture.addListener(listener);

	}
}
