package com.loserico.web.resolver;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LocalDateTimeArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return LocalDateTime.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		String value = request.getParameter(parameter.getParameterName());

		if (StringUtils.isBlank(value)) {
			return null;
		}

		LocalDateTime result = null;
		if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}", value)) {
			result = LocalDateTime.parse(value, ofPattern("yyyy-MM-dd HH:mm"));
		} else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}", value)) {
			result = LocalDateTime.parse(value, ofPattern("yyyy-MM-dd HH:mm:ss"));
		} else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", value)) {
			result = LocalDateTime.parse(value, ofPattern("yyyy-MM-dd"));
		}

		return result;
	}
}