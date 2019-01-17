package com.loserico.search.enums;

public enum Numerics {
	LONG,
	INTEGER,
	SHORT,
	BYTE,
	DOUBLE,
	FLOAT,
	HALF_FLOAT,
	SCALED_FLOAT;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

	
}
