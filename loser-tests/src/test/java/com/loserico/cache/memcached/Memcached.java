package com.loserico.cache.memcached;

public interface Memcached {

	boolean add(String key, Object value);
	
	/**
	 * 将value加入缓存，如果key已经存在则不加入。设置ttl毫秒过期
	 * @param key
	 * @param value
	 * @param ttl time to live, 存活毫秒数
	 * @return
	 */
	boolean add(String key, Object value, long ttl);
	
	/**
	 * 将value加入缓存，如果key已经存在则替换原有值。设置ttl毫秒过期
	 * @param key
	 * @param value
	 * @param ttl time to live, 存活毫秒数
	 * @return
	 */
	boolean set(String key, Object value, long ttl);

	<T> T get(String key, Class<T> clazz);

}