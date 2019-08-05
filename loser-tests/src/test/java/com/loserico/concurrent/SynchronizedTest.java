package com.loserico.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SynchronizedTest {

	private String name = "a";
	
	public synchronized void name() {
		System.out.println("rico");
	}
	
	public void foo() {
		synchronized (this.name) {
			System.out.println("bar");
		}
	}
	
	public static void main(String[] args) {
		SynchronizedTest instance = new SynchronizedTest();
		ExecutorService executors = Executors.newFixedThreadPool(2);
		executors.execute(() -> {
			instance.name();
		});
		
		instance.foo();
	}
}
