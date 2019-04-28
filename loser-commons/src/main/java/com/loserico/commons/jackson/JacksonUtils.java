package com.loserico.commons.jackson;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.loserico.commons.jackson.deserializer.EnumDeserializer;
import com.loserico.commons.resource.PropertyReader;

/**
 * Jackson工具类
 * <p>
 * Copyright: Copyright (c) 2017-10-30 13:13
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@SuppressWarnings("unused")
public final class JacksonUtils {

	private static final Logger logger = LoggerFactory.getLogger(JacksonUtils.class);

	/*
	 * 这里可以自定义Jackson的一些行为, 默认读取的是classpath根目录下的jackson.properties文件
	 * 
	 * #序列化/反序列化优先采用毫秒数方式还是日期字符串形式, false表示采用日期字符串
	 * jackson.epoch.date=false
	 * 
	 * 可以识别的enum类型的属性
	 * jackson.enum.propertes=code, desc, alias
	 * @on
	 */
	private static final PropertyReader propertyReader = new PropertyReader("jackson");
	private static Set<String> enumProperties = propertyReader.getStringAsSet("jackson.enum.propertes");
	private static boolean epochBased = propertyReader.getBoolean("jackson.epoch.date", false);

	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		if (epochBased) { // 这是基于epoch miliseconds的
			DateTimeFormatter epochMilisFormater = new DateTimeFormatterBuilder()
					.appendValue(ChronoField.INSTANT_SECONDS, 1, 19, SignStyle.NEVER) // epoch seconds
					.appendValue(ChronoField.MILLI_OF_SECOND, 3)// milliseconds
					.toFormatter().withZone(ZoneOffset.ofHours(8));// 时区统一东八区

			JavaTimeModule module = new JavaTimeModule();
			module.addSerializer(LocalDate.class,
					new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer(ofPattern("yyyy-MM-dd")));
			module.addDeserializer(LocalDate.class,
					new com.loserico.commons.jackson.serializer.LocalDateDeserializer());

			module.addSerializer(LocalDateTime.class,
					new com.loserico.commons.jackson.serializer.LocalDateTimeSerializer());
			module.addDeserializer(LocalDateTime.class,
					new com.loserico.commons.jackson.serializer.LocalDateTimeDeserializer(epochMilisFormater));

			module.addSerializer(LocalTime.class, new LocalTimeSerializer(ofPattern("HH:mm:ss")));
			objectMapper.registerModule(module);
		} else { // 在这是基于日期字符串的形式
			javaTimeModule.addSerializer(LocalDateTime.class,
					new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer(
							ofPattern("yyyy-MM-dd HH:mm:ss")));
			javaTimeModule.addDeserializer(LocalDateTime.class,
					new com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer(
							ofPattern("yyyy-MM-dd HH:mm:ss")));

			javaTimeModule.addSerializer(LocalDate.class,
					new com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer(ofPattern("yyyy-MM-dd")));
			javaTimeModule.addDeserializer(LocalDate.class,
					new com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer(ofPattern("yyyy-MM-dd")));

			javaTimeModule.addSerializer(LocalTime.class,
					new com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer(ofPattern("HH:mm:ss")));
			javaTimeModule.addDeserializer(LocalTime.class,
					new com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer(ofPattern("HH:mm:ss")));
		}

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
		objectMapper.registerModule(javaTimeModule);

		try {
			Class<?> clazz = Class.forName("javax.persistence.Transient");
			objectMapper.registerModule(new Hibernate5Module());
		} catch (ClassNotFoundException e) {
			logger.info("Hibernate5 module not registed.");
		}

		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
	}

	/**
	 * 将json字符串转成指定对象
	 * 
	 * @param json
	 * @param clazz
	 * @return T
	 */
	public static <T> T toObject(String json, Class<T> clazz) {
		if (isBlank(json)) {
			return null;
		}
		
		if (clazz.isAssignableFrom(String.class)) {
			return (T)json;
		}
		try {
			return objectMapper.readValue(json, clazz);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static <T> T toObject(byte[] src, Class<T> clazz) {
		try {
			return objectMapper.readValue(src, clazz);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * JSON字符串转MAP
	 * 
	 * @param json
	 * @return
	 */
	public static <T> Map<String, T> toMap(String json) {
		if (isBlank(json)) {
			return emptyMap();
		}
		Map<String, T> map = new HashMap<String, T>();
		try {
			map = objectMapper.readValue(json, new TypeReference<Map<String, T>>() {
			});
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return map;
	}

	public static <T> List<T> toList(String jsonArray, Class<T> clazz) {
		if (isBlank(jsonArray)) {
			return emptyList();
		}
		CollectionType javaType = objectMapper.getTypeFactory()
				.constructCollectionType(List.class, clazz);
		try {
			return objectMapper.readValue(jsonArray, javaType);
		} catch (IOException e) {
			logger.error("Parse json array \n{} \n to List of type {} failed", jsonArray, clazz, e);
			return emptyList();
		}
	}

	/**
	 * 将对象转成json串
	 * 
	 * @param T
	 * @return String
	 */
	public static <T> String toJson(T object) {
		if (object == null) {
			return null;
		}
		String json = null;
		try {
			json = objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	public static byte[] toBytes(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return objectMapper.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
		return new byte[0];
	}

	public static <T> String toPrettyJson(T object) {
		if (object == null) {
			return null;
		}
		String json = null;
		try {
			json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage(), e);
		}
		return json;
	}

	public static void writeValue(Writer writer, Object value) {
		try {
			objectMapper.writeValue(writer, value);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new JSONException(e);
		}
	}

	/**
	 * 直接写数据到HttpServletResponse
	 * 
	 * @param response
	 * @param value
	 */
	public static void writeValue(ServletResponse response, Object value) {
		if (response instanceof HttpServletResponse) {
			writeValue((HttpServletResponse) response, value);
		}
	}

	/**
	 * 直接写数据到HttpServletResponse
	 * 
	 * @param response
	 * @param value
	 */
	public static void writeValue(HttpServletResponse response, Object value) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Access-Control-Allow-Headers", "*");
		response.setCharacterEncoding("UTF-8");
		if (isBlank(response.getContentType())) {
			response.setContentType("application/json");
		}
		try {
			objectMapper.writeValue(response.getWriter(), value);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new JSONException(e);
		}
	}

	public static ObjectMapper objectMapper() {
		return objectMapper;
	}
}
