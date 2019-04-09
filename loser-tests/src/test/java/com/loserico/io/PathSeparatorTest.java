package com.loserico.io;

import org.junit.Test;

public class PathSeparatorTest {

	@Test
	public void testSeparator() {
		String path = "/sishuok/master/server1";
		String[] paths = path.split("/");
		for (int i = 0; i < paths.length; i++) {
			String string = paths[i];
			System.out.println(string);
		}
	}
}
