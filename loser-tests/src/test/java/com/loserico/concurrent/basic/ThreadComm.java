package com.loserico.concurrent.basic;

/**
 * Here, since the initial value of turn is 0, the main thread waits until child
 * thread generates a prime number and notifies the main thread. Similarly, after
 * generating a prime number, the child thread waits until main threads print it and
 * notifies the child thread.
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
public class ThreadComm extends Thread {
	static int turn = 0;
	int buf[], n = 2;

	public int nextPrime() {
		while (true) {
			boolean prime = true;
			for (int i = 2; i <= n / 2; i++) {
				if (n % i == 0) {
					prime = false;
					break;
				}
			}
			if (prime) {
				return n++;
			} else {
				n++;
			}
		}
	}

	public ThreadComm(int[] a) {
		buf = a;
		start();
	}

	public void run() {
		while (true) {
			synchronized (buf) {
				while (turn != 0) {
					try {
						buf.wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				buf[0] = nextPrime();
				turn = 1;
				buf.notify();
			}
		}
	}

	public static void main(String args[]) throws Exception {
		int[] a = new int[1];
		ThreadComm st = new ThreadComm(a);
		while (true) {
			synchronized (a) {
				while (turn != 1) {
					a.wait();
				}
				System.out.print(a[0] + " ");
				turn = 0;
				a.notify();
			}
		}
	}
}