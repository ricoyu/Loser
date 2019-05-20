package com.loserico.workbook.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Columns {

	/**
	 * 要映射的Excel列名
	 * 
	 * @return
	 */
	String name();

	/**
	 * 实际操作中经常会同样的数据, 出现多个版本的字段名 比如某列今天叫单据号, 一段时间后改成业务单号
	 * fallback()的作用就是在找不到name()指定的列名时, 转而去找fallback()指定的列
	 * 
	 * @return String
	 * @on
	 */
	String fallback();

	/**
	 * 有时可能不想用列名去匹配, 比如我确定这个字段要映射到第一列
	 * 
	 * @return
	 */
	int index();
}
