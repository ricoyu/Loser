package org.loser.cache;

import static java.util.concurrent.TimeUnit.*;

import java.util.List;

import org.junit.Test;

import com.loserico.cache.redis.JedisUtils;
import com.loserico.cache.redis.status.hash.HSet;
import com.loserico.cache.redis.status.hash.TTL;

public class JedisUtilsHashTest {

	@Test
	public void testHsetWithTTL() {
		HSet result = JedisUtils.HASH.hset("users", "yuxuehua", 31, 10);
		System.out.println(result);
	}
	
	@Test
	public void testHget() {
		String age = JedisUtils.HASH.hget("users", "yuxuehua");
		System.out.println(age);
	}
	
	@Test
	public void testHdel() {
		System.out.println(JedisUtils.HASH.hdel("users", "yuxuehua"));
	}
	
	@Test
	public void testTime() {
		long milis = JedisUtils.HASH.time();
		System.out.println(milis);
	}
	
	@Test
	public void testExpired() throws InterruptedException {
//		HSetStatus result = JedisUtils.HASH.hset("users", "yuxuehua", 31, 1);
//		System.out.println(result);
//		SECONDS.sleep(1);
		List<String> users = JedisUtils.HASH.expiredFields("users");
		System.out.println(users);
	}
	
	@Test
	public void testExpire() {
//		System.out.println(JedisUtils.HASH.expire("users-non-exist", "yuxuehua", 3));;
		//System.out.println(JedisUtils.HASH.hset("users", "yuxuehua", 31, 0));
//		System.out.println(JedisUtils.HASH.hget("users", "yuxuehua"));
//		System.out.println(JedisUtils.HASH.expire("users", "yuxuehua", 3));;
		System.out.println(JedisUtils.HASH.ttl("users", "yuxuehua"));
	}
	
	@Test
	public void testTTL() {
//		System.out.println(JedisUtils.HASH.hset("users", "yuxuehua", 31, 1));
		TTL ttlStatus = JedisUtils.HASH.ttl("users", "yuxuehua");
		System.out.println(ttlStatus);
	}
	
	@Test
	public void testPersist() throws InterruptedException {
		JedisUtils.HASH.hset("users", "yuxuehua", 31, 12);
		System.out.println(JedisUtils.HASH.ttl("users", "yuxuehua"));
		SECONDS.sleep(6);
		System.out.println(JedisUtils.HASH.ttl("users", "yuxuehua"));
		SECONDS.sleep(4);
		System.out.println(JedisUtils.HASH.ttl("users", "yuxuehua"));
		JedisUtils.HASH.persist("users", "yuxuehua");
		System.out.println(JedisUtils.HASH.ttl("users", "yuxuehua"));
		System.out.println(JedisUtils.HASH.hget("users", "yuxuehua"));
		SECONDS.sleep(3);
		System.out.println(JedisUtils.HASH.hget("users", "yuxuehua"));
		int expired = JedisUtils.HASH.expire("users", "yuxuehua", 4);
		System.out.println("成功设置了过期? " + (expired == 1));
		System.out.println(JedisUtils.HASH.ttl("users", "yuxuehua"));
		System.out.println(JedisUtils.HASH.hget("users", "yuxuehua"));
		SECONDS.sleep(3);
		System.out.println(JedisUtils.HASH.ttl("users", "yuxuehua"));
		System.out.println(JedisUtils.HASH.hget("users", "yuxuehua"));
		SECONDS.sleep(1);
		System.out.println(JedisUtils.HASH.ttl("users", "yuxuehua"));
		System.out.println(JedisUtils.HASH.hget("users", "yuxuehua"));
	}
}
