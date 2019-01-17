package com.loserico.lambda;

import java.util.Comparator;

public class Lambda {

	public static void main(String[] args) {
		Runnable runnable = () -> System.out.println("lambda!");
		new Thread(runnable).start();

		Comparator<Integer> comparator = (x, y) -> (x > y) ? -1 : ((x == y ? 0 : 1));
		Comparator<Integer> comp = (x, y) -> {
			return (x > y) ? -1 : ((x == y ? 0 : 1));
		};
	}
}
