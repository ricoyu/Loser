package com.loserico.concurrent.basic;

import java.util.Date;

public class SleepDemo {
	public static void main(String args[]) {
		for (;;) {
			System.out.println("Local date and time: " + new Date());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
			}
		}
	}
}