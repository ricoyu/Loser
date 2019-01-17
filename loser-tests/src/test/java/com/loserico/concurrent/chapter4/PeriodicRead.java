package com.loserico.concurrent.chapter4;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * In the example, an STPE wakes up a thread every 10 milliseconds and has it
 * attempt to poll() from a queue. If the read returns null (because the queue
 * is currently empty), nothing else happens and the thread goes back to sleep.
 * If a work unit was received, the thread prints out the contents of the work
 * unit.
 * 
 * @author Loser
 * @since Jul 13, 2016
 * @version
 *
 */
public class PeriodicRead {
	private ScheduledExecutorService scheduledExecutorService;
	private ScheduledFuture<?> scheduledFuture;
	private BlockingQueue<WorkUnit<String>> blockingQueue = new LinkedBlockingQueue<>();

	private void run() {
		scheduledExecutorService = Executors.newScheduledThreadPool(2);
		scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
			String nextMsg = blockingQueue.poll().getWork();
			if (nextMsg != null) {
				System.out.println("Msg recvd: " + nextMsg);
			}
		}, 10, 10, TimeUnit.MILLISECONDS);
	}

	public void cancel() {
		scheduledExecutorService.schedule(() -> scheduledFuture.cancel(true), 10, TimeUnit.MILLISECONDS);
	}
}
