package com.loserico.hystrix;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

public class BlockingCmdTest {

	private static final Logger logger = LoggerFactory.getLogger(BlockingCmdTest.class);

	@Test
	public void testName() throws InterruptedException, ExecutionException {
		String string = new BlockingCmd().execute();
		System.out.println(string);

		Future<String> future = new BlockingCmd().queue();
		System.out.println(future.get());
	}

	@Test
	public void test2() {
		/*
		 * The semantic difference between observe() and toObservable() is quite
		 * important. toObservable() converts a command to a lazy and cold
		 * Observable—the command will not be executed until someone actually subscribes
		 * to this Observable. Moreover, the Observable is not cached, so each
		 * subscribe() will trigger command execution. observe(), in contrast, invokes
		 * the command asynchronously straight away, returning a hot but also cached
		 * Observable.
		 */
		Observable<String> eager = new BlockingCmd().observe();
		Observable<String> lazy = new BlockingCmd().toObservable();
	}

	@Test
	public void testRetried() throws InterruptedException {
		/*
		 * The preceding pipeline invokes a command, but in case of failure retries
		 * after 500 milliseconds. However, retrying can take up to three seconds; above
		 * that TimeoutEx ception is thrown
		 */
		Observable<String> retried = new BlockingCmd()
				.toObservable()
				.doOnError(ex -> logger.warn("Error", ex))
				.retryWhen(ex -> ex.delay(500, MILLISECONDS))
				.timeout(3, SECONDS);
		Thread.currentThread().join();
	}
}
