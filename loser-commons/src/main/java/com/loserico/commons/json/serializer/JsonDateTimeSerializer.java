package com.loserico.commons.json.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Format date to yyyy-MM-dd HH:mm:ss
 * 
 * @author xuehuyu
 * @since Aug 26, 2015
 * @version
 *
 */
public class JsonDateTimeSerializer extends JsonSerializer<Date> {

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);

	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		String formatted = format.format(value);
		jgen.writeString(formatted);
	}
	

}