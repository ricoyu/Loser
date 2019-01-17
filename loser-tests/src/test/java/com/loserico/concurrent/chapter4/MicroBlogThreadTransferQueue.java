package com.loserico.concurrent.chapter4;

import java.util.concurrent.TransferQueue;

public abstract class MicroBlogThreadTransferQueue extends Thread {
	protected final TransferQueue<Update> updatesQueue;
	protected String text = "";
	protected final int pauseTime;
	private boolean shutdown = false;

	public MicroBlogThreadTransferQueue(TransferQueue<Update> transferQueue, int pause) {
		updatesQueue = transferQueue;
		pauseTime = pause;
	}

	public synchronized void shutdown() {
		shutdown = true;
	}

	@Override
	public void run() {
		while (!shutdown) {
			doAction();
			try {
				Thread.sleep(pauseTime);
			} catch (InterruptedException e) {
				shutdown = true;
			}
		}
	}

	public abstract void doAction();
}
