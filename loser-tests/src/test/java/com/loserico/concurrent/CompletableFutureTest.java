package com.loserico.concurrent;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CompletableFuture is still a relatively fresh concept, despite being introduced almost two
 * years ago (!) in March 2014 with Java 8.
 * 
 * But maybe it's good that this class is not so well known since it can be easily abused,
 * especially with regards to threads and thread pools that are involved along the way. This
 * article aims to describe how threads are used with CompletableFuture.
 * 
 * @author Loser
 * 
 * @since Aug 12, 2016
 * 
 * @version
 *
 */
public class CompletableFutureTest {
	private static final Logger logger = LoggerFactory.getLogger(CompletableFutureTest.class);

	ExecutorService pool = Executors.newFixedThreadPool(10);
	ExecutorService pool2 = Executors.newFixedThreadPool(10);

	/**
	 * This is the fundamental part of the API. There is a convenient supplyAsync() method that is
	 * similar to ExecutorService.submit(), but returning CompletableFuture
	 * 
	 * The problem is, supplyAsync() by default uses ForkJoinPool.commonPool(), thread pool shared
	 * between all CompletableFutures, all parallel streams and all applications deployed on the
	 * same JVM (if you are unfortunate to still use application server with many deployed
	 * artifacts). This hard-coded, unconfigurable thread pool is completely outside of our control,
	 * hard to monitor and scale. Therefore you should always specify your own Executor
	 */
	@Test
	public void testCompletableFuture() {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			try (InputStream is = new URL("http://www.infoq.com/cn").openStream()) {
				logger.info("Downloading");
				return IOUtils.toString(is, StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		try {
			System.out.println(future.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The problem is, supplyAsync() by default uses ForkJoinPool.commonPool(), thread pool shared
	 * between all CompletableFutures, all parallel streams and all applications deployed on the
	 * same JVM (if you are unfortunate to still use application server with many deployed
	 * artifacts). This hard-coded, unconfigurable thread pool is completely outside of our control,
	 * hard to monitor and scale.
	 * 
	 * Therefore you should always specify your own Executor, like here (and have a look at my few
	 * tips how to create one):
	 */
	@Test
	public void testCompletableFutureWithExecutorService() {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			try (InputStream is = new URL("http://www.infoq.com/cn").openStream()) {
				logger.info("Downloading");
				return IOUtils.toString(is, StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, pool);
		System.out.println("----------------");
		future.thenApply(s -> {
			System.out.println(s);
			return s;
		});
		/*
		 * try { System.out.println(future.get()); } catch (InterruptedException e) {
		 * e.printStackTrace(); } catch (ExecutionException e) { e.printStackTrace();
		 * }
		 */
	}

	/**************************************************
	 * **********Callbacks and transformations**************
	 ********************************************/

	/*
	 * Suppose you want to transform given CompletableFuture, e.g. extract the length
	 * of the String
	 * 
	 * As long as the lambda expression inside all of the operators like thenApply is
	 * cheap, we don't really care who calls it. But what if this expression takes a
	 * little bit of CPU time to complete or makes a blocking network call?
	 */
	@Test
	public void testCompletableFutureGetStrLength() {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			try (InputStream is = new URL("http://www.infoq.com/cn").openStream()) {
				logger.info("Downloading");
				return IOUtils.toString(is, UTF_8);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, pool);

		CompletableFuture<Integer> intFuture = future.thenApply(s -> s.length());
	}

	/*
	 * Think about it: we have a background task of type String and we want to apply
	 * some specific transformation asynchronously when that value completes. The
	 * easiest way to implement that is by wrapping the original task (returning
	 * String) and intercepting it when it completes. When the inner task finishes,
	 * our callback kicks in, applies the transformation and returns modified value.
	 * It's like an aspect that sits between our code and original computation result.
	 * That being said it should be fairly obvious that s.length() transformation will
	 * be executed in the same thread as the original task, huh? Not quite!
	 * 
	 * The first transformation in thenApply() is registered while the task is still
	 * running. Thus it will be executed immediately after task completion in the same
	 * thread as the task. However before registering second transformation we wait
	 * until the task actually completes. Even worse, we shutdown the thread pool
	 * entirely, to make sure no other code can ever be executed there. So which
	 * thread will run second transformation? We know it must happen immediately since
	 * the future we register callback on already completed. It turns out that by
	 * default client thread (!) is used!
	 * 
	 * The output is as follows:
	 * 
	 * pool-1-thread-1 | First transformation main | Second transformation
	 * 
	 * Second transformation, when registered, realizes that the CompletableFuture
	 * already finished, so it executes the transformation immediately. There is no
	 * other thread around so thenApply() is invoked in the context of current main
	 * thread. The biggest reason why this behavior is error prone shows up when the
	 * actual transformation is costly. Imagine lambda expression inside thenApply()
	 * doing some heavy computation or blocking network call. Suddenly our
	 * asynchronous CompletableFuture blocks calling thread!
	 */
	@Test
	public void testCompleteableFuture2() throws InterruptedException, ExecutionException {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			try {
				SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "ABC";
		}, pool);

		future.thenApply(s -> {
			logger.info("First transformation");
			return s.length();
		});

		future.get();
		pool.shutdownNow();
		pool.awaitTermination(1, TimeUnit.MINUTES);

		future.thenApply(s -> {
			logger.info("Second transformation");
			return s.length();
		});
	}

	/************ Controlling callback's thread pool *****************/
	@Test
	public void testCompleteableFuture3() throws InterruptedException, ExecutionException {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			try {
				SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "ABC";
		}, pool);

		future.thenApply(s -> {
			logger.info("First transformation");
			return s.length();
		});

		future.get();
		pool.shutdownNow();
		pool.awaitTermination(1, TimeUnit.MINUTES);

		/*
		 * There are two techniques to control which thread executes our callbacks and
		 * transformations. Notice that these solutions are only needed if your
		 * transformations are costly. Otherwise the difference is negligible. So
		 * first of all we can choose the *Async versions of operators
		 * 
		 * This time the second transformation was automatically off-loaded to our
		 * friend, ForkJoinPool.commonPool():
		 */
		/*
		 * future.thenApplyAsync(s -> { logger.info("Second transformation"); return
		 * s.length(); });
		 */

		/*
		 * But we don't like commonPool so we supply our own:
		 * 
		 * Notice that different thread pool was used (pool-1 vs. pool-2):
		 */
		ExecutorService pool2 = Executors.newFixedThreadPool(10);
		future.thenApplyAsync(s -> {
			logger.info("Second transformation");
			return s.length();
		}, pool2);
	}

	/*********** Treating callback like another computation step ************/
	/*
	 * But I believe that if you are having troubles with long-running callbacks and
	 * transformations (remember that this article applies to almost all other methods
	 * on CompletableFuture), you should simply use another explicit
	 * CompletableFuture, like here:
	 * 
	 * This approach is more explicit. Knowing that our transformation has significant
	 * cost we don't risk running it on some arbitrary or uncontrolled thread. Instead
	 * we explicitly model it as asynchronous operation from String to
	 * CompletableFuture<Integer>. However we must replace thenApply() with
	 * thenCompose(), otherwise we'll end up with
	 * CompletableFuture<CompletableFuture<Integer>>.
	 */
	@Test
	public void testCompleteableFuture4() throws InterruptedException, ExecutionException {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			try {
				SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "ABC";
		}, pool);

		CompletableFuture<Integer> intFuture = future.thenCompose(s -> strLen(s));

	}

	// Imagine this is slow and costly
	CompletableFuture<Integer> strLen(String s) {
		return CompletableFuture.supplyAsync(
				() -> s.length(),
				pool2);
	}

	/*
	 * Typically futures represent piece of code running by other thread. But that’s
	 * not always the case. Sometimes you want to create a Future representing some
	 * event that you know will occur, e.g. JMS message arrival. So you have
	 * Future<Message> but there is no asynchronous job underlying this future. You
	 * simply want to complete (resolve) that future when JMS message arrives, and
	 * this is driven by an event. In this case you can simply create
	 * CompletableFuture, return it to your client and whenever you think your results
	 * are available, simply complete() the future and unlock all clients waiting on
	 * that future.
	 * 
	 * This comes quite handy when you want to represent a task in the future, but not
	 * necessarily computational task running on some thread of execution.
	 * CompletableFuture.complete() can only be called once, subsequent invocations
	 * are ignored. But there is a back-door called
	 * CompletableFuture.obtrudeValue(...) which overrides previous value of the
	 * Future with new one. Use with caution.
	 */
	@Test
	public void testCompleteMethod() throws InterruptedException {
		/*
		 * Notice that this future is not associated wtih any Callable<String>, no
		 * thread pool, no asynchronous job. If now the client code calls ask().get()
		 * it will block forever. If it registers some completion callbacks, they will
		 * never fire.
		 */
		CompletableFuture<String> future = new CompletableFuture<String>();
		Executors.newCachedThreadPool().execute(() -> {
			String msg = "";
			try {
				msg = future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			System.out.println(MessageFormat.format("Massage is {0}", msg));
		});

		TimeUnit.SECONDS.sleep(2);
		/*
		 * and at this very moment all clients blocked on Future.get() will get the
		 * result string. Also completion callbacks will fire immediately.
		 */
		future.complete("Rico, u r so handsome!");
	}

	/**
	 * The most generic way to process the result of a computation is to feed it to a function. The
	 * thenApply method does exactly that: accepts a Function instance, uses it to process the
	 * result and returns a Future that holds a value returned by a function:
	 * 
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@Test
	public void testSupplyAsync() throws InterruptedException, ExecutionException {
		CompletableFuture<LocalDateTime> future = CompletableFuture.supplyAsync(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return LocalDateTime.now();
		});
		System.out.println(future.get().format(ofPattern("yyyy-MM-dd HH:mm:ss")));
	}

	/**
	 * If you don’t need to return a value down the Future chain, you can use an instance of the
	 * Consumer functional interface. Its single method takes a parameter and returns void.
	 * 
	 * There’s a method for this use case in the CompletableFuture — the thenAccept method receives
	 * a Consumer and passes it the result of the computation. The final future.get() call returns
	 * an instance of the Void type.
	 */
	@Test
	public void testThenApply() {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			return LocalDateTime.now();
		}).thenApply(date -> {
			logger.info("Begin formating......");
			return date.format(ofPattern("yyyy-MM-dd HH:mm:ss"));
		}).whenComplete((s, e) -> logger.info(s));
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
			return LocalDateTime.now();
		}).thenApply(date -> {
			logger.info("Begin formating......");
			return date.format(ofPattern("yyyy-MM-dd HH:mm:ss"));
		}).whenComplete((s, e) -> {
			logger.info(s);
		});
	}

