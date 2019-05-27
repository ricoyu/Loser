package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

public abstract class BaseCellCommand implements CellCommand {

	// 有些cell里面的值是双引号括起来的, 这里要去掉双引号
	protected static Pattern PATTERN_QUOTE = Pattern.compile("\"(.+)\"");
	
	/**
	 * 要写入的POJO的字段
	 */
	protected Field field;
	
	public BaseCellCommand(Field field) {
		this.field = field;
	}

}
