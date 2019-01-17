package com.loserico.message.jpush;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.ClientConfig;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;

public class JPushTest {
	
	private static final Logger logger = LoggerFactory.getLogger(JPushTest.class);
	
	private static final String appKey = "bd803432aaade235c271563b";
	private static final String secret = "5fb661ec80f9cc4a630e3291";

	@Test
	public void testJpushHelloWorld() {
		JPushClient jpushClient = new JPushClient(secret, appKey, null, ClientConfig.getInstance());

		// For push, all you need do is to build PushPayload object.
		PushPayload payload = buildPushObject();

		try {
			PushResult result = jpushClient.sendPush(payload);
			logger.info("Got result - " + result);

		} catch (APIConnectionException e) {
			// Connection error, should retry later
			logger.error("Connection error, should retry later", e);

		} catch (APIRequestException e) {
			// Should review the error, and fix the request
			logger.error("Should review the error, and fix the request", e);
			logger.info("HTTP Status: " + e.getStatus());
			logger.info("Error Code: " + e.getErrorCode());
			logger.info("Error Message: " + e.getErrorMessage());
		}
	}
	
	public static PushPayload buildPushObject() {
        return PushPayload.alertAll("ALERT");
    }
}
