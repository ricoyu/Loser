package com.loserico.concurrent.basic;

/**
 * Consider a simple program having two threads as follows: The child thread finds
 * (produces) prime numbers and stores them in a variable and main thread prints
 * them. This is a specific example of class producer-consumer problem. Although,
 * this example is not much useful, it helps us in understanding how to write
 * multi-threaded programs where threads communicate with one another.
 * 
 * One simple but elegant solution is to use a variable shared by these two threads.
 * The child thread stores a new prime number here and main thread prints it. The
 * solution must satisfy the following basic requirements:
 * 
 * <pre>
 * • Main thread must not print the same prime number more than once i.e. after printing a number, main thread must 
 * wait for the child thread to generate a new one. 
 * • Similarly, the child thread must not generate a new prime number before the main thread prints the previous one.
 * </pre>
 * 
 * This tells us that a high degree of cooperation is needed between these two
 * threads. A potential solution is shown below:
 * 
 * Both main and child thread use a shared one element array to read and write prime
 * numbers. The coordination is achieved using another shared variable turn which
 * determines a thread’s turn. It is child’s turn if the value of turn is 0, else it
 * is main thread’s turn. A thread waits by spinning in a while loop until it gets
 * its own turn. This means a thread consumes CPU cycles even if it is not its turn.
 * However, ideally, if a thread finds that it is the other thread’s turn, it should
 * wait without consuming any CPU cycles further. So the above solution is
 * inefficient.
 * 
 * Fortunately, to avoid unnecessary polling, Java provides a framework that uses
 * wait(), notify() and notifyAll() methods. These methods are implemented in
 * java.lang.Object, hence is available in all Java objects. To write an
 * inter-thread communication solution, let us understand the functionality of these
 * methods
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
public class ThreadCommSpin extends Thread {
	static int turn = 0;
	int buf[], n = 2;

	public int nextPrime() {
		while (true) {
			boolean prime = true;
			for (int i = 2; i <= n / 2; i++)
				if (n % i == 0) {
					prime = false;
					break;
				}
			if (prime) {
				return n++;
			} else {
				n++;
			}
		}
	}

	public ThreadCommSpin(int[] a) {
		buf = a;
		start();
	}

	public void run() {
		while (true) {
			while (turn != 0) {

			}
			buf[0] = nextPrime();
			turn = 1;
		}
	}

	public static void main(String args[]) throws Exception {
		int[] a = new int[1];
		ThreadCommSpin st = new ThreadCommSpin(a);
		while (true) {
			while (turn != 1) {
			}
			System.out.print(a[0] + " ");
			turn = 0;
		}
	}
}