package com.loserico.commons.json.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleKeyMixin {
	
	@JsonIgnore
	private int hashCode;

	public SimpleKeyMixin(@JsonProperty("params") Object... elements) {
		
	}
}