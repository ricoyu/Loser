package com.loserico.workbook.unmarshal;

import java.lang.reflect.Field;

import com.loserico.workbook.convertor.Convertor;

import lombok.Setter;

/**
 * 负责将某一列的Cell写入到Bean的对应属性里面
 * <p>
 * Copyright: Copyright (c) 2019-05-09 21:04
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Setter
public class CellReader {

	//Cell所在列对应POJO的字段
	private Field field;
	
	private Convertor convertor;
}
