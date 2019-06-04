package com.loserico.cache.redis.redisson.config;

import org.redisson.config.Config;
import org.redisson.config.TransportMode;

public class SimgleNodeRedissonConfig implements RedissonConfigurable {

	@Override
	public Config getConfig(RedissonProperties redissonProperties) {
		Config config = new Config();
		/*
		 * redis:// 这个前缀(schema)是必须的, 因为Redisson不仅支持Redis
		 * 
		 * useSingleServer 			– for single node instance.
		 * useMasterSlaveServers 	– for master with slave nodes. 
		 * useSentinelServers 		– for sentinel nodes. 
		 * useClusterServers 		– for clustered nodes. 
		 * useReplicatedServers 	– for replicated nodes. 
		 * @on
		 */
		config.setTransportMode(TransportMode.EPOLL);
		config.useSingleServer()
				//Redisson 3.7.0 采用redis://前缀的，老版本不带redis://前缀
				.setAddress(redissonProperties.getAddress())
				.setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize())
				.setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
				.setPassword(redissonProperties.getPassword())
				.setRetryAttempts(redissonProperties.getRetryAttempts())
				.setTimeout(redissonProperties.getTimeout())
				.setConnectTimeout(redissonProperties.getConnectTimeout())
				.setDatabase(redissonProperties.getDatabase());
		return config;
	}

}
