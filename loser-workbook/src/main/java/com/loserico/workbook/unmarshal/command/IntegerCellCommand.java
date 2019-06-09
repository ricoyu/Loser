package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.loserico.workbook.utils.ReflectionUtils;

public class IntegerCellCommand extends BaseCellCommand {
	
	private AtomicReference<Function<Cell, Integer>> reference = new AtomicReference<>();

	public IntegerCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(Cell cell, Object pojo) {
		if (reference.get() != null) {
			Integer integerValue = reference.get().apply(cell);
			if (integerValue != null) {
				ReflectionUtils.setField(field, pojo, integerValue);
			}
			return;
		}
		
		if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA) {
			Function<Cell, Integer> convertor = (c) -> (int)c.getNumericCellValue();
			reference.compareAndSet(null, convertor);
			
			Integer integerValue = convertor.apply(cell);
			if (integerValue != null) {
				ReflectionUtils.setField(field, pojo, integerValue);
			}
			return;
		}
		
		Function<Cell, Integer> convertor = (c) -> {
			String value = str(c);
			if (value == null || "".equals(value)) {
				return null;
			}
			return Integer.valueOf(value);
		};
		Integer integerValue = convertor.apply(cell);
		if (integerValue != null) {
			ReflectionUtils.setField(field, pojo, integerValue);
		}
	}

}
