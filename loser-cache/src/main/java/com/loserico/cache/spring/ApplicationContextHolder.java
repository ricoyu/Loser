package com.loserico.cache.spring;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 不依赖Spring相关jar, 通过反射获取ApplicationContext中的Bean
 * <p>
 * Copyright: Copyright (c) 2019-05-31 13:43
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public final class ApplicationContextHolder {

	private static Object applicationContext;

	public static void setApplicationContext(Object applicationContext) {
		ApplicationContextHolder.applicationContext = applicationContext;
	}

	public static <T> T getBean(String beanName) {
		Objects.requireNonNull(applicationContext);
		Objects.requireNonNull(beanName);
		return ReflectionUtils.invokeMethod(applicationContext, "getBean", beanName);
	}

	public static <T> T getBean(String beanName, Class<T> clazz) {
		return ReflectionUtils.invokeMethod(applicationContext, "getBean", clazz, beanName);
	}

	public static <T> T getBean(Class<T> clazz) {
		return ReflectionUtils.invokeMethod(applicationContext, "getBean", clazz);
	}
	
	public static <T> Map<String, T> getBeans(Class<T> clazz) {
		return (Map<String, T>)ReflectionUtils.invokeMethod(applicationContext, "getBeansOfType", clazz);
	}
}