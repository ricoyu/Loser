package com.loserico.commons.jackson;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.loserico.commons.jackson.deserializer.EnumDeserializer;
import com.loserico.commons.jackson.serializer.LocalDateDeserializer;
import com.loserico.commons.jackson.serializer.LocalDateSerializer;
import com.loserico.commons.jackson.serializer.LocalDateTimeDeserializer;
import com.loserico.commons.jackson.serializer.LocalDateTimeSerializer;

/**
 * 统一用UTC时区
 * Java8 日期对象将会被序列化成Epoch毫秒数
 * 转成Java8 日期对象支持如下格式：
 * 	毫秒数
 * 	2018-03-02
 * 	2018-03-02 07:19
 * 	2018-03-02 07:23:00
 * 	2018-03-02 07:23:00.666
 * 	2018-03-02T07:23:00.666
 * <p>
 * Copyright: Copyright (c) 2017-11-02 09:20
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class ObjectMapperFactoryBean implements FactoryBean<ObjectMapper> {

	private Set<String> enumProperties = new HashSet<>();
	private boolean epochBased = true;

	@Override
	public ObjectMapper getObject() throws Exception {
		if (epochBased) {
			return epochMilisBasedObjectMapper();
		} else {
			return stringBasedObjectMapper();
		}
	}

	private ObjectMapper stringBasedObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class, new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(ofPattern("yyyy-MM-dd HH:mm:ss")));
		//TODO [2018-07-16 18:25:46, RicoYu]:如何同时支持多种日期格式反序列化成LocalDateTime
		//javaTimeModule.addDeserializer(LocalDateTime.class, new com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer(ofPattern("yyyy-MM-dd HH:mm:ss")));
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(ofPattern("yyyy-MM-dd HH:mm:ss")));
		
		javaTimeModule.addSerializer(LocalDate.class, new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer(ofPattern("yyyy-MM-dd")));
		javaTimeModule.addDeserializer(LocalDate.class, new com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer(ofPattern("yyyy-MM-dd")));
		
		javaTimeModule.addSerializer(LocalTime.class, new com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer(ofPattern("HH:mm:ss")));
		javaTimeModule.addDeserializer(LocalTime.class, new com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer(ofPattern("HH:mm:ss")));
		objectMapper.registerModule(javaTimeModule);

		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		SimpleModule customModule = new SimpleModule();
		customModule.setDeserializerModifier(new BeanDeserializerModifier() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public JsonDeserializer<Enum> modifyEnumDeserializer(DeserializationConfig config,
					final JavaType type,
					BeanDescription beanDesc,
					final JsonDeserializer<?> deserializer) {

				return new EnumDeserializer((Class<Enum<?>>) type.getRawClass(), enumProperties);
			}

		});
		objectMapper.registerModule(customModule);
		
		/*
		 * 不要对key排序，否则在调x.compareTo(y)时，如果
		 */
		//objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		return objectMapper;
	}

	private ObjectMapper epochMilisBasedObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// formatter that accepts an epoch millis value
		DateTimeFormatter epochMilisFormater = new DateTimeFormatterBuilder()
				.appendValue(ChronoField.INSTANT_SECONDS, 1, 19, SignStyle.NEVER) // epoch seconds
				.appendValue(ChronoField.MILLI_OF_SECOND, 3)// milliseconds
				.toFormatter().withZone(ZoneOffset.ofHours(8));// 时区统一东八区

		JavaTimeModule module = new JavaTimeModule();
		module.addSerializer(LocalDate.class, new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer(ofPattern("yyyy-MM-dd")));
		module.addDeserializer(LocalDate.class, new LocalDateDeserializer());
		
		module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
		module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(epochMilisFormater));
		
		module.addSerializer(LocalTime.class, new LocalTimeSerializer(ofPattern("HH:mm:ss")));
		objectMapper.registerModule(module);

		SimpleModule customModule = new SimpleModule();
		customModule.setDeserializerModifier(new BeanDeserializerModifier() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public JsonDeserializer<Enum> modifyEnumDeserializer(DeserializationConfig config,
					final JavaType type,
					BeanDescription beanDesc,
					final JsonDeserializer<?> deserializer) {

				return new EnumDeserializer((Class<Enum<?>>) type.getRawClass(), enumProperties);
			}

		});
		objectMapper.registerModule(customModule);

		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

		return objectMapper;
	}

	@Override
	public Class<ObjectMapper> getObjectType() {
		return ObjectMapper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public Set<String> getEnumProperties() {
		return enumProperties;
	}

	public void setEnumProperties(Set<String> enumProperties) {
		this.enumProperties = enumProperties;
	}

	public boolean isEpochBased() {
		return epochBased;
	}

	public void setEpochBased(boolean epochBased) {
		this.epochBased = epochBased;
	}
}