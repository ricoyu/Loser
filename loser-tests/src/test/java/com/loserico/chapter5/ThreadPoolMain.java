package com.loserico.chapter5;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;

/**
 * To show the differences between method handles and other techniques, we’ve
 * provided three different ways to access the private cancel() method from
 * outside the class—the methods shown in bold in the listing. We also show two
 * Java 6 style techniques— reflection and a proxy class, and compare them to a
 * new MethodHandlebased approach. We’re using a queue-reading task called
 * QueueReaderTask (which implements Runnable).
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class ThreadPoolMain {

	private ThreadPoolManager manager;

	private void cancelUsingReflection(ScheduledFuture<?> scheduledFuture) {
		Method method = manager.makeReflective();

		try {
			System.out.println("With Reflection");
			method.invoke(scheduledFuture);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Proxy invocation is statically typed
	 * 
	 * @param scheduledFuture
	 */
	private void cancelUsingProxy(ScheduledFuture<?> scheduledFuture) {
		ThreadPoolManager.CancelProxy proxy = manager.makeProxy();

		System.out.println("With Proxy");
		proxy.invoke(manager, scheduledFuture);
	}

	private void cancelUsingMH(ScheduledFuture<?> scheduledFuture) {
		MethodHandle methodHandle = manager.makeMethodHandle();

		try {
			System.out.println("With Method Handle");
			/*
			 * Signature must match exactly
			 */
			methodHandle.invokeExact(manager, scheduledFuture);
		} catch (Throwable e) {//Must catch Throwable
			e.printStackTrace();
		}
	}

	private void run() {
		BlockingQueue<WorkUnit<String>> blockingQueue = new LinkedBlockingQueue<>();
		manager = new ThreadPoolManager(blockingQueue);

		final QueueReaderTask msgReader = new QueueReaderTask(100) {
			@Override
			public void doAction(String msg_) {
				if (msg_ != null) {
					System.out.println("Msg recvd: " + msg_);
				}
			}
		};
		ScheduledFuture<?> scheduledFuture = manager.run(msgReader);
		cancelUsingMH(scheduledFuture);
		// cancelUsingProxy(hndl);
		// cancelUsingReflection(hndl);
	}

	public static void main(String[] args) {
		ThreadPoolMain main = new ThreadPoolMain();
		main.run();
	}
}
