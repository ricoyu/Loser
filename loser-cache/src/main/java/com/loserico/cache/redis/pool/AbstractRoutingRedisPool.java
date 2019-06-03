package com.loserico.cache.redis.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.cache.redis.config.RedisProperties;
import com.loserico.cache.redis.factory.JedisPoolFactories;

import redis.clients.util.Pool;

/**
 * 可动态切换的Jedis Pool
 * <p>
 * Copyright: Copyright (c) 2019-05-31 09:42
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public abstract class AbstractRoutingRedisPool<T> extends Pool<T> {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractRoutingRedisPool.class);

	private boolean lenientFallback = true;
	
	private Map<Object, Pool<T>> targetPools = new HashMap<>();
	
	private Map<String, RedisProperties> redisPropertiesMap = new HashMap<>();

	private Pool<T> defaultTargetPool;
	
	private String defaultTargetPoolName;
	
	/**
	 * Specify whether to apply a lenient fallback to the default Pool
	 * if no specific Pool could be found for the current lookup key.
	 * <p>Default is "true", accepting lookup keys without a corresponding entry
	 * in the target Pool map - simply falling back to the default Pool
	 * in that case.
	 * <p>Switch this flag to "false" if you would prefer the fallback to only apply
	 * if the lookup key was {@code null}. Lookup keys without a Pool
	 * entry will then lead to an IllegalStateException.
	 * @see #setTargetPools
	 * @see #setDefaultTargetPool
	 * @see #determineCurrentLookupKey()
	 */
	public void setLenientFallback(boolean lenientFallback) {
		this.lenientFallback = lenientFallback;
	}
	
	/**
	 * Determine the current lookup key. This will typically be implemented to check a thread-bound
	 * transaction context.
	 * <p>
	 * Allows for arbitrary keys. The returned key needs to match the stored lookup key type, as
	 * resolved by the method.
	 */
	protected abstract Object determineCurrentLookupKey();

	/**
	 * Retrieve the current target Pool. Determines the
	 * {@link #determineCurrentLookupKey() current lookup key}, performs
	 * a lookup in the {@link #setTargetPools targetPools} map,
	 * falls back to the specified
	 * {@link #setDefaultTargetPool default target Pool} if necessary.
	 * @see #determineCurrentLookupKey()
	 */
	public Pool<T> determineTargetPool() {
		Object lookupKey = determineCurrentLookupKey();
		log.info("Current redis pool {}", lookupKey);
		Pool<T> pool = this.targetPools.get(lookupKey);
		if (pool == null && (this.lenientFallback || lookupKey == null)) {
			String defaultPoolName = defaultTargetPoolName.toLowerCase();
			log.info("Switch to default redis pool {}", defaultPoolName);
			pool = this.targetPools.get(defaultPoolName);
		}
		if (pool == null) {
			throw new IllegalStateException("Cannot determine target Pool for lookup key [" + lookupKey + "]");
		}
		return pool;
	}

	public Map<Object, Pool<T>> getTargetPools() {
		return targetPools;
	}

	public void setTargetPools(Map<Object, Pool<T>> targetPools) {
		this.targetPools = targetPools;
	}

	public Pool<T> getDefaultTargetPool() {
		return defaultTargetPool;
	}

	public void setDefaultTargetPool(Pool<T> defaultTargetPool) {
		this.defaultTargetPool = defaultTargetPool;
	}

	public boolean isLenientFallback() {
		return lenientFallback;
	}

	public Map<String, RedisProperties> getRedisPropertiesMap() {
		return redisPropertiesMap;
	}

	public void setRedisPropertiesMap(Map<String, RedisProperties> redisPropertiesMap) {
		this.redisPropertiesMap = redisPropertiesMap;
	}
	
	public String getDefaultTargetPoolName() {
		return defaultTargetPoolName;
	}

	public void setDefaultTargetPoolName(String defaultTargetPoolName) {
		this.defaultTargetPoolName = defaultTargetPoolName;
	}

	@PostConstruct
	public void init() {
		if (redisPropertiesMap != null) {
			for (Entry<String, RedisProperties> entry : redisPropertiesMap.entrySet()) {
				Pool<T> pool = (Pool<T>)JedisPoolFactories.poolFactory().createPool(entry.getValue());
				targetPools.put(entry.getKey().toLowerCase(), pool);
			}
		}
	}
}
