package org.loser.serializer.jackson2;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.charset.Charset;
import java.util.Objects;

import org.loser.serializer.Serializer;
import org.loser.serializer.exception.SerializationException;
import org.loser.serializer.utils.SerializationUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

public class Jackson2JsonRedisSerializer implements Serializer {

	public static final Charset DEFAULT_CHARSET = UTF_8;

	private ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Sets the {@code ObjectMapper} for this view. If not set, a default
	 * {@link ObjectMapper#ObjectMapper() ObjectMapper} is used. <p> Setting a
	 * custom-configured {@code ObjectMapper} is one way to take further control of
	 * the JSON serialization process. For example, an extended
	 * {@link SerializerFactory} can be configured that provides custom serializers
	 * for specific types. The other option for refining the serialization process is
	 * to use Jackson's provided annotations on the types to be serialized, in which
	 * case a custom-configured ObjectMapper is unnecessary.
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		Objects.requireNonNull(objectMapper, "'objectMapper' must not be null");
		this.objectMapper = objectMapper;
	}

	@Override
	public byte[] toBytes(Object object) {
		if (object == null) {
			return SerializationUtils.EMPTY_ARRAY;
		}
		try {
			return this.objectMapper.writeValueAsBytes(object);
		} catch (Exception ex) {
			throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
		}
	}

	@Override
	public <T> T toObject(byte[] bytes, Class<T> clazz) {
		return null;
	}

}