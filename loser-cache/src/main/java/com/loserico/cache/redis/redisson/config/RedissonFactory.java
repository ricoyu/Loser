package com.loserico.cache.redis.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

import com.loserico.commons.resource.PropertyReader;

public class RedissonFactory {
	
	private static final String REDISSON_MODE_SINGLE_INSTANCE = "single";
	private static final String REDISSON_MODE_SENTINEL = "sentinel";
	private static final String REDISSON_MODE_CLUSTER = "cluster";

	public static RedissonClient createRedissonClient(PropertyReader propertyReader) {
		boolean redissonEnabled = propertyReader.getBoolean("redis.redisson.enable");
		if(!redissonEnabled) {
			return null;
		}
		String redissonMode = propertyReader.getString("redis.redisson.mode", "single");
		if(REDISSON_MODE_SINGLE_INSTANCE.equalsIgnoreCase(redissonMode)) {
			return Redisson.create(new SimgleNodeRedissonConfig().getConfig(propertyReader));
		}
		//TODO Sentinel, Cluster mode
		return null;
	}
}
