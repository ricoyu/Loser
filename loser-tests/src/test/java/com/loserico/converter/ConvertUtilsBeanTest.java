package com.loserico.converter;

import static org.junit.Assert.*;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.junit.Test;

public class ConvertUtilsBeanTest {

	@Test
	public void testConvertUtilsBean() {
		ConvertUtilsBean convertUtilsBean = BeanUtilsBean.getInstance().getConvertUtils();
		String s = System.getProperty("java.io.tmpdir");
		System.out.println(s);
	}
	
}
