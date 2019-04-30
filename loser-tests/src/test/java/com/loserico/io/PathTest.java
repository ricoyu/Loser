package com.loserico.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.loserico.io.utils.IOUtils;

public class PathTest {

	@Test
	public void testCreatePath() {
		Path path = Paths.get("/tmp/foo");
		Path p2 = Paths.get(URI.create("file:///Users/joe/FileTest.java"));
	}
	
	@Test
	public void testPathWithParent() throws IOException {
		String dir = "D:\\DeepdataSense\\JDSupplyChainPortal\\exportTemplates\\";
		Path parent = Paths.get(dir);
		Path path = IOUtils.tempFile("aa", "txt").toPath();
		Path child = parent.resolve(path.getFileName());
		System.out.println(child);
	}
}
