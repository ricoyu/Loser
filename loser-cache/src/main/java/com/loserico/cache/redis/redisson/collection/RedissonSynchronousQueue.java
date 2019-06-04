package com.loserico.cache.redis.redisson.collection;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBoundedBlockingQueue;

import com.loserico.cache.redis.cache.interfaze.SynchronousQueue;
import com.loserico.commons.utils.DateUtils;

/**
 * 基于Redisson的分布式SynchronousQueue
 * <p>
 * Copyright: Copyright (c) 2019-06-03 16:43
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 * @param <E>
 */
public class RedissonSynchronousQueue<E> implements SynchronousQueue<E> {
	
	private RBoundedBlockingQueue<E> bouldedBlockingQueue;
	
	private boolean capacitySetSuccess = true;
	
	public RedissonSynchronousQueue(RBoundedBlockingQueue<E> bouldedBlockingQueue, boolean capacitySetSuccess) {
		this.bouldedBlockingQueue = bouldedBlockingQueue;
		this.capacitySetSuccess = capacitySetSuccess;
	}

	@Override
	public List<E> readAll() {
		return bouldedBlockingQueue.readAll();
	}

	@Override
	public boolean add(E e) {
		return bouldedBlockingQueue.add(e);
	}

	@Override
	public boolean offer(E e) {
		return bouldedBlockingQueue.offer(e);
	}

	@Override
	public void put(E e) throws InterruptedException {
		bouldedBlockingQueue.put(e);
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		return bouldedBlockingQueue.offer(e, timeout, unit);
	}

	@Override
	public E remove() {
		return bouldedBlockingQueue.remove();
	}

	@Override
	public E poll() {
		return bouldedBlockingQueue.poll();
	}

	@Override
	public E take() throws InterruptedException {
		return bouldedBlockingQueue.take();
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		return bouldedBlockingQueue.poll(timeout, unit);
	}

	@Override
	public E element() {
		return bouldedBlockingQueue.element();
	}

	@Override
	public E peek() {
		return bouldedBlockingQueue.peek();
	}

	@Override
	public boolean remove(Object o) {
		return bouldedBlockingQueue.remove(o);
	}

	@Override
	public boolean contains(Object o) {
		return bouldedBlockingQueue.contains(o);
	}

    /**
     * Always returns zero.
     * A {@code SynchronousQueue} has no internal capacity.
     *
     * @return zero
     */
	@Override
	public int size() {
		return 0;
	}

	@Override
	public Iterator<E> iterator() {
		return bouldedBlockingQueue.iterator();
	}

    /**
     * Always returns zero.
     * A {@code SynchronousQueue} has no internal capacity.
     *
     * @return zero
     */
	@Override
	public int remainingCapacity() {
		return 0;
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		return bouldedBlockingQueue.drainTo(c);
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		return bouldedBlockingQueue.drainTo(c, maxElements);
	}

    /**
     * Always returns {@code true}.
     * A {@code SynchronousQueue} has no internal capacity.
     *
     * @return {@code true}
     */
	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public Object[] toArray() {
		return bouldedBlockingQueue.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return bouldedBlockingQueue.toArray(a);
	}

    /**
     * Returns {@code false} unless the given collection is empty.
     * A {@code SynchronousQueue} has no internal capacity.
     *
     * @param c the collection
     * @return {@code false} unless given collection is empty
     */
	@Override
	public boolean containsAll(Collection<?> c) {
		return c.isEmpty();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return bouldedBlockingQueue.addAll(c);
	}

    /**
     * Always returns {@code false}.
     * A {@code SynchronousQueue} has no internal capacity.
     *
     * @param c the collection
     * @return {@code false}
     */
	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return bouldedBlockingQueue.retainAll(c);
	}

    /**
     * Does nothing.
     * A {@code SynchronousQueue} has no internal capacity.
     */
	@Override
	public void clear() {
		bouldedBlockingQueue.clear();
	}

    /**
     * Always returns {@code false}.
     * A {@code SynchronousQueue} has no internal capacity.
     *
     * @param c the collection
     * @return {@code false}
     */
	@Override
	public boolean delete() {
		return false;
	}

	@Override
	public boolean isExists() {
		return bouldedBlockingQueue.isExists();
	}

	@Override
	public boolean expire(long timeToLive, TimeUnit timeUnit) {
		return bouldedBlockingQueue.expire(timeToLive, timeUnit);
	}

	@Override
	public boolean expireAt(long timestamp) {
		return bouldedBlockingQueue.expireAt(timestamp);
	}

	@Override
	public boolean expireAt(LocalDateTime localDateTime) {
		return bouldedBlockingQueue.expireAt(DateUtils.toDate(localDateTime));
	}

	@Override
	public boolean clearExpire() {
		return bouldedBlockingQueue.clearExpire();
	}

	@Override
	public long remainTimeToLive() {
		return bouldedBlockingQueue.remainTimeToLive();
	}

	/**
	 * 永远返回true
	 */
	@Override
	public boolean isCapacitySetSuccess() {
		return capacitySetSuccess;
	}

}
