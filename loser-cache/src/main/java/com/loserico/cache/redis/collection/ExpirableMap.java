package com.loserico.cache.redis.collection;

import java.util.concurrent.TimeUnit;

import org.redisson.api.map.event.MapEntryListener;

import com.loserico.cache.redis.cache.interfaze.CacheObject;
import com.loserico.cache.redis.cache.interfaze.Expirable;

public interface ExpirableMap<K, V> extends ConcurrentMap<K, V>, Expirable, CacheObject{

    /**
     * Sets max size of the map.
     * Superfluous elements are evicted using LRU algorithm.
     * 
     * @param maxSize - max size
     *                  If <code>0</code> the cache is unbounded (default).
     */
    void setMaxSize(int maxSize);
    
    /**
     * Tries to set max size of the map. 
     * Superfluous elements are evicted using LRU algorithm. 
     *
     * @param maxSize - max size
     * @return <code>true</code> if max size has been successfully set, otherwise <code>false</code>.
     *         If <code>0</code> the cache is unbounded (default).
     */
    boolean trySetMaxSize(int maxSize);
    
    /**
     * If the specified key is not already associated
     * with a value, associate it with the given value.
     * <p>
     * Stores value mapped by key with specified time to live.
     * Entry expires after specified time to live.
     *
     * @param key - map key
     * @param value - map value
     * @param ttl - time to live for key\value entry.
     *              If <code>0</code> then stores infinitely.
     * @param ttlUnit - time unit
     * @return current associated value
     */
    V putIfAbsent(K key, V value, long ttl, TimeUnit ttlUnit);

    /**
     * If the specified key is not already associated
     * with a value, associate it with the given value.
     * <p>
     * Stores value mapped by key with specified time to live and max idle time.
     * Entry expires when specified time to live or max idle time has expired.
     *
     * @param key - map key
     * @param value - map value
     * @param ttl - time to live for key\value entry.
     *              If <code>0</code> then time to live doesn't affect entry expiration.
     * @param ttlUnit - time unit
     * @param maxIdleTime - max idle time for key\value entry.
     *              If <code>0</code> then max idle time doesn't affect entry expiration.
     * @param maxIdleUnit - time unit
     * <p>
     * if <code>maxIdleTime</code> and <code>ttl</code> params are equal to <code>0</code>
     * then entry stores infinitely.
     *
     * @return current associated value
     */
    V putIfAbsent(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdleTime, TimeUnit maxIdleUnit);

    /**
     * Stores value mapped by key with specified time to live.
     * Entry expires after specified time to live.
     * <p>
     * If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.
     *
     * @param key - map key
     * @param value - map value
     * @param ttl - time to live for key\value entry.
     *              If <code>0</code> then stores infinitely.
     * @param unit - time unit
     * @return previous associated value
     */
    V put(K key, V value, long ttl, TimeUnit unit);

    /**
     * Stores value mapped by key with specified time to live and max idle time.
     * Entry expires when specified time to live or max idle time has expired.
     * <p>
     * If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.
     *
     * @param key - map key
     * @param value - map value
     * @param ttl - time to live for key\value entry.
     *              If <code>0</code> then time to live doesn't affect entry expiration.
     * @param ttlUnit - time unit
     * @param maxIdleTime - max idle time for key\value entry.
     *              If <code>0</code> then max idle time doesn't affect entry expiration.
     * @param maxIdleUnit - time unit
     * <p>
     * if <code>maxIdleTime</code> and <code>ttl</code> params are equal to <code>0</code>
     * then entry stores infinitely.
     *
     * @return previous associated value
     */
    V put(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdleTime, TimeUnit maxIdleUnit);

    /**
     * Stores value mapped by key with specified time to live.
     * Entry expires after specified time to live.
     * <p>
     * If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.
     * <p>
     * Works faster than usual {@link #put(Object, Object, long, TimeUnit)}
     * as it not returns previous value.
     *
     * @param key - map key
     * @param value - map value
     * @param ttl - time to live for key\value entry.
     *              If <code>0</code> then stores infinitely.
     * @param ttlUnit - time unit
     * 
     * @return <code>true</code> if key is a new key in the hash and value was set.
     *         <code>false</code> if key already exists in the hash and the value was updated.
     */
    boolean fastPut(K key, V value, long ttl, TimeUnit ttlUnit);

