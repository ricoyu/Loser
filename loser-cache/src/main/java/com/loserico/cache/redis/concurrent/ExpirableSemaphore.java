package com.loserico.cache.redis.concurrent;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RPermitExpirableSemaphore;

import com.loserico.cache.redis.cache.interfaze.CacheObject;

/**
 * 拿到船票后即使不手工释放，也会自动过期的版本
 * <p>
 * Copyright: Copyright (c) 2018-05-19 21:20
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class ExpirableSemaphore implements CacheObject{
	
	private RPermitExpirableSemaphore semaphore;
	
	private long leaseTime;
	
	private TimeUnit unit;
	
	private boolean permitsSetSuccess;

	public ExpirableSemaphore(RPermitExpirableSemaphore semaphore, long leaseTime, TimeUnit unit) {
		this.semaphore = semaphore;
		this.leaseTime = leaseTime;
		this.unit = unit;
	}
	
	public ExpirableSemaphore(RPermitExpirableSemaphore semaphore, long leaseTime, TimeUnit unit, boolean permitsSetSuccess) {
		this.semaphore = semaphore;
		this.leaseTime = leaseTime;
		this.unit = unit;
		this.permitsSetSuccess = permitsSetSuccess;
	}
	
	/**
	 * 尝试获取船票，获取不到一直阻塞直到leaseTime个单位时间后
	 */
	public String acquire() throws InterruptedException {
		return semaphore.acquire(leaseTime, unit);
	}
	
	/**
	 * 尝试获取船票，如果没有船票则等待，直到 waitTime 个单位时间后超时
	 * 如果成功获取到船票，不手工释放的话 leaseTime 个单位时间后自动释放
	 * @param waitTime 超时时间，单位是创建这个semaphore时传入的TimeUnit，和leaseTime单位是一样的
	 * @return String permitId 用于释放船票
	 * @throws InterruptedException
	 */
	public String tryAcquire(long waitTime) throws InterruptedException {
		return semaphore.tryAcquire(waitTime, leaseTime, unit);
	}
	
	public void release(String permitId) {
		semaphore.release(permitId);
	}

	@Override
	public boolean delete() {
		return semaphore.delete();
	}

	@Override
	public boolean isExists() {
		return semaphore.isExists();
	}

	public boolean isPermitsSetSuccess() {
		return permitsSetSuccess;
	}

}
