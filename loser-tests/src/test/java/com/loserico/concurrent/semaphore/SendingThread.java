package com.loserico.concurrent.semaphore;

public class SendingThread extends Thread {
	SimpleSemaphore semaphore = null;

	public SendingThread(SimpleSemaphore semaphore) {
		this.semaphore = semaphore;
	}

	public void run() {
		System.out.println("准备调用semaphore.take()");
		try {
			sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.semaphore.take();
		System.out.println("成功调用semaphore.take()");
	}
}