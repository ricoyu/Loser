package com.loserico.junit.spring;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

public class LoserService implements InitializingBean, DisposableBean, ApplicationContextAware,
		ApplicationEventPublisherAware, BeanClassLoaderAware, BeanFactoryAware,
		BeanNameAware, EnvironmentAware, ImportAware, ResourceLoaderAware {

	private String name;

	public String getName() {
		return name;
	}

	public LoserService setName(String name) {
		System.out.println("LoserService中利用set方法设置属性值");
		this.name = name;
		return this;
	}

	public LoserService() {
		System.out.println("调用LoserService无参构造函数");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("执行InitializingBean接口的afterPropertiesSet方法");

	}

	@Override
	public void destroy() throws Exception {
		System.out.println("执行DisposableBean接口的destroy方法");
	}

	// 通过<bean>的destroy-method属性指定的销毁方法
	public void destroyMethod() throws Exception {
		System.out.println("执行配置的destroy-method");
	}

	// 通过<bean>的init-method属性指定的初始化方法
	public void initMethod() throws Exception {
		System.out.println("执行配置的init-method");
	}

	@PostConstruct
	public void initPostConstruct() {
		System.out.println("执行@PostConstruct注解标注的方法");
	}

	@PreDestroy
	public void preDestroy() {
		System.out.println("执行@PreDestroy注解标注的方法");
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		System.out.println(
				"执行BeanClassLoaderAware接口定义的setBeanClassLoader,ClassLoader Name = " + classLoader.getClass().getName());
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("执行BeanFactoryAware接口的setBeanFactory, singleton="
				+ beanFactory.isSingleton("LoserService"));
	}

	@Override
	public void setBeanName(String s) {
		System.out.println("执行BeanNameAware接口的setBeanName:: Bean Name defined in context=" + s);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("执行ApplicationContextAware接口的setApplicationContext:: Bean Definition Names="
				+ String.join(", ", applicationContext.getBeanDefinitionNames()));

	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		System.out.println("执行ApplicationEventPublisherAware接口的setApplicationEventPublisher");
	}

	@Override
	public void setEnvironment(Environment environment) {
		System.out.println("执行EnvironmentAware接口的setEnvironment");
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {

		Resource resource = resourceLoader.getResource("classpath:spring-beans.xml");
		System.out.println("执行ResourceLoaderAware接口的setResourceLoader:: Resource File Name=" + resource.getFilename());

	}

	@Override
	public void setImportMetadata(AnnotationMetadata annotationMetadata) {
		System.out.println("执行ImportAware接口的setImportMetadata");
	}
}