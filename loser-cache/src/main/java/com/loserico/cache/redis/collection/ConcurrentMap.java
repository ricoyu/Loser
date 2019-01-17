package com.loserico.cache.redis.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.loserico.cache.redis.cache.interfaze.CacheObject;
import com.loserico.cache.redis.cache.interfaze.Expirable;
import com.loserico.cache.redis.concurrent.Lock;
import com.loserico.cache.redis.concurrent.ReadWriteLock;

public interface ConcurrentMap<K, V> extends java.util.concurrent.ConcurrentMap<K, V>, Expirable, CacheObject {

	/**
	 * Returns <code>RLock</code> instance associated with key
	 * 
	 * @param key - map key
	 * @return lock
	 */
	Lock getLock(K key);
	
    /**
     * Returns <code>ReadWriteLock</code> instance associated with key
     * 
     * @param key - map key
     * @return readWriteLock
     */
	ReadWriteLock getReadWriteLock(K key);
	
    /**
	 * Returns size of value mapped by key in bytes
	 * 
	 * @param key - map key
	 * @return size of value
	 */
	int valueSize(K key);

	/**
	 * Atomically adds the given <code>delta</code> to the current value by mapped
	 * <code>key</code>.
	 *
	 * Works only for <b>numeric</b> values!
	 *
	 * @param key - map key
	 * @param delta the value to add
	 * @return the updated value
	 */
	V addAndGet(K key, Number delta);

	/**
	 * Gets a map slice contains the mappings with defined <code>keys</code> by one
	 * operation. This operation <b>NOT</b> traverses all map entries like any other
	 * <code>filter*</code> method, so works faster.
	 *
	 * The returned map is <b>NOT</b> backed by the original map.
	 *
	 * @param keys - map keys
	 * @return Map object
	 */
	Map<K, V> getAll(Set<K> keys);

	/**
	 * Removes <code>keys</code> from map by one operation
	 *
	 * Works faster than <code>RMap.remove</code> but not returning the value
	 * associated with <code>key</code>
	 *
	 * @param keys - map keys
	 * @return the number of keys that were removed from the hash, not including
	 *         specified but non existing keys
	 */
	long fastRemove(K... keys);

	/**
	 * Associates the specified <code>value</code> with the specified
	 * <code>key</code>.
	 *
	 * Works faster than <code>RMap.put</code> but not returning the previous value
	 * associated with <code>key</code>
	 *
	 * @param key - map key
	 * @param value - map value
	 * @return <code>true</code> if key is a new key in the hash and value was set.
	 *         <code>false</code> if key already exists in the hash and the value was
	 *         updated.
	 */
	boolean fastPut(K key, V value);

	boolean fastPutIfAbsent(K key, V value);

	/**
	 * Read all keys at once
	 *
	 * @return keys
	 */
	Set<K> readAllKeySet();

	/**
	 * Read all values at once
	 *
	 * @return values
	 */
	Collection<V> readAllValues();

	/**
	 * Read all map entries at once
	 *
	 * @return entries
	 */
	Set<Entry<K, V>> readAllEntrySet();

	/**
	 * Returns key set. This method <b>DOESN'T</b> fetch all of them as
	 * {@link #readAllKeySet()} does.
	 */
	@Override
	Set<K> keySet();

	/**
	 * Returns values collections. This method <b>DOESN'T</b> fetch all of them as
	 * {@link #readAllValues()} does.
	 */
	@Override
	Collection<V> values();

	/**
	 * Returns values collections. This method <b>DOESN'T</b> fetch all of them as
	 * {@link #readAllEntrySet()} does.
	 */
	@Override
	Set<java.util.Map.Entry<K, V>> entrySet();

	/**
     * 用完ConcurrentMap 之后切换到离线模式，其他客户端对同名Map的更新不会同步过来。
     * Redis中对应的hash不会被删除
     * 
     * Destroys object when it's not necessary anymore.
     * @on
     */
	void offline();
}
