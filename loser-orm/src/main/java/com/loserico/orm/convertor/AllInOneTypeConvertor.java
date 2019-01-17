package com.loserico.orm.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;

import com.peacefish.orm.commons.enums.ActiveStatus;
import com.peacefish.orm.commons.enums.Gender;

public class AllInOneTypeConvertor {

	public static volatile AllInOneTypeConvertor instance = null;

	public AllInOneTypeConvertor() {

	}

	public static AllInOneTypeConvertor getInstance() {
		if (instance == null) {
			synchronized (AllInOneTypeConvertor.class) {
				if (instance == null) {
					instance = new AllInOneTypeConvertor();
				}
			}
		}
		return instance;
	}
	
	public Object convert2Target(Date value, java.util.Date target) {
		return new java.util.Date(value.getTime());
	}
	
	public Object convert2Target(Timestamp value, java.util.Date target) {
		return new java.util.Date(value.getTime());
	}

	public Object convert2Target(BigDecimal value, Long target) {
		return value.longValue();
	}
	
	public Object convert2Target(BigInteger value, Long target) {
		return value.longValue();
	}

	public Object convert2Target(Integer value, Long target) {
		return value.longValue();
	}
	
	public Object convert2Target(BigDecimal value, Float target) {
		return value.floatValue();
	}

	public Object convert2Target(BigDecimal value, Integer target) {
		return value.intValue();
	}

	public Object convert2Target(BigInteger value, Integer target) {
		return value.intValue();
	}
	
	public Object convert2Target(Long value, Integer target) {
		return value.intValue();
	}

	public Object convert2Target(Integer value, String target) {
		return value.toString();
	}

	
	public String convert2Target(Character value, String target) {
		return value.toString();
	}
	
	public Gender convert2Target(String value, Gender target) {
		return Gender.valueOf(Gender.class, value);
	}
	
	public ActiveStatus convert2Target(String value, ActiveStatus target) {
		return ActiveStatus.valueOf(ActiveStatus.class, value);
	}
	
}
