package com.loserico.generic.typeInfo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class GenericReflectionUtils2 {

	private static final String TYPE_NAME_PREFIX = "class ";

	/**
	 * This method substrings this last value removing the prefix "class " (with the
	 * space) to make it eligible for getClass(Type) that in its turn uses
	 * Class.forName(String) to load the desired class properly.
	 * 
	 * @param type
	 * @return
	 */
	public static String getClassName(Type type) {
		if (type == null) {
			return null;
		}

		String className = type.toString();
		if (className.startsWith(TYPE_NAME_PREFIX)) {
			return className.substring(TYPE_NAME_PREFIX.length());
		}

		return className;
	}

	/**
	 * This method is used to get java.lang.Class object from java.lang.reflect.Type. 
	 * This method takes advantage of the toString() value from a Type which gives 
	 * the fully qualified name of a class as "class some.package.Foo".
	 * 
	 * @param type
	 * @return
	 * @throws ClassNotFoundException
	 * @on
	 */
	public static Class<?> getClass(Type type) throws ClassNotFoundException {
		String className = getClassName(type);
		if (className != null) {
			return Class.forName(className);
		}

		return null;
	}

	/**
	 * This method creates a newly allocated instance of the class represented by
	 * the invoked Type object. The given Type should not represents an abstract
	 * class, an interface, an array class, a primitive type, or void, otherwise an
	 * InstantiationException is thrown.
	 * 
	 * @param type
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static Object newInstance(Type type) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Class<?> clazz = getClass(type);
		if (clazz == null) {
			return null;
		}

		return clazz.newInstance();
	}

	/**
	 * This method returns an array of Type[] objects representing the actual type
	 * arguments to fiven object
	 * 
	 * @param object
	 * @return
	 */
	public static Type[] getParameterizedTypes(Object object) {
		if (object == null) {
			return null;
		}
		Type superClassType = object.getClass().getGenericSuperclass();
		if (!ParameterizedType.class.isAssignableFrom(superClassType.getClass())) {
			return null;
		}
		return ((ParameterizedType) superClassType).getActualTypeArguments();
	}

	/**
	 * This method checks whether a java.lang.reflect.Constructor object with no
	 * parameter types is specified by the invoked Class object or not.
	 * 
	 * @param clazz
	 * @return
	 * @throws SecurityException
	 */
	public static boolean hasDefaultConstructor(Class<?> clazz) throws SecurityException {
		Class<?>[] empty = {};
		try {
			clazz.getConstructor(empty);
		} catch (NoSuchMethodException e) {
			return false;
		}
		return true;
	}
}
