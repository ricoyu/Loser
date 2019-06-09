package com.loserico.workbook.unmarshal;

import static java.util.Collections.emptyList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import com.loserico.concurrent.Concurrent;
import com.loserico.workbook.exception.BindException;
import com.loserico.workbook.exception.BuilderUncompleteException;
import com.loserico.workbook.exception.InvalidConfigurationException;
import com.loserico.workbook.exception.WorkbookCreationException;
import com.loserico.workbook.unmarshal.assassinator.AssassinatorMaster;
import com.loserico.workbook.unmarshal.assassinator.POJOAssassinator;
import com.loserico.workbook.unmarshal.builder.POJOAssassinatorBuilder;
import com.loserico.workbook.unmarshal.iterator.RowIterator;
import com.loserico.workbook.utils.ExcelUtils;

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

	private File file;

	private String sheetName;

	private int fallbackSheetIndex = -1;

	private int titleRowIndex = -1;

	private Class<?> pojoType;

	/** 是否要执行数据校验 */
	private boolean validate = false;

	private Validator validator;

	private ExcelUnmarshaller(Builder builder) {
		this.workbook = builder.workbook;
		this.file = builder.file;
		this.sheetName = builder.sheetName;
		this.fallbackSheetIndex = builder.fallbackSheetIndex;
		this.titleRowIndex = builder.titleRowIndex;
		this.pojoType = builder.pojoType;
		this.validate = builder.validate;
		if (this.validate) {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			this.validator = factory.getValidator();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> unmarshall() {
		List<POJOAssassinator> assassinators = POJOAssassinatorBuilder.build(pojoType);
		if (assassinators.isEmpty()) {
			return emptyList();
		}

		if (this.workbook == null) {
			try {
				this.workbook = ExcelUtils.getWorkbook(this.file);
			} catch (Exception e) {
				throw new WorkbookCreationException(e);
			}
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

				/*
				 * 每反序列化一个POJO执行一次数据校验, 防止在Excel很大时, 全部分序列化完成后再做数据
				 * 校验遇到失败的话整个过程会比较慢的情况. 
				 * 
				 * 这里的哲学是遇到错误尽快发现尽快上报
				 */
				if (validate) {
					Set<ConstraintViolation<T>> violations = validator.validate(instance);
					if (!violations.isEmpty()) {
						Set<ConstraintViolation<?>> vios = new HashSet<>();
						vios.addAll(violations);
						throw new BindException("Row[" + row.getRowNum() + "] validate failed!", vios);
					}
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

		private File file;

		private String sheetName;

		private int fallbackSheetIndex = -1;

		private int titleRowIndex = -1;

		private Class<?> pojoType;

		private boolean validate = false;

		/**
		 * 以File形式指定要反序列化的Excel对象, 与{@code workbook()}是两个互斥的操作
		 * 
		 * @param file
		 * @return Builder
		 */
		public Builder file(File file) {
			this.file = file;
			if (workbook != null && file != null) {
				throw new InvalidConfigurationException("workbook and file cannot both specified!");
			}
			return this;
		}

		/**
		 * 以Workbook形式指定要反序列化的Excel对象, 与{@code file()}是两个互斥的操作
		 * 
		 * @param file
		 * @return Builder
		 */
		public Builder workbook(Workbook workbook) {
			this.workbook = workbook;
			if (workbook != null && file != null) {
				throw new InvalidConfigurationException("workbook and file cannot both specified!");
			}
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

		/**
		 * 是否要对POJO执行JSR380 Bean Validation
		 * 如果执行数据校验, 是以反序列化一个POJO执行一次数据校验的形式出现
		 * 
		 * 参考: https://www.baeldung.com/javax-validation
		 * @param validate
		 * @return Builder
		 * @on
		 */
		public Builder validate(boolean validate) {
			this.validate = validate;
			return this;
		}

		public ExcelUnmarshaller build() {
			// 二者任选其一必须指定
			if (this.file == null && this.workbook == null) {
				throw new BuilderUncompleteException("Either file or workbook must exists!");
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
