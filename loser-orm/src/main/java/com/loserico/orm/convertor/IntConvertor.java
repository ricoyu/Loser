package com.loserico.orm.convertor;

import java.math.BigDecimal;

public class IntConvertor {

	public static int convert(BigDecimal value) {
		if (value == null) {
			return 0;
		}
		return value.intValue();
	}

	public static int convert(Long value) {
		if (value == null) {
			return 0;
		}
		return value.intValue();
	}
	
	public static int convert(Integer value) {
		if (value == null) {
			return 0;
		}
		return value.intValue();
	}

}
