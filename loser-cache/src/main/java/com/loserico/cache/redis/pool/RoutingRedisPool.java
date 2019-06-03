package com.loserico.cache.redis.pool;

import com.loserico.cache.redis.context.RedisPoolContextHolder;

/**
 * 可以动态切换Redis Pool的Pool ;-)
 * <p>
 * Copyright: Copyright (c) 2019-05-31 16:14
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 * @param <T>
 */
public class RoutingRedisPool<T> extends AbstractRoutingRedisPool<T> {

	@Override
	protected Object determineCurrentLookupKey() {
		return RedisPoolContextHolder.getRedisPool();
	}

}
