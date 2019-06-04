package com.loserico.cache.redis.redisson.client;

import com.loserico.cache.redis.redisson.context.RedissonClientContextHolder;

public class RoutingRedissonClient extends AbstractRedissonClient {

	@Override
	protected Object determineCurrentLookupKey() {
		return RedissonClientContextHolder.getRedissonClient();
	}

}
