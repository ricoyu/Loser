package com.loserico.cache.redis.redisson.concurrent;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RSemaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.loserico.cache.redis.concurrent.Semaphore;
import com.loserico.commons.utils.DateUtils;

/**
 * 信号量
 * 用法：创建出Semaphore对象，分发给多个客户端，客户端在同一个Semaphore对象上调用acquire(), release()
 * <p>
 * Copyright: Copyright (c) 2018-05-18 14:09
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class RedissonSemaphore implements Semaphore{

	private static final Logger logger = LoggerFactory.getLogger(RedissonSemaphore.class);

	private RSemaphore semaphore;

	private boolean permitsSetSuccess = false;

	public RedissonSemaphore(RSemaphore semaphore) {
		this.semaphore = semaphore;
	}

	public RedissonSemaphore(RSemaphore semaphore, boolean permitsSetSuccess) {
		this.semaphore = semaphore;
		this.permitsSetSuccess = permitsSetSuccess;
	}

	/**
	 * 尝试获取船票，获取不到一直阻塞
	 * 
	 * @throws InterruptedException
	 */
	@Override
	public void acquire() throws InterruptedException {
		semaphore.acquire(1);
	}

	/**
	 * 获取船票，该方法立即返回获取成功与否状态
	 * 实现的Lua脚本如下
	 * local value = redis.call('get', KEYS[1]);
	 * if (value ~= false and tonumber(value) >= tonumber(ARGV[1])) then
	 *     local val = redis.call('decrby', KEYS[1], ARGV[1]);
	 *     return 1;
	 * end;
	 * return 0;
	 * 
	 * @return
	 * @on
	 */
	@Override
	public boolean tryAcquire() {
		return semaphore.tryAcquire();
	}

	/**
	 * 释放船票
	 * local value = redis.call('incrby', KEYS[1], ARGV[1]);
	 * KEYS[1] 传的是 name
	 * ARGV[1] 传的是1
	 * @on
	 */
	@Override
	public void release() {
		semaphore.release();
	}

	/**
	 * 只有这个semaphore第一次创建的时候可以设置成功，后续的都不能设置成功了
	 * @return
	 */
	public boolean isPermitsSetSuccess() {
		return permitsSetSuccess;
	}

	@Override
	public boolean delete() {
		return semaphore.delete();
	}

	@Override
	public boolean isExists() {
		return semaphore.isExists();
	}

	@Override
	public boolean expire(long timeToLive, TimeUnit timeUnit) {
		return semaphore.expire(timeToLive, timeUnit);
	}

	@Override
	public boolean expireAt(long timestamp) {
		return semaphore.expireAt(timestamp);
	}

	@Override
	public boolean expireAt(LocalDateTime localDateTime) {
		return semaphore.expireAt(DateUtils.toEpochMilis(localDateTime));
	}

	@Override
	public boolean clearExpire() {
		return semaphore.clearExpire();
	}

	@Override
	public long remainTimeToLive() {
		return semaphore.remainTimeToLive();
	}

	@Override
	public void acquire(int permits) throws InterruptedException {
		semaphore.acquire(permits);
	}

	@Override
	public boolean tryAcquire(int permits) {
		return semaphore.tryAcquire(permits);
	}

	@Override
	public boolean tryAcquire(long waitTime, TimeUnit unit) throws InterruptedException {
		return semaphore.tryAcquire(waitTime, unit);
	}

	@Override
	public boolean tryAcquire(int permits, long waitTime, TimeUnit unit) throws InterruptedException {
		return semaphore.tryAcquire(waitTime, unit);
	}

	@Override
	public void release(int permits) {
		semaphore.release(permits);
	}

	@Override
	public int availablePermits() {
		return semaphore.availablePermits();
	}

	@Override
	public int drainPermits() {
		return semaphore.drainPermits();
	}

	@Override
	public boolean trySetPermits(int permits) {
		return semaphore.trySetPermits(permits);
	}

	@Override
	public void reducePermits(int permits) {
		semaphore.reducePermits(permits);
	}
}