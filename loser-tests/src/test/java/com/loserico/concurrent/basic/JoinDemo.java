package com.loserico.concurrent.basic;

import static java.util.Arrays.sort;

/**
 * Multiple threads run concurrently; one does not wait for the other. However,
 * sometimes it is necessary that a thread should not proceed further until another
 * thread finishes its task. For example, consider a simple program having two
 * threads; one of which sorts an array and the other prints the sorted array.
 * Obviously, the print thread must not start printing until sort thread sorts the
 * array. This type of dependency can be achieved using join() method. It makes the
 * caller blocked until the called thread dies.
 * 
 * Here, child thread sorts the array and main thread prints it. The main thread
 * creates a child thread passing an array to be sorted. It then starts the child
 * thread. Since, child thread may take some time to sort the array, main thread
 * must not proceed for printing immediately. That’s why it calls child thread’s
 * join() method that makes the main thread waiting. The join() returns when child
 * terminates after sorting the array. The main thread can then be sure that the
 * array is sorted and can safely print the array.
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
class MyThread3 extends Thread {
	int[] a;

	MyThread3(int[] ar) {
		a = ar;
		start();
	}

	public void run() {
		sort(a);
		System.out.println("Child completed sorting.");
	}
}

public class JoinDemo {
	public static void main(String args[]) throws Exception {
		int a[] = { 2, 6, 4, 0, 1, 5, 3 };
		MyThread3 t = new MyThread3(a);
		t.join();
		System.out.println("Main printing array elements are :");
		for (int i = 0; i < a.length; i++)
			System.out.print(a[i] + " ");
	}
}