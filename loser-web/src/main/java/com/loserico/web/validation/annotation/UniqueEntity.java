package com.loserico.web.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.loserico.web.validation.UniqueEntityValidator;

/**
 * 验证某个字段或者某些字段的组合反映到数据中应该是唯一的
 * 
 * @author Rico Yu ricoyu520@gmail.com
 * @since 2017-06-05 17:29
 * @version 1.0
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = UniqueEntityValidator.class)
@Repeatable(value = UniqueEntities.class)
@Documented
public @interface UniqueEntity {

	String table();

	/**
	 * 要检查唯一性的数据库表字段名
	 * 
	 * @return
	 */
	String[] fieldNames();

	/**
	 * 需要检查唯一性的Bean属性名
	 * 
	 * @return
	 */
	String[] properties();

	/**
	 * Bean中持有主键的属性名，默认为id，表的主键名默认为ID
	 * 
	 * @return
	 */
	String primaryKey() default "id";
	
	/**
	 * <blockquote><pre>
	 * 表设计是都采用了软删除，默认为true
	 * 即一个已经存在的被软删除的对象不会影响数据校验的结果
	 * </pre></blockquote>
	 */
	boolean isSoftDelete() default true;

	String message() default "Entity already exists.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String[] value() default "";
}