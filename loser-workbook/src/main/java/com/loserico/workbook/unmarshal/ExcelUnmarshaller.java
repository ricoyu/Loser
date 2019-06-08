package com.loserico.workbook.unmarshal;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.poi.ss.usermodel.Workbook;

import com.loserico.concurrent.Concurrent;
import com.loserico.workbook.unmarshal.assassinator.AssassinatorMaster;
import com.loserico.workbook.unmarshal.assassinator.POJOAssassinator;
import com.loserico.workbook.unmarshal.builder.POJOAssassinatorBuilder;

import lombok.Builder;

/**
 * 将Excel解析为POJO列表的入口类
 * <p>
 * Copyright: Copyright (c) 2019-05-23 17:42
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Builder
public final class ExcelUnmarshaller<T> {
	
	private Workbook workbook;
	
	private String sheetName;
	
	private int titleRowIndex;
	
	private Class<T> pojoType;

	public static <T> List<T> unmarshall(Workbook workbook, String sheetName, int titleRowIndex, Class<T> pojoType) {
		List<POJOAssassinator> assassinators = POJOAssassinatorBuilder.build(pojoType);
		if (assassinators.isEmpty()) {
			return emptyList();
		}
		
		AssassinatorMaster assassinatorMaster = AssassinatorMaster.builder()
			.sheetName(sheetName)
			.titleRowIndex(titleRowIndex)
			.build();
		assassinatorMaster.train(assassinators, workbook);
		
		ExecutorService executorService = Concurrent.ncoreFixedThreadPool();
		
		return emptyList();
	}
}
