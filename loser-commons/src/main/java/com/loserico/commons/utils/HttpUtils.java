package com.loserico.commons.utils;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.loserico.commons.velocity.tools.IfNotNull;

/**
 * HTTPClient 工具类
 *
 */
public class HttpUtils {

	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	public enum Header {
		//跨域请求时需要设置该头
		ORIGIN("Origin"),
		//跨域请求时需要设置该头
		ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),

		ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials"),

		CONTENT_TYPE("Content-Type"),

		CONTENT_LENGTH("Content-Length");

		@SuppressWarnings("unused")
		private String header;

		private Header(String header) {
			this.setHeader(header);
		}

		public String getHeader() {
			return header;
		}

		public void setHeader(String header) {
			this.header = header;
		}
	}

	public enum MimeType {
		//json
		APPLICATION_JSON("application/json"),
		//js
		APPLICATION_JAVASCRIPT("application/javascript"),
		//文件下载
		APPLICATION_OCTET_STREAM("application/octet-stream"),
		//xml
		APPLICATION_XML("application/xml"),
		//表单提交
		APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
		//文件上传
		MULTIPART_FORM_DATA("multipart/form-data"),
		TEXT_HTML("text/html"),
		//设置该请求头以模仿Chrome浏览器
		CHROME_USER_AGENT(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");

		@SuppressWarnings("unused")
		private String name;

		private MimeType(String name) {
			this.name = name;
		}
	}

	public enum Encoding {

		//UTF-8编码一个中文字符占三个byte
		UTF_8("UTF-8"),
		//GBK编码一个中文字符占两个byte
		GBK("GBK"),
		//任意字符都占1个byte，因此中文会乱码
		ASCII("US-ASCII"),
		//任意字符都占1个byte，因此中文会乱码
		ISO_8859_1("ISO-8859-1");

		@SuppressWarnings("unused")
		private String encoding;

		private Encoding(String encoding) {
			this.encoding = encoding;
		}
	}

	/**
	 * 模拟chrome浏览器的HttpClient User-Agent: Mozilla/5.0 (Windows NT 6.1;
	 * WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102
	 * Safari/537.36 连接失败重试4次
	 * 
	 * @return
	 */
	public static HttpClient chromeHttpClient() {
		ConnectionConfig connectionConfig = ConnectionConfig.custom().setCharset(Consts.UTF_8).build();
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(2000);
		connectionManager.setDefaultMaxPerRoute(40);
		connectionManager.setDefaultConnectionConfig(connectionConfig);

		HttpRequestRetryHandler requestRetryHandler = new StandardHttpRequestRetryHandler(4, true);

		CloseableHttpClient httpClient = HttpClients.custom().setUserAgent(MimeType.CHROME_USER_AGENT.toString())
				.setConnectionManager(connectionManager).setRetryHandler(requestRetryHandler).build();

		return httpClient;
	}

	/**
	 * HTTP POST请求，默认encoding为UTF-8格式
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, Map<String, String> params) {
		Request postRequest = Request.Post(url);
		Form form = Form.form();
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				form.add(key, params.get(key));
			}
		}
		try {
			Content content = postRequest.bodyForm(form.build(), Consts.UTF_8).execute().returnContent();
			return content.asString();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;

	}

	/**
	 * HTTP POST请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, String encoding, Map<String, String> params) {
		Request postRequest = Request.Post(url);
		Form form = Form.form();
		if (params != null && !params.isEmpty()) {
			for (String key : params.keySet()) {
				form.add(key, params.get(key));
			}
		}
		try {
			Content content = postRequest.bodyForm(form.build(), Charset.forName(encoding)).execute().returnContent();
			return content.asString();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;

	}

	/**
	 * Map<String, String>形式，转成param1=value1&param2=value2 Map<String,
	 * List<String>>形式，转成param1=value1&param2=value2&param2=value2
	 * 
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String toParams(Map<String, ?> params) {
		if (params == null || params.isEmpty()) {
			return "";
		}
		//return Joiner.on("&").withKeyValueSeparator("=").join(params);
		StringBuilder sb = new StringBuilder();
		for (String key : params.keySet()) {
			Object value = params.get(key);
			if (value != null) {
				if (Collection.class.isAssignableFrom(value.getClass())) {
					((Collection) value).forEach((element) -> {
						sb.append(key).append("=").append(element).append("&");
					});
				} else {
					sb.append(key).append("=").append(value).append("&");
				}
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static AuthRequest parseAuthRequest(String actualUri, String queryString) {
		Asserts.notNull(actualUri, "actualUri");

		AuthRequest authRequest = new AuthRequest();
		authRequest.setActualUri(actualUri);

		if (isBlank(queryString)) {
			return authRequest;
		}

		Map<String, List<String>> paramMap = splitQuery(queryString);

		/*
		 * uri 参数只支持一个
		 */
		List<String> uris = paramMap.get("uri");
		if (uris.size() > 0 && isNotBlank(uris.get(0))) {
			authRequest.setUri(uris.get(0));
		}

		/*
		 * timestamp 参数只支持一个
		 */
		List<String> timestamps = paramMap.get("timestamp");
		if (timestamps.size() > 0 && isNotBlank(timestamps.get(0))) {
			try {
				authRequest.setTimestamp(Long.parseLong(timestamps.get(0)));
			} catch (NumberFormatException e) {
				logger.error("timestamp参数需要传UNIX miliseconds", e);
			}
		}

		/*
		 * access_token 参数只支持一个
		 */
		List<String> accessTokens = paramMap.get("access_token");
		if (accessTokens.size() > 0 && isNotBlank(accessTokens.get(0))) {
			authRequest.setAccessToken(accessTokens.get(0));
		}

		/*
		 * 删掉uri, timestamp参数，其他参数继续往后面传递
		 */
		paramMap.remove("uri");
		paramMap.remove("timestamp");
		authRequest.setParams(paramMap);

		return authRequest;
	}

	public static Map<String, List<String>> splitQuery(String uri) {
		if (Strings.isNullOrEmpty(uri)) {
			return Collections.emptyMap();
		}

		return stream(uri.split("&"))
				.map(HttpUtils::splitQueryParameter)
				.collect(groupingBy(
						SimpleImmutableEntry::getKey,
						LinkedHashMap::new,
						mapping(Map.Entry::getValue, toList())));
	}

	private static SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
		final int idx = it.indexOf("=");
		final String key = idx > 0 ? it.substring(0, idx) : it;
		final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
		return new SimpleImmutableEntry<>(key, value);
	}

	/**
	 * 与安全相关的一个请求对象封装
	 * <p>
	 * Copyright: Copyright (c) 2018-07-31 17:17
	 * <p>
	 * Company: DataSense
	 * <p>
	 * @author Rico Yu	ricoyu520@gmail.com
	 * @version 1.0
	 * @on
	 */
	public static class AuthRequest {

		private String uri; //请求参数里面的uri

		private String actualUri; //该请求实际访问的URI

		private long timestamp; //表示这个token的有效期

		private String accessToken;

		private Map<String, List<String>> params = new HashMap<>();

		public AuthRequest() {
		}

		public AuthRequest(String uri, boolean requestToken, long timestamp, Map<String, List<String>> params) {
			this.uri = uri;
			this.timestamp = timestamp;
			this.params = params;
		}

		/**
		 * 检查声称要访问的URI和实际访问的URI是否一致
		 * 
		 * @return
		 */
		public boolean requestPathMatchs(String contextPath) {
			//return actualUri.equalsIgnoreCase(uri);
			String withoutContextPath = actualUri.replaceFirst(contextPath, "");
			return uri.endsWith(withoutContextPath);
		}
		
		/**
		 * token中的timestamp和url里面传的参数timestamp要一致
		 * @param currentTimestamp
		 * @return boolean
		 */
		public boolean tokenExpires(long currentTimestamp) {
			return timestamp != currentTimestamp;
		}

		/**
		 * 将map中保存的参数转成字符串形式
		 * 
		 * @return
		 */
		public String toParams() {
			return HttpUtils.toParams(params);
		}

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public Map<String, List<String>> getParams() {
			return params;
		}

		public void setParams(Map<String, List<String>> params) {
			this.params = params;
		}

		public String getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}

		public String getActualUri() {
			return actualUri;
		}

		public void setActualUri(String actualUri) {
			this.actualUri = actualUri;
		}

	}
}
