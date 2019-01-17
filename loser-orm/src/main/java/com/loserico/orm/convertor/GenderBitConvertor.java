package com.loserico.orm.convertor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.peacefish.orm.commons.enums.Gender;

@Converter
public class GenderBitConvertor implements AttributeConverter<Gender, Byte> {

	@Override
	public Byte convertToDatabaseColumn(Gender attribute) {
		if (Gender.MALE == attribute) {
			return 1;
		}
		return 0;
	}

	@Override
	public Gender convertToEntityAttribute(Byte dbData) {
		return dbData.intValue() == 1 ? Gender.MALE : Gender.FEMALE;
	}

}
