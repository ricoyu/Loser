package com.loserico.concurrent.chapter4;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockWithLockMicroBlogNode implements SimpleMicroBlogNode {

	private final String ident;

	private final Lock lock = new ReentrantLock();

	public DeadlockWithLockMicroBlogNode(String ident) {
		this.ident = ident;
	}

	public String getIdent() {
		return ident;
	}

	@Override
	public void propagateUpdate(Update update, SimpleMicroBlogNode backup) {
		// Each thread locks own lock first
		lock.lock();
		try {
			System.out.println(ident + ": recvd: " + update.getUpdateText() + " ; backup: " + backup.getIdent());
			/**
			 * 拿到第一个锁后稍等一秒，这样几乎每次都死锁的， 因为第二个线程有充分的时间去获得锁
			 */
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			/*
			 * Calls confirmUpdate() to acknowledge in other thread The attempt
			 * B to lock the other thread will generally fail, because it’s
			 * already locked (as per figure 4.3). That’s how the deadlock
			 * arises.
			 */
			backup.confirmUpdate(this, update);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void confirmUpdate(SimpleMicroBlogNode other, Update update) {
		// Attempts to lock other thread
		lock.lock();
		try {
			System.out.println(ident + ": recvd confirm: " + update.getUpdateText() + " from " + other.getIdent());
		} finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		final DeadlockWithLockMicroBlogNode local = new DeadlockWithLockMicroBlogNode("localhost:8888");
		final DeadlockWithLockMicroBlogNode other = new DeadlockWithLockMicroBlogNode("localhost:8988");
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

	private static Update getUpdate(String s) {
		Update.Builder b = new Update.Builder();
		b.updateText(s).author(new Author("Ben"));
		return b.build();
	}
}