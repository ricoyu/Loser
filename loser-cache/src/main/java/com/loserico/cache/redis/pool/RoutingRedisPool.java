package com.loserico.cache.redis.pool;

import com.loserico.cache.redis.context.RedisPoolContextHolder;

public class RoutingRedisPool<T> extends AbstractRoutingRedisPool<T> {

	@Override
	protected Object determineCurrentLookupKey() {
		return RedisPoolContextHolder.getRedisPool();
	}

}
