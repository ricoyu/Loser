package com.loserico.workbook.unmarshal.builder;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.loserico.workbook.annotation.Col;
import com.loserico.workbook.unmarshal.assassinator.POJOAssassinator;
import com.loserico.workbook.unmarshal.command.LocalDateCellCommand;
import com.loserico.workbook.unmarshal.command.StringCellCommand;
import com.loserico.workbook.utils.ReflectionUtils;

/**
 * 负责构造 POJOAssassinator 对象, 但是该对象还没有完成初始化
 * 在这边只完成了POJO相关信息的初始化, 对应Excel中的相关信息还未初始化, 比如POJOAssassinator对应Sheet中的哪一列还未确定
 * 
 * <p>
 * Copyright: Copyright (c) 2019-05-23 16:01
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class POJOAssassinatorBuilder {

	public static List<POJOAssassinator> build(Class<?> pojoType) {
		Objects.requireNonNull(pojoType);
		Map<Field, Col> fieldAnnotationMap = ReflectionUtils.annotatedField(pojoType, Col.class);

		List<POJOAssassinator> assassinators = new ArrayList<>();
		for (Entry<Field, Col> entry : fieldAnnotationMap.entrySet()) {
			Field field = entry.getKey();
			Col annotation = entry.getValue();
			POJOAssassinator assassinator = new POJOAssassinator();
			assassinator.setColumnName(annotation.name());
			assassinator.setCellIndex(annotation.index());
			assassinator.setFallbackName(annotation.fallback());
			assassinators.add(assassinator);

			Class<?> fieldType = field.getType();
			if (fieldType.isAssignableFrom(String.class)) {
				assassinator.setCellCommand(new StringCellCommand(field));
				continue;
			}
			if (fieldType.isAssignableFrom(LocalDate.class)) {
				assassinator.setCellCommand(new LocalDateCellCommand(field));
				continue;
			}
		}

		return assassinators;
	}
}
