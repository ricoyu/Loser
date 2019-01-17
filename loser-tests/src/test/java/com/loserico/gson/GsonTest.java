package com.loserico.gson;

import java.time.LocalDate;

import javax.xml.crypto.Data;

import org.junit.Test;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonTest {

	@Test
	public void testToJson() {
		Data obj= new Gson().fromJson("2017-01-01 11:11:11", Data.class);
		String dataStr = new Gson().toJson(obj);
	}
	
	@Test
	public void testJava8DateTime() {
		Gson gson = Converters.registerAll(new GsonBuilder()).create();
		String dateStr = gson.toJson(LocalDate.now());
		System.out.println(dateStr);
		
		LocalDate now = gson.fromJson(dateStr, LocalDate.class);
		System.out.println(now);
	}
}
