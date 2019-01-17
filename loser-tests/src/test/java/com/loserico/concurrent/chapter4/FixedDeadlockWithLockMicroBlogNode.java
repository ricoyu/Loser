package com.loserico.concurrent.chapter4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class needs to implement a different interface, as it needs to have
 * awareness of the confirmation / locking strategy of its backup node
 * 
 * The real solution is to ensure that if the attempt to get the second lock
 * fails, the thread should release the lock it’s holding and wait briefly, as
 * shown in the next listing. This gives the other threads a chance to get a
 * complete set of the locks needed to progress.
 * 
 * In this version, you examine the return code of tryConfirmUpdate(). If it
 * returns false, the original lock will be released. The thread will pause
 * briefly, allowing the other thread to potentially acquire its lock.
 *
 * Run this code a few times, and you should see that both threads are
 * basically always able to progress—you’ve eliminated the deadlock.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class FixedDeadlockWithLockMicroBlogNode implements ConfirmingMicroBlogNode {

	private static Update getUpdate(String s) {
		Update.Builder b = new Update.Builder();
		b.updateText(s).author(new Author("Ben"));
		return b.build();
	}

	private final String ident;
	private final Lock lock = new ReentrantLock();

	public FixedDeadlockWithLockMicroBlogNode(String ident) {
		this.ident = ident;
	}

	@Override
	public String getIdent() {
		return ident;
	}

	@Override
	public void propagateUpdate(Update update, ConfirmingMicroBlogNode backup) {
		boolean acquired = false;
		boolean done = false;

		while (!done) {
			int wait = (int) (Math.random() * 10);
			try {
				acquired = lock.tryLock(wait, TimeUnit.MILLISECONDS);
				if (acquired) {
					System.out.println(ident + ": recvd: " + update.getUpdateText() + " ; backup: " + backup.getIdent());
					/*
					 * Examine return from tryConfirmUpdate()
					 */
					done = backup.tryConfirmUpdate(this, update);
				}
			} catch (InterruptedException e) {
			} finally {
				// 释放第一个锁
				if (acquired) {
					lock.unlock();
				}
			}
			/*
			 * If not done, release lock and wait
			 */
			if (!done)
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
				}
		}
	}

	@Override
	public boolean tryConfirmUpdate(ConfirmingMicroBlogNode other, Update update) {
		long startTime = System.currentTimeMillis();
		boolean acquired = false;
		try {
			int wait = (int) (Math.random() * 10);
			acquired = lock.tryLock(wait, TimeUnit.MILLISECONDS);

			if (acquired) {
				long elapsed = System.currentTimeMillis() - startTime;
				System.out.println(ident + ": recvd confirm: " + update.getUpdateText() + " from " + other.getIdent() + " - took "
						+ elapsed + " millis");
				return true;
			}
		} catch (InterruptedException e) {
		} finally {
			if (acquired) {
				lock.unlock();
			}
		}
		return false;
	}

	public static void main(String[] a) {
		final FixedDeadlockWithLockMicroBlogNode local = new FixedDeadlockWithLockMicroBlogNode("localhost:8888");
		final FixedDeadlockWithLockMicroBlogNode other = new FixedDeadlockWithLockMicroBlogNode("localhost:8988");
		final Update first = getUpdate("1");
		final Update second = getUpdate("2");

		new Thread(new Runnable() {
			public void run() {
				local.propagateUpdate(first, other);
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				other.propagateUpdate(second, local);
			}
		}).start();
	}

}