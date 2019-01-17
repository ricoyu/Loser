package com.loserico.concurrent.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitNotifyTest {
	private static final Logger log = LoggerFactory.getLogger(WaitNotifyTest.class);

	private static final Object obj = new Object();

	static class R implements Runnable {
		int i;

		R(int i) {
			this.i = i;
		}

		public void run() {
			try {
				synchronized (obj) {
					log.debug("线程->  {} 等待中", i);
					obj.wait();
					log.debug("线程->  {} 在运行了", i);
					Thread.sleep(30000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Thread[] rs = new Thread[10];
		for (int i = 0; i < 10; i++) {
			rs[i] = new Thread(new R(i));
		}
		for (Thread r : rs) {
			r.start();
		}

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized (obj) {
			obj.notifyAll();
		}

		System.out.println("..............");
	}
}
