package com.loserico.concurrent.basic;

/**
 * Deadlock is a situation where two or more threads wait for each other
 * indefinitely.
 * 
 * We know that the synchronized keyword may cause the executing thread to block
 * while trying to acquire the lock on the specified object. Since, the thread might
 * already hold locks associated with other objects, two or more threads could each
 * be waiting for one another to release a lock. As a result, they will end up
 * waiting forever. Specifically, a deadlock may occur when multiple threads need
 * the same locks but obtain them in different order.
 * 
 * Here, main thread creates two string objects r1 and r2 and are passed to the
 * MyThread’s constructor in opposite order to create two threads t1 and t2. The
 * thread t1 tries to acquire locks on r1 and r2 in this order whereas t2 wants to
 * acquire locks on the same object but in opposite order. This results in a
 * circular wait and hence deadlock.
 * 
 * <pre>
 * Mutual Exclusion	互斥条件
 * 		指进程对所分配到的资源进行排它性使用，即在一段时间内某资源只由一个进程占用。如果此时还有其它进程请求资源，则请求者只能等待，直至占有资源的进程用毕释放。
 * Hold and Wait	请求和保持条件
 * 		指进程已经保持至少一个资源，但又提出了新的资源请求，而该资源已被其它进程占有，此时请求进程阻塞，但又对自己已获得的其它资源保持不放。
 * No-preemption	不剥夺条件
 * 		指进程已获得的资源，在未使用完之前，不能被剥夺，只能在使用完时由自己释放。
 * Circular Wait	环路等待条件
 * 		指在发生死锁时，必然存在一个进程——资源的环形链，即进程集合{P0，P1，P2，···，Pn}中的P0正在等待一个P1占用的资源；
 * 		P1正在等待P2占用的资源，……，Pn正在等待已被P0占用的资源。
 * </pre>
 * 
 * Note that a deadlock always occurs if the above program is run. In practice,
 * deadlocks may not occur all the time, and instead occur for certain data sets
 * which are often difficult to predict. So, testing for deadlocks is difficult, as
 * deadlocks depend on timing, load, and environment, and thus might happen
 * infrequently or only under certain circumstances. This warns us that extreme care
 * should be taken when writing synchronized multi-threaded programs.
 * 
 * One way to prevent deadlock is to make sure that all locks are always taken in
 * the same order by any thread.
 * 
 * For the following two threads, deadlocks cannot occur.
 * 
 * <pre>
 * MyThread t1 = new MyThread(1, r1, r2);
 * MyThread t2 = new MyThread(2, r1, r2);
 * </pre>
 * 
 * Here, both threads obtain locks on r1 and r2 in the same order; hence deadlock
 * can never occur. Lock ordering is a simple but effective way of preventing
 * deadlock. However, it may only be useful if we have complete knowledge of all the
 * locks at compile time. Moreover there may be situations, where lock ordering is
 * not possible.
 * 
 * The following are some conditions where there may be a chance of deadlock:
 * 
 * <pre>
 * • The code contains any nested synchronized block 
 * • A synchronized method calls another synchronized method 
 * • The code obtains locks on different objects
 * </pre>
 * 
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
class MyThread5 extends Thread {
	String r1, r2;
	int id;

	MyThread5(int i, String s1, String s2) {
		id = i;
		r1 = s1;
		r2 = s2;
		start();
	}

	public void run() {
		synchronized (r1) {
			System.out.println("Thread " + id + " obtained a lock on " + r1);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			System.out.println("Thread " + id + " is waiting to obtain a lock on " + r2);
			synchronized (r2) {
				System.out.println("Thread " + id + " obtained a lock on " + r2);
			}
		}
	}

	/*
	 * Another way to prevent deadlock requires threads to acquire all the locks
	 * that are needed during execution before proceeding.
	 * 
	 * This makes use of an additional lock which controls acquisition of two locks
	 * in a non-overlapped way. Since, a thread will either acquire all locks or
	 * none, there is no circular wait which implies no deadlock. However, since a
	 * thread may hold locks, required for short time, during the thread’s entire
	 * execution period (possibly very long), the effective utilization of the locks
	 * may be low. In practice, deadlock prevention or avoidance or detection
	 * requires special algorithms, many of which can be found in a standard book on
	 * operating system. Unfortunately, there is no best and foolproof algorithm for
	 * these purposes. So, a little care of the program design may help us getting
	 * out of the deadlock situation. So how will you know that there are chances of
	 * deadlock.
	 * 
	 * @see java.lang.Thread#run()
	 */
	/*
	 * public void run() { synchronized (MyThread5.class) { synchronized (r1) {
	 * System.out.println("Thread " + id + " obtained a lock on " + r1); } try {
	 * Thread.sleep(1000); } catch (Exception e) { } System.out.println("Thread " +
	 * id + " is waiting to obtain a lock on " + r2); synchronized (r2) {
	 * System.out.println("Thread " + id + " obtained a lock on " + r2); } } }
	 */
}

public class DeadlockDemo {
	public static void main(String args[]) throws Exception {
		int a[] = { 2, 6, 4, 0, 1, 5, 3 };
		String r1 = new String("R1"), r2 = new String("R2");
		MyThread5 t1 = new MyThread5(1, r1, r2);
		MyThread5 t2 = new MyThread5(2, r2, r1);
	}
}