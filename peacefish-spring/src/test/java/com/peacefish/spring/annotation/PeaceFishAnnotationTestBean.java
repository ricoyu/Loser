package com.peacefish.spring.annotation;

import org.springframework.stereotype.Component;

@Component
public class PeaceFishAnnotationTestBean {

	@PeaceFishAnnotation
	public void initialize() {
		System.out.println("初始化......");
	}
}
