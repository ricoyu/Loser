package com.loserico.cache.redis.pure.concurrent;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import com.loserico.cache.exception.OperationNotSupportedException;
import com.loserico.cache.redis.JedisUtils;
import com.loserico.cache.redis.cache.interfaze.Expirable;
import com.loserico.cache.redis.concurrent.Lock;
import com.loserico.commons.utils.DateUtils;

/**
 * 非阻塞锁
 * <p>
 * Copyright: Copyright (c) 2018-07-16 10:32
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class NonBlockingLock implements Lock, Expirable {

	private String key;

	private String requestId;

	private boolean locked;

	public NonBlockingLock(String key, String requestId, boolean locked) {
		this.key = key;
		if (locked) {
			this.requestId = requestId;
		}
		this.locked = locked;
	}

	@Override
	public void unlock() {
		if (locked) {
			boolean unlockSuccess = JedisUtils.unlock(key, requestId);
			if (!unlockSuccess) {
				throw new OperationNotSupportedException("解锁失败了哟");
			}
		} else {
			throw new OperationNotSupportedException("你还没获取到锁哦");
		}
	}

	@Override
	public void unlockAnyway() {
		if (locked) {
			JedisUtils.unlockAnyway(key, requestId);
		} else {
			throw new OperationNotSupportedException("你还没获取到锁哦");
		}
	}

	public boolean locked() {
		return locked;
	}

	@Override
	public boolean expire(long timeToLive, TimeUnit timeUnit) {
		if (locked) {
			return JedisUtils.expire(timeUnit, ((Long) timeToLive).intValue(), timeUnit);
		}
		throw new OperationNotSupportedException("你还没获取到锁哦");
	}

	@Override
	public boolean expireAt(long timestamp) {
		return JedisUtils.expireAt(key, timestamp);
	}

	@Override
	public boolean expireAt(LocalDateTime timestamp) {
		if (timestamp == null) {
			return false;
		}
		return JedisUtils.expireAt(key, DateUtils.toEpochMilis(timestamp));
	}

	@Override
	public boolean clearExpire() {
		return JedisUtils.persist(key);
	}

	@Override
	public long remainTimeToLive() {
		return JedisUtils.ttl(key);
	}

	/**
	 * 不支持改操作, 请改用RedissonLock
	 * throws UnsupportedOperationException
	 */
	@Override
	public void lock() {
		throw new UnsupportedOperationException("NonBlockingLock not support lock(), use RedissonLock instead");
	}

	/**
	 * 不支持改操作, 请改用RedissonLock
	 * throws UnsupportedOperationException
	 */
	@Override
	public void lockInterruptibly() throws InterruptedException {
		throw new UnsupportedOperationException("NonBlockingLock not support lockInterruptibly(), use RedissonLock instead");
	}

	/**
	 * 不支持改操作, 请改用RedissonLock
	 * throws UnsupportedOperationException
	 */
	@Override
	public boolean tryLock() {
		throw new UnsupportedOperationException("NonBlockingLock not support tryLock(), use RedissonLock instead");
	}

	/**
	 * 不支持改操作, 请改用RedissonLock
	 * throws UnsupportedOperationException
	 */
	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		throw new UnsupportedOperationException("NonBlockingLock not support tryLock(time, unit), use RedissonLock instead");
	}

	/**
	 * 不支持改操作, 请改用RedissonLock
	 * throws UnsupportedOperationException
	 */
	@Override
	public Condition newCondition() {
		throw new UnsupportedOperationException("NonBlockingLock not support newCondition(), use RedissonLock instead");
	}

}
