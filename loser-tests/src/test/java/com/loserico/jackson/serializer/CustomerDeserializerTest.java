package com.loserico.jackson.serializer;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.loserico.io.utils.IOUtils;

public class CustomerDeserializerTest {

	@Test
	public void testDeserializer() {
		try {
			Item itemWithOwner = new ObjectMapper().readValue(IOUtils.readClasspathFileAsInputStream("deserializer.json"), Item.class);
			System.out.println(itemWithOwner);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 也可以在类上标注
	 * @JsonDeserialize(using = ItemDeserializer.class)
	 * 
	 * @on
	 */
	@Test
	public void testCustomDeserializer() {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Item.class, new ItemDeserializer());
		mapper.registerModule(module);
		 
		try {
			Item readValue = mapper.readValue(IOUtils.readClassPathFile("deserializer2.json"), Item.class);
			System.out.println(readValue);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
