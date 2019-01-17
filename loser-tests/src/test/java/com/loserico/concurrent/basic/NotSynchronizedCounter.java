package com.loserico.concurrent.basic;

/**
 * As we have seen in the last examples, the exact sequence in which all running
 * threads are executed depends next to the thread configuration like priority also
 * on the available CPU resources and the way the scheduler chooses the next thread
 * to execute. Although the behavior of the scheduler is completely deterministic,
 * it is hard to predict which threads execute in which moment at a given point in
 * time. This makes access to shared resources critical as it is hard to predict
 * which thread will be the first thread that tries to access it. And often access
 * to shared resources is exclusive, which means only one thread at a given point in
 * time should access this resource without any other thread interfering this
 * access.
 * 
 * A simple example for concurrent access of an exclusive resource would be a static
 * variable that is incremented by more than one thread:
 * 
 * <pre>
 * [thread-2] before: 8
 * [thread-2] after: 9
 * [thread-1] before: 0
 * [thread-1] after: 10
 * [thread-2] before: 9
 * [thread-2] after: 11
 * </pre>
 * 
 * Here, thread-2 retrieves the current value as 8, increments it and afterwards the
 * value is 9. This is how we would have expected it before. But what the following
 * thread executes may wonder us. thread-1 outputs the current value as zero,
 * increments it and afterwards the value is 10. How can this happen? When thread-1
 * read the value of the variable counter, it was 0. Then the context switch
 * executed the second thread and when thread-1 had his turn again, the other
 * threads already incremented the counter up to 9. Now he adds one and gets 10 as a
 * result.
 * 
 * @author Loser
 * @since Aug 20, 2016
 * @version
 *
 */
public class NotSynchronizedCounter implements Runnable {
	private static int counter = 0;

	public void run() {
		while (counter < 10) {
			System.out.println("[" + Thread.currentThread().getName() + "] before: " + counter);
			counter++;
			System.out.println("[" + Thread.currentThread().getName() + "] after: " + counter);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread[] threads = new Thread[5];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new NotSynchronizedCounter(), "thread-" + i);
			threads[i].start();
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
	}
}