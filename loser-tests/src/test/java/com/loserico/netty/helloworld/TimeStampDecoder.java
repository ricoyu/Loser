package com.loserico.netty.helloworld;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * The decoder transfer bytes recieved from socket into a LoopBackTimeStamp object for
 * business handler to process.
 * 
 * The decoder try to read 16 bytes as a whole, then create a LoopBackTimeStamp object
 * from this 16 bytes array. It blocks if less than 16 bytes recieved, until a
 * complete frame recieved.
 * 
 * @author Rico Yu
 * @since 2016-12-20 11:23
 * @version 1.0
 *
 */
public class TimeStampDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		final int messageLength = Long.SIZE / Byte.SIZE * 2;
		if (in.readableBytes() < messageLength) {
			return;
		}

		byte[] bytes = new byte[messageLength];
		in.readBytes(bytes, 0, messageLength); // block until read 16 bytes from
												// sockets
		LoopBackTimeStamp loopBackTimeStamp = new LoopBackTimeStamp();
		loopBackTimeStamp.fromByteArray(bytes);
		out.add(loopBackTimeStamp);
	}
}