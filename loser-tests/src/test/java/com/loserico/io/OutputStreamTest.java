package com.loserico.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

public class OutputStreamTest {

	/**
	 * 输出
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@Test
	public void testWriteOneByte() throws FileNotFoundException, IOException {
		byte[] bytes = "Hello OutputStream".getBytes();
	    try (OutputStream out = new FileOutputStream("hello.txt")) {
	        out.write(bytes[6]);
	    }
	}
}