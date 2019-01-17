package com.loserico.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * 主线程通过doneSignal.await()等待其它线程将doneSignal递减至0。其它的5个InnerThread线程，
 * 每一个都通过doneSignal.countDown()将doneSignal的值减1；当doneSignal为0时，main被唤醒后继续执行。
 * @author Loser
 * @since Jun 7, 2016
 * @version 
 *
 */
public class CountDownLatchTest {

	private static int latchSize = 5;
	
	private static CountDownLatch doneSignal;
	
	public static void main(String[] args) {
		doneSignal = new CountDownLatch(latchSize);
		
		//新建5个任务
		for (int i = 0; i < latchSize; i++) {
			new InnerClass().start();
		}
		System.out.println("main await begin.");
		// "主线程"等待5个任务的完成
		try {
			doneSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("main await finished.");
	}
	
	static class InnerClass extends Thread {

		@Override
		public void run() {
			try {
				Thread.sleep(2000);
				System.out.println(Thread.currentThread().getName() + " sleep 2000ms.");
				// 将CountDownLatch的数值减1
				doneSignal.countDown();
			} catch (Exception e) {

			}
		}
		
	}
}
