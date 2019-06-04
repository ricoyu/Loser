package com.loserico.cache.redis.redisson.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import org.redisson.api.RLock;

import com.loserico.cache.redis.concurrent.Lock;
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
		TransactionEvents.instance().afterCompletion(() -> lock.forceUnlock());
	}

	@Override
	public long remainTimeToLive() {
		return lock.remainTimeToLive();
	}

	@Override
	public void lock() {
		lock.lock();
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		lock.lockInterruptibly();
	}

	@Override
	public boolean tryLock() {
		return lock.tryLock();
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return lock.tryLock(time, unit);
	}

	@Override
	public Condition newCondition() {
		return lock.newCondition();
	}

}
