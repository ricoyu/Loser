package com.loserico.commons.unmarshal;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.ArrayList;
import java.util.List;

import com.loserico.commons.jackson.JacksonUtils;
import com.loserico.commons.utils.PrimitiveUtils;

public class Unmarshaller {

	public static <T> T toObject(byte[] data, Class<T> clazz) {
		if (data == null || data.length == 0) {
			return null;
		}
		if (clazz.equals(String.class)) {
			return (T) new String(data, UTF_8);
		}
		T result = PrimitiveUtils.toPrimitive(data, clazz);
		if (result != null) {
			return result;
		}
		return JacksonUtils.toObject(toString(data), clazz);
	}
	
	public static Integer toInteger(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		return new Integer(new String(data, UTF_8));
	}
	
	public static Long toLong(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		return new Long(new String(data, UTF_8));
	}

	public static <T> List<T> toList(byte[] value, Class<T> clazz) {
		if (value == null || value.length == 0) {
			return new ArrayList<>();
		}
		String json = toString(value);
		return JacksonUtils.toList(json, clazz);
	}

	public static String toString(byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		return new String(data, UTF_8);
	}
}
