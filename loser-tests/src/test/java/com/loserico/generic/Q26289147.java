package com.loserico.generic;

import com.google.common.reflect.TypeToken;

public class Q26289147 {
	public static void main(final String[] args) throws IllegalAccessException, InstantiationException {
		final StrawManParameterizedClass<String> smpc = new StrawManParameterizedClass<String>() {
		};
		final String string = (String) smpc.type.getRawType().newInstance();
		System.out.format("string = \"%s\"", string);
	}

	static abstract class StrawManParameterizedClass<T> {
		final TypeToken<T> type = new TypeToken<T>(getClass()) {
		};
	}
}