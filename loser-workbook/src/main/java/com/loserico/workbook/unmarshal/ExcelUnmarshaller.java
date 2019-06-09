package com.loserico.workbook.unmarshal;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import com.loserico.concurrent.Concurrent;
import com.loserico.workbook.exception.BuilderUncompleteException;
import com.loserico.workbook.unmarshal.assassinator.AssassinatorMaster;
import com.loserico.workbook.unmarshal.assassinator.POJOAssassinator;
import com.loserico.workbook.unmarshal.assassinator.AssassinatorMaster.Builder;
import com.loserico.workbook.unmarshal.builder.POJOAssassinatorBuilder;
import com.loserico.workbook.unmarshal.iterator.RowIterator;

import lombok.extern.slf4j.Slf4j;

/**
 * 将Excel解析为POJO列表的入口类
 * <p>
 * Copyright: Copyright (c) 2019-05-23 17:42
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * 
 * @author Rico Yu ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Slf4j
public final class ExcelUnmarshaller {

	private Workbook workbook;

	private String sheetName;

	private int fallbackSheetIndex = -1;

	private int titleRowIndex = -1;

	private Class<?> pojoType;
	
	private ExcelUnmarshaller(Builder builder) {
		this.workbook = builder.workbook;
		this.sheetName = builder.sheetName;
		this.fallbackSheetIndex = builder.fallbackSheetIndex;
		this.titleRowIndex = builder.titleRowIndex;
		this.pojoType = builder.pojoType;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> unmarshall() {
		List<POJOAssassinator> assassinators = POJOAssassinatorBuilder.build(pojoType);
		if (assassinators.isEmpty()) {
			return emptyList();
		}

		List<T> results = new ArrayList<>();
		AssassinatorMaster assassinatorMaster = AssassinatorMaster.builder()
				.sheetName(sheetName)
				.fallbackSheetIndex(fallbackSheetIndex)
				.titleRowIndex(titleRowIndex)
				.build();
		RowIterator<Row> iterator = assassinatorMaster.train(assassinators, workbook);
		
		if (iterator.getTotalCount() < 10000) {
			while (iterator.hasNext()) {
				Row row = iterator.next();
				T instance;
				try {
					instance = (T) pojoType.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					log.error("Should have a default constructor", e);
					throw new RuntimeException("Should have a default constructor", e);
				}
				results.add(instance);

				for (POJOAssassinator pojoAssassinator : assassinators) {
					pojoAssassinator.assassinate(row, instance);
				}
			}
		} else {
			ExecutorService executorService = Concurrent.ncoreFixedThreadPool();
			while (iterator.hasNext()) {
				Row row = iterator.next();
				T instance;
				try {
					instance = (T) pojoType.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					log.error("Should have a default constructor", e);
					throw new RuntimeException("Should have a default constructor", e);
				}
				results.add(instance);
				
				for (POJOAssassinator pojoAssassinator : assassinators) {
  					executorService.execute(() -> pojoAssassinator.assassinate(row, instance));
				}
			}
			Concurrent.awaitTermination(executorService, 3, TimeUnit.MINUTES);
		}

		return results;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {

		private Workbook workbook;

		private String sheetName;

		private int fallbackSheetIndex = -1;

		private int titleRowIndex = -1;

		private Class<?> pojoType;
		
		public Builder workbook(Workbook workbook) {
			this.workbook = workbook;
			return this;
		}
		
		public Builder sheetName(String sheetName) {
			this.sheetName = sheetName;
			return this;
		}
		
		public Builder fallbackSheetIndex(int fallbackSheetIndex) {
			this.fallbackSheetIndex = fallbackSheetIndex;
			return this;
		}
		
		public Builder titleRowIndex(int titleRowIndex) {
			this.titleRowIndex = titleRowIndex;
			return this;
		}
		
		public Builder pojoType(Class<?> pojoType) {
			this.pojoType = pojoType;
			return this;
		}
		
		public ExcelUnmarshaller build() {
			if (workbook == null) {
				throw new BuilderUncompleteException("workbook must be set!");
			}
			if (sheetName == null && fallbackSheetIndex == -1) {
				throw new BuilderUncompleteException("sheetName or fallbackSheetIndex must be set!");
			}
			if (pojoType == null) {
				throw new BuilderUncompleteException("pojoType must be set!");
			}
			
			return new ExcelUnmarshaller(this);
		}
	}
}
