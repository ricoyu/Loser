package com.loserico.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

import org.junit.Test;

public class IOStreamTest {

	/*
	 * Byte Streams handle I/O of raw binary data.
	 * FileInputStream/FileOutputStream 是基于字节的Stream, 都继承自InputStream/OutputStream
	 * 
	 * Programs use byte streams to perform input and output of 8-bit bytes. All byte stream classes
	 * are descended from InputStream and OutputStream.
	 * 
	 * There are many byte stream classes. To demonstrate how byte streams work, we'll focus on the
	 * file I/O byte streams, FileInputStream and FileOutputStream. Other kinds of byte streams are
	 * used in much the same way; they differ mainly in the way they are constructed.
	 * 
	 * We'll explore FileInputStream and FileOutputStream by examining an example program named
	 * CopyBytes, which uses byte streams to copy xanadu.txt, one byte at a time.
	 * 
	 * Always Close Streams
	 * 
	 * Closing a stream when it's no longer needed is very important — so important that CopyBytes
	 * uses a finally block to guarantee that both streams will be closed even if an error occurs.
	 * This practice helps avoid serious resource leaks.
	 * 
	 * When Not to Use Byte Streams
	 * 
	 * CopyBytes seems like a normal program, but it actually represents a kind of low-level I/O
	 * that you should avoid. Since xanadu.txt contains character data, the best approach is to use
	 * character streams, as discussed in the next section. There are also streams for more
	 * complicated data types. Byte streams should only be used for the most primitive I/O.
	 * @on
	 */
	@Test
	public void testCopyBytes() throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			in = new FileInputStream("xanadu.txt");
			out = new FileOutputStream("outagain.txt");

			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	/*
	 * 字符流都继承自Reader/Writer
	 * 
	 * All character stream classes are descended from Reader and Writer. As with byte streams,
	 * there are character stream classes that specialize in file I/O: FileReader and FileWriter.
	 * 
	 * The Java platform stores character values using Unicode conventions. Character stream I/O
	 * automatically translates this internal format to and from the local character set. In Western
	 * locales, the local character set is usually an 8-bit superset of ASCII.
	 * 
	 * For most applications, I/O with character streams is no more complicated than I/O with byte
	 * streams. Input and output done with stream classes automatically translates to and from the
	 * local character set. A program that uses character streams in place of byte streams
	 * automatically adapts to the local character set and is ready for internationalization — all
	 * without extra effort by the programmer.
	 * 
	 * If internationalization isn't a priority, you can simply use the character stream classes
	 * without paying much attention to character set issues. Later, if internationalization becomes
	 * a priority, your program can be adapted without extensive recoding. See the
	 * Internationalization trail for more information.
	 * @on
	 */
	@Test
	public void testCopyCharacters() throws IOException {
		FileReader fileReader = null;
		FileWriter fileWriter = null;
		try {
			fileReader = new FileReader("xanadu.txt");
			fileWriter = new FileWriter("characteroutput.txt");
			int c;
			while ((c = fileReader.read()) != -1) {
				fileWriter.write(c);
			}
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
			if (fileWriter != null) {
				fileWriter.close();
			}
		}
	}

	/*
	 * 小结:
	 * 
	 * CopyCharacters is very similar to CopyBytes.
	 * 
	 * The most important difference is that CopyCharacters uses FileReader and FileWriter for input
	 * and output in place of FileInputStream and FileOutputStream.
	 * 
	 * Notice that both CopyBytes and CopyCharacters use an int variable to read to and write from.
	 * However, in CopyCharacters, the int variable holds a character value in its last 16 bits; in
	 * CopyBytes, the int variable holds a byte value in its last 8 bits.
	 * 
	 * Character Streams that Use Byte Streams
	 * 
	 * Character streams are often "wrappers" for byte streams. The character stream uses the byte
	 * stream to perform the physical I/O, while the character stream handles translation between
	 * characters and bytes. FileReader, for example, uses FileInputStream, while FileWriter uses
	 * FileOutputStream.
	 * 
	 * There are two general-purpose byte-to-character "bridge" streams: InputStreamReader and
	 * OutputStreamWriter. Use them to create character streams when there are no prepackaged
	 * character stream classes that meet your needs.
	 */

