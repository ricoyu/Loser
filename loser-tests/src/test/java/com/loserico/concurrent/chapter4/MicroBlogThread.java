package com.loserico.concurrent.chapter4;

import java.util.concurrent.BlockingQueue;

public abstract class MicroBlogThread extends Thread {
	protected final BlockingQueue<Update> updatesQueue;
	protected String text = "";
	protected final int pauseTime;
	private boolean shutdown = false;

	public MicroBlogThread(BlockingQueue<Update> blockingQueue, int pause) {
		updatesQueue = blockingQueue;
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
