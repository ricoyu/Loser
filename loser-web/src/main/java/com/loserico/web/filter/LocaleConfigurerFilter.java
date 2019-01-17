package com.loserico.web.filter;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.loserico.web.i18n.LocaleContextHolder;

public class LocaleConfigurerFilter extends OncePerRequestFilter {

	private WebApplicationContext wac;

	@Override
	protected void initFilterBean() throws ServletException {
		wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		LocaleContextHolder.setMessageSource(wac.getBean(MessageSource.class));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		Locale locale = null;
		LocaleResolver localeResolver = null;

		Map<String, LocaleResolver> resolvers = wac.getBeansOfType(LocaleResolver.class);

		if (resolvers.size() == 1) {
			localeResolver = (LocaleResolver) resolvers.values().iterator().next();
		}

		if (localeResolver != null) {
			locale = localeResolver.resolveLocale(req);
			LocaleContextHolder.setLocale(locale);
		} else {
			locale = RequestContextUtils.getLocale((HttpServletRequest) req);
			LocaleContextHolder.setLocale(locale);
		}

		chain.doFilter(req, res);

	}

}