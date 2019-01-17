package com.loserico.message.broker;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InnerBroker {
	
	public static void main(String[] args) throws Exception {

		/*BrokerService broker = new BrokerService();
		broker.setUseJmx(true);
		broker.addConnector("tcp://localhost:61616");
		broker.start();*/

		/*String Uri = "properties:broker.properties";
		BrokerService broker1 = BrokerFactory.createBroker(new URI(Uri));
		broker1.addConnector("tcp://localhost:61616");
		broker1.start();*/

		ApplicationContext ctx = new    ClassPathXmlApplicationContext("applicationContext.xml");

	}
}
