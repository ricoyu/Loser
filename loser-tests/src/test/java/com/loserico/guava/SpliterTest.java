package com.loserico.guava;

import static java.util.Optional.ofNullable;

import org.junit.Test;

import com.google.common.base.Splitter;

public class SpliterTest {

	@Test
	public void testSplitWithNull() {
		Splitter.on(",")
				.trimResults()
				.omitEmptyStrings()
				.splitToList(null);
	}
	
	@Test
	public void testSplitWithNull2() {
		String s = null;
		Splitter.on(",")
				.trimResults()
				.omitEmptyStrings()
				.splitToList(ofNullable(s).orElse(""));
	}
}
