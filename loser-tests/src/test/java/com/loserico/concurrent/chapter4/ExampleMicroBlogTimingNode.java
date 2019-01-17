package com.loserico.concurrent.chapter4;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * As an example, you can replace the synchronized methods in ExampleTimingNode with
 * regular, unsynchronized access if you alter the HashMap called arrivalTime
 * to be a Concurrent- HashMap as well. Notice the lack of locks in the
 * following listing—there is no explicit synchronization at all.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class ExampleMicroBlogTimingNode implements SimpleMicroBlogNode {

	// No public fields
	private final String identifier;

	private final Map<Update, Long> arrivalTime = new ConcurrentHashMap<>();

	// All fields initialized in constructor
	public ExampleMicroBlogTimingNode(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public void propagateUpdate(Update update, SimpleMicroBlogNode backup) {
		long currentTime = System.currentTimeMillis();
		arrivalTime.put(update, currentTime);
	}

	@Override
	public void confirmUpdate(SimpleMicroBlogNode other, Update update) {
		Long timeRecvd = arrivalTime.get(update);
		System.out.println("Recvd confirm: " + update.getUpdateText() + " from " + other.getIdent());
	}

	@Override
	public String getIdent() {
		return identifier;
	}
}
