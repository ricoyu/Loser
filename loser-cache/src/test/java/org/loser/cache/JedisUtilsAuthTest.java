package org.loser.cache;

import java.util.concurrent.TimeUnit;

import javax.xml.transform.sax.SAXSource;

import org.junit.Test;

import com.loserico.cache.redis.JedisUtils;
import com.loserico.commons.jackson.JacksonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JedisUtilsAuthTest {

	public static void main(String[] args) throws InterruptedException {
		JedisUtils.AUTH.onTokenExpire((map) -> {
			JacksonUtils.toPrettyJson(map);
		});
		
		Thread.currentThread().join();
	}

	@Test
	public void testAuth() {
		JedisUtils.AUTH.login("ricoyu", "aaaaaaa", 6, TimeUnit.SECONDS, "测试登录过期", null, null);
	}
	
	@Test
	public void testTtl() {
		Long ttl = JedisUtils.AUTH.usernameTtl("ricoyu");
		System.out.println(ttl);
		Long minutes = ttl / 60;
		Long hours = minutes / 60;
		Long days = hours / 24;
		log.info("user[{}], ttl in seconds[{}], ttl in minutes[{}], ttl in hours[{}], ttl in days[{}]", "ricoyu", ttl, minutes, hours, days);
	}
}
