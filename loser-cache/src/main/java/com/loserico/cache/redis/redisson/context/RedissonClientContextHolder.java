package com.loserico.cache.redis.redisson.context;

import java.util.Objects;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * Redisson Client 切换
 * <p>
 * Copyright: Copyright (c) 2019-06-03 15:30
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public final class RedissonClientContextHolder {

	private static final ThreadLocal<String> CONTEXT_HOLDER = new TransmittableThreadLocal<>();

	public static String getRedissonClient() {
		return CONTEXT_HOLDER.get();
	}

	public static void setRedissonClient(String redisClient) {
		Objects.requireNonNull(redisClient, "redisClient cannot be null");
		CONTEXT_HOLDER.set(redisClient.toLowerCase());
	}

	public static void clear() {
		CONTEXT_HOLDER.remove();
	}
}
