package org.loser.cache;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class CacheBreakdownTest {
	
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
}
