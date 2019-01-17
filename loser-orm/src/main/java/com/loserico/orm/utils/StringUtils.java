package com.loserico.orm.utils;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class StringUtils {
	
	public static boolean equalsIgAny(String source, String... targets) {
		if (source == null) {
			return false;
		}

		if (targets == null) {
			return false;
		}

		boolean isEqual = false;
		for (int i = 0; i < targets.length; i++) {
			String target = targets[i];
			if (target == null) {
				continue;
			}
			if (equalsIgCase(source, target)) {
				isEqual = true;
				break;
			}
		}
		return isEqual;
	}
	
	public static boolean equalsIgCase(String beCompared, String... comparetors) {
		if (beCompared == null) {
			return false;
		}

		if (comparetors == null || comparetors.length == 0) {
			return false;
		}

		boolean equals = false;
		for (int i = 0; i < comparetors.length; i++) {
			String comparetor = comparetors[i];
			if (comparetor != null) {
				if (beCompared.trim().equalsIgnoreCase(comparetor.trim())) {
					return equals = true;
				}
			}

		}
		return equals;
	}
	
	/**
	 * 连接每个字符串，如果字符串为null或者空字符串，则忽略之
	 * 
	 * @param args
	 * @return String
	 */
	public static String concat(String... args) {
		return asList(args).stream()
				.filter(s -> isNotBlank(s))
				.collect(joining());
	}
}
