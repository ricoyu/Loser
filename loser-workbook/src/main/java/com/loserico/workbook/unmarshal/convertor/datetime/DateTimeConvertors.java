package com.loserico.workbook.unmarshal.convertor.datetime;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

/**
 * DateTimeConvertor导演类, 负责总体把关
 * <p>
 * Copyright: Copyright (c) 2019-06-18 14:41
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Slf4j
public final class DateTimeConvertors {

	private static DateTimeConvertor[] convertors = new DateTimeConvertor[] {
			new DateTimeConvertor19(),
			new DateTimeConvertor18(),
			new DateTimeConvertor17(),
			new DateTimeConvertor16(),
			new DateTimeConvertor15(),
			new DateTimeConvertor14(),
			new DateTimeConvertor13()
	};

	public static LocalDateTime convert(String datetime) {
		if (null == datetime || "".equals(datetime)) {
			return null;
		}

		for (int i = 0; i < convertors.length; i++) {
			DateTimeConvertor dateTimeConvertor = convertors[i];
			if (dateTimeConvertor.supports(datetime)) {
				return dateTimeConvertor.convert(datetime);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("No DateTimeConvertor is suitable for convert datetime[{}]", datetime);
		}
		return null;
	}
}
