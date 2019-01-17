package com.loserico.concurrent;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * https://www.javacodegeeks.com/2016/06/java-8-completablefuture-vs-parallel-stream.html
 * This post shows how Java 8’s CompletableFuture compares with parallel streams when peforming asynchronous computations.
 * 结论：CompletableFutures 可以提供更加精细的线程池大小控制，所以在重 I/O 操作场景建议使用
 *     如果你的应用 CPU 已经吃紧了(CPU-intensive)，而没大量I/O操作，那么考虑 parallel stream
 * <p>
 * Copyright: Copyright (c) 2018-01-20 20:22
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class CompletableFutureVSParallelStreamTest {

	private static List<MyTask> tasks = null;

	/**
	 * Let’s create ten tasks, each with a duration of 1 second:
	 * @return
	 */
	@BeforeClass
	public static void init() {
		tasks = IntStream.range(0, 10)
				.mapToObj((duration) -> new MyTask(1))
				.collect(toList());
	}

	/**
	 * Approach 1: Sequentially
	 * 第一种顺序运行
	 * Your first thought might be to calculate the tasks sequentially, as follows:
	 */
	@Test
	public void testSenquentially() {
		long start = System.nanoTime();
		List<Integer> results = tasks.stream()
				.map(MyTask::calculate)
				.collect(toList());
		long duration = (System.nanoTime() - start) / 1_000_000;
		System.out.printf("Processed %d tasks in %d millis\n", tasks.size(), duration);
		System.out.println(results);
		/*
		 * 顺序运行总共花费10秒
		 * main
		 * main
		 * main
		 * main
		 * main
		 * main
		 * main
		 * main
		 * main
		 * main
		 * Processed 10 tasks in 10000 millis
		 * [1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
		 * @on
		 */
	}

	/**
	 * Using a parallel stream
	 * 比挨个跑性能要好
	 * A quick improvement is to convert your code to use a parallel stream, as shown below:
	 */
	@Test
	public void testParallelStream() {
		long start = System.nanoTime();
		List<Integer> results = tasks.parallelStream()
				.map(MyTask::calculate)
				.collect(toList());
		long duration = (System.nanoTime() - start) / 1_000_000;
		System.out.printf("Processed %d tasks in %d millis\n", tasks.size(), duration);
		System.out.println(results);
		System.out.println("可用的处理器数量： " + Runtime.getRuntime().availableProcessors());
		/*
		 * 这种方略快，既有主线程也有worker线程在跑，为什么只有worker1-3呢？跟CPU内核数有关？
		 * 查看ForkJoinPool的构造函数就知道了，默认的线程数就是Runtime.getRuntime().availableProcessors()
		 * 我这台机器是4，所以main+3个worker线程
		 * 
		 * main
		 * ForkJoinPool.commonPool-worker-3
		 * ForkJoinPool.commonPool-worker-1
		 * ForkJoinPool.commonPool-worker-2
		 * ForkJoinPool.commonPool-worker-3
		 * main
		 * ForkJoinPool.commonPool-worker-1
		 * ForkJoinPool.commonPool-worker-2
		 * ForkJoinPool.commonPool-worker-3
		 * main
		 * Processed 10 tasks in 3004 millis
		 * [1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
		 * @on
		 */
	}

	/**
	 * In the code above, we first obtain a list of CompletableFutures and then invoke
	 * the join method on each future to wait for them to complete one by one. Note
	 * that join is the same as get, with the only difference being that the former
	 * doesn’t throw any checked exception, so it’s more convenient in a lambda
	 * expression.
	 * 
	 * 注意这里必须要用两次stream操作 Also, you must use two separate stream pipelines, as opposed
	 * to putting the two map operations after each other, because intermediate stream
	 * operations are lazy and you would have ended up processing your tasks
	 * sequentially! That’s why you first need to collect your CompletableFutures in a
	 * list to allow them to start before waiting for their completion.
	 * 
	 * 还要注意的是与parallelStream相比，CompletableFuture只有ForkJoinPool线程的参与，主线程并没有参与
	 * It took 4 seconds to process 10 tasks. You will notice that only 3 ForkJoinPool
	 * threads were used and that, unlike the parallel stream, the main thread was not
	 * used.
	 */
	@Test
	public void testUsingCompletableFutures() {
		long start = System.nanoTime();
		List<CompletableFuture<Integer>> futures = tasks.stream()
				.map(t -> CompletableFuture.supplyAsync(() -> t.calculate()))
				.collect(toList());

		List<Integer> results = futures.stream()
				.map(CompletableFuture::join)
				.collect(toList());

		long duration = (System.nanoTime() - start) / 1_000_000;
		System.out.printf("Processed %d tasks in %d millis\n", tasks.size(), duration);
		System.out.println(results);

		/*
		 * ForkJoinPool.commonPool-worker-2
		 * ForkJoinPool.commonPool-worker-1
		 * ForkJoinPool.commonPool-worker-3
		 * ForkJoinPool.commonPool-worker-2
		 * ForkJoinPool.commonPool-worker-1
		 * ForkJoinPool.commonPool-worker-3
		 * ForkJoinPool.commonPool-worker-2
		 * ForkJoinPool.commonPool-worker-1
		 * ForkJoinPool.commonPool-worker-3
		 * ForkJoinPool.commonPool-worker-2
		 * Processed 10 tasks in 4005 millis
		 * [1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
		 * @on
		 */
	}

	/**
	 * CompletableFutures 与 parallel streams相较的优势在于其可以自定义 Executor 来执行其任务。甚至可以指定超过处理器个数的线程数
	 * One of the advantages of CompletableFutures over parallel streams is that they
	 * allow you to specify a different Executor to submit their tasks to. This means
	 * that you can choose a more suitable number of threads based on your
	 * application. Since my example is not very CPU-intensive, I can choose to
	 * increase the number of threads to be greater than
	 * Runtime.getRuntime().getAvailableProcessors()
	 */
	@Test
	public void testCompleteableFutureWithExecutor() {
		long start = System.nanoTime();
		ExecutorService executorService = Executors.newFixedThreadPool(Math.min(tasks.size(), 10));
		List<CompletableFuture<Integer>> futures = tasks.stream()
				.map(t -> CompletableFuture.supplyAsync(() -> t.calculate(), executorService))
				.collect(toList());

		List<Integer> result = futures.stream()
				.map(CompletableFuture::join)
				.collect(toList());
		long duration = (System.nanoTime() - start) / 1_000_000;
		System.out.printf("Processed %d tasks in %d millis\n", tasks.size(), duration);
		System.out.println(result);
		executorService.shutdown();
		/*
		 * 可以看到这种方式几乎在一秒内就完成了 After this improvement, it now takes only 1 second to
		 * process 10 tasks.
		 * 
		 * 结论：CompletableFutures 可以提供更加精细的线程池大小控制，所以在重 I/O 操作场景建议使用
		 *    如果你的应用 CPU 已经吃紧了(CPU-intensive)，而没大量I/O操作，那么考虑 parallel stream
		 * As you can see, CompletableFutures provide more control over the size of
		 * the thread pool and should be used if your tasks involve I/O. However, if
		 * you’re doing CPU-intensive operations, there’s no point in having more
		 * threads than processors, so go for a parallel stream, as it is easier to
		 * use.
		 * 
		 * pool-1-thread-2
		 * pool-1-thread-3
		 * pool-1-thread-1
		 * pool-1-thread-5
		 * pool-1-thread-6
		 * pool-1-thread-4
		 * pool-1-thread-8
		 * pool-1-thread-7
		 * pool-1-thread-10
		 * pool-1-thread-9
		 * Processed 10 tasks in 1007 millis
		 * [1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
		 * @on
		 */
	}

	static class MyTask {
		private final int duration;

		public MyTask(int duration) {
			this.duration = duration;
		}

		public int calculate() {
			System.out.println(Thread.currentThread().getName());
			try {
				Thread.sleep(duration * 1000);
			} catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}
			return duration;
		}
	}
}
