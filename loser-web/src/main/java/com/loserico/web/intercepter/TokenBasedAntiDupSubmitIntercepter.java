package com.loserico.web.intercepter;

import static com.loserico.commons.http.MediaType.APPLICATION_JSON_UTF8;
import static com.loserico.commons.utils.StringUtils.concat;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.loserico.cache.redis.JedisUtils;
import com.loserico.commons.jackson.JacksonUtils;
import com.loserico.commons.utils.StringUtils;
import com.loserico.web.annotation.AntiDupSubmit;
import com.loserico.web.enums.StatusCode;
import com.loserico.web.utils.CORS;
import com.loserico.web.vo.Result;
import com.loserico.web.vo.Results;

/**
 * 根据token以及所请求的方法，限定一定时间内不可重复提交
 * 
 * Copyright: Copyright (c) 2017-09-28 16:09
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class TokenBasedAntiDupSubmitIntercepter extends HandlerInterceptorAdapter {

	private static final Logger logger = LoggerFactory.getLogger(TokenBasedAntiDupSubmitIntercepter.class);
	private static final String TOKEN_ANTI_SUBMIT_KEY_TEMPLATE = "anti:dup:submit:{0}:{1}";

	/**
	 * token的名字
	 */
	private String token = "access-token";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return super.preHandle(request, response, handler);
		}

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();
		AntiDupSubmit antiDupSubmit = method.getAnnotation(AntiDupSubmit.class);
		
		if (antiDupSubmit != null) {
			long timeout = antiDupSubmit.value();
			String accessToken = StringUtils.clean(request.getParameter(token));
			if (isBlank(accessToken)) {
				return false;
			}
			
			String fullMethodName = concat(method.getDeclaringClass().getName(), ".", method.getName());
			String key = format(TOKEN_ANTI_SUBMIT_KEY_TEMPLATE, fullMethodName, accessToken);
			boolean success = JedisUtils.setnx(key, "", timeout, MILLISECONDS);

			if (!success) {
				logger.info("捕捉到重复提交了:{}", fullMethodName);
				Result result = Results.fail()
						.code(StatusCode.DUPLICATE_SUBMISSION)
						.build();
				response.setContentType(APPLICATION_JSON_UTF8);
				CORS.builder().allowAll().build(response);
				JacksonUtils.writeValue(response, result);
				return false;
			}

		}

		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
