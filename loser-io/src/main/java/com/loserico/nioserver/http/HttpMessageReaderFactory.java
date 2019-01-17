package com.loserico.nioserver.http;

import com.loserico.nioserver.MessageReader;
import com.loserico.nioserver.MessageReaderFactory;

/**
 * Created by jjenkov on 18-10-2015.
 */
public class HttpMessageReaderFactory implements MessageReaderFactory {

	public HttpMessageReaderFactory() {
	}

	@Override
	public MessageReader createMessageReader() {
		return new HttpMessageReader();
	}
}
