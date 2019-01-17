package com.loserico.lambda;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class LambdaTest {

	@Test
	public void testLambda1() {
//		Arrays.asList("a", "b", "d").forEach((x) -> System.out.println(x));
		Arrays.asList("a", "b", "d").forEach(x -> {
			System.out.println(x);
			System.out.println(x);
		});
	}
	
	@Test
	public void testLambdaExpression() {
		 List<String> stringList = Arrays.asList("String1","String2","String3");
		 stringList.forEach(System.out::println);
	}
}
