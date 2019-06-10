package com.loserico.workbook;

import static com.loserico.commons.jackson.JacksonUtils.toJson;

import java.io.File;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import com.loserico.io.utils.IOUtils;
import com.loserico.workbook.pojo.SettlementItem;
import com.loserico.workbook.unmarshal.ExcelUnmarshaller;
import com.loserico.workbook.utils.ExcelUtils;

public class ExcelUnmarshellTest {

	@Test
	public void testUnmarshall() throws Exception {
		Class.forName("com.loserico.commons.utils.DateUtils");
		File file = IOUtils.readClasspathFileAsFile("excel/958395-one.csv");
//		Workbook workbook = ExcelUtils.getWorkbook(IOUtils.readClasspathFileAsFile("excel/958395-one.csv"));
		long begin = System.currentTimeMillis();
		List<SettlementItem> settlementItems = ExcelUnmarshaller.builder(file)
				.sheetName("992704（2018.9.4结算 ）")
				.fallbackSheetIndex(0)
				.pojoType(SettlementItem.class)
				.validate(true)
				.build()
				.unmarshall();
		long end = System.currentTimeMillis();
		System.out.println("Total row : " + settlementItems.size() + ", Cost " + (end - begin) + " miliseconds");
		System.out.println(toJson(settlementItems));
	}
	
}
