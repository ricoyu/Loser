package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.loserico.workbook.utils.ReflectionUtils;

public class LongCellCommand extends BaseCellCommand {
	
	private AtomicReference<Function<Cell, Long>> reference = new AtomicReference<Function<Cell,Long>>(null);
	
	public LongCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(Cell cell, Object pojo) {
		if (reference.get() != null) {
			Long longValue = reference.get().apply(cell);
			if (longValue != null) {
				ReflectionUtils.setField(field, pojo, longValue);
			}
			return;
		}
		
		if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA) {
			Function<Cell, Long> convertor = (c) -> (long) c.getNumericCellValue();
			reference.compareAndSet(null, convertor);
			Long longValue = convertor.apply(cell);
			if (longValue != null) {
				ReflectionUtils.setField(field, pojo, longValue);
			}
			return;
		}
		
		Function<Cell, Long> convertor = (c) -> {
			String value = str(c);
			if (null == value || "".equals(value)) {
				return null;
			}
			return Long.parseLong(value);
		};
		
		Long longValue = convertor.apply(cell);
		if (longValue != null) {
			ReflectionUtils.setField(field, pojo, longValue);
		}
	}

}
