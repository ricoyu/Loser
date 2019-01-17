package com.loserico.web.utils;

import static com.loserico.commons.utils.StringUtils.joinWith;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CORS {

	public static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	public static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public static final String HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";

	private static final Logger logger = LoggerFactory.getLogger(CORS.class);

	public static CorsBuilder builder() {
		return new CorsBuilder();
	}

	public static class CorsBuilder {
		
		private CorsBuilder() {}

		private List<String> allowedOrigins = new ArrayList<>();
		private List<String> allowedMethods = new ArrayList<>();
		private List<String> allowedHeaders = new ArrayList<>();

		public CorsBuilder allowedOrigins(String... origins) {
			for (int i = 0; i < origins.length; i++) {
				String origin = origins[i];
				if (isNotBlank(origin)) {
					this.allowedOrigins.add(StringUtils.trim(origin));
				} else {
					logger.info("给定的Origin为空，忽略之");
				}
			}
			return this;
		}

		public CorsBuilder allowedMethods(String... methods) {
			for (int i = 0; i < methods.length; i++) {
				String method = methods[i];
				if (isNotBlank(method)) {
					this.allowedMethods.add(StringUtils.trim(method));
				} else {
					logger.info("给定的Mehtod为空，忽略之");
				}
			}
			return this;
		}

		public CorsBuilder allowedHeaders(String... headers) {
			for (int i = 0; i < headers.length; i++) {
				String header = headers[i];
				if (isNotBlank(header)) {
					this.allowedHeaders.add(StringUtils.trim(header));
				} else {
					logger.info("给定的Header为空，忽略之");
				}
			}
			return this;
		}
		
		public CorsBuilder allowAll() {
			this.allowedHeaders.clear();
			this.allowedHeaders.add("*");
			
			this.allowedMethods.clear();
			this.allowedMethods.add("*");
			
			this.allowedOrigins.clear();
			this.allowedOrigins.add("*");
			return this;
		}

		public void build(HttpServletResponse response) {
			response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_HEADERS, joinWith(", ", allowedHeaders));
			response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, joinWith(", ", allowedOrigins));
			response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_METHODS, joinWith(", ", allowedMethods));
		}
	}
}
