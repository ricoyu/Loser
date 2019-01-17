package com.loserico.methodref;

import java.util.function.Function;

import org.junit.Test;

public class FunctionTest {

	@Test
	public void testComposeAndThen() {
		Function<Integer, Integer> times2 = e -> e * 2;
		Function<Integer, Integer> squared = e -> e * e;  
		
		int result = times2.compose(squared).apply(4);  
		System.out.println(result);
		// Returns 32

		int result2 = times2.andThen(squared).apply(4);  
		// Returns 64
		System.out.println(result2);
	}
}
