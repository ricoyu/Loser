package org.loser.serializer.utils;

import static com.alibaba.fastjson.serializer.SerializerFeature.PrettyFormat;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public final class FastJsonUtils {

	static {
		JSON.DEFAULT_GENERATE_FEATURE = SerializerFeature.config(JSON.DEFAULT_GENERATE_FEATURE,
				SerializerFeature.SkipTransientField, false);
		JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	}

	public static String toPrettyJson(Object bean) {
		if (null == bean) {
			return null;
		}
		return JSON.toJSONStringWithDateFormat(bean, "yyyy-MM-dd HH:mm:ss", WriteMapNullValue, PrettyFormat);
	}

	public static String toJson(Object bean) {
		if (null == bean) {
			return null;
		}
		return JSON.toJSONStringWithDateFormat(bean, "yyyy-MM-dd HH:mm:ss", WriteMapNullValue);
	}
}
