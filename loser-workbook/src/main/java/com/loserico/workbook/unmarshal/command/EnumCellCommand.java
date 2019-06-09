package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;

import org.apache.poi.ss.usermodel.Cell;

import com.loserico.commons.utils.EnumUtils;
import com.loserico.workbook.utils.ReflectionUtils;

public class EnumCellCommand extends BaseCellCommand{
	
	public EnumCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(Cell cell, Object pojo) {
		Object value = EnumUtils.lookupEnum(field.getType(), str(cell), "desc", "alias");
		if (value != null) {
			ReflectionUtils.setField(field, pojo, value);
		}
	}

}
