package com.loserico.search.enums;

/**
 * Analysis is the process of converting text, like the body of any email,
 * into tokens or terms which are added to the inverted index for searching.
 * Analysis is performed by an analyzer which can be either a built-in analyzer or a custom analyzer defined per index.
 * 
 * <p>
 * Copyright: Copyright (c) 2018-09-04 15:06
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
public enum Analyzer {

	STANDARD,
	IK_SMART,
	IK_MAX_WORD;

	@Override
	public String toString() {
		return name().toLowerCase();
	}

}
