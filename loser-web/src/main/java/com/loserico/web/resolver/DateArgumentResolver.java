package com.loserico.web.resolver;

import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.loserico.commons.utils.DateUtils;

/**
 * 
 * @author Loser
 * @since May 22, 2016
 * @version 
 *
 */
public class DateArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return Date.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		String value = request.getParameter(parameter.getParameterName());

		if (StringUtils.isBlank(value)) {
			return null;
		}

		Date result = null;
		if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}", value)) {
			result = DateUtils.parse(value, DateUtils.ISO_DATETIME_SHORT);
		} else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}", value)) {
			result = DateUtils.parse(value, DateUtils.ISO_DATETIME);
		} else if (Pattern.matches("\\d{4}-\\d{2}-\\d{1}\\s+\\d{2}:\\d{2}:\\d{2}", value)) {
			result = DateUtils.parse(value, "yyyy-MM-dd HH:mm:ss");
		} else if (Pattern.matches("\\d{4}-\\d{2}-\\d{2}", value)) {
			result = DateUtils.parse(value, DateUtils.ISO_DATE);
		}

		return result;
	}
}
