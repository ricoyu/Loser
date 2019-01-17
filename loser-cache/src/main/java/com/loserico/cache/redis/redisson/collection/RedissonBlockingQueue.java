package com.loserico.cache.redis.redisson.collection;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBoundedBlockingQueue;

import com.loserico.cache.redis.cache.interfaze.BlockingQueue;
import com.loserico.commons.utils.DateUtils;

/**
 * 基于Redis的分布式BlockingQueue
 * <p>
 * Copyright: Copyright (c) 2018-06-17 19:41
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 * @param <V>
 */
public class RedissonBlockingQueue<V> implements BlockingQueue<V> {
	
	private RBoundedBlockingQueue<V> blockingQueue;
	
	private boolean capacitySetSuccess;

	public RedissonBlockingQueue(RBoundedBlockingQueue<V> blockingQueue) {
		this.blockingQueue = blockingQueue;
	}
	
	public RedissonBlockingQueue(RBoundedBlockingQueue<V> blockingQueue, boolean capacitySetSuccess) {
		this.blockingQueue = blockingQueue;
		this.capacitySetSuccess = capacitySetSuccess;
	}
	
	@Override
	public boolean expire(long timeToLive, TimeUnit timeUnit) {
		return blockingQueue.expire(timeToLive, timeUnit);
	}

	@Override
	public boolean expireAt(long timestamp) {
		return blockingQueue.expireAt(timestamp);
	}

	@Override
	public boolean expireAt(LocalDateTime localDateTime) {
		return blockingQueue.expireAt(DateUtils.toEpochMilis(localDateTime));
	}

	@Override
	public boolean clearExpire() {
		return blockingQueue.clearExpire();
	}

	@Override
	public long remainTimeToLive() {
		return blockingQueue.remainTimeToLive();
	}

	@Override
	public boolean add(V e) {
		return blockingQueue.add(e);
	}

	@Override
	public boolean offer(V e) {
		return blockingQueue.offer(e);
	}

	@Override
	public void put(V e) throws InterruptedException {
		blockingQueue.put(e);
	}

	@Override
	public boolean offer(V e, long timeout, TimeUnit unit) throws InterruptedException {
		return blockingQueue.offer(e, timeout, unit);
	}

	@Override
	public V take() throws InterruptedException {
		return blockingQueue.take();
	}

	@Override
	public V poll(long timeout, TimeUnit unit) throws InterruptedException {
		return blockingQueue.poll(timeout, unit);
	}

	@Override
	public int remainingCapacity() {
		return blockingQueue.remainingCapacity();
	}

	@Override
	public boolean remove(Object o) {
		return blockingQueue.remove(o);
	}

	@Override
	public boolean contains(Object o) {
		return blockingQueue.contains(o);
	}

	@Override
	public int drainTo(Collection<? super V> c) {
		return blockingQueue.drainTo(c);
	}

	@Override
	public int drainTo(Collection<? super V> c, int maxElements) {
		return blockingQueue.drainTo(c, maxElements);
	}

	@Override
	public V remove() {
		return blockingQueue.remove();
	}

	@Override
	public V poll() {
		return blockingQueue.poll();
	}

	@Override
	public V element() {
		return blockingQueue.element();
	}

	@Override
	public V peek() {
		return blockingQueue.peek();
	}

	@Override
	public int size() {
		return blockingQueue.size();
	}

	@Override
	public boolean isEmpty() {
		return blockingQueue.isEmpty();
	}

	@Override
	public Iterator<V> iterator() {
		return blockingQueue.iterator();
	}

	@Override
	public Object[] toArray() {
		return blockingQueue.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return blockingQueue.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return blockingQueue.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		return blockingQueue.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return blockingQueue.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return blockingQueue.retainAll(c);
	}

	@Override
	public void clear() {
		blockingQueue.clear();
	}

	@Override
	public List<V> readAll() {
		return blockingQueue.readAll();
	}

	@Override
	public boolean delete() {
		return blockingQueue.delete();
	}

	@Override
	public boolean isExists() {
		return blockingQueue.isExists();
	}

	@Override
	public boolean isCapacitySetSuccess() {
		return capacitySetSuccess;
	}

}
