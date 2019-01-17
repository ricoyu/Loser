package com.peacefish.orm.commons.identifier.redis;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class RedisDistributedIdentifierCacheFactory {
	private static ConcurrentHashMap<String, RedisDistributedIdentifierCache> identifierCaches = new ConcurrentHashMap<>();

	private RedisDistributedIdentifierCacheFactory() {
	}

	public static RedisDistributedIdentifierCache getIdentifierCache(String key, long fetchSize) {
		Objects.requireNonNull(key, "key cannot be null!");

		RedisDistributedIdentifierCache identifierCache = identifierCaches.computeIfAbsent(key,
				(v) -> new RedisDistributedIdentifierCache(key, fetchSize));

		return identifierCache;
	}
}
