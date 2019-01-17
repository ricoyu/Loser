package com.loserico.concurrent.basic;

/**
 * Interrupting a thread means requesting it to stop what it is currently doing and
 * do something else. The request is sent to a thread using its interrupt() method
 * that sets the thread’s interrupt flag. However, how will the thread identify that
 * it has been interrupted?
 * 
 * Note that methods such as sleep(), wait(), join() etc. throw an
 * InterruptedException if they find interrupt flag set. So, a thread can use any of
 * these methods in a try block and provide an appropriate catch block which gets
 * executed when it is interrupted. In this way, a thread can determine if it is
 * interrupted or not.
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
class Timer extends Thread {
	public void run() {
		while (true) {
			System.out.println("Timer running. Date & time: " + new java.util.Date());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("Timer was interrupted");
				return;
			}
		}
	}
}

/**
 * Here, main thread creates and starts a child thread that continuously prints
 * current date and time and sleeps for 1 second. The main thread, after 4 seconds,
 * calls child thread’s interrupt() method that sets child thread’s interrupt flag.
 * If the child thread is sleeping at that time, sleep() throws an
 * InterruptedException when it returns. Otherwise, when sleep() method is
 * encountered the next time, it throws an InterruptedException. Either case, the
 * catch block is executed and the child thread can respond to this interruption.
 * 
 * It is up to the thread exactly how it will respond to an interrupt. However, it
 * is very common that the thread dies upon interruption. In our example, the child
 * thread prints a message and returns. It is also up to the thread how quickly it
 * will respond. For example, if the thread receives an interrupt in the middle of
 * some heavyweight computation, it can only respond at the end of the computation.
 * 
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
public class InterruptDemo {
	public static void main(String args[]) throws InterruptedException {
		Timer t = new Timer();
		t.start();
		Thread.sleep(4000);
		t.interrupt();
	}
}