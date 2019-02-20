package com.loserico.commons.codec;

import static java.nio.charset.StandardCharsets.*;

import java.util.Objects;

import org.apache.commons.codec.binary.Base64;

/**
 * Base64 加密/解密
 * <p>
 * Copyright: Copyright (c) 2019-02-14 16:24
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public final class Base64Utils {

	public static String encode(String source) {
		Objects.requireNonNull(source, "source cannot be null");
		return Base64.encodeBase64String(source.getBytes(UTF_8));
	}
}
