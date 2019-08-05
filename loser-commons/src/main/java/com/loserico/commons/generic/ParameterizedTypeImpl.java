package com.loserico.commons.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl implements ParameterizedType {
	
	@SuppressWarnings("rawtypes")
	private final Class raw;
	private final Type[] args;

	public ParameterizedTypeImpl(@SuppressWarnings("rawtypes") Class raw, Type[] args) {
		this.raw = raw;
		this.args = args != null ? args : new Type[0];
	}

	@Override
	public Type[] getActualTypeArguments() {
		return args;
	}

	@Override
	public Type getRawType() {
		return raw;
	}

	@Override
	public Type getOwnerType() {
		return null;
	}
}