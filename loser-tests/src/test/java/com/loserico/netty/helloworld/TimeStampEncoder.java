package com.loserico.netty.helloworld;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * The encoder TimeStampEncoder tranfer a LoopBackTimeStamp object into byte array
 * that can be sent out.
 * 
 * @author Rico Yu
 * @since 2016-12-20 11:12
 * @version 1.0
 *
 */
public class TimeStampEncoder extends MessageToByteEncoder<LoopBackTimeStamp> {

	@Override
	protected void encode(ChannelHandlerContext ctx, LoopBackTimeStamp msg, ByteBuf out) throws Exception {
		out.writeBytes(msg.toByteArray());
	}
}