package com.loserico.concurrent;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierTest {

	public static void main(String[] args) {
		ExecutorService pool = Executors.newCachedThreadPool();
		final CyclicBarrier cb = new CyclicBarrier(3);
		for (int i = 0; i < 3; i++) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.MICROSECONDS.sleep((long) (Math.random() * 10000));
						System.out.println("线程" + Thread.currentThread().getName() 
								+ " 到达集合地点1, 当前已有 " + (cb.getNumberWaiting() + 1)
								+ "已经到达 , " + ((cb.getNumberWaiting() + 1) == 3 ? "继续走啊!" : "正在等候"));
						cb.await();

						TimeUnit.MICROSECONDS.sleep((long) (Math.random() * 10000));
						System.out.println("线程" + Thread.currentThread().getName() + " 到达集合地点2, 当前已有 "
								+ (cb.getNumberWaiting() + 1)
								+ "已经到达 , " + ((cb.getNumberWaiting() + 1) == 3 ? "继续走啊!" : "正在等候"));
						cb.await();

						TimeUnit.MICROSECONDS.sleep((long) (Math.random() * 10000));
						System.out.println("线程" + Thread.currentThread().getName() + " 到达集合地点3, 当前已有 "
								+ (cb.getNumberWaiting() + 1)
								+ "已经到达 , " + ((cb.getNumberWaiting() + 1) == 3 ? "继续走啊!" : "正在等候"));
						cb.await();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
}