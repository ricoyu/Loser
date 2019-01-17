package com.loserico.concurrent.basic;

import static java.util.Arrays.sort;

class MyThread4 extends Thread {
	int[] a;

	MyThread4(int[] ar) {
		a = ar;
		start();
	}

	public void run() {
		sort(a);
	}
}

/**
 * The main thread has two unsorted arrays a and b. It wants to sort them and merge
 * them to form a third sorted array. However, main thread itself does not want to
 * sort them; instead it gets them sorted using two new threads. The main thread
 * simply merges them after getting them sorted. So, main thread creates two new
 * threads passing on array to be sorted to each and starts them. The two children
 * threads then concurrently sort their respective array. The task of the main
 * thread is to merge these two sorted arrays to form a third sorted one. However,
 * main thread can start merging if two children threads sort them and terminates.
 * So, it invokes their respective join() methods.
 * 
 * @author Loser
 * @since Aug 11, 2016
 * @version
 *
 */
public class JoinTest {
	public static void main(String args[]) throws Exception {
		int a[] = { 2, 3, 4, 0, 1 }, b[] = { 6, 9, 8, 7, 5 };
		MyThread4 t1 = new MyThread4(a);
		MyThread4 t2 = new MyThread4(b);
		t1.join();
		t2.join();
		int result[] = merge(a, b);
		for (int i = 0; i < result.length; i++)
			System.out.print(result[i] + " ");
	}

	static int[] merge(int[] a, int[] b) {
		int i = 0, j = 0, k = 0;
		int[] result = new int[a.length + b.length];
		while (i < a.length && j < b.length) {
			if (a[i] < b[j])
				result[k++] = a[i++];
			else
				result[k++] = b[j++];
		}
		while (i < a.length)
			result[k++] = a[i++];
		while (j < b.length)
			result[k++] = b[j++];
		return result;
	}
}