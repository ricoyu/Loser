package com.loserico.commons.jackson.serializer;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;
import com.loserico.commons.utils.DateUtils;

/**
 * Deserializer for Java 8 temporal {@link LocalDateTime}s.
 *
 * @author Nick Williams
 * @since 2.2.0
 */
public class LocalDateTimeDeserializer
		extends JSR310DateTimeDeserializerBase<LocalDateTime> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(LocalDateTimeDeserializer.class);

	private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();

	private static final DateTimeFormatter formatter1 = ofPattern("yyyy-MM-dd").withZone(ZoneOffset.ofHours(8)); //10
	private static final DateTimeFormatter formatter2 = ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.ofHours(8)); //16
	private static final DateTimeFormatter formatter3 = ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.ofHours(8)); //19
	private static final DateTimeFormatter formatter4 = ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneOffset.ofHours(8)); //23
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);//这种形式：Fri, 03 Aug 2018 03:42:07 GMT

	private static final DateTimeFormatter UTC_EPOC_HMILIS_FORMATER = new DateTimeFormatterBuilder()
			.appendValue(ChronoField.INSTANT_SECONDS, 1, 19, SignStyle.NEVER)
			.appendValue(ChronoField.MILLI_OF_SECOND, 3)
			.toFormatter().withZone(ZoneOffset.ofHours(8));

	public LocalDateTimeDeserializer() {
		this(UTC_EPOC_HMILIS_FORMATER);
	}

	public LocalDateTimeDeserializer(DateTimeFormatter formatter) {
		super(LocalDateTime.class, formatter);
	}

	@Override
	protected JsonDeserializer<LocalDateTime> withDateFormat(DateTimeFormatter formatter) {
		return new LocalDateTimeDeserializer(formatter);
	}

	@Override
	public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		//数字类型的毫秒数形式：1520007540000
		if (parser.hasTokenId(JsonTokenId.ID_NUMBER_INT)) {
			return LocalDateTime.parse(parser.getValueAsString(), _formatter);
		}
		
		if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
			String string = parser.getText().trim();
			if (string.length() == 0) {
				return null;
			}

			try {
				if (string.contains("-") || string.contains(" ")) {
					if (string.length() > 10 && string.charAt(10) == 'T') {
						if (string.endsWith("Z")) {
							return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC); //2018-03-02T16:19:00.000Z
						} else {
							return LocalDateTime.parse(string, DEFAULT_FORMATTER); //2018-03-02T16:19:00.000
						}
					}

					return parse(string);
				}
				return LocalDateTime.parse(string, _formatter);
			} catch (DateTimeException e) {
				throw new UnsupportedOperationException("Cannot update object of type "
						+ LocalDateTime.class.getName() + " (by deserializer of type " + getClass().getName() + ")");
			}
		}


		if (parser.isExpectedStartArrayToken()) {
			if (parser.nextToken() == JsonToken.END_ARRAY) {
				return null;
			}
			int year = parser.getIntValue();
			int month = parser.nextIntValue(-1);
			int day = parser.nextIntValue(-1);
			int hour = parser.nextIntValue(-1);
			int minute = parser.nextIntValue(-1);

			if (parser.nextToken() != JsonToken.END_ARRAY) {
				int second = parser.getIntValue();

				if (parser.nextToken() != JsonToken.END_ARRAY) {
					int partialSecond = parser.getIntValue();
					if (partialSecond < 1_000 &&
							!context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS))
						partialSecond *= 1_000_000; // value is milliseconds, convert it to nanoseconds

					if (parser.nextToken() != JsonToken.END_ARRAY) {
						throw context.wrongTokenException(parser, JsonToken.END_ARRAY, "Expected array to end.");
					}
					return LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
				}
				return LocalDateTime.of(year, month, day, hour, minute, second);
			}
			return LocalDateTime.of(year, month, day, hour, minute);
		}
		if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
			return (LocalDateTime) parser.getEmbeddedObject();
		}

		throw context.wrongTokenException(parser, JsonToken.VALUE_STRING, "Expected array or string.");
	}

	private LocalDateTime parse(String string) {
		int length = string.length();
		switch (length) {
		case 10:
//			long milis = LocalDate.parse(string, formatter1).atStartOfDay(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
//			return LocalDateTime.ofInstant(Instant.ofEpochMilli(milis), ZoneOffset.systemDefault());
			long milis = LocalDate.parse(string, formatter1).atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(milis), ZoneOffset.ofHours(8));
//			long milis = LocalDate.parse(string, formatter1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
//			return LocalDateTime.ofInstant(Instant.ofEpochMilli(milis), ZoneOffset.UTC);
		case 16:
			return LocalDateTime.parse(string, formatter2);
		case 19:
			return LocalDateTime.parse(string, formatter3);
		case 23:
			return LocalDateTime.parse(string, formatter4);
		case 29: //这种形式：Fri, 03 Aug 2018 03:42:07 GMT
			try {
				Date date = simpleDateFormat.parse(string);
				return DateUtils.toLocalDateTime(date);
			} catch (ParseException e) {
				logger.error("解析日期字符串错误: " + string, e);
			}
		default:
			break;
		}

		return LocalDateTime.parse(string, DEFAULT_FORMATTER);
	}
}
