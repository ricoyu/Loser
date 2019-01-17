package com.loserico.generic.typeInfo;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyClass {

	protected List<Integer> stringList = new ArrayList<>();

	public List<Integer> getStringList() {
		return this.stringList;
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Method method = MyClass.class.getMethod("getStringList", null);
		Type[] types = GenericReflectionUtils.getMethodReturnTypeArgs(MyClass.class, "getStringList");
		for(Type type : types) {
			System.out.println(type);
		}
	}
}