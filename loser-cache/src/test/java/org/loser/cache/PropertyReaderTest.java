package org.loser.cache;

import static com.loserico.commons.jackson.JacksonUtils.toJson;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.loserico.commons.jackson.JacksonUtils;
import com.loserico.commons.resource.PropertyReader;

public class PropertyReaderTest {

	@Test
	public void testStrList() {
		PropertyReader propertyReader = new PropertyReader("centre-virtual-account");
		List<String> centres20190101 = propertyReader.getStrList("virtual.account.20190101");
		System.out.println(toJson(centres20190101));
	}
	
	@Test
	public void testGetLocalDate() {
		PropertyReader propertyReader = new PropertyReader("centre-virtual-account");
		LocalDate localDate = propertyReader.getLocalDate("virtual.account.since");
		System.out.println(localDate);
	}
	
	@Test
	public void testGetMap() {
		PropertyReader propertyReader = new PropertyReader("centre-virtual-account");
		Map<String, String> accountMap = propertyReader.getMap("company.accounts");
		System.out.println(JacksonUtils.toPrettyJson(accountMap));
	}
}
