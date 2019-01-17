package com.loserico.concurrent.semaphore;

/**
 * a simple Semaphore implementation
 * 
 * The take() method sends a signal which is stored internally in the Semaphore. 
 * The release() method waits for a signal. 
 * When received the signal flag is cleared again, and the release() method exited.
 * 
 * Using a semaphore like this you can avoid missed signals. 
 * You will call take() instead of notify() and release() instead of wait(). 
 * If the call to take() happens before the call to release() the thread calling release() will still know that take() was called, 
 * because the signal is stored internally in the signal variable. This is not the case with wait() and notify().
 * 
 * @author Rico Yu
 * @since 2016-11-08 14:45
 * @version 1.0
 *
 */
public class SimpleSemaphore {
	private boolean signal = false;

	public synchronized void take() {
		this.signal = true;
		this.notify();
	}

	public synchronized void release() throws InterruptedException {
		while (!this.signal)
			wait();
		this.signal = false;
	}

}