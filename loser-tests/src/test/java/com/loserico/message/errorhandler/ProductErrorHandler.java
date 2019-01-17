package com.loserico.message.errorhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component("productErrorHandler")
public class ProductErrorHandler implements ErrorHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductErrorHandler.class);

	@Override
	public void handleError(Throwable e) {
		logger.error("msg", e);
	}

}
