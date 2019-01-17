package com.loserico.web.validation;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.loserico.web.validation.annotation.Mobile;

public class MobileValidator implements ConstraintValidator<Mobile, String> {

	private static Pattern pattern = Pattern
			.compile("^(0|86|17951)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$");

	@Override
	public void initialize(Mobile constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		//不做必填验证
		if (isBlank(value)) {
			return true;
		}

		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

}
