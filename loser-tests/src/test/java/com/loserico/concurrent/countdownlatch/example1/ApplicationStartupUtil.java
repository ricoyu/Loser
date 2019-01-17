package com.loserico.concurrent.countdownlatch.example1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ApplicationStartupUtil {
	//List of service checkers
	private static List<BaseHealthChecker> services;

	//This latch will be used to wait on
	private static CountDownLatch latch;

	private ApplicationStartupUtil() {
	}

	private final static ApplicationStartupUtil INSTANCE = new ApplicationStartupUtil();

	public static ApplicationStartupUtil getInstance() {
		return INSTANCE;
	}

	public static boolean checkExternalServices() throws Exception {
		//Initialize the latch with number of service checkers
		latch = new CountDownLatch(3);

		//All add checker in lists
		services = new ArrayList<BaseHealthChecker>();
		services.add(new NetworkHealthChecker(latch));
		services.add(new CacheHealthChecker(latch));
		services.add(new DatabaseHealthChecker(latch));

		//Start service checkers using executor framework
		Executor executor = Executors.newFixedThreadPool(services.size());

		for (BaseHealthChecker v : services) {
			executor.execute(v);
		}

		//Now wait till all services are checked
		latch.await();

		//Services are file and now proceed startup
		for (final BaseHealthChecker v : services) {
			if (!v.isServiceUp()) {
				return false;
			}
		}
		return true;
	}
}