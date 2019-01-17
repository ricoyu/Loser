package com.loserico.search.es.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductModel implements Serializable{

	private static final long serialVersionUID = 3542700249228416140L;

	private String uuid;
	
	private String name;
	
	private double price;
	
	private List<String> cats = new ArrayList<>();
	
	public ProductModel(String uuid, String name, double price, List<String> cats) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.price = price;
		this.cats = cats;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public List<String> getCats() {
		return cats;
	}

	public void setCats(List<String> cats) {
		this.cats = cats;
	}
	
	
}
