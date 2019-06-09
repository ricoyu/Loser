package com.loserico.workbook.unmarshal.command;

import static java.time.format.DateTimeFormatter.ofPattern;

import java.lang.reflect.Field;
import java.time.LocalDate;
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
public class LocalDateCellCommand extends BaseCellCommand {

	// yyyyMMdd
	private static final Pattern PT_DATE_CONCISE = Pattern.compile("\\d{8}");
	private static final DateTimeFormatter FMT_DATE_CONCISE = ofPattern("yyyyMMdd"); // 20180711这种格式

	private AtomicReference<Function<Cell, LocalDate>> atomicReference = new AtomicReference<Function<Cell, LocalDate>>(
			null);

	public LocalDateCellCommand(Field field) {
		super(field);
	}

	@Override
	public void invoke(final Cell cell, Object pojo) {
		// ------------- Step 1 -------------
		if (atomicReference.get() != null) {
			LocalDate localDate = atomicReference.get().apply(cell);
			ReflectionUtils.setField(this.field, pojo, localDate);
			return;
		}

		CellType cellTypeEnum = cell.getCellTypeEnum();

		// ------------- Step 2 Cell内容是字符串类型的情况 -------------
		if (cellTypeEnum == CellType.STRING) {
			Function<Cell, LocalDate> functionConvertor = (c) -> {
				String cellValue = str(c);

				return DateUtils.toLocalDate(cellValue.trim());
			};
			LocalDate localDate = functionConvertor.apply(cell);
			ReflectionUtils.setField(field, pojo, localDate);
			atomicReference.compareAndSet(null, functionConvertor);
			return;
		}

		// ------------- Step 3 假设Cell内容是Date类型的 -------------
		LocalDate localDate = DateUtils.toLocalDate(cell.getDateCellValue());
		if (localDate != null) {
			Function<Cell, LocalDate> functionConvertor = (c) -> {
				return DateUtils.toLocalDate(c.getDateCellValue());
			};
			ReflectionUtils.setField(field, pojo, localDate);
			atomicReference.compareAndSet(null, functionConvertor);
			return;
		}

		// ------------- Step 4 -------------
		/*
		 * 遇到Excel某列值看到的是2018-9-10这种形式的, 但获取到的CellType却是Numeric, 拿到的是类似65333这样的值
		 * 所以现在改成先拿DateCellValue(上面一步), 拿不到再走Numberic
		 */
		if (cellTypeEnum == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA) {
			String dateStr = String.valueOf((long) cell.getNumericCellValue());
			if (PT_DATE_CONCISE.matcher(dateStr).matches()) {
				/*
				 * 因为functionConvertor一旦创建, 就会缓存起来, 下次就不会走到这边了
				 * 所以取值就得重新调用: String.valueOf((long) cell.getNumericCellValue())
				 */
				Function<Cell, LocalDate> functionConvertor = (c) -> {
					return LocalDate.parse(String.valueOf((long) c.getNumericCellValue()), FMT_DATE_CONCISE);
				};
				atomicReference.compareAndSet(null, functionConvertor);
				localDate = functionConvertor.apply(cell);
				if (localDate != null) {
					ReflectionUtils.setField(field, pojo, localDate);
				}
			}

			return;
		}
	}

}
