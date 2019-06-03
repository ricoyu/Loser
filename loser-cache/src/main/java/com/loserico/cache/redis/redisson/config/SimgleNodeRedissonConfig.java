package com.loserico.cache.redis.redisson.config;

import org.redisson.config.Config;

import com.loserico.cache.resource.PropertyReader;

public class SimgleNodeRedissonConfig implements RedissonConfigurable {

	@Override
	public Config getConfig(PropertyReader propertyReader) {
		Config config = new Config();
		/*
		 * redis:// 这个前缀(schema)是必须的，因为Redisson不仅支持Redis
		 * 
		 * useSingleServer 			– for single node instance.
		 * useMasterSlaveServers 	– for master with slave nodes. 
		 * useSentinelServers 		– for sentinel nodes. 
		 * useClusterServers 		– for clustered nodes. 
		 * useReplicatedServers 	– for replicated nodes. 
		 * @on
		 */
		String urlPrefix = propertyReader.getString("redis.redisson.url.prefix", "");
		config.useSingleServer()
				//Redisson 3.7.0 采用redis://前缀的，老版本不带redis://前缀
				.setAddress(urlPrefix + propertyReader.getString("redis.host", "localhost") + ":" + propertyReader.getInt("redis.port", 6379))
				.setConnectionMinimumIdleSize(10)
				.setConnectionPoolSize(400)
				.setPassword(propertyReader.getString("redis.password"))
				.setRetryAttempts(propertyReader.getInt("redis.redisson.retryAttempts", 3))
				.setTimeout(propertyReader.getInt("redis.redisson.timeout", 3000))
				.setConnectTimeout(propertyReader.getInt("redis.redisson.connectTimeout", 3000))
				.setDatabase(propertyReader.getInt("redis.db"));
		return config;
	}

}
