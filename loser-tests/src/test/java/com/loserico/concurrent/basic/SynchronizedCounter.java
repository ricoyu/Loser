package com.loserico.concurrent.basic;

/**
 * The solution for problems like this is the synchronized key word in Java. With
 * synchronized you can create blocks of statements which can only be accessed by a
 * thread, which gets the lock on the synchronized resource. Let’s change the run()
 * method from the last example and introduce a synchronized block for the whole
 * class:
 * 
 * The synchronized(SynchronizedCounter.class) statement works like a barrier where
 * all threads have to stop and ask for entrance. Only the first thread that gets
 * the lock on the resources is allowed to pass. Once it has left the synchronized
 * block, another waiting thread can enter and so forth.
 * 
 * The synchronized(SynchronizedCounter.class) statement works like a barrier where
 * all threads have to stop and ask for entrance. Only the first thread that gets
 * the lock on the resources is allowed to pass. Once it has left the synchronized
 * block, another waiting thread can enter and so forth.
 * 
 * With the synchronized block around the output and increment of the counter above
 * the output looks like the following example:
 * 
 * <pre>
 * 	[thread-1] before: 11
 * 	[thread-1] after: 12
 * 	[thread-4] before: 12
 * 	[thread-4] after: 13
 * </pre>
 * 
 * Now you will see only subsequent outputs of before and after that increment the
 * counter variable by one.
 * 
 * The synchronized keyword can be used in two different ways. It can either be used
 * within a method as shown above. In this case you have to provide a resource that
 * is locked by the current thread. This resource has to be chosen carefully because
 * the thread barrier becomes a completely different meaning based on the scope of
 * the variable.
 * 
 * @author Loser
 * @since Aug 20, 2016
 * @version
 *
 */
public class SynchronizedCounter implements Runnable {
	private static int counter = 0;

	public void run() {
		while (counter < 10) {
			synchronized (SynchronizedCounter.class) {
				System.out.println("[" + Thread.currentThread().getName() + "] before: " + counter);
				counter++;
				System.out.println("[" + Thread.currentThread().getName() + "] after: " + counter);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread[] threads = new Thread[5];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new SynchronizedCounter(), "thread-" + i);
			threads[i].start();
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
	}
}