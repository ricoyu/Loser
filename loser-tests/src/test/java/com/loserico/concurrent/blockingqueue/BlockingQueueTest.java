package com.loserico.concurrent.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueTest {
	public static void main(String[] args) {
		final BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(3);
		for (int i = 0; i < 3; i++) {
			final int value = i;
			new Thread() {
				public void run() {
					while (true) {
						try {
							TimeUnit.SECONDS.sleep((long) Math.random() * 10);
							System.out.println(Thread.currentThread().getName() + " 准备放数据!");
							queue.put(value);
							System.out.println(
									Thread.currentThread().getName() + " 已经完成放数据, 队列目前有 " + queue.size() + " 个数据.");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}

		new Thread() {
			public void run() {
				while (true) {
					try {
						TimeUnit.SECONDS.sleep(1);
						System.out.println(Thread.currentThread().getName() + " 准备取数据!");
						Integer value = queue.take();
						System.out.println(Thread.currentThread().getName() + " 已经完成取数据, 数据为: " + value + ", 队列目前有 "
								+ queue.size() + " 个数据.");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
}
