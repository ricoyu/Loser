package com.loserico.concurrent.basic;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 修正这个问题的一种方式是同步访问stopRequested域。
 * 注意写方法requestStop()和读方法stopRequested()都被同步了，只同步写方法还不够！
 * 实际上，如果读和写操作没有都被同步，同步就不会起作用。
 * 
 * @author Loser
 * @since 2016年8月20日
 * @version
 *
 */
public class StopThreadDemo2 {
	private static final Logger log = LoggerFactory.getLogger(StopThreadDemo.class);

	private static boolean stopRequested = false;

	private static synchronized void requestStop() {
		stopRequested = true;
	}

	private static synchronized boolean stopRequested() {
		return stopRequested;
	}

	public static void main(String[] args) throws InterruptedException {
		Thread backgroundThread = new Thread(new Runnable() {
			public void run() {
				int i = 0;
				while (!stopRequested()) {
					log.info(i + "");
					i++;
				}
				log.info("Stoped!");
			}
		});
		backgroundThread.start();

		SECONDS.sleep(1);
		requestStop();
		log.info("Stoped!");
	}
}