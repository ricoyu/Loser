package com.loserico.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import com.loserico.io.utils.IOUtils;
import com.loserico.workbook.utils.ExcelUtils;

public class GIROExcelTest {

	@Test
	public void testRead() throws Exception {
		Workbook workbook = ExcelUtils.getWorkbook(IOUtils.readClasspathFileAsFile("excel/PCSCC - Sept'18 2nd GIRO DD.xlsx").toPath());
		
	}
}
