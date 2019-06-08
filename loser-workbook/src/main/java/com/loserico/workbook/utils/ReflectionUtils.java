package com.loserico.workbook.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ConcurrentReferenceHashMap;

import com.loserico.workbook.exception.FieldWriteException;

/**
 * 专为读写Excel定制的反射工具类
 * <p>
 * Copyright: Copyright (c) 2019-05-23 15:37
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class ReflectionUtils {
	
	private static final Map<Class<?>, Field[]> FIELD_CACHE = new ConcurrentReferenceHashMap<Class<?>, Field[]>(256);

	private static final Field[] NO_FIELDS = {};
	
	/**
	 * 在pojoType中查找标注了annotationClass注解的字段, 并封装成Field -> annotationClassInstance的形式
	 * @param <T>
	 * @param pojoType
	 * @param annotationClass
	 * @return Map<Field, T>
	 */
	public static <T extends Annotation> Map<Field, T> annotatedField(Class<?> pojoType, Class<T> annotationClass) {
		Map<Field, T> fieldAnnotationMap = new HashMap<>();
		Field[] fields = getFields(pojoType);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			T annotation = field.getDeclaredAnnotation(annotationClass);
			fieldAnnotationMap.put(field, annotation);
		}
		
		return fieldAnnotationMap;
	}

	public static void setField(Field field, Object target, Object value) {
		try {
			makeAccessible(field);
			field.set(target, value);
		} catch (IllegalAccessException ex) {
			throw new FieldWriteException(ex);
		}
	}
	
	public static void makeAccessible(Field field) {
		field.setAccessible(true);
	}
	
	/**
	 * 拿所有的field, 包括父类的field
	 * @param clazz
	 * @return Field[]
	 */
	public static Field[] getFields(Class<?> clazz) {
		Field[] result = FIELD_CACHE.get(clazz);
		if (result == null) {
			result = clazz.getDeclaredFields();
			FIELD_CACHE.put(clazz, (result.length == 0 ? NO_FIELDS : result));
			getFieldsFromSuper(clazz, clazz.getSuperclass());
		}
		return FIELD_CACHE.get(clazz);
	}
	
	private static void getFieldsFromSuper(Class<?> originalClazz, Class<?> ancesterClazz) {
		if (ancesterClazz == Object.class) {
			return;
		}
		Field[] fieldsFromSuper = ancesterClazz.getDeclaredFields();
		if (fieldsFromSuper.length > 0) {
			Field[] fields = FIELD_CACHE.get(originalClazz);
			List<Field> fieldList = new ArrayList<>();
			if (fields != null) {
				fieldList.addAll(Arrays.asList(fields));
			}

			for (int j = 0; j < fieldsFromSuper.length; j++) {
				boolean overrided = false; //检查父类的field是不是被子类覆盖了
				Field fieldFromSuper = fieldsFromSuper[j];
				if (fields != null) {
					for (int i = 0; i < fields.length; i++) {
						Field field = fields[i];
						if (field.getName().equals(fieldFromSuper.getName())) {
							overrided = true;
							break;
						}
					}
				}
				if (!overrided) {
					fieldList.add(fieldFromSuper);
				}
			}
			
			fields = fieldList.stream().toArray(Field[]::new);
			FIELD_CACHE.put(originalClazz, fields);
		}
		
		if (ancesterClazz.getSuperclass() != Object.class) {
			getFieldsFromSuper(originalClazz, ancesterClazz.getSuperclass());
		}
	}
}
