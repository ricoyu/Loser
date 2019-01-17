package com.loserico.cache.redis.collection;

import java.util.Iterator;
import java.util.Set;

import org.redisson.api.RLock;
import org.redisson.api.mapreduce.RCollectionMapReduce;

import com.loserico.cache.redis.cache.interfaze.CacheObject;
import com.loserico.cache.redis.cache.interfaze.Expirable;
import com.loserico.cache.redis.concurrent.Lock;

public interface CachedSet<E> extends Expirable, CacheObject, Set<E>{

    /**
     * Returns lock instance associated with <code>value</code>
     * 
     * @param value - set value
     * @return lock
     */
    Lock getLock(E value);
    
    /**
     * Returns values iterator matches <code>pattern</code>. 
     * 
     * @param pattern for values
     * @return iterator
     */
    Iterator<E> iterator(String pattern);
    
    /**
     * Removes and returns random elements from set
     * 
     * @param amount of random values
     * @return random values
     */
    Set<E> removeRandom(int amount);
    
    /**
     * Removes and returns random element from set
     *
     * @return value
     */
    E removeRandom();

    /**
     * Returns random element from set
     *
     * @return value
     */
    E random();

    /**
     * Move a member from this set to the given destination set in.
     *
     * @param destination the destination set
     * @param member the member to move
     * @return true if the element is moved, false if the element is not a
     * member of this set or no operation was performed
     */
    boolean move(String destination, E member);

    /**
     * Read all elements at once
     *
     * @return values
     */
    Set<E> readAll();

    /**
     * Union sets specified by name and write to current set.
     * If current set already exists, it is overwritten.
     *
     * @param names - name of sets
     * @return size of union
     */
    int union(String... names);

    /**
     * Union sets specified by name with current set
     * without current set state change.
     * 
     * @param names - name of sets
     * @return values
     */
    Set<E> readUnion(String... names);

    /**
     * Diff sets specified by name and write to current set.
     * If current set already exists, it is overwritten.
     *
     * @param names - name of sets
     * @return values
     */
    int diff(String... names);

    /**
     * Diff sets specified by name with current set.
     * Without current set state change.
     * 
     * @param names - name of sets
     * @return values
     */

    Set<E> readDiff(String... names);
    /**
     * Intersection sets specified by name and write to current set.
     * If current set already exists, it is overwritten.
     *
     * @param names - name of sets
     * @return size of intersection
     */
    int intersection(String... names);

    /**
     * Intersection sets specified by name with current set
     * without current set state change.
     * 
     * @param names - name of sets
     * @return values
     */
    Set<E> readIntersection(String... names);
}
