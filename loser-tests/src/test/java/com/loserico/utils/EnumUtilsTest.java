package com.loserico.utils;
import org.junit.Test;

import com.loserico.commons.utils.EnumUtils;
import com.loserico.commons.utils.ReflectionUtils;

public class EnumUtilsTest {

	@Test
	public void testGetField() {
		ExportType preview = ExportType.PREVIEW;
		Object value = ReflectionUtils.getField(preview, ExportType.class, "code");
		System.out.println(value);
		
		EnumUtils.lookupEnum(ExportType.class, "PREVIEW");
		EnumUtils.lookupEnum(ExportType.class, "PREVIEW", "code");
		EnumUtils.lookupEnum(ExportType.class, 1);
		EnumUtils.lookupEnum(ExportType.class, 1, "code");
	}

	public static enum ExportType {
	    /** 预览 */
	    PREVIEW,
	    /** 报告 */
	    REPORT
	    ;
	}
}