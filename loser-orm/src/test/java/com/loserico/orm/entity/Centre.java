package com.loserico.orm.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 中心
 * @author Rico Yu	ricoyu520@gmail.com
 * @since 2017-03-03 10:58
 * @version 1.0
 *
 */
@Entity
@Table(name = "CENTRE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Centre implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	//中心名称
	private String centre;
	//客户的中心对应代码,对应customer.xml中的pk_custclass
	private String pkCustomer;
	//应收单 & 收款单对应中心的部门代码
	private String pkDepart;
	//中心负责人名字
	private String principal;
	private String bankAccount;
	//中心联系电话
	private String contactNumber;
	//中心地址
	private String address;
	private String creator;
	private Date createTime;
	private String modifier;
	private Date modifiyTime;

	public Centre() {
	}

	public Centre(String centre) {
		this.centre = centre;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "CREATOR", length = 100, nullable = false)
	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Column(name = "CREATE_TIME", nullable = false, length = 19)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Column(name = "MODIFIER", length = 100, nullable = false)
	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	@Column(name = "MODIFY_TIME", nullable = false, length = 19)
	public Date getModifiyTime() {
		return modifiyTime;
	}

	public void setModifiyTime(Date modifiyTime) {
		this.modifiyTime = modifiyTime;
	}

	@Column(name = "CENTRE", length = 100, nullable = false)
	public String getCentre() {
		return centre;
	}

	public void setCentre(String centre) {
		this.centre = centre;
	}

	@Column(name = "PK_CUSTOMER", length = 20, nullable = false)
	public String getPkCustomer() {
		return pkCustomer;
	}

	public void setPkCustomer(String pkCustomer) {
		this.pkCustomer = pkCustomer;
	}

	@Column(name = "PK_DEPART", length = 20, nullable = false)
	public String getPkDepart() {
		return pkDepart;
	}

	public void setPkDepart(String pkDepart) {
		this.pkDepart = pkDepart;
	}

	@Column(name = "PRINCIPAL", length = 100, nullable = false)
	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	@Column(name = "bank_account", length = 100, nullable = false)
	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	@Column(name = "contact_number", length = 100, nullable = false)
	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	@Column(name = "address", length = 100, nullable = false)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
