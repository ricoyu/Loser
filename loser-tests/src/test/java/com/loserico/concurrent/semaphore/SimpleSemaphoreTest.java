package com.loserico.concurrent.semaphore;

public class SimpleSemaphoreTest {

	public static void main(String[] args) {
		SimpleSemaphore semaphore = new SimpleSemaphore();
		SendingThread sender = new SendingThread(semaphore);
		ReceivingThread receiver = new ReceivingThread(semaphore);
		receiver.start();
		sender.start();
	}
}
