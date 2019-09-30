package com.loserico.http;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.loserico.commons.jsonpath.JsonPathUtils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Slf4j
public class OKHttpTest {

	/*
	 * Download a file, print its headers, and print its response body as a string.
	 */
	@Test
	public void testSynchronousGet() {
		/*
		 * The string() method on response body is convenient and efficient for small documents.
		 * But if the response body is large (greater than 1 MiB), avoid string() because it will
		 * load the entire document into memory. In that case, prefer to process the body as a
		 * stream.
		 */
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("https://publicobject.com/helloworld.txt")
				.build();
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Unexpected code " + response);
			}
			Headers headers = response.headers();
			for (int i = 0; i < headers.size(); i++) {
				log.info(headers.name(i) + ": " + headers.value(i));
			}

			System.out.println(response.body().string());
		} catch (IOException e) {
			log.error("msg", e);
		}
	}

	@Test
	public void testAsynchronousGet() {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("https://publicobject.com/helloworld.txt")
				.build();
		client.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				try (ResponseBody responseBody = response.body()) {
					if (!response.isSuccessful()) {
						throw new IOException("Unexpected code " + response);
					}
					Headers responseHeaders = response.headers();
					for (int i = 0, size = responseHeaders.size(); i < size; i++) {
						System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
					}

					System.out.println(responseBody.string());
				} catch (IOException e) {
					log.error("msg", e);
				}
			}

			@Override
			public void onFailure(Call call, IOException e) {
				log.error("", e);
			}
		});
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * When writing request headers, use header(name, value) to set the only occurrence of
	 * name to value. If there are existing values, they will be removed before the new value
	 * is added. Use addHeader(name, value) to add a header without removing the headers
	 * already present.
	 * 
	 * When reading response a header, use header(name) to return the last occurrence of the
	 * named value. Usually this is also the only occurrence! If no value is present,
	 * header(name) will return null. To read all of a field's values as a list, use
	 * headers(name).
	 */
	@Test
	public void testAccessingHeaders() {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url("https://api.github.com/repos/square/okhttp/issues")
				.header("User-Agent", "OkHttp Headers.java")
				.addHeader("Accept", "application/json; q=0.5")
				.addHeader("Accept", "application/vnd.github.v3+json")
				.build();
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Unexpected code " + response);
			}
			System.out.println(response.headers().toString());
			System.out.println("Server: " + response.header("Server"));
			System.out.println("Date: " + response.header("Date"));
			System.out.println("Vary: " + response.headers("Vary"));
		} catch (IOException e) {
			log.error("msg", e);
		}
	}

	/*
	 * Use an HTTP POST to send a request body to a service. This example posts a markdown
	 * document to a web service that renders markdown as HTML. Because the entire request
	 * body is in memory simultaneously, avoid posting large (greater than 1 MiB) documents
	 * using this API.
	 */
	@Test
	public void testPostingString() throws IOException {
		MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
		OkHttpClient client = new OkHttpClient();
		String postBody = ""
				+ "Releases\n"
				+ "--------\n"
				+ "\n"
				+ " * _1.0_ May 6, 2013\n"
				+ " * _1.1_ June 15, 2013\n"
				+ " * _1.2_ August 11, 2013\n";
		Request request = new Request.Builder()
				.url("https://api.github.com/markdown/raw")
				.post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
				.build();
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful())
				throw new IOException("Unexpected code " + response);

			System.out.println(response.body().string());
		}
	}

	@Test
	public void testPostingFile() throws IOException {
		MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
		OkHttpClient client = new OkHttpClient();
		File file = new File("readme.md");

		Request request = new Request.Builder()
				.url("https://api.github.com/markdown/raw")
				.post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful())
				throw new IOException("Unexpected code " + response);

			System.out.println(response.body().string());
		}
	}

	/*
	 * Use FormBody.Builder to build a request body that works like an HTML <form> tag. Names
	 * and values will be encoded using an HTML-compatible form URL encoding.
	 */
	@Test
	public void testPostingFormParameter() {
		OkHttpClient client = new OkHttpClient();
		RequestBody formBody = new FormBody.Builder()
				.add("search", "Jurassic Park")
				.build();
		Request request = new Request.Builder()
				.url("https://en.wikipedia.org/w/index.php")
				.post(formBody)
				.build();
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new IOException("Unexpected code " + response);
			}
			System.out.println(response.body().string());
		} catch (IOException e) {
			log.error("", e);
		}
	}

	@Test
	public void testPostingFormParameter2() {
		OkHttpClient client = new OkHttpClient();
		RequestBody formBody = new FormBody.Builder()
				.add("Phone", "13913582189")
				.add("Password", "123456")
				.add("AppID", "earbud_app")
				.add("Device", "curl-testingabcss")
				.build();
		Request request = new Request.Builder()
				.url("https://api.toppers.com.cn/users/login")
				.post(formBody)
				.build();
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new RuntimeException("登录失败");
			}
			int statusCpde = response.code();
			String json = response.body().string();
			boolean hasError = JsonPathUtils.ifExists(json, "Errors");
			if (hasError) {
				String errorCode = JsonPathUtils.readNode(json, "$.Errors[0].Code");
				if ("PASSWORD_NOT_CORRECT".equalsIgnoreCase(errorCode)) {
					System.out.println("捕捉到登录密码错误行为");
				}
			} else {
				System.out.println(json);
			}
		} catch (IOException e) {
			log.error("msg", e);
		}
	}
}
