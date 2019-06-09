package com.loserico.workbook.unmarshal.command;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.loserico.commons.utils.DateUtils;
import com.loserico.workbook.utils.ReflectionUtils;

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
public class LocalDateTimeCellCommand extends BaseCellCommand {

	// yyyyMMdd
	private static final Pattern PT_IOS_DATETIME = Pattern.compile("\\d{4}-\\d{2}-\\d{2}(\\s+)\\d{2}:\\d{2}:\\d{2}");
	public static final String FMT_ISO_DATETIME = "yyyy-MM-dd HH:mm:ss";

	private AtomicReference<Function<Cell, LocalDateTime>> atomicReference = new AtomicReference<Function<Cell, LocalDateTime>>(
			null);

	public LocalDateTimeCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(final Cell cell, Object pojo) {
		// ------------- Step 1 -------------
		if (atomicReference.get() != null) {
			LocalDateTime localDateTime = atomicReference.get().apply(cell);
			if (localDateTime != null) {
				ReflectionUtils.setField(this.field, pojo, localDateTime);
			}
			return;
		}

		CellType cellTypeEnum = cell.getCellTypeEnum();

		// ------------- Step 2 Cell内容是字符串类型的情况 -------------
		if (cellTypeEnum == CellType.STRING) {
			Function<Cell, LocalDateTime> functionConvertor = (c) -> {
				String cellValue = str(c);
				return DateUtils.toLocalDateTime(cellValue.trim());
			};
			LocalDateTime localDateTime = functionConvertor.apply(cell);
			if (localDateTime != null) {
				ReflectionUtils.setField(field, pojo, localDateTime);
			}
			atomicReference.compareAndSet(null, functionConvertor);
			return;
		}

		// ------------- Step 3 假设Cell内容是Date类型的 -------------
		Function<Cell, LocalDateTime> functionConvertor = (c) -> {
			return DateUtils.toLocalDateTime(c.getDateCellValue());
		};
		LocalDateTime localDateTime = functionConvertor.apply(cell);
		atomicReference.compareAndSet(null, functionConvertor);
		if (localDateTime != null) {
			ReflectionUtils.setField(field, pojo, localDateTime);
			return;
		}

	}

}
