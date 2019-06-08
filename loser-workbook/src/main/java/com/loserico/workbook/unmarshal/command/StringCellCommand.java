package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.loserico.workbook.utils.ReflectionUtils;

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
public class StringCellCommand extends BaseCellCommand {

	private AtomicReference<Function<Cell, String>> atomicReference = new AtomicReference<>(null);

	public StringCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(Cell cell, Object pojo) {
		if (atomicReference.get() != null) {
			String str = atomicReference.get().apply(cell);
			if (str != null) {
				ReflectionUtils.setField(this.field, pojo, str);
			}
			return;
		}

		if (cell.getCellTypeEnum() == CellType.STRING) {
			Function<Cell, String> convertor = (c) -> {
				String cellValue = c.getStringCellValue();
				if (cellValue == null || "".equals(cellValue.trim())) {
					return null;
				}
				if ("\"\"".equals(cellValue)) {
					return null;
				}
				Matcher matcher = PATTERN_QUOTE.matcher(cellValue);
				if (matcher.matches()) {
					return matcher.group(1).trim();
				}
				return cellValue.trim();
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
			if (value != null) {
				ReflectionUtils.setField(field, pojo, value);
				return;
			}
		}
	}

}
