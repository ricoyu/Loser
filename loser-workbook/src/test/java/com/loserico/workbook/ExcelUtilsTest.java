package com.loserico.workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import com.loserico.io.utils.IOUtils;
import com.loserico.workbook.utils.ExcelUtils;

public class ExcelUtilsTest {

	@Test
	public void testWriteByCellPosi() throws Exception {
		File file = IOUtils.readClasspathFileAsFile("excel/ListMonthInvoiceEmailTemplate.xlsx");
		Workbook workbook = ExcelUtils.getWorkbook(file);
		Sheet sheet = workbook.getSheet("New Invoice - sample");
		ExcelUtils.writeCell(sheet, 0, 0, "September '18 INVOICE For Student 三少爷");
		
		Path path = ExcelUtils.write2TmpFile(workbook, "xlsx");
		System.out.println(path);
	}
}
