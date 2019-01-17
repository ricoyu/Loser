package com.loserico.commons.utils;

import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.stream.Stream;

public final class Sets {

	@SuppressWarnings("unchecked")
	public static final <E> Set<E> asSet(E... args) {
		return Stream.of(args).collect(toSet());
	}
}
