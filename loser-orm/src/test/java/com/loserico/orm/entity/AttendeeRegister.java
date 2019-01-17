package com.loserico.orm.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "attendee_register")
public class AttendeeRegister extends BaseEntity {

	private static final long serialVersionUID = -6703555939793262552L;
	private String childName;
	private LocalDate birthday;
	private String phone;
	private String address;
	private Integer headCount;
	private boolean deleted;
	//标识该预约是否重复
	private boolean duplicate;
	private boolean override;

	@Column(name = "child_name", length = 100, nullable = false)
	@NotBlank(message="您还没有填写宝贝姓名")
	@Length(max = 100)
	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	@Column(name = "BIRTHDAY")
	@NotNull(message="您还没有填写宝贝出生日")
	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	@Column(name = "PHONE", length = 20)
	@NotBlank(message="您还没有填写联系方式")
	@Length(max = 11, message="您填写联系方式不正确")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "ADDRESS", length = 1024)
	@NotBlank(message="您还没有填写地址")
	@Length(max = 1024, message="您填写的地址不正确")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "HEAD_COUNT")
	@Min(1)
	@NotNull(message="您还没有填写到场名额")
	public Integer getHeadCount() {
		return headCount;
	}

	public void setHeadCount(Integer headCount) {
		this.headCount = headCount;
	}

	@Column(name = "deleted")
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Transient
	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	@Transient
	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

}
