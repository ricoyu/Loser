package com.loserico.netty.echo;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @of
 * Having discussed the core business logic implemented by EchoServerHandler, we can
 * now examine the bootstrapping of the server itself, which involves the following:
 * 
 * ■ Bind to the port on which the server will listen for and accept incoming connection requests 
 * ■ Configure Channels to notify an EchoServerHandler instance about inbound messages
 * 
 * Transports
 * 
 * In this section you’ll encounter the term transport. In the standard, multilayered
 * view of networking protocols, the transport layer is the one that provides services
 * for endto- end or host-to-host communications. Internet communications are based on
 * the TCP transport. NIO transport refers to a transport that’s mostly identical to
 * TCP except for server-side performance enhancements provided by the Java NIO
 * implementation.
 * 
 * In the meantime, let’s review the important steps in the server implementation you
 * just completed. These are the primary code components of the server: 
 * ■ The EchoServerHandler implements the business logic. 
 * ■ The main() method bootstraps the server.
 * @on
 * 
 * The following steps are required in bootstrapping:
 * ■ Create a ServerBootstrap instance to bootstrap and bind the server.
 * ■ Create and assign an NioEventLoopGroup instance to handle event processing,
 *   such as accepting new connections and reading/writing data.
 * ■ Specify the local InetSocketAddress to which the server binds.
 * ■ Initialize each new Channel with an EchoServerHandler instance.
 * ■ Call ServerBootstrap.bind() to bind the server. At this point the server is initialized and ready to be used.
 * 
 * @author Rico Yu
 * @since 2016-12-20 16:49
 * @version 1.0
 *
 */
public class EchoServer {
	private final int port;

	public EchoServer(int port) {
		this.port = port;
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Usage: " + EchoServer.class.getSimpleName() + " <port>");
		}
		//Sets the port value (throws a NumberFormatException if the port argument is malformed)
		int port = Integer.parseInt(args[0]);
		//Calls the server’s start() method
		System.out.println("EchoServer started...");
		new EchoServer(port).start();
	}

	/*
	 * In c you create a ServerBootstrap instance. Because you’re using the NIO
	 * transport, you specify the NioEventLoopGroup b to accept and handle new
	 * connections and the NioServerSocketChannel d as the channel type. After this
	 * you set the local address to an InetSocketAddress with the selected port e. The
	 * server will bind to this address to listen for new connection requests.
	 */
	public void start() throws Exception {
		final EchoServerHandler serverHandler = new EchoServerHandler();
		//1 Creates the EventLoopGroup
		EventLoopGroup group = new NioEventLoopGroup();
		//2 Creates the ServerBootstrap
		ServerBootstrap bootstrap = new ServerBootstrap();
		try {
			bootstrap.group(group)
					//3 Specifies the use of an NIO transport Channel
					.channel(NioServerSocketChannel.class)
					//4 Sets the socket address using the specified port
					.localAddress(new InetSocketAddress(port))
					//5 Adds an EchoServerHandler to the Channel’s ChannelPipeline
					/*
					 * In f you make use of a special class, ChannelInitializer. This
					 * is key. When a new connection is accepted, a new child Channel
					 * will be created, and the Channel- Initializer will add an
					 * instance of your EchoServerHandler to the Channel’s
					 * ChannelPipeline. As we explained earlier, this handler will
					 * receive notifications about inbound messages.
					 */
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(serverHandler);
						}
					});

			//6 Binds the server asynchronously; sync() waits for the bind to g complete.
			/*
			 * Next you bind the server g and wait until the bind completes. (The call
			 * to sync() causes the current Thread to block until then.)
			 */
			ChannelFuture channelFuture = bootstrap.bind().sync();
			//7 Gets the CloseFuture of the Channel and blocks the current thread until it’s complete
			/*
			 * At h, the application will wait until the server’s Channel closes
			 * (because you call sync() on the Channel’s CloseFuture). You can then
			 * shut down the EventLoopGroup and release all resources, including all
			 * created threads
			 */
			channelFuture.channel().closeFuture().sync();
		} finally {
			//8 Shuts down the EventLoopGroup, releasing all resources
			group.shutdownGracefully().sync();
		}
	}
}
