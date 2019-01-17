package org.loser.serializer.jackson2;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.loser.serializer.jackson2.module.CacheModule;
import org.loser.serializer.mixins.SimpleKeyMixin;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

/**
 * @author Christoph Strobl
 * @since 1.6
 */
public class GenericJackson2JsonRedisSerializer implements RedisSerializer<Object> {

	private final ObjectMapper mapper;

	/**
	 * Creates {@link GenericJackson2JsonRedisSerializer} and configures
	 * {@link ObjectMapper} for default typing.
	 */
	public GenericJackson2JsonRedisSerializer() {
		this((String) null);
	}

	/**
	 * Creates {@link GenericJackson2JsonRedisSerializer} and configures
	 * {@link ObjectMapper} for default typing using the given {@literal name}. In
	 * case of an {@literal empty} or {@literal null} String the default
	 * {@link JsonTypeInfo.Id#CLASS} will be used.
	 * 
	 * @param classPropertyTypeName Name of the JSON property holding type
	 *            information. Can be {@literal null}.
	 */
	public GenericJackson2JsonRedisSerializer(String classPropertyTypeName) {

		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDateTime.class,
				new LocalDateTimeSerializer(ofPattern("yyyy-MM-dd HH:mm:ss")));
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(ofPattern("yyyy-MM-dd")));
		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(ofPattern("HH:mm:ss")));
		javaTimeModule.addDeserializer(LocalDateTime.class,
				new LocalDateTimeDeserializer(ofPattern("yyyy-MM-dd HH:mm:ss")));
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(ofPattern("yyyy-MM-dd")));
		javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(ofPattern("HH:mm:ss")));
		objectMapper.registerModule(javaTimeModule);
		objectMapper.registerModule(new Hibernate5Module());
		objectMapper.registerModule(new CacheModule());

		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		objectMapper.addMixIn(SimpleKey.class, SimpleKeyMixin.class);

		this.mapper = objectMapper;
		//		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		if (StringUtils.hasText(classPropertyTypeName)) {
			mapper.enableDefaultTypingAsProperty(DefaultTyping.NON_FINAL, classPropertyTypeName);
		} else {
			mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
		}
	}

	/**
	 * Setting a custom-configured {@link ObjectMapper} is one way to take further
	 * control of the JSON serialization process. For example, an extended
	 * {@link SerializerFactory} can be configured that provides custom serializers
	 * for specific types.
	 * 
	 * @param mapper must not be {@literal null}.
	 */
	public GenericJackson2JsonRedisSerializer(ObjectMapper mapper) {
		Assert.notNull(mapper, "ObjectMapper must not be null!");
		this.mapper = mapper;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.redis.serializer.RedisSerializer#serialize(java.lang.
	 * Object)
	 */
	@Override
	public byte[] serialize(Object source) throws SerializationException {

		if (source == null) {
			return null;
		}

//		if (source instanceof String) {
//			return ((String) source).getBytes(UTF_8);
//		}

		try {
			return mapper.writeValueAsBytes(source);
		} catch (JsonProcessingException e) {
			throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.redis.serializer.RedisSerializer#deserialize(byte[])
	 */
	@Override
	public Object deserialize(byte[] source) throws SerializationException {
		return deserialize(source, Object.class);
	}

	/**
	 * @param source can be {@literal null}.
	 * @param type must not be {@literal null}.
	 * @return {@literal null} for empty source.
	 * @throws SerializationException
	 */
	public <T> T deserialize(byte[] source, Class<T> type) throws SerializationException {

		Assert.notNull(type,
				"Deserialization type must not be null! Pleaes provide Object.class to make use of Jackson2 default typing.");

		if (source == null || source.length == 0) {
			return null;
		}

		try {
			return mapper.readValue(source, type);
		} catch (Exception ex) {
			throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}
}