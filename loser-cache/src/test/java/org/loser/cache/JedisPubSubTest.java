package org.loser.cache;

import com.loserico.cache.redis.JedisUtils;

public class JedisPubSubTest {

	public static void main(String[] args) {
		JedisUtils.subscribe(JedisUtils.AUTH.AUTH_TOKEN_EXPIRE_CHANNEL, (channel, message) -> {
			System.out.println(channel + " 频道上有key到期了: " + message);
		});
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
