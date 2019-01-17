package com.loserico.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("messageListener")
public class PeaceFishMessageListener implements MessageListener {
	private static final Logger logger = LoggerFactory.getLogger(PeaceFishMessageListener.class);

	@Override
	public void onMessage(Message message) {
		try {
			logger.info(((TextMessage) message).getText());
		} catch (JMSException e) {
			logger.error("msg", e);
		}
	}

}
