package com.loserico.cache.redis.cache.interfaze;

import java.util.List;

public interface BlockingQueue<V> extends Expirable, CacheObject, java.util.concurrent.BlockingQueue<V> {

	List<V> readAll();

	/**
	 * 如果Capacity已经被设置过，返回false， 否则返回true
	 * @return
	 */
	boolean isCapacitySetSuccess();
}
