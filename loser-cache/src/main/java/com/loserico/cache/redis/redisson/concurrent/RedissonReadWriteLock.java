package com.loserico.cache.redis.redisson.concurrent;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;

import com.loserico.cache.redis.concurrent.Lock;
import com.loserico.cache.redis.concurrent.ReadWriteLock;

public class RedissonReadWriteLock implements ReadWriteLock {

	private RReadWriteLock readWriteLock;
	
	public RedissonReadWriteLock(RReadWriteLock readWriteLock) {
		this.readWriteLock = readWriteLock;
	}
	
	@Override
	public Lock readLock() {
		RLock lock = readWriteLock.readLock();
		return new RedissonLock(lock, lock.isHeldByCurrentThread());
	}

	@Override
	public Lock writeLock() {
		RLock lock = readWriteLock.writeLock();
		return new RedissonLock(lock, lock.isHeldByCurrentThread());
	}

}
