package com.loserico.web.advice;

import static com.loserico.commons.utils.StringUtils.equalTo;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.beans.PropertyEditorSupport;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import com.loserico.commons.utils.DateUtils;

/**
 * 将字符串表示的日期转换成java.util.Date类型，支持yyyy-MM-dd HH:mm:ss和yyyy-MM-dd HH:mm格式
 * 
 * @author Loser
 * @since May 22, 2016
 * @version
 *
 */
@ControllerAdvice
public class GlobalBindingAdivce {

	private static final Logger logger = LoggerFactory.getLogger(GlobalBindingAdivce.class);

	// yyyy-MM-dd HH:mm:ss
	private static final String DATETIME = "\\d{4}-\\d{2}-\\d{2}(\\s+)\\d{2}:\\d{2}:\\d{2}";
	private static final Pattern DATETIME_PATTERN = Pattern.compile(DATETIME);
	// yyyy-MM-dd HH:mm
	private static final String DATETIME_SHORT = "\\d{4}-\\d{2}-\\d{2}(\\s+)\\d{2}:\\d{2}";
	private static final Pattern DATETIME_SHORT_PATTERN = Pattern.compile(DATETIME_SHORT);
	// yyyy-MM-dd
	private static final String DATE = "\\d{4}-\\d{2}-\\d{2}";
	// dd-MM-yyyy
	private static final String DATE_EN = "\\d{2}-\\d{2}-\\\\d{4}";
	// yyyy-MM-d
	private static final String DATE1 = "\\d{4}-\\d{2}-\\d{1}";
	// d-MM-yyyy
	private static final String DATE_EN1 = "\\d{1}-\\d{2}-\\\\d{4}";
	// yyyy-M-d
	private static final String DATE2 = "\\d{4}-\\d{1}-\\d{}";
	// d-M-yyyy
	private static final String DATE_EN2 = "\\d{1}-\\d{1}-\\\\d{4}";
	// yyyy-MM
	private static final String MONTH = "\\d{4}-\\d{2}";
	private static final String MONTH_CONCISE = "\\d{4}\\d{2}";

	private static final String TIME_LONG = "\\d{2}:\\d{2}:\\d{2}";
	private static final String TIME_MIDDLE = "\\d{2}:\\d{2}";
	private static final String TIME_SHORT = "\\d{2}";