    /**
     * Stores value mapped by key with specified time to live and max idle time.
     * Entry expires when specified time to live or max idle time has expired.
     * <p>
     * If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.
     * <p>
     * Works faster than usual {@link #put(Object, Object, long, TimeUnit, long, TimeUnit)}
     * as it not returns previous value.
     *
     * @param key - map key
     * @param value - map value
     * @param ttl - time to live for key\value entry.
     *              If <code>0</code> then time to live doesn't affect entry expiration.
     * @param ttlUnit - time unit
     * @param maxIdleTime - max idle time for key\value entry.
     *              If <code>0</code> then max idle time doesn't affect entry expiration.
     * @param maxIdleUnit - time unit
     * <p>
     * if <code>maxIdleTime</code> and <code>ttl</code> params are equal to <code>0</code>
     * then entry stores infinitely.

     * @return <code>true</code> if key is a new key in the hash and value was set.
     *         <code>false</code> if key already exists in the hash and the value was updated.
     */
    boolean fastPut(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdleTime, TimeUnit maxIdleUnit);
    
    /**
     * If the specified key is not already associated
     * with a value, associate it with the given value.
     * <p>
     * Stores value mapped by key with specified time to live.
     * Entry expires after specified time to live.
     * <p>
     * Works faster than usual {@link #putIfAbsent(Object, Object, long, TimeUnit)}
     * as it not returns previous value.
     *
     * @param key - map key
     * @param value - map value
     * @param ttl - time to live for key\value entry.
     *              If <code>0</code> then stores infinitely.
     * @param ttlUnit - time unit
     * 
     * @return <code>true</code> if key is a new key in the hash and value was set.
     *         <code>false</code> if key already exists in the hash
     */
    boolean fastPutIfAbsent(K key, V value, long ttl, TimeUnit ttlUnit);

    /**
     * If the specified key is not already associated
     * with a value, associate it with the given value.
     * <p>
     * Stores value mapped by key with specified time to live and max idle time.
     * Entry expires when specified time to live or max idle time has expired.
     * <p>
     * Works faster than usual {@link #putIfAbsent(Object, Object, long, TimeUnit, long, TimeUnit)}
     * as it not returns previous value.
     * 
     * @param key - map key
     * @param value - map value
     * @param ttl - time to live for key\value entry.
     *              If <code>0</code> then time to live doesn't affect entry expiration.
     * @param ttlUnit - time unit
     * @param maxIdleTime - max idle time for key\value entry.
     *              If <code>0</code> then max idle time doesn't affect entry expiration.
     * @param maxIdleUnit - time unit
     * <p>
     * if <code>maxIdleTime</code> and <code>ttl</code> params are equal to <code>0</code>
     * then entry stores infinitely.
     *
     * @return <code>true</code> if key is a new key in the hash and value was set.
     *         <code>false</code> if key already exists in the hash.
     */
    boolean fastPutIfAbsent(K key, V value, long ttl, TimeUnit ttlUnit, long maxIdleTime, TimeUnit maxIdleUnit);

    /**
     * Returns the number of entries in cache.
     * This number can reflects expired entries too
     * due to non realtime cleanup process.
     *
     */
    @Override
    int size();

    /**
     * Adds map entry listener
     * 
     * @see org.redisson.api.map.event.EntryCreatedListener
     * @see org.redisson.api.map.event.EntryUpdatedListener
     * @see org.redisson.api.map.event.EntryRemovedListener
     * @see org.redisson.api.map.event.EntryExpiredListener
     * 
     * @param listener - entry listener
     * @return listener id
     */
    int addListener(MapEntryListener listener);
    
    /**
     * Removes map entry listener
     * 
     * @param listenerId - listener id
     */
    void removeListener(int listenerId);

    /**
     * Remaining time to live of map entry associated with a <code>key</code>. 
     *
     * @return time in milliseconds
     *          -2 if the key does not exist.
     *          -1 if the key exists but has no associated expire.
     */
    long remainTimeToLive(K key);
}
