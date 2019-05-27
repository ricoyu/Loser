package com.loserico.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

public class ThreadPoolTest {

	@Test
	public void testSimgleThreadPool() {
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> System.out.println("Hello World"));
	}

	@Test
	public void testFixedThreadPool() throws InterruptedException, ExecutionException {
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
		Future<String> future = executorService.submit(() -> "hello world");
		System.out.println(future.get());
	}
}
