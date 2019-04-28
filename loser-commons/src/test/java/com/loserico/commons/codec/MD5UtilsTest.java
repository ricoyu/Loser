package com.loserico.commons.codec;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.loserico.commons.utils.StringUtils;

public class MD5UtilsTest {

	@Test
	public void testYunQiUrlMD5() {
		String original = "appkey=Fbc6XPl3b8h6rCkymBzkyA&auth_callback=http://localhost:8080/CallBack.ashx&time=1445307713&apptoken=Cvjuc77jFow07WSVfJ2blQ";
		String md5Hash = MD5Utils.md5Hex(original);
		System.out.println(md5Hash);
		assertEquals("0972888fac34d1d151e4433c9dc7a102", md5Hash);
	}

	@Test
	public void testGenerateUrl() {
		String urlTemplate = "https://api.ilifesmart.com/app/auth.authorize?id={0}&appkey={1}&time={2}&auth_callback={3}&did={4}&sign={5}&lang=zh";
		String id = "1";
		String appkey = "Fbc6XPl3b8h6rCkymBzkyA";
		String time = new Date().getTime() / 1000 + "";
		System.out.println(time);
		String callBack = "http://localhost:8080/fack-callback";
		String did = "";

		String signTemplate = "appkey={0}&auth_callback={1}&time={2}&apptoken={3}";
		String original = StringUtils.format(signTemplate, appkey, callBack, time, "Cvjuc77jFow07WSVfJ2blQ");
		System.out.println(original);
		String sign = MD5Utils.md5Hex(original);
		System.out.println(sign);
		String url = StringUtils.format(urlTemplate, id, appkey, time, callBack, did, sign);
		System.out.println(url);
	}
}
