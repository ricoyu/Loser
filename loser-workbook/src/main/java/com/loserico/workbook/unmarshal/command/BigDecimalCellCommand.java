package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.loserico.workbook.utils.ReflectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BigDecimalCellCommand extends BaseCellCommand {
	
	private AtomicReference<Function<Cell, BigDecimal>> reference = new AtomicReference<Function<Cell,BigDecimal>>(null);
	
	public BigDecimalCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(Cell cell, Object pojo) {
		if (reference.get() != null) {
			BigDecimal bigDecimal = reference.get().apply(cell);
			if (bigDecimal != null) {
				ReflectionUtils.setField(field, pojo, bigDecimal);
			}
			return;
		}
		
		if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA) {
			Function<Cell, BigDecimal> convertor = (c) -> {
				double value = c.getNumericCellValue();
				return new BigDecimal(value);
			};
			reference.compareAndSet(null, convertor);
			BigDecimal bigDecimal = convertor.apply(cell);
			if (bigDecimal != null) {
				ReflectionUtils.setField(field, pojo, bigDecimal);
			}
			return;
		}
		
		Function<Cell, BigDecimal> convertor = (c) -> {
			String value = numericStr(c);
			if (null == value || "".equals(value)) {
				return null;
			}
//			log.info("value:[{}]", value);
			return new BigDecimal(value);
		};
		BigDecimal bigDecimal = convertor.apply(cell);
		reference.compareAndSet(null, convertor);
		if (bigDecimal != null) {
			ReflectionUtils.setField(field, pojo, bigDecimal);
		}
	}

}
