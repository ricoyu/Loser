package com.loserico.concurrent.basic;

import static java.util.concurrent.TimeUnit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 你可能期待这个程序运行大约一秒左右，之后主线程将stopRequested设为true，致使后台线程的循环终止。但是在我的机器上，这个程序永远不会停止：因为后台线程永远在循环！
 * 问题在于，由于没有同步，就不能保证后台线程何时“看到”主线程对stopRequested值所作的改变。没有同步，虚拟机将这个代码
 * 
 * <pre>
 * while(!done)
 * 	i++
 * </pre>
 * 
 * 转变成这样
 * 
 * <pre>
 * if (!done)
 * 	while (true)
 * 		i++;
 * </pre>
 * 
 * 这是可以接受的，这种优化称作提升hoisting，正式Hotspot Server VM的工作。结果是个活性失败(liveness
 * failure):这个程序无法前进。
 * 修正这个问题的一种方式是同步访问stopRequested域。
 * 
 * 注：在jdk1.8 X86和X64位 JVM上实测都没有问题，都可以正常结束
 * 
 * @author Loser
 * @since Aug 20, 2016
 * @version
 *
 */
public class StopThreadDemo {
	private static final Logger log = LoggerFactory.getLogger(StopThreadDemo.class);

	private static boolean stopRequested = false;

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
