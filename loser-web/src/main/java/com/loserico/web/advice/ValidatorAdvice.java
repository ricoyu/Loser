package com.loserico.web.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import com.loserico.web.validation.CollectionValidator;

/**
 * Controller advice that adds the {@link CollectionValidator} to the
 * {@link WebDataBinder}.
 * 
 * @author DISID CORPORATION S.L. (www.disid.com)
 */
@ControllerAdvice
public class ValidatorAdvice {

	@Autowired
	protected LocalValidatorFactoryBean validator;

	/**
	 * Adds the {@link CollectionValidator} to the supplied {@link WebDataBinder}
	 * 
	 * @param binder web data binder.
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(new CollectionValidator(validator));
	}
}