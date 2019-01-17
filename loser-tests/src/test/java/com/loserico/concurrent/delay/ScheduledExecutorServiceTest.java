package com.loserico.concurrent.delay;

import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The ScheduledExecutorService interface defines convenient methods for scheduling tasks:<ul>
 * <li><pre>{@code schedule(Callable<V> callable, long delay, TimeUnit unit)}</pre> Executes a Callable task after the specified delay.
 * <li><pre>{@code schedule(Runnable command, long delay, TimeUnit unit)}</pre>  	Executes a Runnable task after a given delay.
 * <li><pre>{@code scheduleAtFixedRate(Runnable command, long initialDelay, long delay, TimeUnit unit)}</pre> 
 * 		Executes a periodic task after an initial delay, then repeat after every given period. 
 * 		If any execution of this task takes longer than its period, then subsequent executions may start late, but will not concurrently execute.
 * <li><pre>{@code scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)}</pre> 
 * 		Executes a periodic task after an initial delay, then repeat after every given delay between the termination of one execution and the commencement of the next.
 * <p>
 * Copyright: Copyright (c) 2018-05-04 10:16
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class ScheduledExecutorServiceTest {

	public static void main(String[] args) throws InterruptedException {
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
		System.out.println("begin");
		executorService.schedule(() -> System.out.println("done"), 3, SECONDS);
		Thread.currentThread().join();
	}
}
