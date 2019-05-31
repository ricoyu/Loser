package com.loserico.cache.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReflectionUtils {

	private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Object target, String methodName, String arg) {
		try {
			Method method = target.getClass().getMethod(methodName, String.class);
			return (T) method.invoke(target, arg);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Object target, String methodName, Class<T> type) {
		try {
			Method method = target.getClass().getMethod(methodName, type);
			return (T) method.invoke(target, type);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Object target, String methodName, Class<T> type, String beanName) {
		try {
			Method method = target.getClass().getMethod(methodName, type);
			return (T) method.invoke(target, beanName, type);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}
}
