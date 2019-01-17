package com.loserico.orm.convertor;

import java.sql.Timestamp;
import java.util.Date;

import com.loserico.orm.dynamic.MethodHandleable;

public class DateTypeConverter implements MethodHandleable{

	public Date convert(Timestamp value) {
		return new Date(value.getTime());
	}
	
	public Date convert(Long value) {
		return new Date(value);
	}
	
	public Date convert(java.sql.Date value) {
		return new Date(value.getTime());
	}

}
