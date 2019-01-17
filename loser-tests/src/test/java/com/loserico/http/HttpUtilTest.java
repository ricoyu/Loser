package com.loserico.http;

import static com.loserico.commons.jackson.JacksonUtils.toJson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Test;

import com.loserico.commons.utils.HttpUtils;
import com.loserico.commons.utils.HttpUtils.AuthRequest;
import com.loserico.nioserver.http.HttpHeaders;
import com.loserico.nioserver.http.HttpUtil;

/**
 * Created by jjenkov on 19-10-2015.
 */
public class HttpUtilTest {

	@Test
	public void testResolveHttpMethod() throws UnsupportedEncodingException {
		assertHttpMethod("GET / HTTP/1.1\r\n", HttpHeaders.HTTP_METHOD_GET);
		assertHttpMethod("POST / HTTP/1.1\r\n", HttpHeaders.HTTP_METHOD_POST);
		assertHttpMethod("PUT / HTTP/1.1\r\n", HttpHeaders.HTTP_METHOD_PUT);
		assertHttpMethod("HEAD / HTTP/1.1\r\n", HttpHeaders.HTTP_METHOD_HEAD);
		assertHttpMethod("DELETE / HTTP/1.1\r\n", HttpHeaders.HTTP_METHOD_DELETE);
	}

	private void assertHttpMethod(String httpRequest, int httpMethod)
			throws UnsupportedEncodingException {
		byte[] source = httpRequest.getBytes("UTF-8");
		HttpHeaders httpHeaders = new HttpHeaders();

		HttpUtil.resolveHttpMethod(source, 0, httpHeaders);
		assertEquals(httpMethod, httpHeaders.httpMethod);
	}

	@Test
	public void testParseHttpRequest() throws UnsupportedEncodingException {
		String httpRequest = "GET / HTTP/1.1\r\n\r\n";

		byte[] source = httpRequest.getBytes("UTF-8");
		HttpHeaders httpHeaders = new HttpHeaders();

		HttpUtil.parseHttpRequest(source, 0, source.length, httpHeaders);

		assertEquals(0, httpHeaders.contentLength);

		httpRequest = "GET / HTTP/1.1\r\n" +
				"Content-Length: 5\r\n" +
				"\r\n1234";
		source = httpRequest.getBytes("UTF-8");

		assertEquals(-1, HttpUtil.parseHttpRequest(source, 0, source.length, httpHeaders));
		assertEquals(5, httpHeaders.contentLength);

		httpRequest = "GET / HTTP/1.1\r\n" +
				"Content-Length: 5\r\n" +
				"\r\n12345";
		source = httpRequest.getBytes("UTF-8");

		assertEquals(42, HttpUtil.parseHttpRequest(source, 0, source.length, httpHeaders));
		assertEquals(5, httpHeaders.contentLength);

		httpRequest = "GET / HTTP/1.1\r\n" +
				"Content-Length: 5\r\n" +
				"\r\n12345" +
				"GET / HTTP/1.1\r\n" +
				"Content-Length: 5\r\n" +
				"\r\n12345";

		source = httpRequest.getBytes("UTF-8");

		assertEquals(42, HttpUtil.parseHttpRequest(source, 0, source.length, httpHeaders));
		assertEquals(5, httpHeaders.contentLength);
		assertEquals(37, httpHeaders.bodyStartIndex);
		assertEquals(42, httpHeaders.bodyEndIndex);
	}

	
	@Test
	public void testParseQueryString() {
		String uri = "/saleOrder/detail?access_token=0aDqIt3fzsJXFRU383gQLvYY8chpPRowOV13WxiDl0eGWRPiuf6fpJzmdunsr2ctlZ&name=测试下中文&timestamp=1533026418857";
		String[] splited = uri.split("\\?");
		if(splited[0].lastIndexOf("access_token") == 0) {
			
		} else {
			if(splited.length != 2) {
				System.out.println("Unauthorized");
				return;
			}
			Map<String, List<String>> map = com.loserico.commons.utils.HttpUtils.splitQuery(splited[1]);
			System.out.println(toJson(map));
			
		}
	}
	
	@Test
	public void testAuthRequest() {
		String uri = "/saleOrder/detail?access_token=0aDqIt3fzsJXFRU383gQLvYY8chpPRowOV13WxiDl0eGWRPiuf6fpJzmdunsr2ctlZ&name=测试下中文&timestamp=1533026418857";
		AuthRequest authRequest = HttpUtils.parseAuthRequest(uri, "");
		System.out.println("Access Token: " + authRequest.getAccessToken());
		System.out.println("Timestamp: " + authRequest.getTimestamp());
		System.out.println("Params: " + authRequest.toParams());
	}
	
	@Test
	public void testName() {
		String uri = "/saleOrder/detail?access_token=0aDqIt3fzsJXFRU383gQLvYY8chpPRowOV13WxiDl0eGWRPiuf6fpJzmdunsr2ctlZ&name=测试下中文&timestamp=1533026418857";
		List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(uri, UTF_8);
		System.out.println(toJson(nameValuePairs));
	}
}
