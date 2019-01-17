package com.loserico.concurrent.stampedlock;

import java.util.concurrent.locks.StampedLock;

/**
 * We have many ways to write similar type of code. In our very simple BankAccount
 * example, the BankAccountSynchronizedVolatile and BankAccountAtomic solutions are
 * both simple and work very well. However, if we had multiple fields and/or an
 * invariant over the field, we would need a slightly stronger mechanism. Let's take
 * for example a point on a plane, consisting of an "x" and a "y". If we move in a
 * diagonal line, we want to update the x and y in an atomic operation. Thus a
 * moveBy(10,10) should never expose a state where x has moved by 10, but y is still
 * at the old point. The fully synchronized approach would work, as would the
 * ReentrantLock and ReentrantReadWriteLock. However, all of these are pessimistic.
 * How can we read state in an optimistic approach, expecting to see the correct
 * values?
 * 
 * Let's start by defining a simple Point class that is synchronized and has three
 * methods for reading and manipulating the state:
 * 
 * @author Loser
 * @since Jul 23, 2016
 * @version
 *
 */
public class Point {
	private int x, y;
	private StampedLock sl = new StampedLock();

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public synchronized void move(int deltaX, int deltaY) {
		x += deltaX;
		y += deltaY;
	}

	/*
	 * If we use a StampedLock, our move() method look similar to the
	 * BankAccountStampedLock.deposit()
	 */
	/*public void move(int deltaX, int deltaY) {
		long stamp = sl.writeLock();
		try {
			x += deltaX;
			y += deltaY;
		} finally {
			sl.unlockWrite(stamp);
		}
	}*/

	public synchronized double distanceFromOrigin() {
		return Math.hypot(x, y);
	}
	
	/*
	 * However, the distanceFromOrigin() method could be rewritten to use an
	 * optimistic read
	 * 
	 * In our optimistic distanceFromOrigin(), we first try to get an optimistic
	 * read stamp. This might come back as zero if a writeLock stamp has been issued
	 * and has not been unlocked yet. However, we assume that it is non-zero and
	 * continue reading the fields into local variables localX and localY. After we
	 * have read x and y, we validate the stamp. Two things could make this fail: 
	 * 1. The sl.tryOptimisticRead() method might have come back as zero initially or
	 * 2. After we obtained our optimistic lock, another thread requested a writeLock(). 
	 * We don't know whether this means our copies are invalid, but we need to be safe, rather than sorry. 
	 * 
	 * In this version we only try this once and
	 * if we do not succeed we immediately move over to the pessimistic read
	 * version. Depending on the system, we could get significant performance gains
	 * by spinning for a while, hoping to do a successful optimistic read. In our
	 * experiments, we also found that a shorter code path between
	 * tryOptimisticRead() and validate() leads to a higher chance of success in the
	 * optimistic read case.
	 */
/*	public double distanceFromOrigin() {
		long stamp = sl.tryOptimisticRead();
		int localX = x, localY = y;
		if (!sl.validate(stamp)) {
			stamp = sl.readLock();
			try {
				localX = x;
				localY = y;
			} finally {
				sl.unlockRead(stamp);
			}
		}
		return Math.hypot(localX, localY);
	}*/
	
	/*
	 * Here is another idiom that retries a number of times before defaulting to the
	 * pessimistic read version. It uses the trick in Java where we break out to a
	 * label, thus jumping out of a code block. We could have also solved this with
	 * a local boolean variable, but to me this is a bit clearer:
	 */
/*	private static final int RETRIES = 5;

	public double distanceFromOrigin() {
		int localX, localY;
		out: {
			// try a few times to do an optimistic read
			for (int i = 0; i < RETRIES; i++) {
				long stamp = sl.tryOptimisticRead();
				localX = x;
				localY = y;
				if (sl.validate(stamp)) {
					break out;
				}
			}
			// pessimistic read
			long stamp = sl.readLock();
			try {
				localX = x;
				localY = y;
			} finally {
				sl.unlockRead(stamp);
			}
		}
		return Math.hypot(localX, localY);
	}*/

/*	public synchronized boolean moveIfAt(int oldX, int oldY, int newX, int newY) {
		if (x == oldX && y == oldY) {
			x = newX;
			y = newY;
			return true;
		} else {
			return false;
		}
	}*/
	
	/*
	 * The last idiom to demonstrate is the conditional write. We first read the
	 * current state. If it is what we expect, then we try to upgrade the read lock
	 * to a write lock. If we succeed, then we update the state and exit, otherwise
	 * we unlock the read lock and then ask for a write lock. The code is a bit
	 * harder to understand, so look it over carefully
	 * 
	 * Whilst this idiom looks clever, I doubt it is really that useful. Initial
	 * tests have shown that it does not perform that well. In addition, it is
	 * difficult to write and understand.
	 */
	public boolean moveIfAt(int oldX, int oldY, int newX, int newY) {
		long stamp = sl.readLock();
		try {
			while (x == oldX && y == oldY) {
				long writeStamp = sl.tryConvertToWriteLock(stamp);
				if (writeStamp != 0) {
					stamp = writeStamp;
					x = newX;
					y = newY;
					return true;
				} else {
					sl.unlockRead(stamp);
					stamp = sl.writeLock();
				}
			}
			return false;
		} finally {
			sl.unlock(stamp); // could be read or write lock
		}
	}
}