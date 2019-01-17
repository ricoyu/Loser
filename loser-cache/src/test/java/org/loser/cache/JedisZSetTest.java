package org.loser.cache;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.LocalDateTime;

import org.junit.Test;

import com.loserico.cache.redis.JedisUtils;
import com.loserico.commons.utils.DateUtils;
import com.loserico.io.utils.IOUtils;

public class JedisZSetTest {

	@Test
	public void testMilis() throws InterruptedException {
		long t1 = DateUtils.toEpochMilis(LocalDateTime.now());
		System.out.println(t1);
		SECONDS.sleep(1);
		long t2 = DateUtils.toEpochMilis(LocalDateTime.now());
		System.out.println(t2);
		SECONDS.sleep(1);
		long t3 = DateUtils.toEpochMilis(LocalDateTime.now());
		System.out.println(t3);
	}
	
	@Test
	public void testTime() {
		String script = IOUtils.readClassPathFile("time-test.lua");
		Object object = JedisUtils.eval(script);
		System.out.println(object);
	}
	
	public static void main(String[] args) {
		String script = IOUtils.readClassPathFile("time-test.lua");
		Object object = JedisUtils.eval(script);
		System.out.println(object);
	}
}