	// -====================================================================================================

	/*
	 * Line-Oriented I/O
	 * 
	 * Character I/O usually occurs in bigger units than single characters. One common unit is the
	 * line: a string of characters with a line terminator at the end. A line terminator can be a
	 * carriage-return/line-feed sequence ("\r\n"), a single carriage-return ("\r"), or a single
	 * line-feed ("\n"). Supporting all possible line terminators allows programs to read text files
	 * created on any of the widely used operating systems.
	 */
	@Test
	public void testCopyLines() throws IOException {
		BufferedReader bufferedReader = null;
		PrintWriter printWriter = null;

		try {
			bufferedReader = new BufferedReader(new FileReader("xanadu.txt"));
			printWriter = new PrintWriter(new FileWriter("characteroutput.txt"));
			/*
			 * Invoking readLine returns a line of text with the line. CopyLines outputs each line using
			 * println, which appends the line terminator for the current operating system. This might not
			 * be the same line terminator that was used in the input file.
			 */
			String l;
			while ((l = bufferedReader.readLine()) != null) {
				printWriter.println(l);
			}
		} finally {
			if (printWriter != null) {
				printWriter.close();
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}

	// -========================================================================================================

	/*
	 * Buffered Streams
	 * 
	 * Most of the examples we've seen so far use unbuffered I/O. This means each read or write
	 * request is handled directly by the underlying OS. This can make a program much less
	 * efficient, since each such request often triggers disk access, network activity, or some
	 * other operation that is relatively expensive.
	 * 
	 * To reduce this kind of overhead, the Java platform implements buffered I/O streams. Buffered
	 * input streams read data from a memory area known as a buffer; the native input API is called
	 * only when the buffer is empty. Similarly, buffered output streams write data to a buffer, and
	 * the native output API is called only when the buffer is full.
	 * 
	 * A program can convert an unbuffered stream into a buffered stream using the wrapping idiom
	 * we've used several times now, where the unbuffered stream object is passed to the constructor
	 * for a buffered stream class. Here's how you might modify the constructor invocations in the
	 * CopyCharacters example to use buffered I/O
	 * 
	 * There are four buffered stream classes used to wrap unbuffered streams: 
	 * BufferedInputStream and BufferedOutputStream create buffered byte streams
	 * BufferedReader and BufferedWriter create buffered character streams.
	 * 
	 * Flushing Buffered Streams
	 * 
	 * It often makes sense to write out a buffer at critical points, without waiting for it to
	 * fill. This is known as flushing the buffer.
	 * 
	 * Some buffered output classes support autoflush, specified by an optional constructor
	 * argument. When autoflush is enabled, certain key events cause the buffer to be flushed. For
	 * example, an autoflush PrintWriter object flushes the buffer on every invocation of println or
	 * format. See Formatting for more on these methods.
	 * 
	 * To flush a stream manually, invoke its flush method. The flush method is valid on any output
	 * stream, but has no effect unless the stream is buffered.
	 * @on
	 */
	@Test
	public void testBufferedStream() throws IOException {
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;

		try {
			bufferedReader = new BufferedReader(new FileReader("xanadu.txt"));
			bufferedWriter = new BufferedWriter(new FileWriter("characteroutput.txt"));
			/*
			 * Invoking readLine returns a line of text with the line. CopyLines outputs each line using
			 * println, which appends the line terminator for the current operating system. This might not
			 * be the same line terminator that was used in the input file.
			 */
			String l;
			while ((l = bufferedReader.readLine()) != null) {
				bufferedWriter.write(l);
				bufferedWriter.newLine();
			}
		} finally {
			if (bufferedWriter != null) {
				bufferedWriter.close();
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}

	/*
	 * Objects of type Scanner are useful for breaking down formatted input into tokens and
	 * translating individual tokens according to their data type.
	 * 
	 * Breaking Input into Tokens
	 * 
	 * By default, a scanner uses white space to separate tokens. (White space characters include
	 * blanks, tabs, and line terminators. For the full list, refer to the documentation for
	 * Character.isWhitespace.) To see how scanning works, let's look at ScanXan, a program that
	 * reads the individual words in xanadu.txt and prints them out, one per line.
	 * 
	 * Notice that ScanXan invokes Scanner's close method when it is done with the scanner object.
	 * Even though a scanner is not a stream, you need to close it to indicate that you're done with
	 * its underlying stream.
	 * 
	 * To use a different token separator, invoke useDelimiter(), specifying a regular expression.
	 * For example, suppose you wanted the token separator to be a comma, optionally followed by
	 * white space. You would invoke: s.useDelimiter(",\\s*");
	 */
	@Test
	public void testScanXan() throws IOException {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new BufferedReader(new FileReader("xanadu.txt")));
			while (scanner.hasNext()) {
				System.out.println(scanner.next());
			}
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	/*
	 * The ScanXan example treats all input tokens as simple String values. Scanner also supports
	 * tokens for all of the Java language's primitive types (except for char), as well as
	 * BigInteger and BigDecimal. Also, numeric values can use thousands separators. Thus, in a US
	 * locale, Scanner correctly reads the string "32,767" as representing an integer value.
	 * 
	 * We have to mention the locale, because thousands separators and decimal symbols are locale
	 * specific. So, the following example would not work correctly in all locales if we didn't
	 * specify that the scanner should use the US locale. That's not something you usually have to
	 * worry about, because your input data usually comes from sources that use the same locale as
	 * you do. But this example is part of the Java Tutorial and gets distributed all over the
	 * world.
	 * 
	 * The ScanSum example reads a list of double values and adds them up. Here's the source:
	 */
	@Test
	public void testScannerNumbers() throws IOException {
		Scanner s = null;
		double sum = 0;

		try {
			s = new Scanner(new BufferedReader(new FileReader("usnumbers.txt")));
			s.useLocale(Locale.US);

			while (s.hasNext()) {
				if (s.hasNextDouble()) {
					sum += s.nextDouble();
				} else {
					s.next();
				}
			}
		} finally {
			s.close();
		}

		System.out.println(sum);
	}

	public static class Password {

		public static void main(String[] args) {
			Console console = System.console();
			if (console == null) {
				System.out.println("No console");
				System.exit(1);
			}

			String login = console.readLine("Enter your login: ");
			char[] oldPassword = console.readPassword("Enter your password: ");

			if (verify(login, oldPassword)) {
				boolean noMatch;
				do {
					char[] newPassword1 = console.readPassword("Enter your new password: ");
					char[] newPassword2 = console.readPassword("Enter new password again: ");
					noMatch = !Arrays.equals(newPassword1, newPassword2);

					if (noMatch) {
						console.format("Passwords don't match. Try again.%n");
					} else {
						change(login, newPassword1);
						console.format("Password for %s changed.%n", login);
					}
					Arrays.fill(newPassword1, ' ');
					Arrays.fill(newPassword2, ' ');
				} while (noMatch);
			}

		}

		// Dummy change method.
		static boolean verify(String login, char[] password) {
			// This method always returns
			// true in this example.
			// Modify this method to verify
			// password according to your rules.
			return true;
		}

		// Dummy change method.
		static void change(String login, char[] password) {
			// Modify this method to change
			// password according to your rules.
		}
	}

	// -============================================================================================================

	/*
	 * Just as data streams support I/O of primitive data types, object streams support I/O of
	 * objects. Most, but not all, standard classes support serialization of their objects. Those
	 * that do implement the marker interface Serializable.
	 * 
	 * This is demonstrated in the following figure, where writeObject is invoked to write a single
	 * object named a. This object contains references to objects b and c, while b contains
	 * references to d and e. Invoking writeobject(a) writes not just a, but all the objects
	 * necessary to reconstitute a, so the other four objects in this web are written also. When a
	 * is read back by readObject, the other four objects are read back as well, and all the
	 * original object references are preserved.
	 */
	@Test
	public void testObjectStream() {

	}
}
