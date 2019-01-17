package com.loserico.concurrent.basic;

import static java.util.concurrent.TimeUnit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StopThhread2中被同步方法的动作即使没有同步也是原子的。换句话说，这些方法的同步只是为了它的通信效果，而不是为了互斥访问。
 * 虽然循环中的每个迭代的同步开销很小，还是有其他更正确的替代方法，它更加简洁，性能也可能更好。如果stopRequested被声明为volatile，
 * StopThread2中的锁就可以省略。虽然volatile修饰符不执行互斥访问，但它保证任何一个线程在读取该域的时候都将看到刚刚被写入的值。
 * 
 * @author Loser
 * @since Aug 20, 2016
 * @version
 *
 */
public class StopThreadDemo3 {
	private static final Logger log = LoggerFactory.getLogger(StopThreadDemo.class);

	private static volatile boolean stopRequested = false;

	public static void main(String[] args) throws InterruptedException {
		Thread backgroundThread = new Thread(() -> {
			int i = 0;
			while (!stopRequested) {
				log.info(i + "");
				i++;
			}
			log.info("Stoped!");
		});
		backgroundThread.start();

		SECONDS.sleep(1);
		stopRequested = true;
		log.info("Stoped!");
	}
}