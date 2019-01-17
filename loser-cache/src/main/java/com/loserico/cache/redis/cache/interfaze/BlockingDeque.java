package com.loserico.cache.redis.cache.interfaze;

import java.util.List;

public interface BlockingDeque<E> extends java.util.concurrent.BlockingDeque<E>, CacheObject, Expirable {
	
	List<E> readAll();
}
