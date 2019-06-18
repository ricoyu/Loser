package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.loserico.workbook.utils.ReflectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 负责从Cell中读取值并设置到String类型的字段上
 * <p>
 * Copyright: Copyright (c) 2019-05-23 14:41
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Slf4j
public class StringCellCommand extends BaseCellCommand {

	private AtomicReference<Function<Cell, String>> atomicReference = new AtomicReference<>(null);

	public StringCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(Cell cell, Object pojo) {
		Function<Cell, String> func = atomicReference.get();
		if (func != null) {
			String str = null;
			try {
				str = func.apply(cell);
				if (str != null) {
					ReflectionUtils.setField(this.field, pojo, str);
				}
				return;
			} catch (Exception e) {
				/*
				 * 在Excel中的同一列的不同行出现不同的数据格式时会抛异常
				 * 重新构造Convertor
				 */
				log.error("这是同一列出现了不同的数据格式吗?, Row[{}], Column[{}]" + e.getMessage(), cell.getRowIndex(), cell.getColumnIndex());
				atomicReference.compareAndSet(func, null);
			}
		}
		if (cell.getCellTypeEnum() == CellType.STRING) {
			Function<Cell, String> convertor = (c) -> {
				return str(c);
			};
			String value = convertor.apply(cell);
			if (value != null) {
				ReflectionUtils.setField(field, pojo, value);
			}
			atomicReference.compareAndSet(null, convertor);
			return;
		}

		if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA) {
			Function<Cell, String> convertor = (c) -> {
				return new BigDecimal(c.getNumericCellValue()).toPlainString();
			};

			String value = convertor.apply(cell);
			atomicReference.compareAndSet(null, convertor);
			if (value != null) {
				ReflectionUtils.setField(field, pojo, value);
			}
		}
	}

}
