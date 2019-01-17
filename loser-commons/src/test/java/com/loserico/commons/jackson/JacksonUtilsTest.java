package com.loserico.commons.jackson;

import java.time.LocalDateTime;

import org.junit.Test;

public class JacksonUtilsTest {

	@Test
	public void testSerializeLocalDateTime() {
		System.out.println(JacksonUtils.toJson(LocalDateTime.now()));;
	}
}
