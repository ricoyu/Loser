package com.loserico.concurrent.semaphore;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreSignalTest {

	public static void main(String[] args) throws InterruptedException {
		Semaphore semaphore = new Semaphore(1);
		Sender sender = new Sender(semaphore, "Sender");
		Receiver receiver = new Receiver(semaphore, "Receiver");
		sender.start();
		SECONDS.sleep(1);
		receiver.start();

	}
}

class Receiver extends Thread {

	private Semaphore semaphore;

	public Receiver(Semaphore semaphore, String name) {
		super(name);
		this.semaphore = semaphore;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + " 准备调用semaphore.acquire()");
		try {
			semaphore.acquire();
			System.out.println(Thread.currentThread().getName() + " 成功调用semaphore.acquire()");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println(Thread.currentThread().getName() + " 准备调用semaphore.release()");
		semaphore.release();
		System.out.println(Thread.currentThread().getName() + " 成功调用semaphore.release()");
	}

}

class Sender extends Thread {

	private Semaphore semaphore;

	public Sender(Semaphore semaphore, String name) {
		super(name);
		this.semaphore = semaphore;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + " 准备调用semaphore.acquire()");
		try {
			semaphore.acquire();
			System.out.println(Thread.currentThread().getName() + " 准备成功semaphore.acquire()");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + " 等待7秒");
		try {
			TimeUnit.SECONDS.sleep(7);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + " 准备调用semaphore.release()");
		semaphore.release();
		System.out.println(Thread.currentThread().getName() + " 成功调用semaphore.release()");
	}

}
