package com.loserico.workbook;

import static com.loserico.commons.jackson.JacksonUtils.toJson;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.loserico.io.utils.IOUtils;
import com.loserico.workbook.pojo.FaPiao;
import com.loserico.workbook.pojo.SettlementItem;
import com.loserico.workbook.unmarshal.ExcelUnmarshaller;

public class ExcelUnmarshellTest {

	@Test
	public void testUnmarshall() throws Exception {
//		Class.forName("com.loserico.commons.utils.DateUtils");
		File file = IOUtils.readClasspathFileAsFile("excel/1005466.xlsx");
//		Workbook workbook = ExcelUtils.getWorkbook(IOUtils.readClasspathFileAsFile("excel/958395-one.csv"));
		ExcelUnmarshaller unmarshaller = ExcelUnmarshaller.builder(file)
//				.sheetName("Sheet1")
				.fallbackSheetIndex(0)
				.pojoType(SettlementItem.class)
//				.validate(true)
				.build();
		long total = 0;
//		for (int i = 0; i < 10; i++) {
			long begin = System.currentTimeMillis();
			List<SettlementItem> settlementItems = unmarshaller.unmarshall();
			long end = System.currentTimeMillis();
			System.out.println("Total row : " + settlementItems.size() + ", Cost " + (end - begin) + " miliseconds");
			total += (end - begin);
			unmarshaller.reset();
//		}
		System.out.println("Average time: " + total / 10);
		System.out.println(toJson(settlementItems));
	}

	@Test
	public void testUnmarshall2() throws Exception {
		File file = IOUtils.readClasspathFileAsFile("excel/gys-apple.xls");
		ExcelUnmarshaller unmarshaller = ExcelUnmarshaller.builder(file)
				.sheetName("Sheet1")
				.fallbackSheetIndex(0)
				.pojoType(FaPiao.class)
				.build();
		long begin = System.currentTimeMillis();
		List<FaPiao> settlementItems = unmarshaller
				.unmarshall();
		long end = System.currentTimeMillis();
		System.out.println("Total row : " + settlementItems.size() + ", Cost " + (end - begin) + " miliseconds");
//		System.out.println(toJson(settlementItems));
	}

}
