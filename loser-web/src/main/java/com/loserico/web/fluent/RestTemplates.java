package com.loserico.web.fluent;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.loserico.commons.utils.ReflectionUtils;

public final class RestTemplates {

	private static RestTemplate restTemplate = new RestTemplate();

	private RestTemplates() {
	}

	public static class Builder {

		protected String url;

		protected HttpMethod method;

		protected HttpHeaders headers = new HttpHeaders();

		public <T> ResponseEntity<T> exchange(Class<T> responseType, Object... uriVariables)
				throws RestClientException {

			RequestCallback requestCallback = ReflectionUtils.invokeMethod(restTemplate, "httpEntityCallback",
					new HttpEntity<String>(this.headers), responseType);
			ResponseExtractor<ResponseEntity<T>> responseExtractor = ReflectionUtils.invokeMethod(restTemplate,
					"responseEntityExtractor", responseType);
			return restTemplate.execute(url, method, requestCallback, responseExtractor, uriVariables);
		}

		public <T> T responseBody(Class<T> responseType, Object... uriVariables) throws RestClientException {
			return exchange(responseType, uriVariables).getBody();
		}

	}

	public static class GetBuilder extends Builder {

		protected HttpHeaders headers = new HttpHeaders();

		public GetBuilder(String url) {
			this.url = url;
			this.method = HttpMethod.GET;
		}

		public GetBuilder contentType(MediaType mediaType) {
			this.headers.setContentType(mediaType);
			return this;
		}

	}

	public static class PostBuilder extends Builder {

		private HttpEntity<?> requestEntity;

		public PostBuilder(String url) {
			this.url = url;
			this.method = HttpMethod.POST;
		}

		public PostBuilder contentType(MediaType mediaType) {
			this.headers.setContentType(mediaType);
			return this;
		}
		
		public <T> PostBuilder requestEntity(T requestEntity) {
			this.requestEntity = new HttpEntity<T>(requestEntity, headers);
			return this;
		}

		public <T> T postForObject(Class<T> responseType, Object... uriVariables)
				throws RestClientException {
			RequestCallback requestCallback = ReflectionUtils.invokeMethod(restTemplate, "httpEntityCallback",
					requestEntity, responseType);
			HttpMessageConverterExtractor<T> responseExtractor = new HttpMessageConverterExtractor<T>(responseType,
					restTemplate.getMessageConverters());
			return restTemplate.execute(url, HttpMethod.POST, requestCallback, responseExtractor, uriVariables);
		}
	}

	public static GetBuilder get(final String uri) {
		return new GetBuilder(uri);
	}

	public static GetBuilder getJSON(final String uri) {
		return (GetBuilder) new GetBuilder(uri).contentType(MediaType.APPLICATION_JSON);
	}

	public static PostBuilder post(final String uri) {
		return new PostBuilder(uri);
	}

}
