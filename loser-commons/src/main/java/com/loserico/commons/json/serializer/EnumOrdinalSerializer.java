package com.loserico.commons.json.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@SuppressWarnings("rawtypes")
public class EnumOrdinalSerializer extends JsonSerializer<Enum>{

	@Override
	public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		if (value == null) {
			return;
		}
		gen.writeNumber(value.ordinal());
	}

}
