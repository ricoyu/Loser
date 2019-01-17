package com.loserico.web.intercepter;

import java.util.Locale;

import com.peacefish.spring.utils.LocaleUtils;

public class LocaleChangeInterceptor extends org.springframework.web.servlet.i18n.LocaleChangeInterceptor{

	@Override
	protected Locale parseLocaleValue(String locale) {
		return LocaleUtils.toLocale(locale);
	}

}
