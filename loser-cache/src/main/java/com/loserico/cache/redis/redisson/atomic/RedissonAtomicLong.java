package com.loserico.cache.redis.redisson.atomic;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RAtomicLong;

import com.loserico.cache.redis.cache.interfaze.CacheObject;
import com.loserico.cache.redis.concurrent.atomic.AtomicLong;
import com.loserico.commons.utils.DateUtils;

public class RedissonAtomicLong implements AtomicLong, CacheObject {

	private RAtomicLong atomicLong;

	public RedissonAtomicLong(RAtomicLong atomicLong) {
		this.atomicLong = atomicLong;
	}

	@Override
	public long getAndDecrement() {
		return atomicLong.getAndDecrement();
	}

	@Override
	public long addAndGet(long delta) {
		return atomicLong.addAndGet(delta);
	}

	@Override
	public boolean compareAndSet(long expect, long update) {
		return atomicLong.compareAndSet(expect, update);
	}

	@Override
	public long decrementAndGet() {
		return atomicLong.decrementAndGet();
	}

	@Override
	public long get() {
		return atomicLong.get();
	}

	@Override
	public long getAndAdd(long delta) {
		return atomicLong.getAndAdd(delta);
	}

	@Override
	public long getAndSet(long newValue) {
		return atomicLong.getAndSet(newValue);
	}

	@Override
	public long incrementAndGet() {
		return atomicLong.incrementAndGet();
	}

	@Override
	public long getAndIncrement() {
		return atomicLong.getAndIncrement();
	}

	@Override
	public void set(long newValue) {
		atomicLong.set(newValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T backdrop() {
		return (T) atomicLong;
	}

	@Override
	public boolean expire(long timeToLive, TimeUnit timeUnit) {
		return atomicLong.expire(timeToLive, timeUnit);
	}

	@Override
	public boolean expireAt(long timestamp) {
		return atomicLong.expireAt(timestamp);
	}

	@Override
	public boolean delete() {
		return atomicLong.delete();
	}

	@Override
	public boolean isExists() {
		return atomicLong.isExists();
	}

	@Override
	public boolean expireAt(LocalDateTime localDateTime) {
		return atomicLong.expireAt(DateUtils.toEpochMilis(localDateTime));
	}

	@Override
	public boolean clearExpire() {
		return atomicLong.clearExpire();
	}

	@Override
	public long remainTimeToLive() {
		return atomicLong.remainTimeToLive();
	}

}