	@Test
	public void testThenAccept() {
		CompletableFuture<LocalDateTime> getDateFuture = new CompletableFuture<>();
		getDateFuture.thenAccept(
				localDateTime -> logger.info(localDateTime.format(ofPattern("yyyy-MM-dd HH:mm:ss"))));
		getDateFuture.complete(LocalDateTime.now());
		logger.info("Completed!");
	}

	@Test
	public void testThenAcceptAsync() {
		CompletableFuture<LocalDateTime> getDateFuture = new CompletableFuture<>();
		getDateFuture.thenAcceptAsync(
				localDateTime -> logger.info(localDateTime.format(ofPattern("yyyy-MM-dd HH:mm:ss"))));
		getDateFuture.thenAcceptAsync(
				localDateTime -> logger.info(localDateTime.format(ofPattern("yyyy-MM-dd"))));
		getDateFuture.complete(LocalDateTime.now());
		logger.info("Completed!");
	}

	/**
	 * if you neither need the value of the computation nor want to return some value at the end of
	 * the chain, then you can pass a Runnable lambda to the thenRun method. In the following
	 * example, after the future.get() method is called, we simply print a line in the console:
	 */
	@Test
	public void testThenRun() {
		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "Hello");
		CompletableFuture<Void> future = completableFuture.thenRun(() -> System.out.println("Computation finished."));
		future.join();
	}

	@Test
	public void testHandle() {
		CompletableFuture<LocalDateTime> getDateFuture = new CompletableFuture<>();
		getDateFuture.handle((localDateTime, e) -> {
			if (localDateTime != null) {
				logger.info(localDateTime.format(ofPattern("MM/dd/yyyy HH:mm:ss")));
			} else {
				logger.info("exception", e);
			}
			return null;
		});
		// getDateFuture.completeExceptionally(new
		// IllegalArgumentException("something wrong"));
		getDateFuture.complete(null);
		logger.info("Complete");
	}

	/*
	 * 	public <U,V> CompletableFuture<V> thenCombineAsync(
	 *	    CompletableFuture<? extends U> other,
	 *	    BiFunction<? super T,? super U,? extends V> fn,
	 *	    Executor executor)
	 *
	 *@on
	 */
	@Test
	public void testThenCombineAsync() {

	}

}
