package com.loserico.search.annotation.parser;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.loserico.commons.utils.ReflectionUtils;
import com.loserico.search.annotation.Indexed;
import com.loserico.search.exeption.IndexedConfigurationException;

/**
 * @Index注解解析器
 * <p>
 * Copyright: Copyright (c) 2018-08-20 21:19
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class IndexedParser {

	private String index;

	private String type;

	private String idField; //记录的是主键字段名

	private IndexedParser() {
	}

	private IndexedParser(String index, String type, String idField) {
		this.index = index;
		this.type = type;
		this.idField = idField;
	}

	public static IndexedParser parse(Class<?> clazz) {
		if (clazz == null) {
			return new IndexedParser();
		}

		Indexed indexed = clazz.getAnnotation(Indexed.class);
		if (indexed == null) {
			return new IndexedParser();
		}

		String index = indexed.index();
		String type = indexed.type();

		if (isBlank(index) || isBlank(type)) {
			return new IndexedParser();
		}

		IndexedParser indexedParser = new IndexedParser(index, type, indexed.id());
		return indexedParser;
	}

	/**
	 * 验证注解配置是否合法，不合法则抛出异常
	 */
	public IndexedParser requireLegal() {
		boolean legal = isNotBlank(index) && isNotBlank(type) && isNotBlank(idField);
		if (!legal) {
			throw new IndexedConfigurationException("POJO需要标注@Indexed,并且需要指定index,type,id");
		}
		
		return this;
	}

	/**
	 * 从POJO中根据主键字段名抽取主键值
	 * 
	 * @param pojo
	 * @param idField
	 * @return String
	 */
	private static String extractId(Object pojo, String idField) {
		Object idValue = ReflectionUtils.getField(pojo, idField);
		if (idValue == null) {
			return null;
		}
		return idValue.toString();
	}

	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	public String getId(Object pojo) {
		return extractId(pojo, idField);
	}

	public String getIdField() {
		return idField;
	}

}
