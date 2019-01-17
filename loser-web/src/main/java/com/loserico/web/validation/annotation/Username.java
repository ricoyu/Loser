package com.loserico.web.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.loserico.web.validation.UsernameValidator;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=UsernameValidator.class)
@Documented
public @interface Username {

	String message() default "Please enter valid username";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
