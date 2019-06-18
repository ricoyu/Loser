package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;

import com.loserico.workbook.unmarshal.convertor.datetime.DateTimeConvertors;
import com.loserico.workbook.utils.ReflectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 负责将Cell里的值设置到LocalDate类型的字段上
 * <p>
 * Copyright: Copyright (c) 2019-05-23 14:37
 * <p>
 * Company: Sexy Uncle Inc.
 * <p>
 * @author Rico Yu  ricoyu520@gmail.com
 * @version 1.0
 * @on
 */
@Slf4j
public class LocalDateTimeCellCommand extends BaseCellCommand {
	
	private DataFormatter dataFormatter = new DataFormatter();

	private ZoneId zoneId = ZoneId.systemDefault();

	public LocalDateTimeCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(final Cell cell, Object pojo) {
		String value = dataFormatter.formatCellValue(cell);
		LocalDateTime localDateTime = DateTimeConvertors.convert(value);
		if (localDateTime != null) {
			ReflectionUtils.setField(field, pojo, localDateTime);
		}
		
		/*CellType cellTypeEnum = cell.getCellTypeEnum();
		
		// ------------- Step 2 Cell内容是字符串类型的情况 -------------
		if (cellTypeEnum == CellType.STRING) {
			LocalDateTime localDateTime = DateTimeConvertors.convert(str(cell));
			if (localDateTime != null) {
				ReflectionUtils.setField(field, pojo, localDateTime);
			}
			return;
		}
		
		// ------------- Step 3 假设Cell内容是Date类型的 -------------
		Date dateCellValue = cell.getDateCellValue();
		if (dateCellValue == null) {
			return;
		}
		LocalDateTime localDateTime = dateCellValue.toInstant()
				.atZone(zoneId)
				.toLocalDateTime();
		if (localDateTime != null) {
			ReflectionUtils.setField(field, pojo, localDateTime);
		}*/

	}

}
