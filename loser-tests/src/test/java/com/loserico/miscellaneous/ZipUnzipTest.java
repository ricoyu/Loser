package com.loserico.miscellaneous;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

import com.loserico.io.utils.IOUtils;

public class ZipUnzipTest {

	/**
	 * 压缩单个文件
	 * 
	 * @throws IOException
	 */
	@Test
	public void testZip() throws IOException {
		FileOutputStream fos = new FileOutputStream("compressed.zip");
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		File filetoZip = IOUtils.readClasspathFileAsFile("com/loserico/miscellaneous/zip.txt");
		FileInputStream fis = new FileInputStream(filetoZip);
		ZipEntry zipEntry = new ZipEntry(filetoZip.getName());
		zipOut.putNextEntry(zipEntry);

		byte[] buffer = new byte[1024];
		int lengh = 0;
		while ((lengh = fis.read(buffer)) >= 0) {
			zipOut.write(buffer, 0, lengh);
		}

		zipOut.close();
		fis.close();
		fos.close();
	}

	/**
	 * 压缩多个文件
	 * 
	 * @throws IOException
	 */
	@Test
	public void zipMultiFiles() throws IOException {
		List<String> srcFiles = Arrays.asList("com/loserico/miscellaneous/test1.txt",
				"com/loserico/miscellaneous/test2.txt");
		FileOutputStream fos = new FileOutputStream("multiCompressed.zip");
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		for (String srcFile : srcFiles) {
			File fileToZip = IOUtils.readClasspathFileAsFile(srcFile);
			FileInputStream fis = new FileInputStream(fileToZip);
			ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
			zipOut.putNextEntry(zipEntry);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) >= 0) {
				zipOut.write(buffer, 0, length);
			}
			fis.close();
		}
		zipOut.close();
		fos.close();
	}

	@Test
	public void testDirZip() throws IOException {
		String sourceFile = "D:\\Learning\\Git\\07-branch";
		FileOutputStream fos = new FileOutputStream("dirCompressed.zip");
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		File fileToZip = new File(sourceFile);

		zipFile(fileToZip, fileToZip.getName(), zipOut);
		zipOut.close();
		fos.close();
	}

	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}
}
