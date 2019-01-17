package com.loserico.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;

import org.junit.Test;

/**
 * A simple way of accessing a server is to use the URLConnection class. This class represents a
 * connection between an application and a URL instance. A URL instance represents a resource on
 * the Internet.
 * 
 * @author Rico Yu
 * @since 2016-11-29 20:51
 * @version 1.0
 *
 */
public class URLConnectionTest {
	public static void main(String[] args) {
		new Date().getTime();
	}

	/**
	 * a URL instance is created for the Google website. Using the URL class' openConnection
	 * method, a URLConnection instance is created. A BufferedReader instance is used to read
	 * lines from the connection that is then displayed
	 * 
	 * @throws IOException
	 */
	@Test
	public void testURLConnection() throws IOException {
		URL url = new URL("http://www.csdn.net");
		URLConnection connection = url.openConnection();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			System.out.println(line);
		}
		bufferedReader.close();
	}

	/**
	 * @throws IOException 
	 * @formatter:off
	 * We can rework the previous example to illustrate the use of channels and buffers. 
	 * The URLConnection instance is created as before. We will create a ReadableByteChannel
	 * instance and then a ByteBuffer instance, as illustrated in the next example. 
	 * 
	 * The ReadableByteChannel instance allows us to read from the site using its read method. 
	 * A ByteBuffer instance receives data from the channel and is used as the argument of the read method. 
	 * 
	 * The buffer created holds 64 bytes at a time. The read method returns the number of bytes read. 
	 * The ByteBuffer class' array method returns an array of bytes, which is used as the argument of the String class' constructor. 
	 * This is used to display the data read. The clear method is used to reset the buffer so that it can be used again
	 */
	@Test
	public void testURLConnectionWithNIO() throws IOException {
		URL url = new URL("http://www.csdn.net");
		URLConnection connection = url.openConnection();
		InputStream inputStream = connection.getInputStream();
		
		ReadableByteChannel channel = Channels.newChannel(inputStream);
		ByteBuffer byteBuffer = ByteBuffer.allocate(64);
		
		while ((channel.read(byteBuffer)) != -1) {
			System.out.println(new String(byteBuffer.array()));
			byteBuffer.clear();
		}
		channel.close();
	}
}
