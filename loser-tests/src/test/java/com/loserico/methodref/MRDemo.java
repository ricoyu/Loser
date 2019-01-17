package com.loserico.methodref;

import java.util.Arrays;
import java.util.function.Consumer;

public class MRDemo {
	public static void main(String[] args) {
		int[] array = { 10, 2, 19, 5, 17 };
		/*
		 * public static void sort(int[] a) Consumer代表了接收一个参数，不返回值的操作
		 * Represents an operation that accepts a single input argument and
		 * returns no result.
		 * 静态方法Arrays.sort虽然是一个普通的方法，Arrays也包含了很多其他方法，但是sort接收一个参数，并且不返回值
		 * 所以sort方法就可以赋值给Consumer
		 */
		Consumer<int[]> consumer = Arrays::sort;
		// 在给定参数上执行相应操作，即调用sort(array)
		consumer.accept(array);
		for (int x : array) {
			System.out.println(x);
		}
		System.out.println();

		int[] array2 = { 19, 5, 14, 3, 21, 4 };
		Consumer<int[]> consumer2 = (a) -> Arrays.sort(a);
		consumer2.accept(array2);
		for (int x : array2) {
			System.out.println(x);
		}
	}
}