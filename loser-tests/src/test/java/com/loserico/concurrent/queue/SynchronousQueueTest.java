package com.loserico.concurrent.queue;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.twitter.jsr166e.ThreadLocalRandom;

import lombok.extern.slf4j.Slf4j;

/**
 * https://www.baeldung.com/java-synchronous-queue
 * 
 * <p>
 * Copyright: Copyright (c) 2019-05-27 11:30
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Slf4j
public class SynchronousQueueTest {

	/**
	 * To see why the SynchronousQueue can be so useful, we will implement a logic using a shared
	 * variable between two threads and next, we will rewrite that logic using SynchronousQueue
	 * making our code a lot simpler and more readable.
	 * 
	 * Let’s say that we have two threads – a producer and a consumer – and when the producer is
	 * setting a value of a shared variable, we want to signal that fact to the consumer thread.
	 * Next, the consumer thread will fetch a value from a shared variable.
	 * 
	 * We will use the CountDownLatch to coordinate those two threads, to prevent a situation when
	 * the consumer accesses a value of a shared variable that was not set yet.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void testImplementHandoffsUsingSharedVariables() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(2);

		/*
		 * We will define a sharedState variable and a CountDownLatch that will be used for coordinating
		 * processing:
		 */
		AtomicInteger sharedState = new AtomicInteger();
		CountDownLatch countDownLatch = new CountDownLatch(1);

		/*
		 * The producer will save a random integer to the sharedState variable, 
		 * and execute the countDown() method on the countDownLatch, signaling to 
		 * the consumer that it can fetch a value from the sharedState:
		 */
		Runnable producer = () -> {
			Integer producedElement = ThreadLocalRandom
					.current()
					.nextInt();
			sharedState.set(producedElement);
			log.info("Saving an element: {} to the exchange point", producedElement);
			countDownLatch.countDown();
		};

		/*
		 * The consumer will wait on the countDownLatch using the await() method. 
		 * When the producer signals that the variable was set, 
		 * the consumer will fetch it from the sharedState:
		 */
		Runnable consumer = () -> {
			try {
				countDownLatch.await();
				Integer consumedElement = sharedState.get();
				log.info("consumed an element: {} from the exchange point", consumedElement);
			} catch (InterruptedException e) {
				log.error("", e);
			}
		};

		executor.execute(producer);
		executor.execute(consumer);

		executor.shutdown();
		executor.awaitTermination(500, MILLISECONDS);
		assertEquals(countDownLatch.getCount(), 0);
	}

	/**
	 * Let’s now implement the same functionality as in the previous section, but with a
	 * SynchronousQueue. It has a double effect because we can use it for exchanging state between
	 * threads and for coordinating that action so that we don’t need to use anything besides
	 * SynchronousQueue.
	 * @throws InterruptedException 
	 */
	@Test
	public void testImplementHandoffsUsingSynchronousQueue() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		//Firstly, we will define a queue:
		SynchronousQueue<Integer> queue = new SynchronousQueue<>();
		
		//The producer will call a put() method that will block until some other thread takes an element from the queue:
		Runnable producer = () -> {
			Integer producedElement = ThreadLocalRandom
					.current()
					.nextInt();
			try {
				queue.put(producedElement);
				log.info("Saving an element: {} to the exchange point", producedElement);
			} catch (InterruptedException e) {
				log.error("", e);
			}
		};
		
		Runnable consumer = () -> {
			Integer consumedElement;
			try {
				consumedElement = queue.take();
				log.info("consumed an element: {} from the exchange point", consumedElement);
			} catch (InterruptedException e) {
				log.error("", e);
			}
		};
		
		executor.execute(producer);
		executor.execute(consumer);
		executor.shutdown();
		executor.awaitTermination(1, SECONDS);
	}
}
