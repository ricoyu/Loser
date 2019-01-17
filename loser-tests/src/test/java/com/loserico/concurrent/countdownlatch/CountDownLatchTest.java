package com.loserico.concurrent.countdownlatch;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CountDownLatchTest {

	@Test
	public void testWhenParallelProcessing_thenMainThreadWillBlockUntilCompletion() throws InterruptedException {
		List<String> outputScaner = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch countDownLatch = new CountDownLatch(5);
		List<Thread> workers = Stream
				.generate(() -> new Thread(new Worker(outputScaner, countDownLatch)))
				.limit(5)
				.collect(toList());

		workers.forEach(Thread::start);
		countDownLatch.await();
		outputScaner.add("Latch released");
		Assertions.assertThat(outputScaner)
				.containsExactly(
						"Counted down",
						"Counted down",
						"Counted down",
						"Counted down",
						"Counted down",
						"Latch released");
	}

	/**
	 * Now, let’s modify our test so it blocks until all the Workers have started,
	 * unblocks the Workers, and then blocks until the Workers have finished:
	 * 
	 * This pattern is really useful for trying to reproduce concurrency bugs, as can
	 * be used to force thousands of threads to try and perform some logic in
	 * parallel.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testWhenDoingLotsOfThreadsInParallel_thenStartThemAtTheSameTime() throws InterruptedException {
		List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch readyThreadCounter = new CountDownLatch(5);
		CountDownLatch callingThreadBlocker = new CountDownLatch(1);
		CountDownLatch completedThreadCounter = new CountDownLatch(5);

		List<Thread> workers = Stream
				.generate(() -> new Thread(new WaitingWorker(
						outputScraper, readyThreadCounter, callingThreadBlocker, completedThreadCounter)))
				.limit(5)
				.collect(toList());

		workers.forEach(Thread::start);
		readyThreadCounter.await();
		outputScraper.add("Workers ready");
		callingThreadBlocker.countDown();
		completedThreadCounter.await();
		outputScraper.add("Workers complete");

		Assertions.assertThat(outputScraper)
				.containsExactly(
						"Workers ready",
						"Counted down",
						"Counted down",
						"Counted down",
						"Counted down",
						"Counted down",
						"Workers complete");
	}

	@Test
	public void testWhenFailingToParallelProcess_thenMainThreadShouldGetNotGetStuck() throws InterruptedException {
		List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch countDownLatch = new CountDownLatch(5);
		List<Thread> workers = Stream
				.generate(() -> new Thread(new BrokenWorker(outputScraper, countDownLatch)))
				.limit(5)
				.collect(toList());

		workers.forEach(Thread::start);
		countDownLatch.await(3L, TimeUnit.SECONDS);
		System.out.println("done");
	}
}
