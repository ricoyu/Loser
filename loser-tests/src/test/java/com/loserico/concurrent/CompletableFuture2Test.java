package com.loserico.concurrent;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * http://www.importnew.com/10815.html
 * 
 * Asynchronous Computation in Java
 * Java中的异步计算
 * 
 * Asynchronous computation is difficult to reason about. Usually we want to think of
 * any computation as a series of steps. But in case of asynchronous computation,
 * actions represented as callbacks tend to be either scattered across the code or
 * deeply nested inside each other. Things get even worse when we need to handle
 * errors that might occur during one of the steps.
 * 
 * <p>
 * Copyright: Copyright (c) 2018-01-21 14:24
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class CompletableFuture2Test {

	/**
	 * 提取、修改包装的值
	 * 
	 * 通常futures代表其它线程中运行的代码，但事实并非总是如此。有时你想要创造一个Future来表示你知道将会发生什么，例如JMS message
	 * arrival。所以你有Future但是未来并没有潜在的异步工作。你只是想在未来JMS消息到达时简单地完成（解决），这是由一个事件驱动的。在这种情况下，你可以简单地创建CompletableFuture来返还给你的客户端，只要你认为你的结果是可用的，仅仅通过complete()就能解锁所有等待Future的客户端。
	 */
	@Test
	public void testComplete() {
		CompletableFuture<String> completableFuture = ask();
		/*
		 * 此时此刻所有客户端Future.get()将得到字符串的结果，同时完成回调以后将会立即生效。当你想代表Future的任务时是非常方便的，
		 * 而且没有必要去计算一些执行线程的任务上。CompletableFuture.complete()只能调用一次，后续调用将被忽略。
		 * 但也有一个后门叫做CompletableFuture.obtrudeValue(…)覆盖一个新Future之前的价值，请小心使用。
		 * 
		 * 有时你想要看到信号发生故障的情况，如你所知Future对象可以处理它所包含的结果或异常。如果你想进一步传递一些异常，
		 * 可以用CompletableFuture.completeExceptionally(ex)
		 * (或者用obtrudeException(ex)这样更强大的方法覆盖前面的异常)。
		 */
		completableFuture.complete("Hello world");
	}

	/**
	 * 首先你可以简单地创建新的CompletableFuture并且给你的客户端
	 * 
	 * 注意这个future和Callable没有任何联系，没有线程池也不是异步工作。如果现在客户端代码调用ask().get()它将永远阻塞。如果寄存器完成回调，它们就永远不会生效了。
	 * 
	 * @return
	 */
	public CompletableFuture<String> ask() {
		final CompletableFuture<String> future = new CompletableFuture<>();
		return future;
	}

	/**
	 * CompletableFuture.getNow(valueIfAbsent)
	 * 方法没有阻塞但是如果Future还没完成将返回默认值，这使得当构建那种我们不想等太久的健壮系统时非常有用。
	 * @on
	 */
	@Test
	public void testGetNow() {
		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
			try {
				SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "Hello";
		});
		System.out.println(completableFuture.getNow("world"));
	}

	/**
	 * The best part of the CompletableFuture API is the ability to combine
	 * CompletableFuture instances in a chain of computation steps.
	 * 
	 * The result of this chaining is itself a CompletableFuture that allows further
	 * chaining and combining. This approach is ubiquitous in functional languages and
	 * is often referred to as a monadic design pattern.
	 * 
	 * In the following example we use the thenCompose method to chain two Futures
	 * sequentially.
	 * 
	 * Notice that this method takes a function that returns a CompletableFuture
	 * instance. The argument of this function is the result of the previous
	 * computation step. This allows us to use this value inside the next
	 * CompletableFuture‘s lambda
	 * 
	 * @on
	 */
	@Test
	public void testThenCompose() {
		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
				.thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " world"));
		System.out.println(completableFuture.join());
	}

	/**
	 * The thenCompose method together with thenApply implement basic building blocks
	 * of the monadic pattern. They closely relate to the map and flatMap methods of
	 * Stream and Optional classes also available in Java 8.
	 * 
	 * Both methods receive a function and apply it to the computation result, but the
	 * thenCompose (flatMap) method receives a function that returns another object of
	 * the same type. This functional structure allows composing the instances of
	 * these classes as building blocks.
	 * 
	 * If you want to execute two independent Futures and do something with their
	 * results, use the thenCombine method that accepts a Future and a Function with
	 * two arguments to process both results
	 */
	@Test
	public void testThenCombine() {
		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello")
				.thenCombine(CompletableFuture.supplyAsync(() -> "world"), (s1, s2) -> s1 + s2);
		try {
			System.out.println(completableFuture.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 这个版本就是前两个CompletableFuture的结果直接消费掉，无需返回结果的情况 A simpler case is when you want to
	 * do something with two Futures‘ results, but don’t need to pass any resulting
	 * value down a Future chain. The thenAcceptBoth method is there to help
	 * 
	 * 等待所有的 CompletableFutures 完成
	 * 如果不是产生新的CompletableFuture连接这两个结果，我们只是希望当完成时得到通知，我们可以使用thenAcceptBoth()/runAfterBoth()系列的方法
	 */
	@Test
	public void testThenAcceptBoth() {
		CompletableFuture future = CompletableFuture.supplyAsync(() -> "Hello")
				.thenAcceptBoth(CompletableFuture.supplyAsync(() -> " World"),
						(s1, s2) -> System.out.println(s1 + s2));
	}

	@Test
	public void testRunAfterBoth() {
		CompletableFuture future = CompletableFuture.supplyAsync(() -> {
			try {
				SECONDS.sleep(1);
				System.out.println("done1");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "Hello";
		})
				.runAfterBoth(CompletableFuture.supplyAsync(() -> {
					try {
						SECONDS.sleep(2);
						System.out.println("done2");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return " World";
				}),
						() -> {
							System.out.println("done");
						});
	}

	/**
	 * Running Multiple Futures in Parallel
	 * 
	 * When we need to execute multiple Futures in parallel, we usually want to wait
	 * for all of them to execute and then process their combined results.
	 * 
	 * The CompletableFuture.allOf static method allows to wait for completion of all
	 * of the Futures provided as a var-arg:
	 */
	@Test
	public void testAllOf() {
		CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
		CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "Beautiful");
		CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> "World");

		CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2, future3);
		combinedFuture.join();
		System.out.println(future1.isDone());
		System.out.println(future2.isDone());
		System.out.println(future3.isDone());

		/*
		 * Notice that the return type of the CompletableFuture.allOf() is a
		 * CompletableFuture<Void>. The limitation of this method is that it does not
		 * return the combined results of all Futures. Instead you have to manually
		 * get results from Futures. Fortunately, CompletableFuture.join() method and
		 * Java 8 Streams API makes it simple:
		 */
		String combined = Stream.of(future1, future2, future3)
				.map(CompletableFuture::join)
				.collect(Collectors.joining(" "));
		System.out.println(combined);
	}

	/**
	 * Async Methods
	 * 
	 * Most methods of the fluent API in CompletableFuture class have two additional
	 * variants with the Async postfix. These methods are usually intended for running
	 * a corresponding step of execution in another thread.
	 * 
	 * The methods without the Async postfix run the next execution stage using a
	 * calling thread. The Async method without the Executor argument runs a step
	 * using the common fork/join pool implementation of Executor that is accessed
	 * with the ForkJoinPool.commonPool() method. The Async method with an Executor
	 * argument runs a step using the passed Executor.
	 * 
	 * Here’s a modified example that processes the result of a computation with a
	 * Function instance. The only visible difference is the thenApplyAsync method.
	 * But under the hood the application of a function is wrapped into a ForkJoinTask
	 * instance (for more information on the fork/join framework, see the article
	 * “Guide to the Fork/Join Framework in Java”). This allows to parallelize your
	 * computation even more and use system resources more efficiently.
	 * 
	 * @on
	 */
	@Test
	public void testThenApplyAsync() {
		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
			System.out.println(Thread.currentThread().getName());
			try {
				SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Hello");
			return "Hello";
		});
		CompletableFuture<String> future = completableFuture.thenApplyAsync((s) -> {
			System.out.println(Thread.currentThread().getName());
			try {
				SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("world");
			return s + " world";
		});
		System.out.println(future.join());
	}

	@Test
	public void testThenApplyAsyncWithExecutor() {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
			System.out.println(Thread.currentThread().getName());
			try {
				SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Hello");
			return "Hello";
		}, executorService);
		CompletableFuture<String> future = completableFuture.thenApplyAsync((s) -> {
			System.out.println(Thread.currentThread().getName());
			try {
				SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("world");
			return s + " world";
		}, executorService);
		System.out.println(future.join());
		/*
		 * pool-1-thread-1 Hello pool-1-thread-2 world Hello world
		 * 
		 * 说明还是挨个顺序跑的，只不过用不同的线程去跑而已
		 */
	}

	@Test
	public void testExceptionHandle() {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			throw new RuntimeException();
			//			return "Hello";
		});

		/*
		 * exceptionally()会返回原始future的返回值如果没有抛异常的话
		 * 如果原始future抛异常则执行exceptionally指定的function
		 */
		CompletableFuture<String> safe = future.exceptionally(ex -> "We have a problem: " + ex.getMessage());
		System.out.println(safe.join());

		//一个更加灵活的方法是handle()接受一个函数，它接收正确的结果或异常
		CompletableFuture<Integer> moreSafe = future.handle((ok, e) -> {
			if (ok != null) {
				System.out.println(ok);
				return 0;
			} else {
				e.printStackTrace();
				return -1;
			}
		});
		System.out.println(moreSafe.join());
	}

}
