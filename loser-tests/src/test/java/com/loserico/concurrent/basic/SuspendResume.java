package com.loserico.concurrent.basic;

/**
 * A thread may be suspended and resumed using the combination of wait() and
 * notify() methods.
 * 
 * The class MyThread has a boolean field active that represents the current status
 * of the thread. The methods Suspend() and Resume() change this flag to suspend and
 * resume a thread respectively. The main thread suspends and resumes after every 1
 * second.
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
class MyThread extends Thread {
	boolean active = true;

	public void doSuspend() {
		active = false;
	}

	public synchronized void doResume() {
		active = true;
		notify();
	}

	public synchronized void run() {
		try {
			while (true) {
				if (active) {
					System.out.println("Running...");
					Thread.sleep(500);
				} else {
					System.out.println("Suspended...");
					wait();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

public class SuspendResume {
	public static void main(String args[]) throws Exception {
		MyThread mt = new MyThread();
		mt.start();
		while (true) {
			Thread.sleep(1000);
			mt.doSuspend();
			Thread.sleep(1000);
			mt.doResume();
		}
	}
}