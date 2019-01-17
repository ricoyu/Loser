package org.loser.serializer.jackson;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.loser.serializer.mixins.SimpleKeyMixin;
import org.springframework.cache.interceptor.SimpleKey;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JacksonTest {

	@Test
	public void testSimpleKey() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withSetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		objectMapper.addMixIn(SimpleKey.class, SimpleKeyMixin.class);
		SimpleKey simpleKey = new SimpleKey("android", "13913582189",
				"oLLKNMRMXZfeE4KnJ1UP0dk4S7nKKHtKWq0Fne62psOzst7LK2vcp4eOac3RPJhQ28DD1VfHDNeNZbs7MtbeC30gSwL9ir0y29D");
		String jsonResult = objectMapper.writeValueAsString(simpleKey);
		System.out.println(jsonResult);
		try {
			SimpleKey simpleKey2 = objectMapper.readValue(jsonResult, SimpleKey.class);
			System.out.println(simpleKey2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//		System.out.println(simpleKey.toString());
	}
	
	@Test
	public void testName() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println(objectMapper.writeValueAsBytes("zbcdefg"));
		System.out.println("zbcdefg".getBytes(UTF_8));
	}
	
	class MyDtoNullKeySerializer extends JsonSerializer<Object> {
	    @Override
	    public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused) throws IOException, JsonProcessingException {
	        jsonGenerator.writeFieldName("");
	    }
	}


	@Test
	public void givenAllowingMapObjectWithNullKey_whenWriting_thenCorrect() throws JsonProcessingException {
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.getSerializerProvider().setNullKeySerializer(new MyDtoNullKeySerializer());
	    mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
	    
	    SimpleModule simpleModule = new SimpleModule();
	    
//	    simpleModule.addSerializer(Map.class, new MapSerializer());
	    
	    Map<Object, Object> params = new HashMap<>();
//	    params.put(null, "sss");
	    params.put(1, "sss");
	    params.put("a", "aaa");
	    params.put(2, null);
	    new TreeMap<Object,Object>(params);

	    String dtoMapAsString = mapper.writeValueAsString(params);
	    System.out.println(dtoMapAsString);
	}
	
}
