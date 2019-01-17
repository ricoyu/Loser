package com.loserico.cache.redis.redisson.collection;

import static com.loserico.commons.utils.DateUtils.SHANG_HAI;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RList;

import com.loserico.cache.redis.collection.CachedList;
import com.loserico.commons.utils.DateUtils;

public class RedissonArrayList<E> implements CachedList<E> {

	private RList<E> rList;

	public RedissonArrayList(RList<E> rList) {
		this.rList = rList;
	}

	@Override
	public boolean expire(long timeToLive, TimeUnit timeUnit) {
		return rList.expire(timeToLive, timeUnit);
	}

	@Override
	public boolean expireAt(long timestamp) {
		return rList.expireAt(timestamp);
	}

	@Override
	public boolean expireAt(LocalDateTime localDateTime) {
		long timestamp = DateUtils.toEpochMilis(localDateTime, SHANG_HAI);
		return rList.expireAt(timestamp);
	}

	@Override
	public boolean clearExpire() {
		return rList.clearExpire();
	}

	@Override
	public long remainTimeToLive() {
		return rList.remainTimeToLive();
	}

	@Override
	public int size() {
		return rList.size();
	}

	@Override
	public boolean isEmpty() {
		return rList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return rList.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return rList.iterator();
	}

	@Override
	public Object[] toArray() {
		return rList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return rList.toArray(a);
	}

	@Override
	public boolean add(E e) {
		return rList.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return rList.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return rList.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return rList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return rList.addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return rList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return rList.retainAll(c);
	}

	@Override
	public void clear() {
		rList.clear();
	}

	@Override
	public E get(int index) {
		return rList.get(index);
	}

	@Override
	public E set(int index, E element) {
		return rList.set(index, element);
	}

	@Override
	public void add(int index, E element) {
		rList.add(index, element);
	}

	@Override
	public E remove(int index) {
		return rList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return rList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return rList.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return rList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return rList.listIterator(index);
	}

	@Override
	public Integer addAfter(E elementToFind, E element) {
		return rList.addAfter(elementToFind, element);
	}

	@Override
	public Integer addBefore(E elementToFind, E element) {
		return rList.addBefore(elementToFind, element);
	}

	@Override
	public void fastSet(int index, E element) {
		rList.fastSet(index, element);
	}

	@Override
	public RList<E> subList(int fromIndex, int toIndex) {
		return rList.subList(fromIndex, toIndex);
	}

	@Override
	public List<E> readAll() {
		return rList.readAll();
	}

	@Override
	public void trim(int fromIndex, int toIndex) {
		rList.trim(fromIndex, toIndex);
	}

	@Override
	public void fastRemove(int index) {
		rList.fastRemove(index);
	}

}
