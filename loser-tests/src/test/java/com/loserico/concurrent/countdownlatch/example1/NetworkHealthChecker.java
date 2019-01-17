package com.loserico.concurrent.countdownlatch.example1;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.CountDownLatch;

public class NetworkHealthChecker extends BaseHealthChecker {
	public NetworkHealthChecker(CountDownLatch latch) {
		super("Network Service", latch);
	}

	@Override
	public void verifyService() {
		System.out.println("Checking " + this.getServiceName());
		try {
			MILLISECONDS.sleep(7000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(this.getServiceName() + " is UP");
	}
}