package com.loserico.concurrent.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Within the main method we start a new thread first, which would sleep for a very
 * long time (about 290.000 years) if it would not be interrupted.
 * 
 * To get the program finished before this time has passed by, myThread is
 * interrupted by calling interrupt() on its instance variable in the main method.
 * This causes an InterruptedException within the call of sleep() and is printed on
 * the console as “Interrupted by exception!”.
 * 
 * Having logged the exception the thread does some busy waiting until the
 * interrupted flag on the thread is set. This again is set from the main thread by
 * calling interrupt() on the thread’s instance variable. Overall we see the
 * following output on the console:
 * 
 * <pre>
	myThread Beging sleeping...
	main	 Sleeping in main thread for 5s...
	main 	 Interrupting myThread
	main 	 Sleeping in main thread for 5s again...
	myThread Interrupted by exception!
	main 	 Interrupting myThread again
	myThread Interrupted for the second time.
 * </pre>
 * 
 * What is interesting in this output, are the lines 3 and 4. If we go through the
 * code we might have expected that the string “Interrupted by exception!” is
 * printed out before the main thread starts sleeping again with “Sleeping in main
 * thread for 5s…”. But as you can see from the output, the scheduler has executed
 * the main thread before it started myThread again. Hence myThread prints out the
 * reception of the exception after the main thread has started sleeping.
 * 
 * It is a basic observation when programming with multiple threads that logging
 * output of threads is to some extend hard to predict as it’s hard to calculate
 * which thread gets executed next. Things get even worse when you have to cope with
 * more threads whose pauses are not hard coded as in the examples above. In these
 * cases the whole program gets some kind of inner dynamic that makes concurrent
 * programming a challenging task.
 * 
 * @author Loser
 * @since Aug 20, 2016
 * @version
 *
 */
public class InterruptExample implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(InterruptExample.class);

	public void run() {
		try {
			log.info("Beging sleeping...");
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			log.info("Interrupted by exception!");
		}
		while (!Thread.interrupted()) {
			// do nothing here
		}
		log.info("Interrupted for the second time.");
	}

	public static void main(String[] args) throws InterruptedException {
		Thread myThread = new Thread(new InterruptExample(), "myThread");
		myThread.start();
		log.info("Sleeping in main thread for 5s...");
		Thread.sleep(5000);
		log.info("Interrupting myThread");
		myThread.interrupt();
		log.info("Sleeping in main thread for 5s again...");
		Thread.sleep(5000);
		log.info("Interrupting myThread again");
		myThread.interrupt();
	}
}