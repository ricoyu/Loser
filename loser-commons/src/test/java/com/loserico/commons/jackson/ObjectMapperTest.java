package com.loserico.commons.jackson;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperTest {

	@Test
	public void testUTC_CST() throws Exception {
			ObjectMapperFactoryBean factoryBean = new ObjectMapperFactoryBean();
			ObjectMapper objectMapper = factoryBean.getObject();
			LocalDateTime now =LocalDateTime.now();
			System.out.println(now);
			String timestamp = objectMapper.writeValueAsString(now);
			System.out.println(timestamp);
//			LocalDateTime now = LocalDateTime.now();
//			System.out.println(now);
//			String timestamp = objectMapper.writeValueAsString(now);
//			System.out.println(timestamp);
//			now = objectMapper.readValue(timestamp, LocalDateTime.class);
//			System.out.println(now);
			
//			LocalDateTime now = Instant.ofEpochMilli(1520221143070L).atZone(ZoneOffset.UTC).toLocalDateTime();
//			System.out.println(now);
//			now = objectMapper.readValue("1520221143070", LocalDateTime.class);
//			System.out.println(now);
//			String timestamp = objectMapper.writeValueAsString(now);
//			System.out.println(timestamp);
//			LocalDateTime now = objectMapper.readValue("1520186359263", LocalDateTime.class);
//			System.out.println(now);
			
//			Person person = objectMapper.readValue("{\"birthday\": \"2018-03-02\", \"birthday1\": \"2018-03-02 07:19\", \"birthday2\": \"2018-03-02 07:23:00\", \"birthday3\": \"2018-03-02 07:23:00.666\", \"birthday4\": \"2018-03-02T07:23:00.666\"}", Person.class);
//			Person person = objectMapper.readValue("{\"birthday\": \"2018-03-02T16:19:00\"}", Person.class);
//			Person person = objectMapper.readValue("{\"birthday\": \"2018-03-02 16:19:00\"}", Person.class);
//			LocalDateTime localDateTime = objectMapper.readValue("2018-03-02T16:19:00.666", LocalDateTime.class);
//			System.out.println(person.getBirthday()); //2018-03-02
//			System.out.println(person.getBirthday1()); //2018-03-02 07:19
//			System.out.println(person.getBirthday2()); //2018-03-02 07:23:00
//			System.out.println(person.getBirthday3()); //2018-03-02 07:23:00.666
//			System.out.println(person.getBirthday4()); //2018-03-02T07:23:00.666
			System.out.println("=====================");
//			System.out.println(objectMapper.writeValueAsString(person.getBirthday())); //2018-03-02
//			System.out.println(objectMapper.writeValueAsString(person.getBirthday1())); //2018-03-02 07:19
//			System.out.println(objectMapper.writeValueAsString(person.getBirthday2())); //2018-03-02 07:23:00
//			System.out.println(objectMapper.writeValueAsString(person.getBirthday3())); //2018-03-02 07:23:00.666
//			System.out.println(objectMapper.writeValueAsString(person.getBirthday4())); //2018-03-02T07:23:00.666
//			System.out.println(localDateTime);
//			System.out.println("yyyy-MM-dd".length());
//			System.out.println("yyyy-MM-dd HH:mm".length());
//			System.out.println("yyyy-MM-dd HH:mm:ss".length());
//			System.out.println("yyyy-MM-dd HH:mm:ss.SSS".length());
	}
}
