package com.loserico.jpa.vo;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentVO implements Serializable {

	private static final long serialVersionUID = -1437093485696704461L;

	public static String domain;
	private Long id;
	private Long gradeId;
	private String gender;
	private String headUrl;
	private String chineseName;
	private String englishName;
	private String nationality;
	private Boolean permResident;
	private String preExperience;
	private String hobby;
	private String habit;
	private String others;
	private String cardType;
	private String idCard;
	private String especiallyAsked;
	private LocalDate dob;
	private boolean deleted;
	private String name;
	private String className;
	private String principal;
	private Long father;
	private String fatherName;
	private Long mother;
	private String motherName;
	private String headIcon;
	private String hxGroupId;
	private String gradeName;
	private LocalDateTime enrollDate;
	private LocalDate withdrawDate;
	private Long enrollStatus;
	private String operateUser;
	private String auditRemark;
	private Long operateUserCode;
	private String operatePhone;
	private String operateName;
	private Long familyId;
	private LocalDateTime dropOutDate;
	private String dropOutRemark;
	private LocalDateTime modifyTime;
	private String enrollStatusDesc;
	
	private String creator;

	private LocalDateTime createTime;

	private String modifier;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGender() {
		return gender;
	}

	public String getHeadUrl() {

		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public Boolean getPermResident() {
		return permResident;
	}

	public void setPermResident(Boolean permResident) {
		this.permResident = permResident;
	}

	public String getPreExperience() {
		return preExperience;
	}

	public void setPreExperience(String preExperience) {
		this.preExperience = preExperience;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}

	public String getHabit() {
		return habit;
	}

	public void setHabit(String habit) {
		this.habit = habit;
	}

	public String getOthers() {
		return others;
	}

	public void setOthers(String others) {
		this.others = others;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	@NotBlank(message = "{NotBlank.idCard}")
	@Size(min = 4, message = "{Size.min.idCard}")
	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getEspeciallyAsked() {
		return especiallyAsked;
	}

	public void setEspeciallyAsked(String especiallyAsked) {
		this.especiallyAsked = especiallyAsked;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@NotNull(message = "{NotNull.dob}")
	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public Long getFather() {
		return father;
	}

	public void setFather(Long father) {
		this.father = father;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public Long getMother() {
		return mother;
	}

	public void setMother(Long mother) {
		this.mother = mother;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	@JsonProperty("fullUrl")
	public String getFullUrl() {
		return isBlank(headUrl) ? null : domain + this.headUrl;
	}

	public static String getDomain() {
		return domain;
	}

	public static void setDomain(String domain) {
		StudentVO.domain = domain;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	public String getHxGroupId() {
		return hxGroupId;
	}

	public void setHxGroupId(String hxGroupId) {
		this.hxGroupId = hxGroupId;
	}

	public LocalDate getWithdrawDate() {
		return withdrawDate;
	}

	public void setWithdrawDate(LocalDate withdrawDate) {
		this.withdrawDate = withdrawDate;
	}

	public LocalDateTime getEnrollDate() {
		return enrollDate;
	}

	public void setEnrollDate(LocalDateTime enrollDate) {
		this.enrollDate = enrollDate;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public Long getEnrollStatus() {
		return enrollStatus;
	}

	public void setEnrollStatus(Long enrollStatus) {
		this.enrollStatus = enrollStatus;
	}

	public String getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}

	public String getAuditRemark() {
		return auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}

	public Long getOperateUserCode() {
		return operateUserCode;
	}

	public void setOperateUserCode(Long operateUserCode) {
		this.operateUserCode = operateUserCode;
	}

	public String getOperatePhone() {
		return operatePhone;
	}

	public void setOperatePhone(String operatePhone) {
		this.operatePhone = operatePhone;
	}

	public String getOperateName() {
		return operateName;
	}

	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}

	public Long getFamilyId() {
		return familyId;
	}

	public void setFamilyId(Long familyId) {
		this.familyId = familyId;
	}

	public LocalDateTime getDropOutDate() {
		return dropOutDate;
	}

	public void setDropOutDate(LocalDateTime dropOutDate) {
		this.dropOutDate = dropOutDate;
	}

	public String getDropOutRemark() {
		return dropOutRemark;
	}

	public void setDropOutRemark(String dropOutRemark) {
		this.dropOutRemark = dropOutRemark;
	}

	public LocalDateTime getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(LocalDateTime modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Long getGradeId() {
		return gradeId;
	}

	public void setGradeId(Long gradeId) {
		this.gradeId = gradeId;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getEnrollStatusDesc() {
		return enrollStatusDesc;
	}

	public void setEnrollStatusDesc(String enrollStatusDesc) {
		this.enrollStatusDesc = enrollStatusDesc;
	}
	
}