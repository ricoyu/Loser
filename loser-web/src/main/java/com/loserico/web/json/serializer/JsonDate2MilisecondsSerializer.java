package com.loserico.web.json.serializer;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * 将Date输出为毫秒数
 * @author Loser
 * @since Mar 31, 2016
 * @version 
 *
 */
public class JsonDate2MilisecondsSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		String result = "0";
		if(value != null) {
			result = String.valueOf(value.getTime());
		}
		gen.writeString(result);
	}
}
