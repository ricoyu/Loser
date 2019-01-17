package com.loserico.utils;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import com.loserico.io.utils.IOUtils;
import com.loserico.workbook.utils.ExcelUtils;

public class ExcelUtilsTest {

	@Test
	public void testWrite2Xls() {
		Path path = IOUtils.readClasspathFileAsFile("excel/UOB-Template.xls").toPath();
		ExcelUtils.write2Excel(path, "IBG_PAYMNT_DTL", emptyList());
	}
	
	@Test
	public void testWorkbookFactory() throws Exception {
		//File file = IOUtils.readClasspathFileAsFile("excel/DBS_GIRO_REPORT.xls");
		File file = new File("D:\\DeepdataSense\\pims\\doc\\GIRO数据导出TXT\\DBS\\DBS.xlsx");
		Workbook workbook = ExcelUtils.getWorkbook(file.toPath());

		Sheet sheet = workbook.getSheetAt(0);
		Row row = sheet.getRow(0);
		
		System.out.println(ExcelUtils.stringVal(row, 11));

		Iterator<Cell> it = row.cellIterator();
		int index = 0;
		while (it.hasNext()) {
			Cell cell = (Cell) it.next();
			System.out.println(cell.toString());
			if (isNotBlank(cell.toString())) {
				System.out.println(index);
				break;
			}
			index++;
		}
		ExcelUtils.closeWorkbook(workbook);
	}
	
	@Test
	public void testValueDate() throws Exception {
		//File file = IOUtils.readClasspathFileAsFile("excel/DBS_GIRO_REPORT.xls");
		File file = new File("D:\\DeepdataSense\\pims\\doc\\GIRO数据导出TXT\\DBS\\DBS.xlsx");
		Workbook workbook = ExcelUtils.getWorkbook(file.toPath());
		
		Sheet sheet = workbook.getSheetAt(0);
		Row row = sheet.getRow(1);
		
		Iterator<Cell> it = row.cellIterator();
		int index = 0;
		while (it.hasNext()) {
			Cell cell = (Cell) it.next();
			System.out.println(cell.toString());
			System.out.println(index);
			index++;
		}
		ExcelUtils.closeWorkbook(workbook);
	}
}
