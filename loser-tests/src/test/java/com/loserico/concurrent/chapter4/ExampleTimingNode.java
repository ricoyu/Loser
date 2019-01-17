package com.loserico.concurrent.chapter4;

import java.util.HashMap;
import java.util.Map;

/**
 * The ExampleTimingNode class will receive updates by having its
 * propagateUpdate() method called and can also be queried to see if it has
 * received a specific update. This situation provides a classic conflict
 * between a read and a write operation, so synchronization is used to prevent
 * inconsistency.
 * 
 * This seems fantastic at first glance—the class is both safe and live. The
 * problem comes with performance—just because something is safe and live
 * doesn’t mean it’s necessarily going to be very quick. You have to use
 * synchronized to coordinate all the accesses (both get and put) to the
 * arrivalTime map, and that locking is ultimately going to slow you down. This
 * is a central problem of this way of handling concurrency.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class ExampleTimingNode implements SimpleMicroBlogNode {

	// No public fields
	private final String identifier;

	private final Map<Update, Long> arrivalTime = new HashMap<>();

	// All fields initialized in constructor
	public ExampleTimingNode(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * All methods are synchronized
	 */
	@Override
	public synchronized void propagateUpdate(Update update, SimpleMicroBlogNode backup) {
		long currentTime = System.currentTimeMillis();
		arrivalTime.put(update, currentTime);
	}

	/**
	 * All methods are synchronized
	 */
	@Override
	public synchronized void confirmUpdate(SimpleMicroBlogNode other, Update update) {
		Long timeRecvd = arrivalTime.get(update);
		System.out.println("Recvd confirm: " + update.getUpdateText() + " from " + other.getIdent());
	}

	/**
	 * All methods are synchronized
	 */
	@Override
	public synchronized String getIdent() {
		return identifier;
	}

}