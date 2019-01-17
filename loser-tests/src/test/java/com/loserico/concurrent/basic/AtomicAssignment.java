package com.loserico.concurrent.basic;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * The Java language provides some basic operations that are atomic and that
 * therefore can be used to make sure that concurrent threads always see the same
 * value:<br/>
 * 
 * <pre>
 * Read and write operations to reference variables and primitive variables (except long and double) 
 * Read and write operations for all variables declared as volatile
 * </pre>
 * 
 * let’s assume we have a HashMap filled with properties that are read from a file
 * and a bunch of threads that work with these properties. It is clear that we need
 * some kind of synchronization here, as the process of reading the file and update
 * the Map costs time and that during this time other threads are executed.
 * 
 * We cannot easily share one instance of this Map between all threads and work on
 * this Map during the update process. This would lead to an inconsistent state of
 * the Map, which is read by the accessing threads. With the knowledge from the last
 * section we could of course use a synchronized block around each access
 * (read/write) of the map to ensure that the all threads only see one state and not
 * a partially updated Map. But this leads to performance problems if the concurrent
 * threads have to read very often from the Map.
 * 
 * Since we know that write operations to a reference are atomic, we can create a
 * new Map each time we read the file and update the reference that is shared
 * between the threads in one atomic operation. In this implementation the worker
 * threads will never read an inconsistent Map as the Map is updated with one atomic
 * operation:
 * 
 * @author Loser
 * @since Aug 20, 2016
 * @version
 *
 */
public class AtomicAssignment implements Runnable {
	private static volatile Map<String, String> configuration = new HashMap<String, String>();

	public void run() {
		for (int i = 0; i < 100; i++) {
			Map<String, String> currConfig = configuration;
			String value1 = currConfig.get("key-1");
			String value2 = currConfig.get("key-2");
			String value3 = currConfig.get("key-3");
			/*
			 * if (!(value1.equals(value2) && value2.equals(value3))) { throw new
			 * IllegalStateException("Values are not equal."); }
			 */
			assertEquals(value1, value2);
			assertEquals(value3, value2);
			try {
				MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void readConfig() {
		Map<String, String> newConfig = new HashMap<String, String>();
		String currentTime = now().format(ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		newConfig.put("key-1", currentTime);
		newConfig.put("key-2", currentTime);
		newConfig.put("key-3", currentTime);
		configuration = newConfig;
	}

	/**
	 * The above example is a little more complex, but not hard to understand. The
	 * Map, which is shared, is the configuration variable of AtomicAssignment. In
	 * the main() method we read the configuration initially one time and add three
	 * keys to the Map with the same value (here the current time including
	 * milliseconds). Then we start a “configuration-thread” that simulates the
	 * reading of the configuration by adding all the time the current timestamp
	 * three times to the map. The five worker threads then read the Map using the
	 * configuration variable and compare the three values. If they are not equal,
	 * they throw an IllegalStateException.
	 * 
	 * You can run the program for some time and you will not see any
	 * IllegalStateException. This is due the fact that we assign the new Map to the
	 * shared configuration variable in one atomic operation:
	 * 
	 * <pre>
	 * configuration = newConfig;
	 * </pre>
	 * 
	 * We also read the value of the shared variable within one atomic step:
	 * 
	 * <pre>
	 * Map<String, String> currConfig = configuration;
	 * </pre>
	 * 
	 * As both steps are atomic, we will always get a reference to a valid Map
	 * instance where all three values are equal. If you change for example the
	 * run() method in a way that it uses the configuration variable directly
	 * instead of copying it first to a local variable, you will see
	 * IllegalStateExceptions very soon because the configuration variable always
	 * points to the “current” configuration. When it has been changed by the
	 * configuration-thread, subsequent read accesses to the Map will already read
	 * the new values and compare them with the values from the old map.
	 * 
	 * The same is true if you work in the readConfig() method directly on the
	 * configuration variable instead of creating a new Map and assigning it in one
	 * atomic operation to the shared variable. But it may take some time, until you
	 * see the first IllegalStateException. And this is true for all applications
	 * that use multi-threading. Concurrency problems are not always visible at
	 * first glance, but they need some testing under heavy-load conditions in order
	 * to appear.
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		readConfig();
		Thread configThread = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < 100; i++) {
					readConfig();
					try {
						MILLISECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, "configuration-thread");
		configThread.start();
		Thread[] threads = new Thread[5];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new AtomicAssignment(), "thread-" + i);
			threads[i].start();
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		configThread.join();
		System.out.println("[" + Thread.currentThread().getName() + "] All threads have finished.");
	}
}