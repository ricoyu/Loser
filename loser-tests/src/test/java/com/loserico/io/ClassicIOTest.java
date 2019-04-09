package com.loserico.io;

import java.io.File;

import org.junit.Test;

/**
 * 
 * <p>
 * Copyright: Copyright (c) 2019-03-21 13:17
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class ClassicIOTest {

	/*
	 * The File class is the cornerstone of Java’s original way to do file I/O. This abstraction can
	 * represent both files and directories, but in doing so is sometimes a bit cumbersome to deal
	 * with
	 */
	@Test
	public void testFile() {
		// Get a file object to represent the user's home directory
		File homeDir = new File(System.getProperty("user.home"));
		
		// Create an object to represent a config file (should
		// already be present in the home directory)
		File file = new File(homeDir, "app.conf");
		
		// Check the file exists, really is a file & is readable
		if (file.exists() && file.isFile() && file.canRead()) {
			// Create a file object for a new configuration directory
			File configDir = new File(file, ".configdir");
			// And create it
			configDir.mkdir();
		}
		
		System.out.println("can read: " + file.canRead());
		System.out.println("can write: " + file.canWrite());
		System.out.println("can execute: " + file.canExecute());
	}
}
