package com.peacefish.orm.commons.identifier.redis;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.cache.redis.JedisUtils;

class RedisDistributedIdentifierCache {

	private static final Logger logger = LoggerFactory.getLogger(RedisDistributedIdentifierCache.class);

	private long fetchSize;

	private String key; //redis 的key

	private Queue<Long> queue = new ConcurrentLinkedQueue<>();

	public RedisDistributedIdentifierCache(String key, long fetchSize) {
		this.fetchSize = fetchSize;
		this.key = key;
	}

	public synchronized Long nextIdentifer() {
		if (queue.isEmpty()) {
			try {
				Long postValue = JedisUtils.incrBy(key, fetchSize);
				Long preValue = postValue - fetchSize;
				for (long i = preValue + 1; i <= postValue; i++) { //节点第一次创建的时候从0开始，因此取preValue + 1
					queue.offer(i);
				}
			} catch (Exception e) {
				logger.error("Something error occurs when Create identifier", e);
			}
		}

		return queue.poll();
	}

}