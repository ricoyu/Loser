package com.peacefish.spring.concurrent;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.peacefish.spring.exception.AsyncExecutionException;
import com.peacefish.spring.exception.ConcurrentOperationException;

/**
 * 并发操作模版类 
 * <p>
 * Copyright: Copyright (c) 2017-09-28 14:02<br/>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public final class ConcurrentTemplate {

	private static final Logger logger = LoggerFactory.getLogger(ConcurrentTemplate.class);

	private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

	private static final ThreadLocal<Signal> signalLocal = new ThreadLocal<>();

	/**
	 * 将任务提交给futureResult(supplier)异步执行, 可以提交多次任务, 
	 * 然后主线程调用ConcurrentTemplate.await()等待提交的所有任务执行完毕
	 * 
	 * @param supplier
	 * @return FutureResult<T>
	 * @on
	 */
	public static <T> FutureResult<T> futureResult(Supplier<T> supplier) {
		Objects.nonNull(supplier);
		/*
		 * 这段拿Signal不需要放同步块, 在同一段业务代码中调用futureResult(supplier)是先后次序调用的
		 * 如果不同的业务代码同时在调用futureResult(supplier), 他们又是处于不同的Thread中, 所以从signalLocal.get()拿Signal对象是相互隔离的
		 * 
		 * Signal对象是在当前业务代码里第一次调用ConcurrentTemplate.futureResult(supplier)时创建的, 
		 * ConcurrentTemplate.await()方法只负责取出Signal对象, 该方法不会创建Signal对象
		 * @on
		 */
		Signal signal = signal();
		Future<T> future = EXECUTOR_SERVICE.submit(() -> {
			try {
				T result = supplier.get();
				logger.info("线程[" + Thread.currentThread().getName() + "]执行完毕, 等待countDownLatch");
				return result;
			} catch (Throwable e) {
				logger.error("", e);
				throw new AsyncExecutionException("", e);
			} finally {
				signal.countDown();
			}
		});
		return new FutureResult<>(future);
	}

	/**
	 * 从ThreadLocal中拿Signal对象, 这个要在主线程里面调用
	 * 提取出来是因为Signal对象在lamd里面用到了, 需要是事实上的final
	 * @on
	 */
	private static Signal signal() {
		Signal signal = signalLocal.get();
		if (signal == null) {
			signal = new Signal();
			signal.takeSeat();
			signalLocal.set(signal);
		} else {
			signal.takeSeat();
		}
		return signal;
	}

	/**
	 * 在调用{@code futureResult(Supplier<T> supplier)}后, 业务代码需要调用 {@code await()}等待异步任务执行完毕
	 * 没有超时时间, 发生异常记录log
	 * 
	 * @param countDownLatch
	 * @on
	 */
	public static void await() {
		/*
		 * 这个方法只会从signalLocal中取出Signal, 不负责创建
		 */
		Signal signal = signalLocal.get();
		if (signal == null) {
			throw new ConcurrentOperationException("先执行任务再调用我");
		}

		/*
		 * 业务方法调用ConcurrentTemplate.await()时, 所有任务都已经提交给futureResult(supplier)了
		 * 也就记录好了有几个位置
		 * @on
		 */
		String threadName = Thread.currentThread().getName();
		logger.info("线程[" + threadName + "]: 有{}个座位, 都坐好了, 老司机要开车了!", signal.seats());
		CountDownLatch countDownLatch = new CountDownLatch(signal.seats());
		try {
			logger.info("线程[{}]上锁并准备调用countDownLatchReady.signalAll()", threadName);
			synchronized (signal) {
				signal.countDownLatch = countDownLatch;
				logger.info("线程[" + threadName + "]: 大家可以继续了");
				signal.notifyAll();
				logger.info("线程[{}]成功通知到在 countDownLatchReady 上 await()的线程", threadName);
			}
			countDownLatch.await();
		} catch (Throwable e) {
			logger.error("线程[{}] 调用countDownLatchReady.signalAll() 报错", threadName);
			logger.error("", e);
		} finally {
			logger.error("线程[{}] 从signalLocal中删除Signal对象, 清理ThreadLocal", threadName);
			signalLocal.remove();
		}
	}

	/**
	 * 使用默认的CachedThreadPool异步执行并返回结果, 
	 * 这是单个任务的一部执行, 不需要配合ConcurrentTemplate.await()使用
	 * 使用默认的CachedThreadPool异步执行并返回结果, 这是单个任务的一部执行, 不需要配合ConcurrentTemplate.await()使用
	 * 
	 * @param executorService
	 * @param supplier
	 * @param countDownLatch
	 * @return FutureResult<T>
	 * @on
	 */
	public static <T> FutureResult<T> submit(Supplier<T> supplier) {
		Future<T> future = EXECUTOR_SERVICE.submit(() -> supplier.get());
		return futureResult(future);
	}

	/**
	 * 使用默认的CachedThreadPool异步执行并返回结果
	 * 
	 * @param executorService
	 * @param supplier
	 * @param countDownLatch
	 * @return FutureResult<T>
	 */
	public static <T> FutureResult<T> futureResult(CountDownLatch countDownLatch, Supplier<T> supplier) {
		return futureResult(EXECUTOR_SERVICE, countDownLatch, supplier);
	}

	/**
	 * 异步执行并返回结果
	 * 
	 * @param executorService
	 * @param supplier
	 * @param countDownLatch
	 * @return FutureResult<T>
	 */
	public static <T> FutureResult<T> futureResult(ExecutorService executorService, CountDownLatch countDownLatch,
			Supplier<T> supplier) {
		Objects.nonNull(executorService);
		Objects.nonNull(supplier);
		Objects.nonNull(countDownLatch);
		Future<T> future = executorService.submit(() -> {
			try {
				return supplier.get();
			} catch (Exception e) {
				logger.error("", e);
				throw new AsyncExecutionException("", e);
			} finally {
				countDownLatch.countDown();
			}
		});
		return new FutureResult<>(future);
	}

	/**
	 * 等待, 没有超时时间, 发生异常记录log
	 * 
	 * @param countDownLatch
	 */
	public static void await(CountDownLatch countDownLatch) {
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			logger.error("msg", e);
			throw new AsyncExecutionException("", e);
		}
	}

	/**
	 * 等待，没有超时时间，发生异常则运行task并记录log
	 * 
	 * @param countDownLatch
	 * @param task
	 */
	public static void await(CountDownLatch countDownLatch, Runnable task) {
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			logger.error("msg", e);
			task.run();
		}
	}

	/**
	 * 等待，有超时时间，发生异常记录log
	 * 
	 * @param countDownLatch
	 */
	public static void await(CountDownLatch countDownLatch, long timeout, TimeUnit unit) {
		try {
			countDownLatch.await(timeout, unit);
		} catch (InterruptedException e) {
			logger.error("msg", e);
			throw new AsyncExecutionException("", e);
		}
	}

	/**
	 * 等待，有超时时间，发生异常运行task并记录log
	 * 
	 * @param countDownLatch
	 */
	public static void await(CountDownLatch countDownLatch, long timeout, TimeUnit unit, Runnable task) {
		try {
			countDownLatch.await(timeout, unit);
		} catch (InterruptedException e) {
			logger.error("msg", e);
			task.run();
		}
	}

	/**
	 * 执行某个task<br/>
	 * 注意：执行过程中抛出异常则捕获，不会往上抛
	 * 
	 * @on
	 * @param executorService
	 * @param task
	 */
	public static void execute(ExecutorService executorService, Runnable task) {
		Objects.nonNull(executorService);
		Objects.nonNull(task);
		try {
			executorService.execute(task);
		} catch (Throwable e) {
			logger.error("", e);
			throw new AsyncExecutionException("", e);
		}
	}

	/**
	 * 执行某个task<br/>
	 * 
	 * @on
	 * @param executorService
	 * @param task
	 */
	public static void execute(Runnable task) {
		execute(EXECUTOR_SERVICE, task);
	}

	/**
	 * 执行某个task<br/>
	 * 注意：执行过程中抛出异常则捕获，不会往上抛
	 * 
	 * @on
	 * @param executorService
	 * @param task
	 */
	public static void execute(CountDownLatch countDownLatch, Runnable task) {
		execute(EXECUTOR_SERVICE, countDownLatch, task);
	}

	/**
	 * 执行某个task<br/>
	 * 注意：执行过程中抛出异常则捕获，不会往上抛
	 * 
	 * @on
	 * @param executorService
	 * @param task
	 */
	public static void execute(ExecutorService executorService, CountDownLatch countDownLatch, Runnable task) {
		Objects.nonNull(executorService);
		Objects.nonNull(task);
		executorService.execute(() -> {
			try {
				TransactionSynchronizationManager.setActualTransactionActive(true);
				task.run();
			} catch (Throwable e) {
				logger.error("", e);
				throw new AsyncExecutionException("", e);
			} finally {
				countDownLatch.countDown();
			}
		});
	}

	/**
	 * 执行某个task<br/>
	 * 注意：执行过程中抛出异常则捕获，不会往上抛
	 * 
	 * @on
	 * @param executorService
	 * @param task
	 */
	public static void execute(Runnable task, String errorMsg) {
		execute(EXECUTOR_SERVICE, task, errorMsg);
	}

	/**
	 * 执行某个task<br/>
	 * 注意：执行过程中抛出异常则捕获，不会往上抛
	 * 
	 * @on
	 * @param executorService
	 * @param task
	 */
	public static void execute(ExecutorService executorService,
			Runnable task,
			String errorMsg) {
		Objects.nonNull(executorService);
		Objects.nonNull(task);
		try {
			executorService.execute(task);
		} catch (Throwable e) {
			logger.error(errorMsg, e);
			throw new AsyncExecutionException("", e);
		}
	}

	/**
	 * 异步执行并返回结果
	 * 
	 * @param executorService
	 * @param supplier
	 * @param countDownLatch
	 * @return FutureResult<T>
	 */
	private static <T> FutureResult<T> futureResult(Future<T> future) {
		return new FutureResult<>(future);
	}

	private static class Signal {
		private int seat = 0;
		private CountDownLatch countDownLatch;

		public synchronized void takeSeat() {
			seat++;
		}

		public synchronized int seats() {
			return seat;
		}

		public void countDown() {
			try {
				synchronized (this) {
					while (countDownLatch == null) {
						synchronized (this) {
							this.wait();
						}
					}
				}
				countDownLatch.countDown();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
}