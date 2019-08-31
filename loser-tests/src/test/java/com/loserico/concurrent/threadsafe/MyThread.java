package com.loserico.concurrent.threadsafe;

public class MyThread implements Runnable {

	private A a = null;
	private String name;
	
	@Override
	public void run() {
		for (int i = 0; i < 1000; i++) {
			a.aPlus();
			
			if (i == 999) {
				System.out.println("thread " + name +" is over");
			}
		}
	}

	public MyThread(A a, String name) {
		this.a = a;
		this.name = name;
	}
}
