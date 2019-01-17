package com.loserico.chapter5;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * The ThreadPool- Manager is responsible for scheduling new jobs onto a thread
 * pool, and is lightly adapted from listing 4.15. It also provides the
 * capability to cancel a running job, but this method is private.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class ThreadPoolManager {

	private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
	private final BlockingQueue<WorkUnit<String>> blockingQueue;

	public ThreadPoolManager(BlockingQueue<WorkUnit<String>> blockingQueue) {
		this.blockingQueue = blockingQueue;
	}

	public ScheduledFuture<?> run(QueueReaderTask msgReader) {
		msgReader.setQueue(blockingQueue);
		return scheduledExecutorService.scheduleAtFixedRate(msgReader, 10, 10, TimeUnit.MILLISECONDS);
	}

	private void cancel(final ScheduledFuture<?> scheduledFuture) {
		scheduledExecutorService.schedule(new Runnable() {
			public void run() {
				scheduledFuture.cancel(true);
			}
		}, 10, TimeUnit.MILLISECONDS);
	}

	public Method makeReflective() {
		Method method = null;

		try {
			Class<?>[] argTypes = new Class[] { ScheduledFuture.class };
			method = ThreadPoolManager.class.getDeclaredMethod("cancel", argTypes);
			method.setAccessible(true);
		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		return method;
	}

	public static class CancelProxy {
		private CancelProxy() {
		}

		public void invoke(ThreadPoolManager threadPoolManager, ScheduledFuture<?> scheduledFuture) {
			threadPoolManager.cancel(scheduledFuture);
		}
	}

	public CancelProxy makeProxy() {
		return new CancelProxy();
	}

	public MethodHandle makeMethodHandle() {
		MethodHandle methodHandle;
		MethodType methodType = MethodType.methodType(void.class, ScheduledFuture.class);

		try {
			methodHandle = MethodHandles.lookup().findVirtual(ThreadPoolManager.class, "cancel", methodType);
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw (AssertionError) new AssertionError().initCause(e);
		}

		return methodHandle;
	}
}