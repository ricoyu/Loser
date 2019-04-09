package com.loserico.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.loserico.commons.jsonpath.JsonPathUtils;

/**
 * https://www.baeldung.com/httpclient-post-http-request
 * 
 * <p>
 * Copyright: Copyright (c) 2019-03-14 22:09
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public class HttpClientTest {

	@Test
	public void testRequestForJsonResponse() throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("https://api.toppers.com.cn/users/login");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Phone", "13913582189"));
		params.add(new BasicNameValuePair("Password", "123456s"));
		params.add(new BasicNameValuePair("AppID", "earbud_app"));
		params.add(new BasicNameValuePair("Device", "curl-testingabcss"));
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		CloseableHttpResponse response = client.execute(httpPost);
		System.out.println("Status Code:" + response.getStatusLine().getStatusCode());
//		assertThat(response.getStatusLine().getStatusCode(), equalTo(200));

		HttpEntity entity = response.getEntity();
		Header encodingHeader = entity.getContentEncoding();

		// you need to know the encoding to parse correctly
		Charset encoding = encodingHeader == null ? StandardCharsets.UTF_8 : Charsets.toCharset(encodingHeader.getValue());

		/*
		 * {"Birthday":"9999-12-31","Icon":
		 * "https://usermgr.oss-cn-beijing.aliyuncs.com/user_default_avatar.jpeg","AppKey":
		 * "6a7a378c28d38c3fdd6c4d29f969743ffba60bff","Id":4738,"Name":"用户5915630513","Phone":
		 * "13913582189","Sex":"UNKNOWN","Signature":"","RegisterDate":
		 * "2019-03-12T15:39:43+08:00"}
		 * 
		 * {"Errors":[{"Code":"PASSWORD_NOT_CORRECT","Message":"用户或是密码不正确"}]}
		 */
		// use org.apache.http.util.EntityUtils to read json as string
		String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
		boolean hasError = JsonPathUtils.ifExists(json, "Errors");
		if (hasError) {
			String errorCode = JsonPathUtils.readNode(json, "$.Errors[0].Code");
			if ("PASSWORD_NOT_CORRECT".equalsIgnoreCase(errorCode)) {
				System.out.println("捕捉到登录密码错误行为");
			}
		} else {

		}
		client.close();
	}

	@Test
	public void testName() throws ClientProtocolException, IOException {
		Request fluentRequest = Request.Post("https://api.toppers.com.cn/users/login-proxy");
		HttpResponse httpResponse = fluentRequest.bodyForm(Form.form()
				.add("Phone", "13913582189")
				.add("Password", "123456s")
				.add("AppID", "earbud_app")
				.add("Device", "curl-testingabcss")
				.build())
				.execute().returnResponse();
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		httpResponse.getAllHeaders();
		HttpEntity entity = httpResponse.getEntity();
		Header encodingHeader = entity.getContentEncoding();

		// you need to know the encoding to parse correctly
		Charset encoding = encodingHeader == null ? StandardCharsets.UTF_8 : Charsets.toCharset(encodingHeader.getValue());
		String json = EntityUtils.toString(entity, encoding);
		if (statusCode != 200 ) {
			boolean hasError = JsonPathUtils.ifExists(json, "Errors");
			if (hasError) {
				String errorCode = JsonPathUtils.readNode(json, "$.Errors[0].Code");
				if ("PASSWORD_NOT_CORRECT".equalsIgnoreCase(errorCode)) {
					System.out.println("捕捉到登录密码错误行为");
				}
			} else {
				
			}
		}
	}
}
