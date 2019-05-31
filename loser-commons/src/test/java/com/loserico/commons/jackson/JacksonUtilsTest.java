package com.loserico.commons.jackson;

import java.time.LocalDateTime;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JacksonUtilsTest {

	@Test
	public void testSerializeLocalDateTime() {
		System.out.println(JacksonUtils.toJson(LocalDateTime.now()));;
	}
	
	@Test
	public void testSerializeQuota() throws JsonProcessingException {
		User user = new User();
		user.setAge(12);
		user.setName("星爵");
		System.out.println(JacksonUtils.toJson(user));
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS, true);
		System.out.println(objectMapper.writeValueAsString(user));
	}
	
	static class User {
		private int age;
		private String name;
		
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}
}
