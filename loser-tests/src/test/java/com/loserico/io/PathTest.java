package com.loserico.io;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class PathTest {

	@Test
	public void testCreatePath() {
		Path path = Paths.get("/tmp/foo");
		Path p2 = Paths.get(URI.create("file:///Users/joe/FileTest.java"));
	}
}
