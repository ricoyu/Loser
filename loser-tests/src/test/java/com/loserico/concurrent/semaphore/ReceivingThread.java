package com.loserico.concurrent.semaphore;

public class ReceivingThread extends Thread {
	private SimpleSemaphore semaphore = null;

	public ReceivingThread(SimpleSemaphore semaphore) {
		this.semaphore = semaphore;
	}

	public void run() {
		try {
			System.out.println("准备调用semaphore的release()");
			this.semaphore.release();
			System.out.println("成功调用semaphore的release()");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// receive signal, then do something...
	}
}