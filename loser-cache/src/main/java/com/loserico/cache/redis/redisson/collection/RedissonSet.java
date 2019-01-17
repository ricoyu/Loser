package com.loserico.cache.redis.redisson.collection;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RSet;
import org.redisson.api.mapreduce.RCollectionMapReduce;

import com.loserico.cache.redis.collection.CachedSet;
import com.loserico.cache.redis.concurrent.Lock;
import com.loserico.cache.redis.redisson.concurrent.RedissonLock;
import com.loserico.commons.utils.DateUtils;

public class RedissonSet<E> implements CachedSet<E> {
	
	private RSet<E> rSet;
	
	public RedissonSet(RSet<E> rSet) {
		this.rSet = rSet;
	}

	@Override
	public boolean expire(long timeToLive, TimeUnit timeUnit) {
		return rSet.expire(timeToLive, timeUnit);
	}

	@Override
	public boolean expireAt(long timestamp) {
		return rSet.expireAt(timestamp);
	}

	@Override
	public boolean expireAt(LocalDateTime localDateTime) {
		return rSet.expireAt(DateUtils.toEpochMilis(localDateTime));
	}

	@Override
	public boolean clearExpire() {
		return rSet.clearExpire();
	}

	@Override
	public long remainTimeToLive() {
		return rSet.remainTimeToLive();
	}

	@Override
	public boolean delete() {
		return rSet.delete();
	}

	@Override
	public boolean isExists() {
		return rSet.isExists();
	}

	@Override
	public int size() {
		return rSet.size();
	}

	@Override
	public boolean isEmpty() {
		return rSet.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return rSet.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return rSet.iterator();
	}

	@Override
	public Object[] toArray() {
		return rSet.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return rSet.toArray(a);
	}

	@Override
	public boolean add(E e) {
		return rSet.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return rSet.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return rSet.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return rSet.addAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return rSet.retainAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return rSet.removeAll(c);
	}

	@Override
	public void clear() {
		rSet.clear();
	}

	@Override
	public Lock getLock(E value) {
		return new RedissonLock(rSet.getLock(value), true);
	}

	@Override
	public Iterator<E> iterator(String pattern) {
		return rSet.iterator(pattern);
	}

	@Override
	public Set<E> removeRandom(int amount) {
		return rSet.removeRandom(amount);
	}

	@Override
	public E removeRandom() {
		return rSet.removeRandom();
	}

	@Override
	public E random() {
		return rSet.random();
	}

	@Override
	public boolean move(String destination, E member) {
		return rSet.move(destination, member);
	}

	@Override
	public Set<E> readAll() {
		return rSet.readAll();
	}

	@Override
	public int union(String... names) {
		return rSet.union(names);
	}

	@Override
	public Set<E> readUnion(String... names) {
		return rSet.readUnion(names);
	}

	@Override
	public int diff(String... names) {
		return rSet.diff(names);
	}

	@Override
	public Set<E> readDiff(String... names) {
		return rSet.readDiff(names);
	}

	@Override
	public int intersection(String... names) {
		return rSet.intersection(names);
	}

	@Override
	public Set<E> readIntersection(String... names) {
		return rSet.readIntersection(names);
	}

}
