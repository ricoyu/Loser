package com.loserico.hystrix;

import static org.junit.Assert.*;

import org.junit.Test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class CommandHelloFailure extends HystrixCommand<String> {

	private final String name;

	public CommandHelloFailure(String name) {
		super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
		this.name = name;
	}

	@Override
	protected String run() throws Exception {
		throw new RuntimeException("this command always fail!");
	}

	/*
	 * In an ordinary HystrixCommand you implement a fallback by means of a
	 * getFallback() implementation. Hystrix will execute this fallback for all
	 * types of failure such as run() failure, timeout, thread pool or semaphore
	 * rejection, and circuit-breaker short-circuiting. The following example
	 * includes such a fallback
	 */
	@Override
	protected String getFallback() {
		return "Hello Failure " + name + "!";
	}

	public static void main(String[] args) throws InterruptedException {
        assertEquals("Hello Failure World!", new CommandHelloFailure("World").execute());
        assertEquals("Hello Failure Bob!", new CommandHelloFailure("Bob").execute());
        Thread.currentThread().join();
    }
}
