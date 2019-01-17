package com.loserico.orm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BOOK")
public class Book extends BaseEntity {

	private static final long serialVersionUID = 8099603301590727281L;

	@Column(name = "ISBN")
	private String isbn;

	@Column(name = "NAME")
	private String name;
	
	private boolean bestSeller;
	
	private boolean computer;

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isComputer() {
		return computer;
	}

	public void setComputer(boolean computer) {
		this.computer = computer;
	}

	public boolean isBestSeller() {
		return bestSeller;
	}

	public void setBestSeller(boolean bestSeller) {
		this.bestSeller = bestSeller;
	}

/*	public boolean isBestSeller() {
		return isBestSeller;
	}

	public void setBestSeller(boolean isBestSeller) {
		this.isBestSeller = isBestSeller;
	}*/


}
