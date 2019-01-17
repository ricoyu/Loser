package com.loserico.orm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "CUSTOMER_CODE")
public class CustomerCode extends BaseEntity {

	private static final long serialVersionUID = 7035058467418968453L;
	private String bu;
	private String code;
	private String name;
	private String mneCode;
	private String category;
	private String className;
	private String supplier;
	private String custPrep;
	private String nricPassport;
	private String contact1;
	private String contact2;
	private String tel1;
	private String tel2;

	@Column(name = "BU", length = 50)
	public String getBu() {
		return bu;
	}

	public void setBu(String bu) {
		this.bu = bu;
	}

	@Column(name = "CODE", length = 20)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "NAME", length = 100)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "MNE_CODE", length = 50)
	public String getMneCode() {
		return mneCode;
	}

	public void setMneCode(String mneCode) {
		this.mneCode = mneCode;
	}

	@Column(name = "CATEGORY", length = 50)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Column(name = "SUPPLIER", length = 50)
	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	@Column(name = "CUST_PREP", length = 50)
	public String getCustPrep() {
		return custPrep;
	}

	public void setCustPrep(String custPrep) {
		this.custPrep = custPrep;
	}

	@Column(name = "NRIC_PASSPORT", length = 50)
	public String getNricPassport() {
		return nricPassport;
	}

	public void setNricPassport(String nricPassport) {
		this.nricPassport = nricPassport;
	}

	@Column(name = "CONTACT1", length = 50)
	public String getContact1() {
		return contact1;
	}

	public void setContact1(String contact1) {
		this.contact1 = contact1;
	}

	@Column(name = "CONTACT2", length = 50)
	public String getContact2() {
		return contact2;
	}

	public void setContact2(String contact2) {
		this.contact2 = contact2;
	}

	@Column(name = "TEL1", length = 50)
	public String getTel1() {
		return tel1;
	}

	public void setTel1(String tel1) {
		this.tel1 = tel1;
	}

	@Column(name = "TEL2", length = 50)
	public String getTel2() {
		return tel2;
	}

	public void setTel2(String tel2) {
		this.tel2 = tel2;
	}

	@Column(name="CLASS_NAME")
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
