package com.loserico.jackson;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class EventDeserializer {
	public String name;

	@JsonDeserialize(using = CustomDateDeserializer.class)
	@JsonProperty("eventDate")
	public LocalDateTime birthday;
}