package com.loserico.concurrent.basic;

import java.util.Random;

/**
 * As we have seen in the last section we can let our thread sleep until it is woken
 * up by another thread. Another important feature of threads that you will have to
 * use from time to time is the ability of a thread to wait for the termination of
 * another thread.
 * 
 * Let’s assume you have to implement some kind of number crunching operation that
 * can be divided into several parallel running threads. The main thread that starts
 * the so called worker threads has to wait until all its child threads have
 * terminated. The following code shows how this can be achieved:
 * 
 * @author Loser
 * @since Aug 20, 2016
 * @version
 *
 */
public class JoinExample implements Runnable {
	private Random rand = new Random(System.currentTimeMillis());

	public void run() {
		// simulate some CPU expensive task
		for (int i = 0; i < 100000000; i++) {
			rand.nextInt();
		}
		System.out.println("[" + Thread.currentThread().getName() + "] finished.");

	}

	public static void main(String[] args) throws InterruptedException {
		Thread[] threads = new Thread[5];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new JoinExample(), "joinThread-" + i);
			threads[i].start();
		}
		/*
		 * Within our main method we create an array of five Threads, which are all
		 * started one after the other. Once we have started them, we wait in the
		 * main Thread for their termination. The threads itself simulate some
		 * number crunching by computing one random number after the other. Once
		 * they are finished, they print out “finished”. Finally the main thread
		 * acknowledges the termination of all of its child threads:
		 */
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		System.out.println("[" + Thread.currentThread().getName() + "] All threads have finished.");
	}
}