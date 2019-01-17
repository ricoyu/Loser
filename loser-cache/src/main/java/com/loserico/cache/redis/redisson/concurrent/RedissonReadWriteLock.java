package com.loserico.cache.redis.redisson.concurrent;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;

import com.loserico.cache.redis.concurrent.Lock;
import com.loserico.cache.redis.concurrent.ReadWriteLock;
import com.loserico.commons.utils.DateUtils;

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

	@Override
	public boolean expire(long timeToLive, TimeUnit timeUnit) {
		return readWriteLock.expire(timeToLive, timeUnit);
	}

	@Override
	public boolean expireAt(long timestamp) {
		return readWriteLock.expireAt(timestamp);
	}

	@Override
	public boolean expireAt(LocalDateTime timestamp) {
		return readWriteLock.expireAt(DateUtils.toDate(timestamp));
	}

	@Override
	public boolean clearExpire() {
		return readWriteLock.clearExpire();
	}

	@Override
	public long remainTimeToLive() {
		return readWriteLock.remainTimeToLive();
	}

}
