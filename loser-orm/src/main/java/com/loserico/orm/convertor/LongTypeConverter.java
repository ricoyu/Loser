package com.loserico.orm.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.loserico.orm.dynamic.MethodHandleable;

public class LongTypeConverter implements MethodHandleable {

	public Long convert(BigDecimal value) {
		return value.longValue();
	}

	public Long convert(BigInteger value) {
		return value.longValue();
	}

	public Long convert(Integer value) {
		return value.longValue();
	}
}
