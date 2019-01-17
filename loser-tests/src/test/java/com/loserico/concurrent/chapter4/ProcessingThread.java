package com.loserico.concurrent.chapter4;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * In the following listing, a group of processing threads within a single
 * process want to know that at least half of them have been properly
 * initialized (assume that initialization of a processing thread takes a
 * certain amount of time) before the system as a whole starts sending updates
 * to any of them.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class ProcessingThread extends Thread {
	private static int MAX_THREADS = 4;
	private final String ident;
	private final CountDownLatch latch;

	public ProcessingThread(String ident, CountDownLatch countDownLatch) {
		this.ident = ident;
		this.latch = countDownLatch;
	}

	public String getIdentifier() {
		return ident;
	}

	public void initialize() {
		latch.countDown();
		System.out.println(getIdentifier() +" count down!");
	}

	public void run() {
		initialize();
	}

	public static void main(String[] a) {
		final int quorum = 1 + (int) (MAX_THREADS / 2);
		final CountDownLatch countDownLatch = new CountDownLatch(quorum);

		final Set<ProcessingThread> nodes = new HashSet<>();
		try {
			for (int i = 0; i < MAX_THREADS; i++) {
				ProcessingThread local = new ProcessingThread("localhost:" + (9000 + i), countDownLatch);
				nodes.add(local);
				local.start();
			}
			System.out.println("main thread await!");
			countDownLatch.await();
			System.out.println("main thread going!");
		} catch (InterruptedException e) {

		} finally {
		}
	}
}