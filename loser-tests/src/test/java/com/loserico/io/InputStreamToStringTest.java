package com.loserico.io;

import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.StandardCopyOption.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.CharStreams;

import lombok.extern.slf4j.Slf4j;

/**
 * In this tutorial we’ll look at how to convert an InputStream to a String, 
 * using Guava, the Apache Commons IO library, and plain Java.
 * 
 * 各种InputStream转String的方法
 * 
 * https://www.baeldung.com/convert-input-stream-to-string
 * https://www.baeldung.com/java-io-conversions
 * 
 * <p>
 * Copyright: Copyright (c) 2019-06-17 10:15
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Slf4j
public class InputStreamToStringTest {

	/**
	 * Let’s start with a Guava example – leveraging the ByteSource functionality
	 * 这个版本不需要自己显式关闭InputStream
	 * 
	 * @throws IOException
	 */
	@Test
	public void testConvertingWithGuava() throws IOException {
		String originalString = RandomStringUtils.randomAlphabetic(8);
		InputStream inputStream = new ByteArrayInputStream(originalString.getBytes(UTF_8));

		/*
		 * first – we wrap our InputStream a ByteSource – and as far as I’m aware, this is the easiest way to do so
		 */
		ByteSource byteSource = new ByteSource() {

			@Override
			public InputStream openStream() throws IOException {
				return inputStream;
			}
		};

		/*
		 *  then – we view our ByteSource as a CharSource with a UTF8 charset.
		 *  finally – we use the CharSource to read it as a String.
		 */
		String txt = byteSource.asCharSource(UTF_8).read();
		assertThat(txt, equalTo(originalString));
	}

	/**
	 * A simpler way of doing the conversion with Guava, but the stream needs to be
	 * explicitly closed; luckily, we can simply use the try-with-resources syntax
	 * to take care of that:
	 */
	@Test
	public void testConvertingWithGuavaAndJava7() {
		String originalString = randomAlphabetic(8);
		InputStream inputStream = new ByteArrayInputStream(originalString.getBytes(UTF_8));

		String txt = null;
		try (final Reader reader = new InputStreamReader(inputStream)) {
			txt = CharStreams.toString(reader);
		} catch (IOException e) {
			log.error("msg", e);
		}

		assertThat(txt, equalTo(originalString));
	}

	/**
	 * Let’s now look at how to do this with the Commons IO library.
	 * 
	 * An important caveat here is that – as opposed to Guava – neither of these
	 * examples will close the InputStream – which is why I personally prefer the
	 * Guava solution.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testConvertingWithApacheCommons() throws IOException {
		String originalString = randomAlphabetic(8);
		InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

		String txt = IOUtils.toString(inputStream, UTF_8);
		assertThat(txt, equalTo(originalString));
	}

	/**
	 * We can also use a StringWriter to do the conversion
	 * 
	 * @throws IOException
	 */
	@Test
	public void testConvertingWithStringWriter() throws IOException {
		String originalString = randomAlphabetic(8);
		InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

		StringWriter stringWriter = new StringWriter();
		String encoding = UTF_8.name();
		IOUtils.copy(inputStream, stringWriter, encoding);

		assertThat(stringWriter.toString(), equalTo(originalString));
	}

	/**
	 * Let’s look now at a lower level approach using plain Java – an InputStream
	 * and a simple StringBuilder:
	 * 
	 * @throws IOException
	 */
	@Test
	public void testConvertingWithPlainJava() throws IOException {
		String originalString = randomAlphabetic(8);
		InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

		StringBuilder stringBuilder = new StringBuilder();
		try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {
			int c = 0;
			while ((c = reader.read()) != -1) { // int 转 char 是按照ASCII码转的
				stringBuilder.append((char) c);
			}
		}

		assertEquals(stringBuilder.toString(), originalString);
	}

	/**
	 * Next – let’s look at a plain Java example – using a standard text Scanner:
	 * 
	 * Note that the InputStream is going to be closed by the closing of the
	 * Scanner.
	 * 
	 * The only reason this is a Java 7 example, and not a Java 5 one is the use of
	 * the try-with-resources statement – turning that into a standard try-finally
	 * block will compile just fine with Java 5.
	 */
	@Test
	public void testConvertingWithScanner() {
		String originalString = randomAlphabetic(8);
		InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

		String txt = null;
		try (Scanner scanner = new Scanner(inputStream, UTF_8.name())) {
			txt = scanner.useDelimiter("\\A").next();
		}

		assertThat(txt, equalTo(originalString));
	}

	/**
	 * Finally, let’s look at another plain Java example, this time using the
	 * ByteArrayOutputStream class:
	 * 
	 * In this example, first the InputStream is converted to a
	 * ByteArrayOutputStream by reading and writing byte blocks, then the
	 * OutputStream is transformed to a byte array, which is used to create a
	 * String.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testConvertingWithByteArrayOutputStream() throws IOException {
		String originalString = randomAlphabetic(8);
		InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[1024];
		while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		byte[] byteArray = buffer.toByteArray();

		String txt = new String(byteArray, UTF_8);
		assertThat(txt, equalTo(originalString));
	}

	/**
	 * Another solution is to copy the content of the InputStream to a file, then
	 * convert this to a String:
	 * 
	 * Here, we’re using the java.nio.file.Files class to create a temporary file,
	 * as well as copy the content of the InputStream to the file. Then, the same
	 * class is used to convert the file content to a String with the readAllBytes()
	 * method.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testConvertingWithTempFile() throws IOException {
		String originalString = randomAlphabetic(8);
		InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
		Path tempFile = Files.createTempDirectory("").resolve(UUID.randomUUID().toString() + ".tmp");
		Files.copy(inputStream, tempFile, REPLACE_EXISTING);
		String txt = new String(Files.readAllBytes(tempFile));

		assertThat(txt, equalTo(originalString));
	}
}
