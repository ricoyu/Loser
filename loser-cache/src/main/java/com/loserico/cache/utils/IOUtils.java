package com.loserico.cache.utils;

import static java.lang.Thread.currentThread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class IOUtils {

	private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);
	/**
	 * Represents the end-of-file (or stream).
	 */
	public static final int EOF = -1;

	/**
	 * The Unix directory separator character.
	 */
	public static final String DIR_SEPARATOR_UNIX = "/";
	/**
	 * The Windows directory separator character.
	 */
	public static final char DIR_SEPARATOR_WINDOWS = '\\';
	/**
	 * The system directory separator character.
	 */
	public static final char DIR_SEPARATOR = File.separatorChar;
	/**
	 * The Unix line separator string.
	 */
	public static final String LINE_SEPARATOR_UNIX = "\n";
	/**
	 * The Windows line separator string.
	 */
	public static final String LINE_SEPARATOR_WINDOWS = "\r\n";

	public static final String CLASSPATH_PREFIX = "classpath*:";

	/**
	 * 读取classpath下文件内容,文件不存在则返回null PathMatchingResourcePatternResolver
	 * 
	 * @param fileName
	 * @return String
	 */
	public static String readClassPathFile(String fileName) {
		InputStream in = readClasspathFileAsInputStream(fileName);
		if (in == null) {
			logger.debug("Cannot file {} under classpath", fileName);
			return null;
		}
		return readFile(in);
	}

	/**
	 * 读取classpath下某个文件，返回InputStream
	 * 
	 * @param fileName
	 * @return
	 */
	public static InputStream readClasspathFileAsInputStream(String fileName) {
		ClassLoader classLoader = firstNonNull(currentThread().getContextClassLoader(), IOUtils.class.getClassLoader());
		URL url = classLoader.getResource(fileName);
		if (url == null && !fileName.startsWith(DIR_SEPARATOR_UNIX)) {
			logger.debug("Cannot find file {} under classpath", fileName);
			url = classLoader.getResource("/" + fileName);
		}
		if (url != null) {
			try {
				return url.openStream();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				return null;
			}
		}

		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource resource = resolver.getResource(fileName);
		if (resource.exists()) {
			try {
				return resource.getInputStream();
			} catch (IOException e) {
				logger.error("", e);
				return null;
			}
		}

		try {
			if (!fileName.startsWith(DIR_SEPARATOR_UNIX)) {
				fileName = CLASSPATH_PREFIX + DIR_SEPARATOR_UNIX + "**" + DIR_SEPARATOR_UNIX + fileName;
			}
			Resource[] resources = resolver.getResources(fileName);
			if (resources.length > 0) {
				return resources[0].getInputStream();
			}
		} catch (IOException e) {
			logger.error("", e);
			return null;
		}

		return null;
	}

	public static String readFile(InputStream in) {
		StringBuilder result = new StringBuilder();
		try (Scanner scanner = new Scanner(in)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append(LINE_SEPARATOR_UNIX);
			}
			scanner.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return result.toString();
	}
	
	private static <T> T firstNonNull(T first, T second) {
		return first != null ? first : second;
	}
}
