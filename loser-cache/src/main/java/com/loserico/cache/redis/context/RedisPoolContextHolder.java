package com.loserico.cache.redis.context;

import java.util.Objects;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * Redis Pool 切换
 * <p>
 * Copyright: Copyright (c) 2019-05-31 15:07
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class RedisPoolContextHolder {

	private static final ThreadLocal<String> CONTEXT_HOLDER = new TransmittableThreadLocal<>();

	public static String getRedisPool() {
		return CONTEXT_HOLDER.get();
	}

	public static void setRedisPool(String redisPool) {
		Objects.requireNonNull(redisPool, "redisPool cannot be null");
		CONTEXT_HOLDER.set(redisPool.toLowerCase());
	}

	public static void clear() {
		CONTEXT_HOLDER.remove();
	}
}