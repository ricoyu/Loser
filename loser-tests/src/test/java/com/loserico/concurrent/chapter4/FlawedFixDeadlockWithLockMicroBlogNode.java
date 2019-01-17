package com.loserico.concurrent.chapter4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 尝试修改死锁问题，但该方案是有瑕疵的 In this example, we’ve replaced the unconditional lock
 * with tryLock() with a timeout. This is an attempt to remove the deadlock by
 * giving other threads a chance to get at the lock.
 * 
 * If you run the code, you’ll see that it seems to resolve the deadlock, but
 * only sometimes. You’ll see the “received confirm of update” text, but only
 * some of the time.
 * 
 * In fact, the deadlock hasn’t really been resolved, because if the initial
 * lock is obtained (in propagateUpdate()) the thread calls confirmUpdate() and
 * never releases the first lock until completion. If both threads manage to
 * acquire their first lock before either can call confirmUpdate(), the threads
 * will still be deadlocked.
 * 
 * Why does the flawed attempt seem to work sometimes? You’ve seen that the
 * deadlock still exists, so what is it that causes the code in the flawed
 * solution to sometimes succeed? The extra complexity in the code is the
 * culprit. It affects the JVM’s thread scheduler and makes it less easy to
 * predict. This means that it will sometimes schedule the threads so that one
 * of them (usually the first thread) is able to get into confirmUpdate() and
 * acquire the second lock before the second thread can run. This is also
 * possible in the original code, but much less likely.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class FlawedFixDeadlockWithLockMicroBlogNode implements SimpleMicroBlogNode {

	private static Update getUpdate(String s) {
		Update.Builder b = new Update.Builder();
		b.updateText(s).author(new Author("Ben"));
		return b.build();
	}

	private final String ident;

	private final Lock lock = new ReentrantLock();

	public FlawedFixDeadlockWithLockMicroBlogNode(String ident) {
		this.ident = ident;
	}

	public String getIdent() {
		return ident;
	}

	@Override
	public void propagateUpdate(Update update, SimpleMicroBlogNode backup) {
		boolean acquired = false;

		while (!acquired) {
			try {
				int wait = (int) (Math.random() * 10);
				// Try and lock, with random timeout
				acquired = lock.tryLock(wait, TimeUnit.MILLISECONDS);
				if (acquired) {
					System.out.println(ident + ": recvd: " + update.getUpdateText() + " ; backup: " + backup.getIdent());
					// Confirm on other thread
					backup.confirmUpdate(this, update);
				} else {
					Thread.sleep(wait);
				}
			} catch (InterruptedException e) {
			} finally {
				// Only unlock if locked
				if (acquired) {
					lock.unlock();
				}
			}
		}
	}

	@Override
	public void confirmUpdate(SimpleMicroBlogNode other, Update update) {
		boolean acquired = false;

		while (!acquired) {
			try {
				int wait = (int) (Math.random() * 10);
				acquired = lock.tryLock(wait, TimeUnit.MILLISECONDS);
				if (acquired) {
					System.out.println(ident + ": recvd confirm: " + update.getUpdateText() + " from " + other.getIdent());
				} else {
					Thread.sleep(wait);
				}
			} catch (InterruptedException e) {
			} finally {
				if (acquired)
					lock.unlock();
			}
		}
	}

	public static void main(String[] args) {
		final FlawedFixDeadlockWithLockMicroBlogNode local = new FlawedFixDeadlockWithLockMicroBlogNode("localhost:8888");
		final FlawedFixDeadlockWithLockMicroBlogNode other = new FlawedFixDeadlockWithLockMicroBlogNode("localhost:8988");
		final Update first = getUpdate("1");
		final Update second = getUpdate("2");

		new Thread(new Runnable() {
			public void run() {
				local.propagateUpdate(first, other);
			}
		}).start();

		// 第二个线程稍等一分钟再启动的话基本上就不会死锁了，因为第一个线程有足够的时候获取第一第二个锁
		/*
		 * try { Thread.sleep(1000); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 */
		new Thread(new Runnable() {
			public void run() {
				other.propagateUpdate(second, local);
			}
		}).start();
	}

}