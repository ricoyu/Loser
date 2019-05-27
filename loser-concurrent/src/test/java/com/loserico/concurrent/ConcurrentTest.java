package com.loserico.concurrent;

import static java.util.concurrent.TimeUnit.*;

import org.junit.Test;
import org.springframework.core.env.SystemEnvironmentPropertySource;

public class ConcurrentTest {

	@Test
	public void testSubmitAwait() {
		Concurrent.submit(() -> "a");
		Concurrent.submit(() -> {
			try {
				SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "b";
		});
		Concurrent.submit(() -> "c");
		Concurrent.submit(() -> "d");
		Concurrent.await();
		System.out.println("done");
	}
	
	@Test
	public void testSubmitAwaitWithException() {
		Concurrent.submit(() -> "a");
		Concurrent.submit(() -> {
			try {
				SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "b";
		});
		Concurrent.submit(() -> {
			try {
				SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			throw new NullPointerException();
//			return "c";
		});
		Concurrent.submit(() -> "d");
		Concurrent.await();
		System.out.println("done");
	}
	
	@Test
	public void testSubmitAwaitSuccess() {
		long begin = System.currentTimeMillis();
		FutureResult<String> aFutureResult = Concurrent.submit(() -> "a");
		FutureResult<String> bFutureResult = Concurrent.submit(() -> {
			try {
				SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "b";
		});
		FutureResult<String> cFutureResult = Concurrent.submit(() -> {
			try {
				SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "c";
		});
		FutureResult<String> dFutureResult = Concurrent.submit(() -> "d");
		Concurrent.await();
		long end = System.currentTimeMillis();
		System.out.println("done in " + (end - begin) / 1000 );
		System.out.println(aFutureResult.get());
		System.out.println(bFutureResult.get());
		System.out.println(cFutureResult.get());
		System.out.println(dFutureResult.get());
	}
}
