package com.loserico.cache.redis.collection;

import java.util.List;

import org.redisson.api.RList;

import com.loserico.cache.redis.cache.interfaze.Expirable;

/**
 * Redis 缓存版本的List
 * <p>
 * Copyright: Copyright (c) 2018-06-05 13:29
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 * @param <E>
 */
public interface CachedList<E> extends Expirable, List<E> {

    /**
     * Add <code>element</code> after <code>elementToFind</code>
     * 
     * @param elementToFind - object to find
     * @param element - object to add
     * @return new list size
     */
    Integer addAfter(E elementToFind, E element);
    
    /**
     * Add <code>element</code> before <code>elementToFind</code>
     * 
     * @param elementToFind - object to find
     * @param element - object to add
     * @return new list size
     */
    Integer addBefore(E elementToFind, E element);
    
    /**
     * Set <code>element</code> at <code>index</code>.
     * Works faster than {@link #set(int, Object)} but 
     * doesn't return previous element.
     * 
     * @param index - index of object
     * @param element - object to set
     */
    void fastSet(int index, E element);

    RList<E> subList(int fromIndex, int toIndex);

    /**
     * Read all elements at once
     *
     * @return list of values
     */
    List<E> readAll();

    /**
     * Trim list and remains elements only in specified range
     * <tt>fromIndex</tt>, inclusive, and <tt>toIndex</tt>, inclusive.
     *
     * @param fromIndex - from index
     * @param toIndex - to index
     */
    void trim(int fromIndex, int toIndex);

    void fastRemove(int index);
}
