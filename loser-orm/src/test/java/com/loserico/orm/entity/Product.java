package com.loserico.orm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;

@Entity
public class Product extends BaseEntity {
	private static final long serialVersionUID = 6214428178780557633L;

	@Version
	@Column(name = "version")
	private int version = 0;

	@Column
	private String name;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}