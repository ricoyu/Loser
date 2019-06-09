package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.loserico.workbook.utils.ReflectionUtils;

/**
 * Cell是布尔类型的, 直接从Cell获取相应值
 * Cell是字符串类型, "TRUE", "true", "Y", "Yes", "1" --> true
 * 				  "FALSE","false","N", "No",  "0" --> false
 * 				  null 或者空字符串则忽略
 * Cell是数字类型的, 1 --> true
 * 				  0 --> false
 * <p>
 * Copyright: Copyright (c) 2019-06-09 16:37
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class BooleanCellCommand extends BaseCellCommand {
	
	private AtomicReference<Function<Cell, Boolean>> reference = new AtomicReference<Function<Cell,Boolean>>(null);

	public BooleanCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(Cell cell, Object pojo) {
		if (reference.get() != null) {
			Boolean value = reference.get().apply(cell);
			if (value != null) {
				ReflectionUtils.setField(field, pojo, value);
				return;
			}
		}
		
		if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
			Function<Cell, Boolean> convertor = c -> c.getBooleanCellValue();
			Boolean value = convertor.apply(cell);
			if (value != null) {
				ReflectionUtils.setField(field, pojo, value);
			}
			return;
		}
		
		if (cell.getCellTypeEnum() == CellType.STRING) {
			Function<Cell, Boolean> convertor = (c) -> {
				String value = str(c);
				if (value == null || "".equals(value)) {
					return null;
				}
				if ("true".equalsIgnoreCase(value) ||
						"y".equalsIgnoreCase(value) ||
						"1".equalsIgnoreCase(value)) {
					return true;
				}
				if ("false".equalsIgnoreCase(value) ||
						"n".equalsIgnoreCase(value) ||
						"no".equalsIgnoreCase(value) ||
						"0".equalsIgnoreCase(value)) {
					return false;
				}
				return null;
			};
			Boolean booleanValue = convertor.apply(cell);
			if (booleanValue != null) {
				ReflectionUtils.setField(field, pojo, booleanValue);
			}
			reference.compareAndSet(null, convertor);
			return;
		}
		
		if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA) {
			Function<Cell, Boolean> convertor = (c) -> {
				double doubleValue = c.getNumericCellValue();
				if (1.0d == doubleValue) {
					return true;
				}
				if (0.0d == doubleValue) {
					return false;
				}
				return null;
			};
			Boolean booleanValue = convertor.apply(cell);
			if (booleanValue != null) {
				ReflectionUtils.setField(field, pojo, booleanValue);
			}
			reference.compareAndSet(null, convertor);
			return;
		}
	}

}
