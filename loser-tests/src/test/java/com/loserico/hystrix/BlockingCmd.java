package com.loserico.hystrix;

import static java.nio.charset.StandardCharsets.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class BlockingCmd extends HystrixCommand<String>{
	
	private static final Logger logger = LoggerFactory.getLogger(BlockingCmd.class);

	protected BlockingCmd() {
		super(HystrixCommandGroupKey.Factory.asKey("SomeGroup"));
	}

	@Override
	protected String run() throws Exception {
		URL url = new URL("http://csdn.net");
		try (InputStream inputStream = url.openStream()) {
			return IOUtils.toString(inputStream, UTF_8);
		} catch (IOException e) {
			logger.error("msg", e);
		}
		return null;
	}

}
