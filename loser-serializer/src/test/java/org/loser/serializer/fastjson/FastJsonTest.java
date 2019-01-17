package org.loser.serializer.fastjson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class FastJsonTest {

	@Test
	public void testOfficalDemo() {
		Group group = new Group();
		group.setId(0L);
		group.setName("admin");

		User guestUser = new User();
		guestUser.setId(2L);
		guestUser.setName("guest");
		guestUser.setBirthday(LocalDate.now());

		User rootUser = new User();
		rootUser.setId(3L);
		rootUser.setName("root");

		group.addUser(guestUser);
		group.addUser(rootUser);

		String jsonString = JSON.toJSONString(group);

		System.out.println(jsonString);
		Group decodeJson = JSON.parseObject(jsonString, Group.class);
		System.out.println(decodeJson.getName());
	}

	@Test(expected = ClassCastException.class)
	public void testGenericParse() {
		List<User> users = new ArrayList<User>();
		User guestUser = new User();
		guestUser.setId(2L);
		guestUser.setName("guest");
		guestUser.setBirthday(LocalDate.now());

		User rootUser = new User();
		rootUser.setId(3L);
		rootUser.setName("root");

		users.add(guestUser);
		users.add(rootUser);

		String result = JSON.toJSONString(users);
		List<User> decodedUsers = JSON.parseObject(result, new TypeReference<List<User>>() {
		});
		decodedUsers.forEach(System.out::println);
		List<User> decodedUsers2 = JSON.parseObject(result, List.class);
		decodedUsers2.forEach(System.out::println);
	}

	/**
	 * 自行做性能测试时，关闭循环引用检测的功能。
	 */
	@Test
	public void testDisableCircularReferenceDetect() {
		User user = new User();
		String result = JSON.toJSONString(user, SerializerFeature.DisableCircularReferenceDetect);
		User user2 = JSON.parseObject(result, User.class, Feature.DisableCircularReferenceDetect);
	}

	/**
	 * fastjson如何处理日期
	 * 
	 * 反序列化能够自动识别如下日期格式： ISO-8601日期格式 yyyy-MM-dd yyyy-MM-dd HH:mm:ss yyyy-MM-dd
	 * HH:mm:ss.SSS 毫秒数字 毫秒数字字符串 .NET JSON日期格式 new Date(198293238)
	 * 
	 */
	@Test
	public void testDateFormat() {
		//fastjson处理日期的API很简单，例如：
		//		System.out.println(JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
		//		System.out.println(JSON.toJSONStringWithDateFormat(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss.SSS"));

		//使用ISO-8601日期格式
		//		System.out.println(JSON.toJSONString(new Date(), SerializerFeature.UseISO8601DateFormat));
		//		System.out.println(JSON.toJSONString(LocalDateTime.now(), SerializerFeature.UseISO8601DateFormat));

		//全局修改日期格式
		JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd";
		System.out.println(JSON.toJSONString(new Date(), SerializerFeature.WriteDateUseDateFormat));
		System.out.println(JSON.toJSONString(LocalDateTime.now(), SerializerFeature.WriteDateUseDateFormat));
	}

	@Test
	public void testFastJsonFeatureBean() {
		User user = new User();
		System.out.println(JSON.toJSONString(user));
	}

	@Test
	public void testFastJsonFeatureBean2() {
		FastJSONFeatureBean fastJSONFeatureBean = new FastJSONFeatureBean();
		User user = new User();
		System.out.println(JSON.toJSONString(user));
	}
}
