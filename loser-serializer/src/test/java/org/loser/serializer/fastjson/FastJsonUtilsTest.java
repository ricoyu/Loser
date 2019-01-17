package org.loser.serializer.fastjson;

import static com.alibaba.fastjson.serializer.SerializerFeature.WRITE_MAP_NULL_FEATURES;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteDateUseDateFormat;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;

import java.time.LocalDateTime;

import org.junit.Test;
import org.loser.serializer.utils.FastJsonUtils;

import com.alibaba.fastjson.JSON;

public class FastJsonUtilsTest {

	class Customer {
		private String name;
		private LocalDateTime birthday;
		private Long age;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public LocalDateTime getBirthday() {
			return birthday;
		}

		public void setBirthday(LocalDateTime birthday) {
			this.birthday = birthday;
		}

		public Long getAge() {
			return age;
		}

		public void setAge(Long age) {
			this.age = age;
		}
	}

	@Test
	public void testToString() {
		Customer customer1 = new Customer();
		customer1.setAge(22l);
		customer1.setBirthday(LocalDateTime.now());
		customer1.setName("rico");
		System.out.println(JSON.toJSONString(customer1, WriteDateUseDateFormat));
		System.out.println(FastJsonUtils.toPrettyJson(customer1));
//		System.out.println(JSON.toJSONStringWithDateFormat(customer1, "yyyy-MM-dd HH:mm:ss", WriteMapNullValue));

		customer1.setAge(null);
		System.out.println(FastJsonUtils.toPrettyJson(customer1));
		customer1.setBirthday(null);
		System.out.println(FastJsonUtils.toPrettyJson(customer1));
	}
}
