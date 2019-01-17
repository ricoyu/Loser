package com.loserico.orm.operations;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.loserico.orm.entity.BaseEntity;
import com.peacefish.orm.commons.enums.Gender;

public class User extends BaseEntity {

	private static final long serialVersionUID = -2467389753957466344L;
	private Long id;

	private String creator;

	private LocalDateTime createTime;

	private String modifier;

	private LocalDateTime modifiyTime;
	private String username;
	private String password;
	private Boolean locked = Boolean.FALSE;
	private Gender gender;
	private LocalDate birthday;
	private String email;
	private String cellphone;
	private String name;
	private Integer version;
	private String salt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDateTime getModifiyTime() {
		return modifiyTime;
	}

	public void setModifiyTime(LocalDateTime modifiyTime) {
		this.modifiyTime = modifiyTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

}