package com.loserico.workbook.unmarshal.command;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

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

	private AtomicReference<Function<Cell, LocalDateTime>> atomicReference = new AtomicReference<Function<Cell, LocalDateTime>>(
			null);
	private ZoneId zoneId = ZoneId.systemDefault();

	public LocalDateTimeCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(final Cell cell, Object pojo) {
		// ------------- Step 1 -------------
		Function<Cell, LocalDateTime> func = atomicReference.get();
		if (func != null) {
			LocalDateTime localDateTime = null;
			try {
				localDateTime = func.apply(cell);
				if (localDateTime != null) {
					ReflectionUtils.setField(this.field, pojo, localDateTime);
				}
				return;
			} catch (Exception e) {
				log.error("这是同一列出现了不同的数据格式吗?, Row[{}], Column[{}]" + e.getMessage(), cell.getRowIndex(),
						cell.getColumnIndex());
				atomicReference.compareAndSet(func, null);
			}
		}

		CellType cellTypeEnum = cell.getCellTypeEnum();

		// ------------- Step 2 Cell内容是字符串类型的情况 -------------
		if (cellTypeEnum == CellType.STRING) {
			Function<Cell, LocalDateTime> functionConvertor = (c) -> {
				String cellValue = str(c);
				return DateTimeConvertors.convert(cellValue);
			};
			atomicReference.compareAndSet(null, functionConvertor);

			LocalDateTime localDateTime = functionConvertor.apply(cell);
			if (localDateTime != null) {
				ReflectionUtils.setField(field, pojo, localDateTime);
			}
			return;
		}

		// ------------- Step 3 假设Cell内容是Date类型的 -------------
		Function<Cell, LocalDateTime> functionConvertor = (c) -> {
			Date dateCellValue = c.getDateCellValue();
			if (dateCellValue == null) {
				return null;
			}
			return dateCellValue.toInstant()
					.atZone(zoneId)
					.toLocalDateTime();
		};
		LocalDateTime localDateTime = functionConvertor.apply(cell);
		atomicReference.compareAndSet(null, functionConvertor);
		if (localDateTime != null) {
			ReflectionUtils.setField(field, pojo, localDateTime);
		}

	}

}
