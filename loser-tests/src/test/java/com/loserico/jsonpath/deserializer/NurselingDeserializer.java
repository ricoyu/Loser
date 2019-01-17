package com.loserico.jsonpath.deserializer;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.loserico.commons.utils.DateUtils;
import com.loserico.jsonpath.Nurseling;

public class NurselingDeserializer extends StdDeserializer<Nurseling> {

	private static final long serialVersionUID = -1986469894569239292L;

	public NurselingDeserializer() {
		this(null);
	}

	public NurselingDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Nurseling deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode nurselingNode = p.getCodec().readTree(p);
		Nurseling nurseling = new Nurseling();
		nurseling.setCreator(nurselingNode.get("creator") == null ? null : nurselingNode.get("creator").textValue());
		nurseling.setModifier(nurselingNode.get("modifier") == null ? null : nurselingNode.get("modifier").textValue());
		nurseling.setCreateTime(nurselingNode.get("createTime") == null ? null
				: LocalDateTime.parse(nurselingNode.get("createTime").textValue(),
						ofPattern(DateUtils.UTC_DATETIME_FORMAT2)));
		nurseling.setModifier(nurselingNode.get("modifier") == null ? null : nurselingNode.get("modifier").textValue());
		nurseling.setCreateTime(nurselingNode.get("modifyTime") == null ? null
				: LocalDateTime.parse(nurselingNode.get("modifyTime").textValue(),
						ofPattern(DateUtils.UTC_DATETIME_FORMAT2)));
		nurseling.setLatitude(
				nurselingNode.get("location") == null ? null : nurselingNode.get("location").get("lat").doubleValue());
		nurseling.setLongitude(
				nurselingNode.get("location") == null ? null : nurselingNode.get("location").get("lon").doubleValue());
		nurseling.setAgeGroup(nurselingNode.get("ageGroup") == null ? null : nurselingNode.get("ageGroup").intValue());
		nurseling.setCity(nurselingNode.get("city") == null ? null : nurselingNode.get("ageGroup").textValue());
		nurseling.setConsumeLevel(
				nurselingNode.get("consumeLevel") == null ? null : nurselingNode.get("consumeLevel").textValue());
		nurseling.setIncomeLevel(
				nurselingNode.get("incomeLevel") == null ? null : nurselingNode.get("incomeLevel").textValue());
		nurseling.setCount(nurselingNode.get("count") == null ? null : nurselingNode.get("count").floatValue());
		nurseling.setGender(nurselingNode.get("gender") == null ? null : nurselingNode.get("gender").textValue());
		nurseling.setGridId(nurselingNode.get("gridId") == null ? null : nurselingNode.get("gridId").textValue());
		nurseling.setMarried(nurselingNode.get("married") == null ? null : nurselingNode.get("married").booleanValue());
		nurseling.setType(nurselingNode.get("type") == null ? null : nurselingNode.get("type").intValue());
		return nurseling;
	}

}