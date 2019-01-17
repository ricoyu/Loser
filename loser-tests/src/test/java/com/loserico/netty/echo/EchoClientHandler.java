package com.loserico.netty.echo;

import static io.netty.buffer.Unpooled.copiedBuffer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @of
 * Like the server, the client will have a ChannelInboundHandler to process the data.
 * In this case, you’ll extend the class SimpleChannelInboundHandler to handle all the
 * needed tasks. This requires overriding the following methods: 
 * ■ channelActive()— Called after the connection to the server is established
 * ■ channelRead0() — Called when a message is received from the server 
 * ■ exceptionCaught()— Called if an exception is raised during processing
 * 
 * SimpleChannelInboundHandler vs. ChannelInboundHandler 
 * 
 * You may be wondering why we used SimpleChannelInboundHandler in the client instead of the
 * ChannelInboundHandlerAdapter used in the EchoServerHandler. 
 * 
 * This has to do with the interaction of two factors: 
 * how the business logic processes messages and how Netty manages resources. 
 * 
 * In the client, when channelRead0() completes, you have the incoming message and you’re done with it. 
 * When the method returns, SimpleChannelInboundHandler takes care of releasing the memory reference to the
 * ByteBuf that holds the message. 
 * 
 * In EchoServerHandler you still have to echo the incoming message to the sender, and the write() operation, 
 * which is asynchronous, may not complete until after channelRead() returns. 
 * For this reason EchoServerHandler extends ChannelInboundHandlerAdapter, which doesn’t
 * release the message at this point. The message is released in channelReadComplete()
 * in the EchoServerHandler when writeAndFlush() is called.
 * @on
 * @author Rico Yu
 * @since 2016-12-20 17:15
 * @version 1.0
 *
 */
//Marks this class as one whose instances can be shared among channels
@Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

	/**
	 * First you override channelActive(), invoked when a connection has been
	 * established. This ensures that something is written to the server as soon as
	 * possible, which in this case is a byte buffer that encodes the string "Netty
	 * rocks!".
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("请输入:");
		String message = reader.readLine();
		//When notified that the channel is active, sends a message
		ctx.writeAndFlush(copiedBuffer(message, CharsetUtil.UTF_8));
	}

	/**
	 * Next you override the method channelRead0(). This method is called whenever
	 * data is received. Note that the message sent by the server may be received in
	 * chunks. That is, if the server sends 5 bytes, there’s no guarantee that all 5
	 * bytes will be received at once. Even for such a small amount of data, the
	 * channelRead0() method could be called twice, first with a ByteBuf (Netty’s byte
	 * container) holding 3 bytes, and second with a ByteBuf holding 2 bytes. As a
	 * stream-oriented protocol, TCP guarantees that the bytes will be received in the
	 * order in which they were sent by the server.
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		//Logs a dump of the received message
		System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("请输入:");
		String message = reader.readLine();
		ctx.writeAndFlush(message);
	}

	//On exception, logs the error and closes channel
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
