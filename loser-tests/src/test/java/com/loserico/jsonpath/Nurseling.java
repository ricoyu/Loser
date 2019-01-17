package com.loserico.jsonpath;

import static com.loserico.commons.utils.StringUtils.concat;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.loserico.entity.BaseEntity;
import com.loserico.jsonpath.deserializer.NurselingDeserializer;
import com.loserico.security.codec.HashUtils;

@JsonDeserialize(using=NurselingDeserializer.class)
public class Nurseling extends BaseEntity implements Serializable {

	private static final long serialVersionUID = -1068391972343167707L;

	private String city;

	private String gridId;

	@JsonIgnore
	private Double latitude;

	@JsonIgnore
	private Double longitude;

	private String incomeLevel; //H M L

	private String consumeLevel; //H M L

	private String gender; // M F

	private int ageGroup; // 1 ~ 5

	private boolean married;

	private float count;

	private int type;
	
	@JsonIgnore
	private String hashValue;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getGridId() {
		return gridId;
	}

	public void setGridId(String gridId) {
		this.gridId = gridId;
	}

	public String getIncomeLevel() {
		return incomeLevel;
	}

	public void setIncomeLevel(String incomeLevel) {
		this.incomeLevel = incomeLevel;
	}

	public String getConsumeLevel() {
		return consumeLevel;
	}

	public void setConsumeLevel(String consumeLevel) {
		this.consumeLevel = consumeLevel;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(int ageGroup) {
		this.ageGroup = ageGroup;
	}

	public boolean isMarried() {
		return married;
	}

	public void setMarried(boolean married) {
		this.married = married;
	}

	public float getCount() {
		return count;
	}

	public void setCount(float count) {
		this.count = count;
	}

	public Double getLatitude() {
		return null;
	}

	public Double getLongitude() {
		return null;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getHashValue() {
		if(isBlank(hashValue)) {
			this.hashValue = HashUtils.sha1(concat(Double.toString(latitude), Double.toString(longitude)));
		}
		return hashValue;
	}

	public void setHashValue(String hashValue) {
		this.hashValue = hashValue;
	}
}