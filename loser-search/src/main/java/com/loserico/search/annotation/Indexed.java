package com.loserico.search.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记POJO将被索引并指定Index、Type、ID
 * <p>
 * Copyright: Copyright (c) 2018-08-20 21:23
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Indexed {

	/**
	 * The index name 
	 * 默认是实体类名小写
	 * 
	 * @return String
	 * @on
	 */
	String index() default "";

	/**
	 * The type name
	 * Elasticsearch 6.0.0 开始一个index只能有一种mapping type，官方建议用_doc
	 * 
	 * @return String
	 * @on
	 */
	String type() default "_doc";

	/**
	 * 文档的ID保存在哪个字段上，默认ID字段
	 * 
	 * @return String
	 */
	String id() default "id";

}
