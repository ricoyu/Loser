package com.loserico.concurrent.threadsafe;

public class ThreadUnsafeTest {

	public static void main(String[] args) throws InterruptedException {
		A a = new A();
		
		Thread t1 = new Thread(new MyThread(a, "t1111"));
		Thread t2 = new Thread(new MyThread(a, "t2222"));
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
		
		System.out.println("aa==" + a.getA());
	}
}
