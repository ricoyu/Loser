package com.loserico.network;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

/**
 * An IP address is represented by the InetAddress class. Addresses can be either unicast where
 * it identifies a specific address, or it can be multicast, where a message is sent to more
 * than one address.
 * 
 * @author Rico Yu
 * @since 2016-11-29 12:25
 * @version 1.0
 *
 */
public class InetAddressTest {

	/**
	 * The InetAddress class has no public constructors. To get an instance, use one of the
	 * several static get type methods. For example, the getByName method takes a string
	 * representing the address as shown next. The string in this case is a Uniform Resource
	 * Locator (URL)
	 * 
	 * @throws IOException
	 */
	@Test
	public void testInetAddress() throws IOException {
		InetAddress address = InetAddress.getByName("www.163.com");
		System.out.println(address);

		InetAddress localhost = InetAddress.getLocalHost();
		System.out.println(localhost);

		System.out.println("CanonicalHostName: " + address.getCanonicalHostName());
		System.out.println("HostAddress: " + address.getHostAddress());
		System.out.println("HostName: " + address.getHostName());

		/*
		 * To test to see whether this address is reachable, use the isReachable method as shown
		 * next. Its argument specifies how long to wait before deciding that the address cannot
		 * be reached. The argument is the number of milliseconds to wait
		 */
		System.out.println(address.isReachable(2000));

		/*
		 * There are also the Inet4Address and Inet6Address classes that support IPv4 and IPv6
		 * addresses, respectively.
		 */
		Inet4Address inet4Address = (Inet4Address)Inet4Address.getByName("www.csdn.net");
		System.out.println("HostAddress: " + inet4Address.getHostAddress());
	}
}
