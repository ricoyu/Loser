package com.loserico.utils;

import static java.nio.charset.StandardCharsets.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.loserico.io.utils.IOUtils;

public class PropertyUtilsTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PropertyUtilsTest.class);

	@Test
	public void testLoadProperty() throws IOException {
		URL url = Resources.getResource("3月份发生.properties");
		System.out.println(Resources.toString(url, UTF_8));
		ByteSource byteSource = Resources.asByteSource(url);
		Properties properties = new Properties();
		
		try (InputStream inputStream = byteSource.openBufferedStream();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
			properties.load(inputStreamReader);
			properties.list(System.out);
			
			Map<String, String> centres = Maps.fromProperties(properties);
			centres.keySet().forEach(System.out::println);
		} catch (IOException e) {
			logger.error("msg", e);
		}
	}
	
	@Test
	public void testLoadText() {
//		String content = IOUtils.readClassPathFile("3月份发生.properties");
//		String[] lines = content.split(IOUtils.LINE_SEPARATOR_UNIX);
//		Arrays.asList(lines).stream().forEach(System.out::println);
		
//		IOUtils.readLinesFromClassPath("3月份发生.properties")
		IOUtils.readLinesFromClassPath("March.properties")
			.forEach(System.out::println);
		
	}
}