	@InitBinder
	public void binder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new DateEditor());
		binder.registerCustomEditor(LocalDate.class, new LocalDateEditor());
		binder.registerCustomEditor(LocalDateTime.class, new LocalDateTimeEditor());
		binder.registerCustomEditor(LocalTime.class, new LocalTimeEditor());
		binder.registerCustomEditor(YearMonth.class, new YearMonthEditor());
	}

	private class DateEditor extends PropertyEditorSupport {

		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if (StringUtils.isBlank(text)) {
				setValue(null);
			} else {
				Date result = null;
				try {
					Matcher matcher = DATETIME_PATTERN.matcher(text);
					if (matcher.matches()) {
						String spaces = matcher.group(1);
						if (!equalTo(spaces, " ")) {
							text = text.replace(spaces, " ");
						}
						result = DateUtils.parse(text, DateUtils.FMT_ISO_DATETIME_SHORT);
						setValue(result);
						return;
					}

					matcher = DATETIME_SHORT_PATTERN.matcher(text);
					if (matcher.matches()) {
						String spaces = matcher.group(1);
						if (!equalTo(spaces, " ")) {
							text = text.replace(spaces, " ");
						}
						text = text.replace(spaces, " ");
						result = DateUtils.parse(text, DateUtils.FMT_ISO_DATETIME);
						setValue(result);
						return;
					}

					if (Pattern.matches(DATE, text)) {
						result = DateUtils.parse(text, DateUtils.FMT_ISO_DATE);
						setValue(result);
						return;
					}
				} catch (Exception e) {
					logger.error("msg", e);
				}

			}
		}

	}

	private class LocalDateTimeEditor extends PropertyEditorSupport {

		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if (StringUtils.isBlank(text)) {
				setValue(null);
			} else {
				LocalDateTime result = null;
				try {
					if (Pattern.matches(DATETIME_SHORT, text)) {
						result = LocalDateTime.parse(text, ofPattern("yyyy-MM-dd HH:mm"));
					} else if (Pattern.matches(DATETIME, text)) {
						result = LocalDateTime.parse(text, ofPattern("yyyy-MM-dd HH:mm:ss"));
					} else if (Pattern.matches(DATE, text)) {
						result = LocalDateTime.parse(text + " 00:00:00", ofPattern("yyyy-MM-dd HH:mm:ss"));
					} else if (Pattern.matches(MONTH, text)) {// yyyy-MM 不含日期
						result = LocalDateTime.parse(text + "-01 00:00:00", ofPattern("yyyy-MM-dd HH:mm:ss"));
					} else if (Pattern.matches(DATE_EN, text)) {// dd-MM-yyyy 不含时间
						result = LocalDateTime.parse(text + " 00:00:00", ofPattern("dd-MM-yyyy HH:mm:ss"));
					} else if (text.contains("-") || text.contains(" ")) {
						if (text.length() > 10 && text.charAt(10) == 'T') {
							if (text.endsWith("Z")) {
								result = LocalDateTime.ofInstant(Instant.parse(text), ZoneOffset.UTC); // 2018-03-02T16:19:00.000Z
							} else {
								result = LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME); // 2018-03-02T16:19:00.000
							}
						}
					} else {
						long milis = Long.parseLong(text);
						result = LocalDateTime.ofInstant(Instant.ofEpochMilli(milis), ZoneOffset.ofHours(8));
					}
				} catch (Exception e) {
					logger.error("msg", e);
				}

				setValue(result);
			}
		}

	}

	private class LocalDateEditor extends PropertyEditorSupport {

		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if (StringUtils.isBlank(text)) {
				setValue(null);
			} else {
				LocalDate result = null;
				try {
					if (Pattern.matches(DATE, text)) {
						result = LocalDate.parse(text, ofPattern("yyyy-MM-dd"));
					} else if (Pattern.matches(DATE1, text)) {
						result = LocalDate.parse(text, ofPattern("yyyy-MM-d"));
					} else if (Pattern.matches(DATE2, text)) {
						result = LocalDate.parse(text, ofPattern("yyyy-M-d"));
					} else if (Pattern.matches(MONTH, text)) {// yyyy-MM 不含日期
						result = LocalDate.parse(text + "-01", ofPattern("yyyy-MM-dd"));
					} else if (Pattern.matches(DATE_EN, text)) {// dd-MM-yyyy 不含时间
						result = LocalDate.parse(text, ofPattern("dd-MM-yyyy"));
					} else if (Pattern.matches(DATE_EN1, text)) {// d-MM-yyyy 不含时间
						result = LocalDate.parse(text, ofPattern("d-MM-yyyy"));
					} else if (Pattern.matches(DATE_EN2, text)) {// d-M-yyyy 不含时间
						result = LocalDate.parse(text, ofPattern("d-M-yyyy"));
					} else { // 认为是Epoch毫秒数
						result = Instant.ofEpochMilli(Long.parseLong(text)).atZone(ZoneOffset.ofHours(8)).toLocalDate();
					}
				} catch (Exception e) {
					logger.error("msg", e);
				}

				setValue(result);
			}
		}

	}

	private class LocalTimeEditor extends PropertyEditorSupport {

		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if (StringUtils.isBlank(text)) {
				setValue(null);
			} else {
				LocalTime result = null;
				try {
					if (Pattern.matches(TIME_LONG, text)) {
						result = LocalTime.parse(text, ofPattern("HH:mm:ss"));
					} else if (Pattern.matches(TIME_MIDDLE, text)) {
						result = LocalTime.parse(text, ofPattern("HH:mm"));
					} else if (Pattern.matches(TIME_SHORT, text)) {
						result = LocalTime.parse(text, ofPattern("HH"));
					}
				} catch (Exception e) {
					logger.error("msg", e);
				}

				setValue(result);
			}
		}

	}

	private class YearMonthEditor extends PropertyEditorSupport {

		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			if (StringUtils.isBlank(text)) {
				setValue(null);
			} else {
				YearMonth result = null;
				try {
					if (Pattern.matches(MONTH, text)) {// yyyy-MM
						result = YearMonth.parse(text);
					} else if (Pattern.matches(MONTH_CONCISE, text)) {// yyyyMM
						result = YearMonth.parse(text, ofPattern("yyyyMM"));
					}
				} catch (Exception e) {
					logger.error("msg", e);
				}

				setValue(result);
			}
		}

	}

}