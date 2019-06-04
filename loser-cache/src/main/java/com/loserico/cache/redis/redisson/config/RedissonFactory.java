package com.loserico.cache.redis.redisson.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import com.loserico.cache.resource.PropertyReader;

public class RedissonFactory {

	private static final String REDISSON_MODE_SINGLE_INSTANCE = "single";
	private static final String REDISSON_MODE_SENTINEL = "sentinel";
	private static final String REDISSON_MODE_CLUSTER = "cluster";

	public static RedissonClient createRedissonClient(RedissonProperties redissonProperties) {
		return Redisson.create(new SimgleNodeRedissonConfig().getConfig(redissonProperties));
	}

	public static RedissonClient createRedissonClient(PropertyReader propertyReader) {
		boolean redissonEnabled = propertyReader.getBoolean("redisson.default-enable", true);
		if (!redissonEnabled) {
			return null;
		}
		String redissonMode = propertyReader.getString("redisson.mode", "single");
		if (REDISSON_MODE_SINGLE_INSTANCE.equalsIgnoreCase(redissonMode)) {
			Config config = new Config();
			config.useSingleServer()
					.setAddress(propertyReader.getString("redisson.address", "redis://localhost:6379"))
					.setConnectionMinimumIdleSize(10)
					.setConnectionPoolSize(400)
					.setPassword(propertyReader.getString("redisson.password"))
					.setRetryAttempts(propertyReader.getInt("redisson.retryAttempts", 3))
					.setTimeout(propertyReader.getInt("redisson.timeout", 3000))
					.setConnectTimeout(propertyReader.getInt("redisson.connectTimeout", 3000))
					.setDatabase(propertyReader.getInt("redisson.database", 0));
			return Redisson.create(config);
		}
		// TODO Sentinel, Cluster mode
		return null;
	}
}
