package com.loserico.orm.hibernate.reflection;

import java.io.Serializable;

import javax.persistence.AttributeConverter;

import com.loserico.orm.convertor.AllInOneTypeConvertor;

public class MethodAccessWrapper implements Serializable {
	private static final long serialVersionUID = 5196975494169894941L;
	
	AllInOneTypeConvertor allInOneTypeConvertor = AllInOneTypeConvertor.getInstance();
	private int methodIndex;
	private int convertorMethodIndex = -1;
	private AttributeConverter<?, ?> converter;

	@SuppressWarnings("rawtypes")
	private Class paramType;

	public int getMethodIndex() {
		return methodIndex;
	}

	public void setMethodIndex(int methodIndex) {
		this.methodIndex = methodIndex;
	}

	@SuppressWarnings("rawtypes")
	public Class getParamType() {
		return paramType;
	}

	@SuppressWarnings("rawtypes")
	public void setParamType(Class paramType) {
		this.paramType = paramType;
	}

	public int getConvertorMethodIndex() {
		return convertorMethodIndex;
	}

	public void setConvertorMethodIndex(int convertorMethodIndex) {
		this.convertorMethodIndex = convertorMethodIndex;
	}

	public AttributeConverter getConverter() {
		return converter;
	}

	public void setConverter(AttributeConverter converter) {
		this.converter = converter;
	}

}