package com.loserico.message;

import org.apache.activemq.broker.BrokerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BrokerTest {
	
	@SuppressWarnings({ "resource", "unused" })
	public static void main(String[] args) throws Exception {
		/*BrokerService broker = new BrokerService();
		broker.setUseJmx(true);
		broker.addConnector("tcp://localhost:61616");
		broker.start();*/
		
		/*String url = "properties:broker.properties";
		BrokerService broker1 = BrokerFactory.createBroker(URI.create(url));
		broker1.addConnector("tcp://localhost:61616");
		broker1.start();*/
		
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		BrokerService broker = context.getBean("broker", BrokerService.class);
	}

}
