package com.peacefish.spring.annotation.processor;

import static java.text.MessageFormat.format;

import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import com.peacefish.spring.annotation.PeaceFishAnnotation;

public class PeaceFishAnnotationProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
		if (methods != null) {
			for (Method method : methods) {
				PeaceFishAnnotation annotation = AnnotationUtils.findAnnotation(method, PeaceFishAnnotation.class);
				String message = (annotation == null) ? "Not found" : "Found";
				System.out.println(format("In {0} {1} @PeaceFishAnnotation", beanName, message));
			}
		}
		return bean;
	}

}
