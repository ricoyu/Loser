package com.loserico.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

/**
 * a byte represents an 8-bit data item and a character represents a 16-bit data item. Also,
 * Java’s char and java.lang. String types naturally handle characters instead of bytes.
 * 
 * More importantly, byte streams have no knowledge of character sets (sets of mappings between
 * integer values, known as code points, and symbols, such as Unicode) and their character
 * encodings (mappings between the members of a character set and sequences of bytes that encode
 * these characters for efficiency, such as UTF-8).
 * 
 * Writer declares several <code>append()</code> methods for appending characters to this
 * writer. These methods exist because Writer implements the {@code java.lang.Appendable}
 * interface, which is used in partnership with the {@code java.util.Formatter} class to output
 * formatted strings.
 * 
 * Writer declares additional write() methods, including a convenient void write(String str)
 * method for writing a String object’s characters to this writer.
 * 
 * @author Rico Yu
 * @since 2016-11-28 17:18
 * @version 1.0
 *
 */
public class WriterTest {

	/**
	 * The concrete OutputStreamWriter class (a Writer subclass) is a bridge between an incoming
	 * sequence of characters and an outgoing stream of bytes. Characters written to this writer
	 * are encoded into bytes according to the default or specified character encoding.
	 * @throws IOException 
	 * @formatter:off
	 * <pre>OutputStreamWriter(OutputStream out)</pre>
	 * <pre>OutputStreamWriter(OutputStream out, String charsetName)</pre>
	 */
	@Test
	public void testOutputStreamWriter() throws IOException {
		FileOutputStream fos = new FileOutputStream("polish.txt");
		OutputStreamWriter writer = new OutputStreamWriter(fos, "8859_2");
		char ch = '\u0323'; // Accented N.
		writer.write(ch);
		writer.flush();
		writer.close();
	}
}
