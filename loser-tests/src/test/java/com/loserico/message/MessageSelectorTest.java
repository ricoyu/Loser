package com.loserico.message;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class MessageSelectorTest {

	public void sendStockMessage(Session session, MessageProducer producer, Destination destination,
			String payload, String symbol, double price) throws JMSException {
		TextMessage message = session.createTextMessage();
		message.setText(payload);
		message.setStringProperty("SYMBOL", symbol);
		message.setDoubleProperty("PRICE", price);
		producer.send(message);
	}

	/**
	 * This consumer receives only messages matching the query defined in the selector.
	 * @param session
	 * @param destination
	 * @throws JMSException
	 */
	public void filterMessage(Session session, Destination destination) throws JMSException {
		String selector = "SYMBOL = 'AAPL'";
		MessageConsumer consumer = session.createConsumer(destination, selector);

		String priceSelector = "SYMBOL = 'AAPL' AND PRICE > 12.36";
		MessageConsumer priceConsumer = session.createConsumer(destination, priceSelector);

		String ciscoSelector = "SYMBOL IN ('AAPL', 'CSCO') AND PRICE > 12.36 AND PE_RATIO < 30%";
		MessageConsumer ciscoConsumer = session.createConsumer(destination, ciscoSelector);
	}
}
