package com.loserico.lambda;

import java.math.BigDecimal;

@FunctionalInterface
public interface BigDecimalFunction<R> {

	R apply(BigDecimal value);
}
