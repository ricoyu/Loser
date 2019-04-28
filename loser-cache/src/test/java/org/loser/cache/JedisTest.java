package org.loser.cache;

import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

/**
 * https://github.com/xetorthio/jedis/wiki
 * <p>
 * Copyright: Copyright (c) 2018-01-18 17:58
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class JedisTest {
	
	private static final Logger logger = LoggerFactory.getLogger(JedisTest.class);
	
	private static JedisPool pool = null;
	
	@BeforeClass
	public static void setup() {
		pool = new JedisPool(new JedisPoolConfig(), "192.168.102.106");
	}
	
	@AfterClass
	public static void tearDown() {
		pool.close();
	}

	@Test
	public void testSingleNode() {
		Jedis jedis = new Jedis("192.168.102.106", 6379);
		jedis.auth("deepdata$");
		jedis.set("foo", "bar");
		String value = jedis.get("foo");
		System.out.println(value);
	}

	/**
	 * using Jedis in a multithreaded environment
	 * 
	 * You shouldn't use the same instance from different threads because you'll have
	 * strange errors. And sometimes creating lots of Jedis instances is not good
	 * enough because it means lots of sockets and connections, which leads to strange
	 * errors as well. A single Jedis instance is not threadsafe!
	 * 
	 * To avoid these problems, you should use JedisPool, which is a threadsafe pool
	 * of network connections. You can use the pool to reliably create several Jedis
	 * instances, given you return the Jedis instance to the pool when done. This way
	 * you can overcome those strange errors and achieve great performance.
	 */
	@Test
	public void testJedisPool() {
		/*
		 * init a pool
		 * You can store the pool somewhere statically, it is thread-safe.
		 * JedisPoolConfig includes a number of helpful Redis-specific connection pooling defaults.
		 * @on
		 */
//		JedisPool pool = new JedisPool(new JedisPoolConfig(), "192.168.102.106");
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost", 6379, 3000, "deepdata$", 0, false, null, null, null);
		try (Jedis jedis = pool.getResource()) {
			jedis.set("foo", "bar");
			String foobar = jedis.get("foo");
			
			jedis.zadd("sose", 0, "car");
			jedis.zadd("sose", 0, "bike");
			Set<String> sose = jedis.zrange("sose", 0, 1);
			System.out.println(sose);
		}
		// ... when closing your application:
		pool.close();
	}

	@Test
	public void testCluster() {
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		//Jedis Cluster will attempt to discover cluster nodes automatically
		jedisClusterNodes.add(new HostAndPort("192.168.102.106", 6379));
		JedisCluster jc = new JedisCluster(jedisClusterNodes);
		jc.set("foo", "bar");
		String value = jc.get("foo");
	}
	
	@Test
	public void testTransaction() {
		Jedis jedis = pool.getResource();
		jedis.watch("foo", "foo2");
		Transaction t = jedis.multi();
		t.set("foo", "bar");
		t.exec();
		System.out.println(jedis.get("foo"));
		jedis.close();
	}
	
	@Test
	public void testKeys() {
		Jedis jedis = pool.getResource();
		Set<String> keys = jedis.keys("*");
		keys.forEach(System.out::println);
		jedis.close();
	}

}
