package com.loserico;

import java.util.Locale;

import org.junit.Test;

import com.peacefish.spring.utils.LocaleUtils;

public class LocaleTest {

	@Test
	public void testZhCN() {
//		Locale locale = Locale.forLanguageTag("zh");
//		Locale locale = Locale.forLanguageTag("zh_CN");
//		Locale locale = Locale.forLanguageTag("zh-CN");
		System.out.println(Locale.getDefault());
		Locale locale = LocaleUtils.toLocale("zh");
		System.out.println(locale.toString());
		locale = LocaleUtils.toLocale("zh-CN");
		System.out.println(locale.toString());
		locale = LocaleUtils.toLocale("zh_CN");
		System.out.println(locale.toString());
	}
}
