package com.loserico.functional;

import java.math.BigDecimal;

import org.junit.Test;

import com.loserico.lambda.BigDecimalFunction;

public class FunctionTest {

	@Test
	public void testFunction() {
		BigDecimalFunction<Integer> bigDecimalFunction =  value -> value.intValue();
		Integer result = bigDecimalFunction.apply(new BigDecimal(123));
		System.out.println(result);
	}
}
