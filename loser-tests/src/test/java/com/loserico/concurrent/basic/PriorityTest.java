package com.loserico.concurrent.basic;

/**
 * Note that a priority merely tells how important a thread should get with respect
 * to others. Programs with multiple threads, specifically with different
 * priorities, may behave differently at different platforms especially on
 * preemptive and non-preemptive ones. The following program demonstrates how two
 * threads with different priorities are handled.
 * 
 * The main thread creates and starts two threads; one having highest priority and
 * the other one having lowest priority. The two threads continuously increment
 * their respective local variable count. The main thread waits for 100 milliseconds
 * and prints the current values of the local count variable of the two threads.
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
class MyThread2 extends Thread {
	int count = 0;

	public int getCount() {
		return count;
	}

	public void run() {
		while (true)
			count++;
	}
}

public class PriorityTest {
	public static void main(String args[]) throws InterruptedException {
		MyThread2 t1 = new MyThread2();
		MyThread2 t2 = new MyThread2();
		t1.setPriority(Thread.MAX_PRIORITY);
		t2.setPriority(Thread.MIN_PRIORITY);
		t1.start();
		t2.start();
		Thread.sleep(1000);
		System.out.println("Thread 1 count: " + t1.getCount());
		System.out.println("Thread 2 count: " + t2.getCount());
	}
}