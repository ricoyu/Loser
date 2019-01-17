package com.loserico.cache.redis.redisson.collection;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RMapCache;
import org.redisson.api.map.event.MapEntryListener;

import com.loserico.cache.redis.cache.interfaze.CacheObject;
import com.loserico.cache.redis.collection.ExpirableMap;

public class RedissonExpirableMap<K, V> extends RedissonConcurrentMap<K, V> implements ExpirableMap<K, V>, CacheObject {

	private RMapCache<K, V> rMapCache;

	public RedissonExpirableMap(RMapCache<K, V> rMapCache) {
		super(rMapCache);
		this.rMapCache = rMapCache;
	}

	@Override
	public void setMaxSize(int maxSize) {
		rMapCache.setMaxSize(maxSize);
	}

	@Override
	public boolean trySetMaxSize(int maxSize) {
		return rMapCache.trySetMaxSize(maxSize);
	}

	@Override
	public V putIfAbsent(K key, V value, long ttl, TimeUnit ttlUnit) {
		return rMapCache.putIfAbsent(key, value, ttl, ttlUnit);
	}

	@Override
	public V putIfAbsent(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdleTime, TimeUnit maxIdleUnit) {
		return rMapCache.putIfAbsent(key, value, ttl, ttlUnit, maxIdleTime, maxIdleUnit);
	}

	@Override
	public V put(K key, V value, long ttl, TimeUnit unit) {
		return rMapCache.put(key, value, ttl, unit);
	}

	@Override
	public V put(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdleTime, TimeUnit maxIdleUnit) {
		return rMapCache.put(key, value, ttl, ttlUnit, maxIdleTime, maxIdleUnit);
	}

	@Override
	public boolean fastPut(K key, V value, long ttl, TimeUnit ttlUnit) {
		return rMapCache.fastPut(key, value, ttl, ttlUnit);
	}

	@Override
	public boolean fastPut(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdleTime, TimeUnit maxIdleUnit) {
		return rMapCache.fastPut(key, value, ttl, ttlUnit, maxIdleTime, maxIdleUnit);
	}

	@Override
	public boolean fastPutIfAbsent(K key, V value, long ttl, TimeUnit ttlUnit) {
		return rMapCache.fastPutIfAbsent(key, value, ttl, ttlUnit);
	}

	@Override
	public boolean fastPutIfAbsent(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdleTime, TimeUnit maxIdleUnit) {
		return rMapCache.fastPutIfAbsent(key, value, ttl, ttlUnit, maxIdleTime, maxIdleUnit);
	}

	@Override
	public int addListener(MapEntryListener listener) {
		return rMapCache.addListener(listener);
	}

	@Override
	public void removeListener(int listenerId) {
		rMapCache.removeListener(listenerId);
	}

	@Override
	public long remainTimeToLive(K key) {
		return rMapCache.remainTimeToLive(key);
	}

}
