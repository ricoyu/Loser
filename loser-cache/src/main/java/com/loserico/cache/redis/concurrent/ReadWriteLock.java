package com.loserico.cache.redis.concurrent;

import com.loserico.cache.redis.cache.interfaze.Expirable;

/**
 * A {@code ReadWriteLock} maintains a pair of associated {@link
 * Lock locks}, one for read-only operations and one for writing.
 * The {@link #readLock read lock} may be held simultaneously by
 * multiple reader threads, so long as there are no writers.  The
 * {@link #writeLock write lock} is exclusive.
 *
 * Works in non-fair mode. Therefore order of read and write
 * locking is unspecified.
 * 
 * <p>
 * Copyright: Copyright (c) 2018-05-19 20:00
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public interface ReadWriteLock extends Expirable{

	/**
	 * Returns the lock used for reading.
	 *
	 * @return the lock used for reading
	 */
	Lock readLock();

	/**
	 * Returns the lock used for writing.
	 *
	 * @return the lock used for writing
	 */
	Lock writeLock();
	
}
