package com.loserico.web.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.loserico.web.validation.AllowedValueValidator;

/**
 * Validate actual value against provided valid candidate values
 * if candidate values are not provided, validate actual value is not empty
 * @author xuehuyu
 * @since May 8, 2015
 * @version 
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllowedValueValidator.class)
@Documented
public @interface AllowedValues {

	/**
	 * Allowed candidate values, default to all values
	 * @return String[]
	 */
	String[] value() default {};
	
	/**
	 * All values are allowed except values provided here
	 * @return String[]
	 */
	String[] except() default {};
	
	/**
	 * Should each value in value and except be checked in case sensitive mode? default false
	 * @return boolean
	 */
	boolean caseSensitive() default false;
	
	//control if this field is mandatory
	boolean mandatory() default true;

	String message() default "Provided value is not valid.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
