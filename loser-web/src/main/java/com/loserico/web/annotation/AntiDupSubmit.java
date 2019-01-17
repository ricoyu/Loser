package com.loserico.web.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 防止表单重复提交<br/>
 * 默认1000毫秒内不接受重复提交
 * <p>
 * Copyright: Copyright (c) 2017-09-28 16:10
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface AntiDupSubmit {

	/**
	 * 重复提交时间间隔，默认1秒
	 * @return
	 */
	long value() default 1000L;
}
