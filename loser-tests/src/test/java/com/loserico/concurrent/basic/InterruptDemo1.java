package com.loserico.concurrent.basic;

/**
 * *Alternatively, a thread may use static interrupted() method to check if it was
 * interrupted. The method returns a true if the interrupt flag is set; false
 * otherwise. So, a thread, on its way to normal work, may devote some time to
 * inspect the method’s return value as follows:
 * 
 * <pre>
 * while (true) {
 * 	// do normal work
 * 	// check it was interrupted
 * 	if (Thread.interrupted()) {
 * 		// it was interrupted, respond
 * 	}
 * }
 * </pre>
 * 
 * Note that interrupted()method clears the interrupt flag of the thread. This means
 * if the method is called twice in succession, the second call would return false
 * unless the current thread is interrupted again in the meanwhile.
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
class Timer2 extends Thread {
	public void run() {
		while (true) {
			System.out.println("Timer running. Date & time: " + new java.util.Date());
			if (Thread.interrupted()) {
				System.out.println("Timer was interrupted");
				return;
			}
		}
	}
}

public class InterruptDemo1 {
	public static void main(String args[]) throws InterruptedException {
		Timer2 t = new Timer2();
		t.start();
		Thread.sleep(20);
		t.interrupt();
	}
}