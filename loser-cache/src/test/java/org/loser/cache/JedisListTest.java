package org.loser.cache;

import java.time.LocalDateTime;

import org.junit.Test;

import com.loserico.cache.redis.JedisUtils;

public class JedisListTest {

	@Test
	public void testLPush() {
		long length = JedisUtils.LIST.lpush("list:test", "a", "c", "b");
		System.out.println(length);
	}
	
	@Test
	public void testLPushObject() {
		long length = JedisUtils.LIST.lpush("list:test:object", new Object(), LocalDateTime.now());
		System.out.println(length);
	}
	
	@Test
	public void testLpop() {
		String e = JedisUtils.LIST.lpop("list:test");
		System.out.println(e);
	}
	
	@Test
	public void testBLpop() {
		while (true) {
			String e = JedisUtils.LIST.blpop("list:test");
			System.out.println(e);
		}
	}
}
