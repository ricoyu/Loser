package com.loserico.workbook;

import static com.loserico.commons.jackson.JacksonUtils.toJson;

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
		Workbook workbook = ExcelUtils.getWorkbook(IOUtils.readClasspathFileAsFile("excel/1005466.xlsx"));
		long begin = System.currentTimeMillis();
		List<SettlementItem> settlementItems = ExcelUnmarshaller.builder()
				.workbook(workbook)
				.sheetName("992704（2018.9.4结算 ）")
				.fallbackSheetIndex(0)
				.pojoType(SettlementItem.class)
				.build()
				.unmarshall();
		long end = System.currentTimeMillis();
		System.out.println("Total row : " + settlementItems.size() + ", Cost " + (end - begin) + " miliseconds");
//		System.out.println(toJson(settlementItems));
	}
}
