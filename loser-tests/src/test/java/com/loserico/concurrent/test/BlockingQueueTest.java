package com.loserico.concurrent.test;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.*;
import static java.util.concurrent.TimeUnit.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockingQueueTest {
	private static final Logger log = LoggerFactory.getLogger(BlockingQueueTest.class);

	/**
	 * The flag wasInterrupted indicates whether the blocking thread was
	 * interrupted, the flag reachedAfterGet shows that the line after the get has
	 * been executed and finally the throwableThrown would tell us that any kind of
	 * Throwable was thrown. With the getter methods for these flags we can now
	 * write a unit test, that first creates an empty queue, starts our
	 * BlockingThread, waits for some time and then inserts a new element into the
	 * queue.
	 * 
	 * @throws InterruptedException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testPutOnEmptyQueueBlocks() throws InterruptedException {
		final SimpleBlockingQueue queue = new SimpleBlockingQueue();
		BlockingThread blockingThread = new BlockingThread(queue);
		blockingThread.start();
		SECONDS.sleep(5);
		assertThat(blockingThread.isReachedAfterGet(), is(false));
		log.info("blockingThread.isReachedAfterGet() [{}]", blockingThread.isReachedAfterGet());
		assertThat(blockingThread.isWasInterrupted(), is(false));
		log.info("blockingThread.isWasInterrupted() [{}]", blockingThread.isWasInterrupted());
		assertThat(blockingThread.isThrowableThrown(), is(false));
		log.info("blockingThread.isThrowableThrown() [{}]", blockingThread.isThrowableThrown());
		queue.put(new Object());
		log.info("queue.put(new Object())");
		SECONDS.sleep(1);
		assertThat(blockingThread.isReachedAfterGet(), is(true));
		log.info("blockingThread.isReachedAfterGet() [{}]", blockingThread.isReachedAfterGet());
		assertThat(blockingThread.isWasInterrupted(), is(false));
		log.info("blockingThread.isWasInterrupted() [{}]", blockingThread.isWasInterrupted());
		assertThat(blockingThread.isThrowableThrown(), is(false));
		log.info("blockingThread.isThrowableThrown() [{}]", blockingThread.isThrowableThrown());
		blockingThread.join();
	}

	@Test
	public void testParallelInsertionAndConsumption() throws InterruptedException, ExecutionException {
		SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<Integer>();
		ExecutorService threadPool = Executors.newFixedThreadPool(6);
		CountDownLatch latch = new CountDownLatch(6);
		List<Future<Integer>> futuresPut = new ArrayList<Future<Integer>>();

		for (int i = 0; i < 3; i++) {
			Future<Integer> submit = threadPool.submit(() -> {
				int sum = 0;
				for (int j = 0; j < 10; j++) {
					int nextInt = ThreadLocalRandom.current().nextInt(100);
					queue.put(nextInt);
					log.info("queue.put({})", nextInt);
					sum += nextInt;
				}
				MILLISECONDS.sleep(120);
				latch.countDown();
				log.info("latch.countDown()");
				return sum;
			});
			futuresPut.add(submit);
		}

		List<Future<Integer>> futuresGet = new ArrayList<Future<Integer>>();
		for (int i = 0; i < 3; i++) {
			Future<Integer> submit = threadPool.submit(() -> {
				int count = 0;
				try {
					for (int j = 0; j < 10; j++) {
						log.info("try queue.get()");
						Integer got = queue.get();
						log.info("got[{}]", got);
						count += got;
					}
				} catch (InterruptedException e) {
				}
				MILLISECONDS.sleep(120);
				latch.countDown();
				log.info("latch.countDown()");
				return count;
			});
			futuresGet.add(submit);
		}
		log.info("====Before await! Current time[{}]=====", now().format(ofPattern("yyyy-MM-dd HH:mm:ss")));
		latch.await();
		log.info("====After await! Current time[{}]=====", now().format(ofPattern("yyyy-MM-dd HH:mm:ss")));
		int sumPut = 0;
		for (Future<Integer> future : futuresPut) {
			sumPut += future.get();
		}
		log.info("sumPut:[{}], Current time[{}]", sumPut, now().format(ofPattern("yyyy-MM-dd HH:mm:ss")));
		int sumGet = 0;
		for (Future<Integer> future : futuresGet) {
			sumGet += future.get();
		}
		log.info("sumGet:[{}], Current time[{}]", sumGet, now().format(ofPattern("yyyy-MM-dd HH:mm:ss")));
		assertThat(sumPut, is(sumGet));
	}

	/**
	 * Although this implementation is very simple, it is not that easy to test all
	 * the functionality, especially the blocking feature. When we just call get()
	 * on an empty queue, the current thread is blocked until another threads
	 * inserts a new item into the queue. This means that we need at least two
	 * different threads in our unit test. While the one thread blocks, the other
	 * thread waits for some specific time. If during this time the other thread
	 * does not execute further code, we can assume that the blocking feature is
	 * working. One way to check that the blocking thread is not executing any
	 * further code is the addition of some boolean flags that are set, when either
	 * an exception was thrown or the line after the get() call was executed:
	 * 
	 * @author Loser
	 * @since Aug 20, 2016
	 * @version
	 *
	 */
	private static class BlockingThread extends Thread {
		@SuppressWarnings("rawtypes")
		private SimpleBlockingQueue queue;
		private boolean wasInterrupted = false;
		private boolean reachedAfterGet = false;
		private boolean throwableThrown;

		@SuppressWarnings("rawtypes")
		public BlockingThread(SimpleBlockingQueue queue) {
			this.queue = queue;
		}

		public void run() {
			try {
				try {
					log.info("Before get(), current time:[{}]", now().format(ofPattern("yyyy-MM-dd HH:mm:ss")));
					queue.get();
					log.info("After get(), current time:[{}]", now().format(ofPattern("yyyy-MM-dd HH:mm:ss")));
				} catch (InterruptedException e) {
					wasInterrupted = true;
					log.info("Interrupted! wasInterrupted={}, current time:[{}]", wasInterrupted,
							now().format(ofPattern("yyyy-MM-dd HH:mm:ss")));
				}
				reachedAfterGet = true;
				log.info("reachedAfterGet={}", reachedAfterGet);
			} catch (Throwable t) {
				throwableThrown = true;
			}
		}

		public boolean isWasInterrupted() {
			return wasInterrupted;
		}

		public boolean isReachedAfterGet() {
			return reachedAfterGet;
		}

		public boolean isThrowableThrown() {
			return throwableThrown;
		}
	}
}