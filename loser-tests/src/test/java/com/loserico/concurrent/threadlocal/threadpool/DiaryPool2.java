package com.loserico.concurrent.threadlocal.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class CustomThreadPoolExecutor extends ThreadPoolExecutor {
	public CustomThreadPoolExecutor(int corePoolSize,
			int maximumPoolSize, long keepAliveTime,
			TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime,
				unit, workQueue);
	}

	@Override
	public void beforeExecute(Thread t, Runnable r) {
		if (t == null || r == null) {
			throw new NullPointerException();
		}
		Diary.setDay(Day.MONDAY);
		super.beforeExecute(t, r);
	}
}

public final class DiaryPool2 {
	final int numOfThreads = 2; // Maximum number of threads allowed in pool
	final Executor exec;
	final Diary diary;

	DiaryPool2() {
	    exec = (Executor) Executors.newFixedThreadPool(numOfThreads);
	    diary = new Diary();
	  }

	public void doSomething1() {
		exec.execute(new Runnable() {
			@Override
			public void run() {
				diary.setDay(Day.FRIDAY);
				diary.threadSpecificTask();
			}
		});
	}

	public void doSomething2() {
		exec.execute(new Runnable() {
			@Override
			public void run() {
				diary.threadSpecificTask();
			}
		});
	}

	public static void main(String[] args) {
		DiaryPool dp = new DiaryPool();
		dp.doSomething1(); // Thread 1, requires current day as Friday
		dp.doSomething2(); // Thread 2, requires current day as Monday
		dp.doSomething2(); // Thread 3, requires current day as Monday
	}
}