package com.loserico.commons.utils;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static org.springframework.beans.BeanUtils.getPropertyDescriptors;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import com.loserico.commons.exception.BeanCloneException;

public class BeanUtils {

	private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

	/**
	 * 将VO拷贝到Persistence 状态的Entity里面， 默认忽略id, creator, modifier, createTime,
	 * modifyTime, version这六个属性
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyToExistingEntity(Object source, Object target) {
		copyProperties(source, target, "id", "creator", "modifier", "createTime", "modifyTime", "version");
	}

	/**
	 * 将VO拷贝到Persistence 状态的Entity里面，默认忽略id, createTime, modifyTime, version这四个属性
	 * <p>额外再忽略ignoreProperties
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyToExistingEntity(Object source, Object target, String... ignoreProperties) {
		List<String> ignoredProperties = new ArrayList<>();
		ignoredProperties.addAll(Arrays.asList(ignoreProperties));
		ignoredProperties.addAll(Arrays.asList("id", "creator", "modifier", "createTime", "modifyTime", "version"));
		copyProperties(source, target, ignoredProperties.stream().toArray(String[]::new));
	}

	/**
	 * 从source拷贝到target，拷贝所有，包括值为null的属性
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyProperties(Object source, Object target) {
		copyProperties(source, target, false);
	}

	/**
	 * 根据class创建相应对象，从source拷贝到target<br/>
	 * 拷贝所有，包括值为null的属性<br/>
	 * 该类需要有一个默认构造函数<p>
	 * 
	 * @param source
	 * @param target
	 * @on
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copyProperties(Object source, Class<? super T> clazz) {
		T target = null;
		try {
			target = (T) clazz.newInstance();
			copyProperties(source, target, false);
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("msg", e);
		}
		return target;
	}

	/**
	 * 从 sources 中取出元素挨个拷贝属性
	 * 根据class创建相应对象，从source拷贝到target，拷贝所有，包括值为null的属性
	 * 
	 * 该类需要有一个默认构造函数
	 * 
	 * @param source
	 * @param target
	 * @on
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> copyProperties(List<?> sources, Class<? super T> clazz) {
		List<T> results = new ArrayList<>();
		for (Object source : sources) {
			results.add((T) copyProperties(source, clazz));
		}

		return results;
	}

	/**
	 * 从 sources 中取出元素挨个拷贝属性
	 * 根据class创建相应对象，从source拷贝到target，拷贝所有，包括值为null的属性
	 * 
	 * 该类需要有一个默认构造函数
	 * 
	 * @param source
	 * @param target
	 * @on
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> copyProperties(List<?> sources, Class<? super T> clazz, String... ignoreProperty) {
		List<T> results = new ArrayList<>();
		for (Object source : sources) {
			results.add((T) copyProperties(source, clazz, ignoreProperty));
		}

		return results;
	}

	/**
	 * 从source拷贝到target，可以指定忽略哪些属性，值为null的属性也会拷贝
	 * 
	 * @param source
	 * @param target
	 * @param ignoreProperties
	 */
	public static void copyProperties(Object source, Object target, String... ignoreProperties) {
		copyProperties(source, target, false, ignoreProperties);
	}

	/**
	 * 从source拷贝到target，可以指定忽略哪些属性，值为null的属性也会拷贝
	 * 
	 * @param source
	 * @param target
	 * @param ignoreProperties
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copyProperties(Object source, Class<? super T> clazz, String... ignoreProperties) {
		T target = null;
		try {
			target = (T) clazz.newInstance();
			copyProperties(source, target, false, ignoreProperties);
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("msg", e);
		}
		return target;
	}

	/**
	 * 拷贝source到target，不拷贝指定的属性
	 * 
	 * @param source
	 * @param target
	 * @param ignoreNull
	 * @param ignoreProperties
	 * @throws BeansException
	 */
	public static void copyProperties(Object source, Object target, boolean ignoreNull, String... ignoreProperties)
			throws BeansException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

		for (PropertyDescriptor targetPd : targetPds) {
			Method writeMethod = targetPd.getWriteMethod();
			if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
				PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null) {
					Method readMethod = sourcePd.getReadMethod();
					if (readMethod != null &&
							ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
						try {
							if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
								readMethod.setAccessible(true);
							}
							Object value = readMethod.invoke(source);
							//如果指定了ignoreNull，则不拷贝值为null的属性
							if (ignoreNull && value == null) {
								continue;
							}
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, value);
						} catch (Throwable ex) {
							throw new FatalBeanException(
									"Could not copy property '" + targetPd.getName() + "' from source to target", ex);
						}
					}
				}
			}
		}
	}

	/**
	 * 拷贝source到target，不拷贝指定的属性
	 * 
	 * @param source
	 * @param target
	 * @param ignoreNull
	 * @param ignoreProperties
	 * @throws BeansException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T copyProperties(Object source, Class<? super T> clazz, boolean ignoreNull,
			String... ignoreProperties) {
		T target = null;
		try {
			target = (T) clazz.newInstance();
			copyProperties(source, target, ignoreNull, ignoreProperties);
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error("msg", e);
		}
		return target;
	}

	@SuppressWarnings("unchecked")
	public static <T> T cloneBean(T bean) {
		try {
			return (T) BeanUtilsBean.getInstance().cloneBean(bean);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException
				| NoSuchMethodException e) {
			throw new BeanCloneException(e);
		}

	}
}
