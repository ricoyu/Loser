package com.loserico.workbook.exception;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * 解析Excel, 绑定到POJO后执行数据校验失败时抛出该异常
 * <p>
 * Copyright: Copyright (c) 2019-06-09 19:36
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class BindException extends RuntimeException {

	private static final long serialVersionUID = 8698549120652570643L;
	
	private Set<ConstraintViolation<?>> violations = new HashSet<>();

	public BindException() {
	}

	public BindException(String message, Set<ConstraintViolation<?>> violations) {
		super(message);
		this.violations = violations;
	}

	public Set<ConstraintViolation<?>> getViolations() {
		return violations;
	}

	public void setViolations(Set<ConstraintViolation<?>> violations) {
		this.violations = violations;
	}

	@Override
	public String getMessage() {
		return this.violations.toString();
	}
	

}
