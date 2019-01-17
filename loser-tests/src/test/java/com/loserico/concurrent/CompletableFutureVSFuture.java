package com.loserico.concurrent;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

/**
 * https://www.javacodegeeks.com/2016/06/back-completablefuture-java-8-feature-highlight.html
 * 
 * Java 8 was released on March 2014 and arrived with a long list of new features. One
 * of the less talked about, extremely useful yet misunderstood features is a brand
 * new and improved extension to the Future interface: CompletableFuture<T>. In the
 * following post we’ll present an overall view of CompletableFuture, exactly how is
 * it different from a simple Future and when it can be useful.
 * 
 * So What’s New in CompletableFuture?
 * 
 * CompletableFuture<T> extends Future<T> and makes it… completable. This is a big
 * deal, considering that Future objects were limited before Java 8, with only 5
 * methods available.
 * 
 * CompletableFuture<T> extends Future<T> by providing functional, monadic (!) 
 * operations and promoting asynchronous, event-driven programming model, 
 * as opposed to blocking in older Java. 
 * 
 * This new and improved CompletableFuture has 2 main benefits:
 * 可以显式调用complete()方法以结束任务
 * 1. It can be explicitly completed by calling the complete() method without any synchronous wait. 
 * It allows values of any type to be available in the future with default return values, even if the 
 * computation didn’t complete, using default / intermediate results. 
 * 
 * 2. With tens of new methods, it also allows you to build a pipeline data process in a series of actions. 
 * You can find a number of patterns for CompletableFutures such as creating a CompletableFuture from a task, 
 * or building a CompletableFuture chain.
 * 
 * <p> Copyright: Copyright (c) 2018-01-20 21:08 <p> Company: DataSense <p>
 * 
 * @author Rico Yu ricoyu520@gmail.com
 * 
 * @version 1.0
 * @on
 */
public class CompletableFutureVSFuture {

	/*
	 * Java 7 introduced us to Future, which represents the result of an asynchronous
	 * computation. The main advantage of using the Future object is that you can do
	 * other things while waiting for external resources. A non-blocking way to wait
	 * for a resource.
	 * 
	 * Using a Future means you can write a method and instead of it immediately
	 * returning the result, it will return a Future object. When you’ll need the
	 * actual result, just use Future.get() which will return the value after the
	 * computation is done.
	 * 
	 * You also get methods for checking if the computation is done, and a way to
	 * cancel / check if it was canceled.
	 * 
	 * For example, let’s say you’re making a call to some external resource, like…
	 * Marvel’s developer API, pulling out all superheros that have the letter… “C” in
	 * their name:
	 */
	@Test
	public void testFuture() {
		ExecutorService executorService = Executors.newCachedThreadPool();
		Future<String> marvel = executorService.submit(new Callable<String>() {
			public String call() {
				System.out.println(Thread.currentThread().getName());
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
				return "Hello";
			}
		});

		// other very important stuff of course, non-blocking ftw

		try {
			System.out.println(marvel.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} // this bit is blocking if the result isn’t ready yet

		/*
		 * Yup, if we do want go for a fully async non-blocking option we’re out of
		 * luck. We have no assurance that the Future is actually there and we might
		 * have to wait. This is where CompletableFuture comes in, and helps with a
		 * cheeky workaround.
		 */
	}
	
	@Test
	public void testCompletableFuture() {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			try {
				SECONDS.sleep(3);
//				SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "Hello";
		});
		try {
			SECONDS.sleep(1);
//			SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(future.isDone());
		future.complete("World"); //如果运行到这里的时候任务还没完成则返回默认值World
		System.out.println(future.join());
	}
}
