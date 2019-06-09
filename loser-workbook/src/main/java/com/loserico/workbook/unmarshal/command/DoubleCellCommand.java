package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;

import org.apache.poi.ss.usermodel.Cell;

public class DoubleCellCommand extends BaseCellCommand {

	public DoubleCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(Cell cell, Object pojo) {
	}

}
