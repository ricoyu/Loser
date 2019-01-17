package com.loserico.concurrent.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

public class Asd {
	@Test
	public void testParallelInsertionAndConsumption() throws InterruptedException, ExecutionException {
		final SimpleBlockingQueue<Integer> queue = new SimpleBlockingQueue<Integer>();
		ExecutorService threadPool = Executors.newFixedThreadPool(3);
		final CountDownLatch latch = new CountDownLatch(3);
		List<Future<Integer>> futuresPut = new ArrayList<Future<Integer>>();
		for (int i = 0; i < 3; i++) {
			Future<Integer> submit = threadPool.submit(new Callable<Integer>() {
				public Integer call() {
					int sum = 0;
					for (int i = 0; i < 1000; i++) {
						int nextInt = ThreadLocalRandom.current().nextInt(100);
						queue.put(nextInt);
						sum += nextInt;
					}
					latch.countDown();
					return sum;
				}
			});
			futuresPut.add(submit);
		}
		List<Future<Integer>> futuresGet = new ArrayList<Future<Integer>>();
		for (int i = 0; i < 3; i++) {
			Future<Integer> submit = threadPool.submit(new Callable<Integer>() {
				public Integer call() {
					int count = 0;
					try {
						for (int i = 0; i < 1000; i++) {
							Integer got = queue.get();
							count += got;
						}
					} catch (InterruptedException e) {
					}
					latch.countDown();
					return count;
				}
			});
			futuresGet.add(submit);
		}
		latch.await();
		int sumPut = 0;
		for (Future<Integer> future : futuresPut) {
			sumPut += future.get();
		}
		int sumGet = 0;
		for (Future<Integer> future : futuresGet) {
			sumGet += future.get();
		}
		assertThat(sumPut, is(sumGet));
	}
}