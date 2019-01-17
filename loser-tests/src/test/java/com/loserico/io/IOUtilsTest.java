package com.loserico.io;

import static java.text.MessageFormat.format;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.junit.Test;

import com.loserico.io.utils.IOUtils;

public class IOUtilsTest {

	@Test
	public void testMove() {
		Path source = Paths.get("D:\\ERP\\Taidii\\export\\customer\\Customer.xml");
		Path targetFolder = Paths.get("D:\\ERP\\Taidii\\export\\customer\\archive");
		try {
			IOUtils.move(source, targetFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testMoveAndRename() {
		Path source = Paths.get("D:\\ERP\\Taidii\\export\\customer\\Customer.xml");
		Path targetFolder = Paths.get("D:\\ERP\\Taidii\\export\\customer\\archive");
		try {
			IOUtils.move(source, targetFolder, format("Customer{0}.xml", LocalDateTime.now().format(ofPattern("yyyyMMdd"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testReadFile() throws IOException {
		Path path = Paths.get("D:\\Dropbox\\doc\\铁血大明.txt");
		long begintime = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			String contents = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		}
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - begintime);
	}//5307
	
	@Test
	public void testReadFile2() throws IOException {
		Path path = Paths.get("D:\\Dropbox\\doc\\铁血大明.txt");
		long begintime = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			String contents = IOUtils.readFile(path);
		}
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - begintime);
	}
}
