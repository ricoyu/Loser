package com.loserico.netty.helloworld;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * All three methods are overriden methods.
 * 
 * The first channelRead() read loop back message and print out time spent on the
 * trip.
 * 
 * The second method handle the event fired by IdleStateHandler (you may want to
 * scroll up to review how server pipeline configured). When idle too long, a
 * LoopBackTimeStamp object is sent out as heart beat.
 * 
 * @author Rico Yu
 * @since 2016-12-20 11:29
 * @version 1.0
 *
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		LoopBackTimeStamp ts = (LoopBackTimeStamp) msg;
		ts.setRecvTimeStamp(System.nanoTime());
		System.out.println("loop delay in ms : " + 1.0 * ts.timeLapseInNanoSecond() / 1000000L);
	}

	// Here is how we send out heart beat for idle to long
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.ALL_IDLE) { // idle for no read and write
				ctx.writeAndFlush(new LoopBackTimeStamp());
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}