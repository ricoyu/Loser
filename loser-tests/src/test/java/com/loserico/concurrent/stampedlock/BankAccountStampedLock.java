package com.loserico.concurrent.stampedlock;

import java.util.concurrent.locks.StampedLock;

/**
 * Our sixth version uses StampedLock. I have written two getBalance() methods. The
 * first uses pessimistic read locks, the other optimistic. In our case, since there
 * are no invariants on the fields that would somehow restrict the values, we never
 * need to have a pessimistic lock. Thus the optimistic read is only to ensure memory
 * visibility, much like the BankAccountSynchronizedVolatile approach.
 * 
 * @author Loser
 * @since Jul 23, 2016
 * @version
 *
 */
public class BankAccountStampedLock {
	private final StampedLock sl = new StampedLock();
	private long balance;

	public BankAccountStampedLock(long balance) {
		this.balance = balance;
	}

	public void deposit(long amount) {
		long stamp = sl.writeLock();
		try {
			balance += amount;
		} finally {
			sl.unlockWrite(stamp);
		}
	}

	public void withdraw(long amount) {
		long stamp = sl.writeLock();
		try {
			balance -= amount;
		} finally {
			sl.unlockWrite(stamp);
		}
	}

	public long getBalance() {
		long stamp = sl.readLock();
		try {
			return balance;
		} finally {
			sl.unlockRead(stamp);
		}
	}

	/*
	 * In our getBalanceOptimisticRead(), we could retry several times. However, as I
	 * said before, if memory visibility is all we care about, then StampedLock is
	 * overkill.
	 */
	public long getBalanceOptimisticRead() {
		long stamp = sl.tryOptimisticRead();
		long balance = this.balance;
		if (!sl.validate(stamp)) {
			stamp = sl.readLock();
			try {
				balance = this.balance;
			} finally {
				sl.unlockRead(stamp);
			}
		}
		return balance;
	}
}