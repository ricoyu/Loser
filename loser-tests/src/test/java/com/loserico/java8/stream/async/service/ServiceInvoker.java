package com.loserico.java8.stream.async.service;

import org.springframework.web.client.RestTemplate;

import com.loserico.java8.stream.async.model.Client;

public class ServiceInvoker {
	private static final String URI = "http://localhost:8081/spring-rest-simple/clients/{clientId}";
	private RestTemplate restTemplate = new RestTemplate();
	
	public Client invoke(String id) {
		return restTemplate.getForObject(URI, Client.class, id);
	}
}