package com.loserico.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;

import org.junit.Test;

/**
 * @formatter:off
 * 
 * Reader declares {@code read(char[])} and {@code read(char[], int, int)} methods instead of
 * {@code read(byte[])} and {@code read(byte[], int, int)} methods. 
 * 
 * Reader doesn’t declare an {@code available()} method. 
 * Reader declares a boolean {@code ready()} method that returns true when the next read() call is guaranteed not to block for input.  
 * Reader declares an int {@code read(CharBuffer target)} method for reading characters from a character buffer.
 * @formatter:on
 * @author Rico Yu
 * @since 2016-11-28 17:32
 * @version 1.0
 *
 */
public class ReaderTest {

	/**
	 * The concrete InputStreamReader class (a Reader subclass) is a bridge between an incoming
	 * stream of bytes and an outgoing sequence of characters. Characters read from this reader
	 * are decoded from bytes according to the default or specified character encoding.
	 * 
	 * InputStreamReader(InputStream in)
	 * InputStreamReader(InputStream in, String charsetName)
	 * @throws IOException 
	 */
	@Test
	public void testInputStreamReader() throws IOException {
		FileInputStream fis = new FileInputStream("polish.txt");
		InputStreamReader reader = new InputStreamReader(fis, "8859_2");
	}
}
