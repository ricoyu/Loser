package com.loserico.lambda;

import java.util.function.IntPredicate;

public class Lambda1 {

	public static void main(String[] args) {
		IntPredicate intPredicate = n -> n > 2;
		System.out.println(intPredicate.test(3));
		System.out.println(intPredicate.test(2));
		System.out.println(intPredicate.test(1));
	}
}
