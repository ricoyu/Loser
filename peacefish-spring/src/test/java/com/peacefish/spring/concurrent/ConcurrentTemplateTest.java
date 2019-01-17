package com.peacefish.spring.concurrent;

import static java.util.concurrent.TimeUnit.*;

public class ConcurrentTemplateTest {

	public static void main(String[] args) {
		ConcurrentTemplate.futureResult(() -> {
			try {
				SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("线程[" + Thread.currentThread().getName() + "]开始执行");
			return Thread.currentThread().getName();
		});
		ConcurrentTemplate.futureResult(() -> {
			try {
				SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("线程[" + Thread.currentThread().getName() + "]开始执行");
			return Thread.currentThread().getName();
		});
		ConcurrentTemplate.futureResult(() -> {
			if (true) {
				throw new RuntimeException();
			}
			/*
			 * try { SECONDS.sleep(1); } catch (InterruptedException e) {
			 * e.printStackTrace(); }
			 * System.out.println("线程["+Thread.currentThread().getName()+"]开始执行");
			 */
			return Thread.currentThread().getName();
		});
		System.out.println("主线程开始等待");
		ConcurrentTemplate.await();
		System.out.println("done");

		ConcurrentTemplate.futureResult(() -> {
			try {
				SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("线程[" + Thread.currentThread().getName() + "]开始执行");
			return Thread.currentThread().getName();
		});
		ConcurrentTemplate.futureResult(() -> {
			if (true) {
				throw new RuntimeException();
			}
			return Thread.currentThread().getName();
		});
		System.out.println("主线程开始等待");
		ConcurrentTemplate.await();
		System.out.println("done");
	}
}
