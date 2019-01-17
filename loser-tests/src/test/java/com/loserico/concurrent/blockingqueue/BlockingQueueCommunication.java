package com.loserico.concurrent.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueCommunication {

	public static void main(String[] args) {
		final Business business = new Business();
		new Thread() {
			public void run() {
				for (int i = 0; i < 50; i++) {
					business.stub(i);
				}
			}
		}.start();

		for (int i = 0; i < 50; i++) {
			business.main(i);
		}
	}

	static class Business {
		BlockingQueue<Integer> queue1 = new ArrayBlockingQueue<Integer>(1);
		BlockingQueue<Integer> queue2 = new ArrayBlockingQueue<Integer>(1);

		{
			try {
				queue2.put(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void stub(int i) {
			try {
				queue1.put(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (int j = 0; j < 10; j++) {
				System.out.println("stub thread sequence of " + j + ", loop of " + i);
			}

			try {
				queue2.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void main(int i) {
			try {
				queue2.put(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (int j = 0; j < 100; j++) {
				System.out.println("main thread sequence of " + j + ", loop of " + i);
			}

			try {
				queue1.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
