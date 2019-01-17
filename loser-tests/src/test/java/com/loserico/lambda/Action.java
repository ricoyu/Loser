package com.loserico.lambda;

@FunctionalInterface
public interface Action {

	public String aString();

	default void b() {
	};
}
