package com.loserico.orm.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 
 * @author Rico Yu	ricoyu520@gmail.com
 * @since 2017-03-03 10:28
 * @version 1.0
 *
 */
@Entity
@Table(name = "CUSTOMER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Customer implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String creator;
	private Date createTime;
	private String modifier;
	private Date modifiyTime;
	//客户类型
	private String customerType;
	//Taidii学生ID
	private String taidiiUid;
	//用友学生ID
	private String yongyouUid;
	//学生姓名
	private String customerRef;
	//所属中心ID
	private Long centreId;
	private String centre;
	//学生NRIC/Passport
	private String nricPassport;
	//出生日期
	private Date dob;
	//性别
	private String gender;
	//入学日
	private Date admDate;
	//退学日
	private Date leaveDate;
	//政府补助金额
	private Double govSubVal;
	//政府补助状态
	private String govSubStatus;
	//政府补助到期日
	private Date govSubExpDate;
	//内部补助金额
	private Double intSubVal;
	//内部补助状态
	private String intSubStatus;
	//内部补助到期日
	private Date intSubExpDate;
	//StartupGrant金额
	private Double staGraVal;
	//StartupGrant状态
	private String staGraStatus;
	//StartupGrant到期日
	private Date staGraExpDate;
	//额外补助金额
	private Double addSubVal;
	//额外补助状态
	private String addSubStatus;
	//额外补助到期日
	private Date addSubExpDate;
	//FAS金额
	private Double fasVal;
	//FAS状态
	private String fasStatus;
	//FAS到期日
	private Date fasExpDate;
	//客户所属中心银行帐号
	private String bankAccount;
	private boolean deleted;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", updatable = false, unique = true, nullable = false)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "TAIDII_UID")
	public String getTaidiiUid() {
		return taidiiUid;
	}

	public void setTaidiiUid(String taidiiUid) {
		this.taidiiUid = taidiiUid;
	}

	@Column(name = "YONGYOU_UID")
	public String getYongyouUid() {
		return yongyouUid;
	}

	public void setYongyouUid(String yongyouUid) {
		this.yongyouUid = yongyouUid;
	}

	@Column(name = "CUSTOMER_REF")
	public String getCustomerRef() {
		return customerRef;
	}

	public void setCustomerRef(String customerRef) {
		this.customerRef = customerRef;
	}

	@Column(name = "NRIC_PASSPORT")
	public String getNricPassport() {
		return nricPassport;
	}

	public void setNricPassport(String nricPassport) {
		this.nricPassport = nricPassport;
	}

	@Column(name = "DOB")
	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	@Column(name = "GENDER")
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Column(name = "ADM_DATE")
	public Date getAdmDate() {
		return admDate;
	}

	public void setAdmDate(Date admDate) {
		this.admDate = admDate;
	}

	@Column(name = "LEAVE_DATE")
	public Date getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
	}

	@Column(name = "GOV_SUB_VAL")
	public Double getGovSubVal() {
		return govSubVal;
	}

	public void setGovSubVal(Double govSubVal) {
		this.govSubVal = govSubVal;
	}

	@Column(name = "GOV_SUB_STATUS")
	public String getGovSubStatus() {
		return govSubStatus;
	}

	public void setGovSubStatus(String govSubStatus) {
		this.govSubStatus = govSubStatus;
	}

	@Column(name = "GOV_SUB_EXP_DATE")
	public Date getGovSubExpDate() {
		return govSubExpDate;
	}

	public void setGovSubExpDate(Date govSubExpDate) {
		this.govSubExpDate = govSubExpDate;
	}

	@Column(name = "INT_SUB_VAL")
	public Double getIntSubVal() {
		return intSubVal;
	}

	public void setIntSubVal(Double intSubVal) {
		this.intSubVal = intSubVal;
	}

	@Column(name = "INT_SUB_STATUS")
	public String getIntSubStatus() {
		return intSubStatus;
	}

	public void setIntSubStatus(String intSubStatus) {
		this.intSubStatus = intSubStatus;
	}

	@Column(name = "INT_SUB_EXP_DATE")
	public Date getIntSubExpDate() {
		return intSubExpDate;
	}

	public void setIntSubExpDate(Date intSubExpDate) {
		this.intSubExpDate = intSubExpDate;
	}

	@Column(name = "STA_GRA_VAL")
	public Double getStaGraVal() {
		return staGraVal;
	}

	public void setStaGraVal(Double staGraVal) {
		this.staGraVal = staGraVal;
	}

	@Column(name = "STA_GRA_STATUS")
	public String getStaGraStatus() {
		return staGraStatus;
	}

	public void setStaGraStatus(String staGraStatus) {
		this.staGraStatus = staGraStatus;
	}

	@Column(name = "STA_GRA_EXP_DATE")
	public Date getStaGraExpDate() {
		return staGraExpDate;
	}

	public void setStaGraExpDate(Date staGraExpDate) {
		this.staGraExpDate = staGraExpDate;
	}

	@Column(name = "ADD_SUB_VAL")
	public Double getAddSubVal() {
		return addSubVal;
	}

	public void setAddSubVal(Double addSubVal) {
		this.addSubVal = addSubVal;
	}

	@Column(name = "ADD_SUB_STATUS")
	public String getAddSubStatus() {
		return addSubStatus;
	}

	public void setAddSubStatus(String addSubStatus) {
		this.addSubStatus = addSubStatus;
	}

	@Column(name = "ADD_SUB_EXP_DATE")
	public Date getAddSubExpDate() {
		return addSubExpDate;
	}

	public void setAddSubExpDate(Date addSubExpDate) {
		this.addSubExpDate = addSubExpDate;
	}

	@Column(name = "FAS_VAL")
	public Double getFasVal() {
		return fasVal;
	}

	public void setFasVal(Double fasVal) {
		this.fasVal = fasVal;
	}

	@Column(name = "FAS_STATUS")
	public String getFasStatus() {
		return fasStatus;
	}

	public void setFasStatus(String fasStatus) {
		this.fasStatus = fasStatus;
	}

	@Column(name = "FAS_EXP_DATE")
	public Date getFasExpDate() {
		return fasExpDate;
	}

	public void setFasExpDate(Date fasExpDate) {
		this.fasExpDate = fasExpDate;
	}

	@Column(name = "CUSTOMER_TYPE")
	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
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

	public void setModifier(String lastModifier) {
		this.modifier = lastModifier;
	}

	@Column(name = "MODIFY_TIME", nullable = false, length = 19)
	public Date getModifyTime() {
		return modifiyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifiyTime = modifyTime;
	}

	/**
	 * 在Entity被持久化之前做一些操作
	 */
	@PrePersist
	protected void onPrePersist() {
		Date now = new Date();
		setCreateTime(now);
		setModifyTime(now);
		setCreator("admin");
		setModifier("admin");
	}

	@PreUpdate
	protected void onPreUpdate() {
		setModifyTime(new Date());
		setModifier("admin");
	}

	@Transient
	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	@Column(name = "CENTRE_ID", nullable = false, length = 19)
	public Long getCentreId() {
		return centreId;
	}

	public void setCentreId(Long centreId) {
		this.centreId = centreId;
	}

	@Column(name = "CENTRE", length = 100, nullable = false)
	public String getCentre() {
		return centre;
	}

	public void setCentre(String centre) {
		this.centre = centre;
	}

	@Column(name = "DELETED")
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}