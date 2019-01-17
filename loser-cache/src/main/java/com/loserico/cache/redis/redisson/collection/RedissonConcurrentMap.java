package com.loserico.cache.redis.redisson.collection;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLock;
import org.redisson.api.RMap;

import com.loserico.cache.redis.collection.ConcurrentMap;
import com.loserico.cache.redis.concurrent.Lock;
import com.loserico.cache.redis.concurrent.ReadWriteLock;
import com.loserico.cache.redis.redisson.concurrent.RedissonLock;
import com.loserico.cache.redis.redisson.concurrent.RedissonReadWriteLock;
import com.loserico.commons.utils.DateUtils;

public class RedissonConcurrentMap<K, V> implements ConcurrentMap<K, V> {

	private RMap<K, V> rMap;

	public RedissonConcurrentMap(RMap<K, V> rMap) {
		this.rMap = rMap;
	}

	@Override
	public int size() {
		return rMap.size();
	}

	@Override
	public boolean isEmpty() {
		return rMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return rMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return rMap.containsValue(value);
	}

	@Override
	public void clear() {
		rMap.clear();
	}

	@Override
	public V get(Object key) {
		return rMap.get(key);
	}

	@Override
	public V put(K key, V value) {
		return rMap.put(key, value);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return rMap.putIfAbsent(key, value);
	}

	@Override
	public V addAndGet(K key, Number delta) {
		return rMap.addAndGet(key, delta);
	}

	@Override
	public V remove(Object key) {
		return rMap.remove(key);
	}

	@Override
	public V replace(K key, V value) {
		return rMap.replace(key, value);
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return rMap.replace(key, oldValue, newValue);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return rMap.remove(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		rMap.putAll(map);
	}

	@Override
	public Map<K, V> getAll(Set<K> keys) {
		return rMap.getAll(keys);
	}

	@SuppressWarnings("unchecked")
	@Override
	public long fastRemove(K... keys) {
		return rMap.fastRemove(keys);
	}

	@Override
	public boolean fastPut(K key, V value) {
		return rMap.fastPut(key, value);
	}

	@Override
	public boolean fastPutIfAbsent(K key, V value) {
		return rMap.fastPutIfAbsent(key, value);
	}

	@Override
	public Set<K> readAllKeySet() {
		return rMap.readAllKeySet();
	}

	@Override
	public Collection<V> readAllValues() {
		return rMap.readAllValues();
	}

	@Override
	public Set<Entry<K, V>> readAllEntrySet() {
		return rMap.readAllEntrySet();
	}

	@Override
	public Set<K> keySet() {
		return rMap.keySet();
	}

	@Override
	public Collection<V> values() {
		return rMap.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return rMap.entrySet();
	}

	@Override
	public Lock getLock(K key) {
		RLock rLock = rMap.getLock(key);
		return new RedissonLock(rLock, rLock.isHeldByCurrentThread());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void offline() {
		if(RLocalCachedMap.class.isInstance(rMap)) {
			((RLocalCachedMap)rMap).destroy();
		} else {
			throw new UnsupportedOperationException("这个版本的ConcurrentMap不可以Destroy哦~");
		}
	}

	@Override
	public int valueSize(K key) {
		return rMap.valueSize(key);
	}

	@Override
	public boolean expire(long timeToLive, TimeUnit timeUnit) {
		return rMap.expire(timeToLive, timeUnit);
	}

	@Override
	public boolean expireAt(long timestamp) {
		return rMap.expireAt(timestamp);
	}

	@Override
	public boolean expireAt(LocalDateTime localDateTime) {
		return rMap.expireAt(DateUtils.toEpochMilis(localDateTime));
	}

	@Override
	public boolean clearExpire() {
		return rMap.clearExpire();
	}

	@Override
	public long remainTimeToLive() {
		return rMap.remainTimeToLive();
	}

	@Override
	public boolean delete() {
		return rMap.delete();
	}

	@Override
	public boolean isExists() {
		return rMap.isExists();
	}

	@Override
	public ReadWriteLock getReadWriteLock(K key) {
		return new RedissonReadWriteLock(rMap.getReadWriteLock(key));
	}

}
