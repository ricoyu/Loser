package com.loserico.cache.redis.redisson.concurrent;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;

import com.loserico.cache.redis.concurrent.Lock;
import com.loserico.commons.utils.DateUtils;
import com.peacefish.spring.transaction.TransactionEvents;

/**
 * Redisson 锁相关操作返回结果
 * <p>
 * Copyright: Copyright (c) 2018-05-18 11:52
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class RedissonLock implements Lock{

	private boolean locked;
	
	private RLock lock;
	
	public RedissonLock(RLock lock, boolean locked) {
		this.lock = lock;
		this.locked = locked;
	}

	/**
	 * 是否成功获取锁
	 * @return boolean
	 */
	public boolean locked() {
		return locked;
	}

	/**
	 * 释放锁。需要先检查是否成功获取锁，没获得锁就调用该方法将抛异常
	 * @throws IllegalMonitorStateException
	 */
	public void unlock() throws IllegalMonitorStateException{
		lock.unlock();
	}

	/**
	 * 释放锁。需要先检查是否成功获取锁，没获得锁就调用该方法将抛异常
	 * 在事务环境，不管事务提交与否，都会释放锁
	 * @throws IllegalMonitorStateException
	 */
	public void unlockAnyway() throws IllegalMonitorStateException{
		TransactionEvents.instance().afterCommit(() -> lock.unlock());
	}

	@Override
	public boolean expire(long timeToLive, TimeUnit timeUnit) {
		return lock.expire(timeToLive, timeUnit);
	}

	@Override
	public boolean expireAt(long timestamp) {
		return lock.expireAt(timestamp);
	}

	@Override
	public boolean expireAt(LocalDateTime timestamp) {
		return lock.expireAt(DateUtils.toDate(timestamp));
	}

	@Override
	public boolean clearExpire() {
		return lock.clearExpire();
	}

	@Override
	public long remainTimeToLive() {
		return lock.remainTimeToLive();
	}

}
