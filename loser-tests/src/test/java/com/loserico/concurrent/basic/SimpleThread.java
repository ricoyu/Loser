package com.loserico.concurrent.basic;

public class SimpleThread extends Thread {
	public void run() {
		for (int i = 0; i < 4; i++)
			System.out.println("In MyThread: " + i);
	}

	public static void main(String args[]) {
		SimpleThread st = new SimpleThread();
		st.start();
		for (int i = 0; i < 6; i++)
			System.out.println("In main thread: " + i);
	}
}