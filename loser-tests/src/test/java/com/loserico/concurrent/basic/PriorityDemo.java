package com.loserico.concurrent.basic;

/**
 * In Java, every thread has a priority. Higher priority threads get more preference
 * in terms of CPU, I/O time, etc. than lower priority threads. However, how threads
 * with different priorities should be handled depends absolutely on the underlying
 * platform specifically on its scheduling algorithm. Conceptually, threads of equal
 * priority should get equal chance. Similarly, higher priority threads should
 * ideally receive more importance than lower priority ones.
 * 
 * Priorities are represented by integer numbers from 1 (lowest) to 10 (highest)
 * which are represented by two static final fields MIN_PRIORITY and MAX_PRIORITY of
 * Thread class respectively. A new thread receives its initial priority equal to
 * the priority of its creator thread. The JVM assigns a priority value equal to the
 * final field NORM_PRIORITY to the main thread.
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
public class PriorityDemo extends Thread {
	public void run() {
		System.out.println("Child's initial priority: " + getPriority());
		setPriority(3);
		System.out.println("After change, child's priority: " + getPriority());
	}

	public static void main(String args[]) {
		Thread t = Thread.currentThread();
		System.out.println("Main's initial priority: " + t.getPriority());
		t.setPriority(7);
		System.out.println("After change, main's priority: " + t.getPriority());
		new PriorityDemo().start();
	}
}