package com.loserico.commons.utils;

import static java.time.format.DateTimeFormatter.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * @author Loser
 * @since May 24, 2016
 * @version 2.0
 *
 */
public final class DateUtils {

	private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

	/** 毫秒 */
	public final static long MS = 1;
	/** 每秒钟的毫秒数 */
	public final static long SECOND_MS = MS * 1000;
	/** 每分钟的毫秒数 */
	public final static long MINUTE_MS = SECOND_MS * 60;
	/** 每小时的毫秒数 */
	public final static long HOUR_MS = MINUTE_MS * 60;
	/** 每天的毫秒数 */
	public final static long DAY_MS = HOUR_MS * 24;

	public static final int MINUTES_PER_HOUR = 60;
	public static final int SECONDS_PER_MINUTE = 60;
	public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

	/**
	 * Date format pattern used to parse HTTP date headers in RFC 1123 format.
	 * <p>
	 * 星期二, 24 五月 2016 13:51:38 CST
	 * <p>
	 * Tue, 24 May 2016 13:52:03 CST
	 * <p>
	 * Wed, 16 Nov 2016 10:43:15 GMT
	 */
	public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

	/**
	 * Date format pattern used to parse HTTP date headers in RFC 1036 format. Tue, 24-May-16
	 * 13:50:15 CST 星期二, 24-五月-16 13:50:34 CST
	 */
	public static final String PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";

	/**
	 * Date format pattern used to parse HTTP date headers in ANSI C Tue May 24 13:53:34 2016 星期二 五月
	 * 24 13:54:01 2016 {@code asctime()} format.
	 */
	public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";

	private static final List<String[]> DATE_PATTERN_FORMART_LIST = new ArrayList<>();
	static {

	}

	/**
	 * -------------- 正则表达式, 日期格式对 -------------------------
	 */
	// yyyy-MM-dd
	private static final String PT_ISO_DATE = "\\d{4}-\\d{2}-\\d{2}";
	public static final String FMT_ISO_DATE = "yyyy-MM-dd";
	// yyyy-MM-d
	private static final String PT_ISO_DATE_1 = "\\d{4}-\\d{2}-\\d{1}";
	public static final String FMT_ISO_DATE_1 = "yyyy-MM-d";
	// yyyy-M-dd
	private static final String PT_ISO_DATE_2 = "\\d{4}-\\d{1}-\\d{2}";
	public static final String FMT_ISO_DATE_2 = "yyyy-M-dd";
	// yyyy-M-d
	private static final String PT_ISO_DATE_3 = "\\d{4}-\\d{1}-\\d{1}";
	public static final String FMT_ISO_DATE_3 = "yyyy-M-d";

	// MM-dd-yyyy
	private static final String PT_DATE_EN = "\\d{2}-\\d{2}-\\\\d{4}";
	public static final String FMT_DATE_FORMAT_EN = "MM/dd/yyyy";
	// MM-d-yyyy
	private static final String PT_DATE_EN_1 = "\\d{2}-\\d{1}-\\\\d{4}";
	public static final String FMT_DATE_FORMAT_EN_1 = "MM/d/yyyy";
	// M-dd-yyyy
	private static final String PT_DATE_EN_2 = "\\d{1}-\\d{2}-\\\\d{4}";
	public static final String FMT_DATE_FORMAT_EN_2 = "M/dd/yyyy";
	// M-d-yyyy
	private static final String PT_DATE_EN_3 = "\\d{1}-\\d{1}-\\\\d{4}";
	public static final String FMT_DATE_FORMAT_EN_3 = "M/d/yyyy";

	// yyyy/MM/dd
	private static final String PT_DATE_EN_4 = "\\d{4}/\\d{2}/\\d{2}";
	public static final String FMT_DATE_FORMAT_EN_4 = "yyyy/MM/dd"; // 2018/11/11这种格式
	// yyyy/MM/d
	private static final String PT_DATE_EN_5 = "\\d{4}/\\d{2}/\\d{1}";
	public static final String FMT_DATE_FORMAT_EN_5 = "yyyy/MM/d"; // 2018/11/1这种格式
	// yyyy/M/dd
	private static final String PT_DATE_EN_6 = "\\d{4}/\\d{1}/\\d{2}";
	public static final String FMT_DATE_FORMAT_EN_6 = "yyyy/M/dd"; // 2018/7/11这种格式
	// yyyy/M/d
	private static final String PT_DATE_EN_7 = "\\d{4}/\\d{1}/\\d{1}";
	public static final String FMT_DATE_FORMAT_EN_7 = "yyyy/M/d"; // 2018/7/1这种格式

	// yyyyMMdd
	private static final String PT_DATE_CONCISE = "\\d{8}";
	public static final String FMT_DATE_CONCISE = "yyyyMMdd"; // 20180711这种格式

	//d-MMM-yy
	private static final String PT_DATE_FORMAT_EN_8 = "\\d{1}-\\w{3}-\\d{2}";
	public static final String FMT_DATE_FORMAT_EN_8 = "d-MMM-yy"; // 15-Sep-18 1-Sep-18 这种格式

	// 2016-05-24 13:54:30.926
	public static final String ISO_DATE_MILISECONDS = "yyyy-MM-dd HH:mm:ss.SSS";
	
	// ----------------------- 下面是日期时间类型 ---------------------------------------------------
	// yyyy-MM-dd HH:mm:ss
	private static final String PT_ISO_DATETIME = "\\d{4}-\\d{2}-\\d{2}(\\s+)\\d{2}:\\d{2}:\\d{2}";
	public static final String FMT_ISO_DATETIME = "yyyy-MM-dd HH:mm:ss";
	// yyyy-MM-d HH:mm:ss
	private static final String PT_ISO_DATETIME_1 = "\\d{4}-\\d{2}-\\d{1}(\\s+)\\d{2}:\\d{2}:\\d{2}";
	public static final String FMT_ISO_DATETIME_1 = "yyyy-MM-d HH:mm:ss";
	// yyyy-M-dd HH:mm:ss
	private static final String PT_ISO_DATETIME_2 = "\\d{4}-\\d{1}-\\d{2}(\\s+)\\d{2}:\\d{2}:\\d{2}";
	public static final String FMT_ISO_DATETIME_2 = "yyyy-M-dd HH:mm:ss";
	// yyyy-M-d HH:mm:ss
	private static final String PT_ISO_DATETIME_3 = "\\d{4}-\\d{1}-\\d{1}(\\s+)\\d{2}:\\d{2}:\\d{2}";
	public static final String FMT_ISO_DATETIME_3 = "yyyy-M-d HH:mm:ss";
	// yyyy-M-d H:mm:ss
	private static final String PT_ISO_DATETIME_4 = "\\d{4}-\\d{1}-\\d{1}(\\s+)\\d{1}:\\d{2}:\\d{2}";
	public static final String FMT_ISO_DATETIME_4 = "yyyy-M-d H:mm:ss";

	// yyyy-MM-d H:mm:ss
	private static final String PT_ISO_DATETIME_5 = "\\d{4}-\\d{2}-\\d{1}(\\s+)\\d{1}:\\d{2}:\\d{2}";
	public static final String FMT_ISO_DATETIME_5 = "yyyy-MM-d H:mm:ss";

	// yyyy-MM-dd HH:mm
	private static final String PT_ISO_DATETIME_SHORT = "\\d{4}-\\d{2}-\\d{2}(\\s+)\\d{2}:\\d{2}";
	public static final String FMT_ISO_DATETIME_SHORT = "yyyy-MM-dd HH:mm";
	// yyyy-MM-d HH:mm
	private static final String PT_ISO_DATETIME_SHORT_1 = "\\d{4}-\\d{2}-\\d{1}(\\s+)\\d{2}:\\d{2}";
	public static final String FMT_ISO_DATETIME_SHORT_1 = "yyyy-MM-d HH:mm";
	// yyyy-M-dd HH:mm
	private static final String PT_ISO_DATETIME_SHORT_2 = "\\d{4}-\\d{1}-\\d{2}(\\s+)\\d{2}:\\d{2}";
	public static final String FMT_ISO_DATETIME_SHORT_2 = "yyyy-M-dd HH:mm";
	// yyyy-M-d HH:mm
	private static final String PT_ISO_DATETIME_SHORT_3 = "\\d{4}-\\d{1}-\\d{1}(\\s+)\\d{2}:\\d{2}";
	public static final String FMT_ISO_DATETIME_SHORT_3 = "yyyy-M-d HH:mm";

	// yyyy-MM-dd H:mm
	private static final String PT_ISO_DATETIME_SHORT_4 = "\\d{4}-\\d{2}-\\d{2}(\\s+)\\d{1}:\\d{2}";
	public static final String FMT_ISO_DATETIME_SHORT_4 = "yyyy-MM-dd H:mm";
	// yyyy-MM-d H:mm
	private static final String PT_ISO_DATETIME_SHORT_5 = "\\d{4}-\\d{2}-\\d{1}(\\s+)\\d{1}:\\d{2}";
	public static final String FMT_ISO_DATETIME_SHORT_5 = "yyyy-MM-d H:mm";
	// yyyy-M-dd H:mm
	private static final String PT_ISO_DATETIME_SHORT_6 = "\\d{4}-\\d{1}-\\d{2}(\\s+)\\d{1}:\\d{2}";
	public static final String FMT_ISO_DATETIME_SHORT_6 = "yyyy-M-dd H:mm";
	// yyyy-M-d H:mm
	private static final String PT_ISO_DATETIME_SHORT_7 = "\\d{4}-\\d{1}-\\d{1}(\\s+)\\d{1}:\\d{2}";
	public static final String FMT_ISO_DATETIME_SHORT_7 = "yyyy-M-d H:mm";

	//MM/dd/yyyy HH:mm:ss
	private static final String PT_DATETIME_FORMAT_EN = "\\d{2}/\\d{2}/\\d{4}(\\s+)\\d{2}:\\d{2}:\\d{2}";
	public static final String FMT_DATETIME_FORMAT_EN = "MM/dd/yyyy HH:mm:ss";
	//yyyy/MM/dd HH:mm:ss
	private static final String PT_DATETIME_FORMAT_EN_1 = "\\d{4}/\\d{2}/\\d{2}(\\s+)\\d{2}:\\d{2}:\\d{2}";
	public static final String FMT_DATETIME_FORMAT_EN_1 = "yyyy/MM/dd HH:mm:ss";
	//yyyy/MM/d HH:mm:ss
	private static final String PT_DATETIME_FORMAT_EN_2 = "\\d{4}/\\d{2}/\\d{1}(\\s+)\\d{2}:\\d{2}:\\d{2}";
	public static final String FMT_DATETIME_FORMAT_EN_2 = "yyyy/MM/d HH:mm:ss";
	//yyyy/M/dd HH:mm:ss
	private static final String PT_DATETIME_FORMAT_EN_3 = "\\d{4}/\\d{1}/\\d{2}(\\s+)\\d{2}:\\d{2}:\\d{2}";
	public static final String FMT_DATETIME_FORMAT_EN_3 = "yyyy/M/dd HH:mm:ss";
	//yyyy/M/d HH:mm:ss
	private static final String PT_DATETIME_FORMAT_EN_4 = "\\d{4}/\\d{1}/\\d{1}(\\s+)\\d{2}:\\d{2}:\\d{2}";
	public static final String FMT_DATETIME_FORMAT_EN_4 = "yyyy/M/d HH:mm:ss";
	
	//MM/dd/yyyy HH:mm:ss
	private static final String PT_DATETIME_FORMAT_EN_5 = "\\d{2}/\\d{2}/\\d{4}(\\s+)\\d{1}:\\d{2}:\\d{2}";
	public static final String FMT_DATETIME_FORMAT_EN_5 = "MM/dd/yyyy H:mm:ss";
	//yyyy/MM/dd HH:mm:ss
	private static final String PT_DATETIME_FORMAT_EN_6 = "\\d{4}/\\d{2}/\\d{2}(\\s+)\\d{1}:\\d{2}:\\d{2}";
	public static final String FMT_DATETIME_FORMAT_EN_6 = "yyyy/MM/dd H:mm:ss";
	//yyyy/MM/d HH:mm:ss
	private static final String PT_DATETIME_FORMAT_EN_7 = "\\d{4}/\\d{2}/\\d{1}(\\s+)\\d{1}:\\d{2}:\\d{2}";
	public static final String FMT_DATETIME_FORMAT_EN_7 = "yyyy/MM/d H:mm:ss";
	//yyyy/M/dd HH:mm:ss
	private static final String PT_DATETIME_FORMAT_EN_8 = "\\d{4}/\\d{1}/\\d{2}(\\s+)\\d{1}:\\d{2}:\\d{2}";
	public static final String FMT_DATETIME_FORMAT_EN_8 = "yyyy/M/dd H:mm:ss";
	//yyyy/M/d HH:mm:ss
	private static final String PT_DATETIME_FORMAT_EN_9 = "\\d{4}/\\d{1}/\\d{1}(\\s+)\\d{1}:\\d{2}:\\d{2}";
	public static final String FMT_DATETIME_FORMAT_EN_9 = "yyyy/M/d H:mm:ss";

	public static final String UTC_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String UTC_DATETIME_FORMAT2 = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	public static final String DEFAULT_FORMAT = FMT_ISO_DATETIME;


	private static final String PT_HTTP_DATE_TIME_HEADER_PATTERN = "[MTWFS][a-z]{2},\\s+\\d{2}\\s+[JFMASOND][a-z]{2}\\s+\\d{4}\\s+\\d{2}:\\d{2}:\\d{2}\\s+GMT";
	// 这种形式：Fri, 03 Aug 2018 03:42:07 GMT
	public static final String FMT_HTTP_DATE_HEADER_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
	
	// yyyy-MM
	private static final String PT_MONTH = "\\d{4}-\\d{2}";

	private static final String PT_TIME_LONG = "\\d{2}:\\d{2}:\\d{2}";
	private static final String PT_TIME_MIDDLE = "\\d{2}:\\d{2}";
	private static final String PT_TIME_SHORT = "\\d{2}";

	public static TimeZone PST = TimeZone.getTimeZone("America/Los_Angeles");
	public static TimeZone LONDON = TimeZone.getTimeZone("Europe/London");
	public static TimeZone INDIA = TimeZone.getTimeZone("Asia/Calcutta");
	public static TimeZone CHINA = TimeZone.getTimeZone("Asia/Chongqing");
	public static TimeZone JAPAN = TimeZone.getTimeZone("Asia/Tokyo");
	public static TimeZone GMT = TimeZone.getTimeZone("GMT");

	public static final ZoneId CTT = ZoneId.of("Asia/Shanghai");
	public static final ZoneId SHANG_HAI = CTT;

	public static Map<TimeZone, Locale> timeZoneLocaleMap = new HashMap<>();
	static {
		timeZoneLocaleMap.put(CHINA, Locale.CHINA);
		timeZoneLocaleMap.put(GMT, Locale.ENGLISH);
		timeZoneLocaleMap.put(PST, Locale.ENGLISH);
		timeZoneLocaleMap.put(LONDON, Locale.ENGLISH);
		timeZoneLocaleMap.put(INDIA, Locale.ENGLISH);
		timeZoneLocaleMap.put(JAPAN, Locale.JAPAN);
	}

	public static enum Type {
		Year,
		Month,
		Week,
		Day,
		Hour,
		Minutes,
		Seconds;
	}

	/** This class should not be instantiated. */
	private DateUtils() {
	}

	/**
	 * Clears thread-local variable containing {@link java.text.DateFormat} cache.
	 *
	 * @since 4.3
	 */
	public static void clearThreadLocal() {
		SimpleDateFormatHolder.clearThreadLocal();
	}

	/**
	 * A factory for {@link SimpleDateFormat}s. The instances are stored in a threadlocal way
	 * because SimpleDateFormat is not threadsafe as noted in {@link SimpleDateFormat its javadoc}.
	 *
	 */
	final static class SimpleDateFormatHolder {

		private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREADLOCAL_FORMATS = new ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>();

		/**
		 * 获取SimpleDateFormat对象，timezone默认为Asia/Chongqing，locale为SIMPLIFIED_CHINESE
		 * 
		 * @param pattern
		 * @return
		 */
		public static SimpleDateFormat formatFor(final String pattern) {
			Objects.requireNonNull(pattern);
			final SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
			Map<String, SimpleDateFormat> formats = ref == null ? null : ref.get();
			if (formats == null) {
				formats = new HashMap<String, SimpleDateFormat>();
				THREADLOCAL_FORMATS.set(new SoftReference<Map<String, SimpleDateFormat>>(formats));
			}

			SimpleDateFormat format = formats.get(pattern);
			if (format == null) {
				format = new SimpleDateFormat(pattern, Locale.CHINA);
				format.setTimeZone(CHINA);
				formats.put(pattern, format);
			}

			return format;
		}

		/**
		 * 根据format和timezone获取SimpleDateFormat对象，根据时区决定locale是什么
		 * 
		 * @param pattern
		 * @param timezone
		 * @return
		 */
		public static SimpleDateFormat formatFor(final String pattern, TimeZone timezone) {
			Objects.requireNonNull(pattern);
			final SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
			Map<String, SimpleDateFormat> formats = ref == null ? null : ref.get();
			if (formats == null) {
				formats = new HashMap<String, SimpleDateFormat>();
				THREADLOCAL_FORMATS.set(new SoftReference<Map<String, SimpleDateFormat>>(formats));
			}

			SimpleDateFormat format = formats.get(pattern + timezone.getID());
			if (format == null) {
				Locale locale = timeZoneLocaleMap.get(timezone.getID());
				if (locale == null) {
					format = new SimpleDateFormat(pattern);
				} else {
					format = new SimpleDateFormat(pattern, locale);
				}
				format.setTimeZone(timezone);
				formats.put(pattern + timezone.getID(), format);
			}

			return format;
		}

		/**
		 * 根据format,locale获取SimpleDateFormat对象，显示指定locale
		 * 
		 * @param pattern
		 * @param timezone
		 * @param locale
		 * @return
		 */
		public static SimpleDateFormat formatFor(final String pattern, Locale locale) {
			Objects.requireNonNull(pattern);
			final SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
			Map<String, SimpleDateFormat> formats = ref == null ? null : ref.get();
			if (formats == null) {
				formats = new HashMap<String, SimpleDateFormat>();
				THREADLOCAL_FORMATS.set(new SoftReference<Map<String, SimpleDateFormat>>(formats));
			}

			SimpleDateFormat format = formats.get(pattern + locale.getCountry());
			if (format == null) {
				format = new SimpleDateFormat(pattern, locale);
				formats.put(pattern + locale.getCountry(), format);
			}

			return format;
		}

		/**
		 * 根据format,timezone和locale获取SimpleDateFormat对象，显示指定timezone和locale
		 * 
		 * @param pattern
		 * @param timezone
		 * @param locale
		 * @return
		 */
		public static SimpleDateFormat formatFor(final String pattern, TimeZone timezone, Locale locale) {
			Objects.requireNonNull(pattern);
			final SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
			Map<String, SimpleDateFormat> formats = ref == null ? null : ref.get();
			if (formats == null) {
				formats = new HashMap<String, SimpleDateFormat>();
				THREADLOCAL_FORMATS.set(new SoftReference<Map<String, SimpleDateFormat>>(formats));
			}

			SimpleDateFormat format = formats.get(pattern + timezone.getID() + locale.getCountry());
			if (format == null) {
				format = new SimpleDateFormat(pattern, locale);
				format.setTimeZone(timezone);
				formats.put(pattern + timezone.getID() + locale.getCountry(), format);
			}

			return format;
		}

		public static void clearThreadLocal() {
			THREADLOCAL_FORMATS.remove();
		}

	}

	/**
	 * 根据正则表达式匹配日期格式并解析日期字符串, 时区为"Asia/Chongqing", Locale为CHINA
	 * 
	 * @param source
	 * @return Date
	 */
	public static Date parse(String source) {
		Assert.notNull(source, "Date value");
		SimpleDateFormat simpleDateFormat = null;
		if (source.matches(PT_ISO_DATETIME)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_1);
		} else if (source.matches(PT_ISO_DATETIME_1)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_1);
		} else if (source.matches(PT_ISO_DATETIME_2)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_2);
		} else if (source.matches(PT_ISO_DATETIME_3)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_3);
		} else if (source.matches(PT_ISO_DATETIME_4)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_4);
		} else if (source.matches(PT_ISO_DATETIME_5)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_5);
		} else if (source.matches(PT_ISO_DATETIME_SHORT)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT);
		} else if (source.matches(PT_ISO_DATETIME_SHORT_1)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT_1);
		} else if (source.matches(PT_ISO_DATETIME_SHORT_2)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT_2);
		} else if (source.matches(PT_ISO_DATETIME_SHORT_3)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT_3);
		} else if (source.matches(PT_ISO_DATETIME_SHORT_4)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT_4);
		} else if (source.matches(PT_ISO_DATETIME_SHORT_5)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT_5);
		} else if (source.matches(PT_ISO_DATETIME_SHORT_6)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT_6);
		} else if (source.matches(PT_ISO_DATETIME_SHORT_7)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT_7);
		} else if (source.matches(PT_DATETIME_FORMAT_EN)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATETIME_FORMAT_EN);
		} else if (source.matches(PT_DATETIME_FORMAT_EN_1)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATETIME_FORMAT_EN_1);
		} else if (source.matches(PT_DATETIME_FORMAT_EN_2)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATETIME_FORMAT_EN_2);
		} else if (source.matches(PT_DATETIME_FORMAT_EN_3)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATETIME_FORMAT_EN_3);
		} else if (source.matches(PT_DATETIME_FORMAT_EN_4)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATETIME_FORMAT_EN_4);
		} else if (source.matches(PT_DATETIME_FORMAT_EN_5)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATETIME_FORMAT_EN_5);
		} else if (source.matches(PT_DATETIME_FORMAT_EN_6)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATETIME_FORMAT_EN_6);
		} else if (source.matches(PT_DATETIME_FORMAT_EN_7)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATETIME_FORMAT_EN_7);
		} else if (source.matches(PT_DATETIME_FORMAT_EN_8)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATETIME_FORMAT_EN_8);
		} else if (source.matches(PT_DATETIME_FORMAT_EN_9)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATETIME_FORMAT_EN_9);
		} else if (source.matches(PT_HTTP_DATE_TIME_HEADER_PATTERN)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_HTTP_DATE_HEADER_FORMAT, Locale.US);
		} else if (source.matches(PT_ISO_DATE)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATE);
		} else if (source.matches(PT_ISO_DATE_1)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATE_1);
		} else if (source.matches(PT_ISO_DATE_2)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATE_2);
		} else if (source.matches(PT_ISO_DATE_3)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATE_3);
		} else if (source.matches(PT_DATE_EN)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN);
		} else if (source.matches(PT_DATE_EN_1)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_1);
		} else if (source.matches(PT_DATE_EN_2)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_2);
		} else if (source.matches(PT_DATE_EN_3)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_3);
		} else if (source.matches(PT_DATE_EN_4)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_4);
		} else if (source.matches(PT_DATE_EN_5)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_5);
		} else if (source.matches(PT_DATE_EN_6)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_6);
		} else if (source.matches(PT_DATE_EN_7)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_7);
		} else if (source.matches(PT_MONTH)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor("yyyy-MM");
		}

		try {
			return simpleDateFormat.parse(source);
		} catch (ParseException e) {
			log.error(MessageFormat.format("Parse date string:[{0}]", source));
		}
		return null;
	}

	/**
	 * 根据指定的format解析日期字符串, 时区为"Asia/Chongqing", Locale为CHINA
	 * 
	 * @param source
	 * @param format
	 * @return Date
	 */
	public static Date parse(String source, String format) {
		if (isBlank(source)) {
			return null;
		}
		Objects.requireNonNull(format);
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(format);
		try {
			return simpleDateFormat.parse(source);
		} catch (ParseException e) {
			log.error(MessageFormat.format("Parse date string:[{0}]", source));
		}
		return null;
	}

	/**
	 * 采用"yyyy-MM-dd HH:mm:ss", 根据指定的时区解析日期字符串
	 * 
	 * @param source
	 * @return Date
	 */
	public static Date parse(String source, TimeZone timezone) {
		if (isBlank(source)) {
			return null;
		}

		SimpleDateFormat simpleDateFormat = null;
		if (source.matches(PT_ISO_DATETIME)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(DEFAULT_FORMAT);
		} else if (source.matches(PT_ISO_DATETIME_1)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_1);
		} else if (source.matches(PT_ISO_DATETIME_2)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_2);
		} else if (source.matches(PT_ISO_DATETIME_3)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_3);
		} else if (source.matches(PT_ISO_DATETIME_SHORT)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT);
		} else if (source.matches(PT_ISO_DATETIME_SHORT_1)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT_1);
		} else if (source.matches(PT_ISO_DATETIME_SHORT_2)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT_2);
		} else if (source.matches(PT_ISO_DATETIME_SHORT_3)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATETIME_SHORT_3);
		} else if (source.matches(PT_HTTP_DATE_TIME_HEADER_PATTERN)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_HTTP_DATE_HEADER_FORMAT, Locale.US);
		} else if (source.matches(PT_ISO_DATE)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATE);
		} else if (source.matches(PT_ISO_DATE_1)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATE_1);
		} else if (source.matches(PT_ISO_DATE_2)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATE_2);
		} else if (source.matches(PT_ISO_DATE_3)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_ISO_DATE_3);
		} else if (source.matches(PT_DATE_EN)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN);
		} else if (source.matches(PT_DATE_EN_1)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_1);
		} else if (source.matches(PT_DATE_EN_2)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_2);
		} else if (source.matches(PT_DATE_EN_2)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_3);
		} else if (source.matches(PT_DATE_EN_4)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_4);
		} else if (source.matches(PT_DATE_EN_5)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_5);
		} else if (source.matches(PT_DATE_EN_6)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_6);
		} else if (source.matches(PT_DATE_EN_7)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor(FMT_DATE_FORMAT_EN_7);
		} else if (source.matches(PT_MONTH)) {
			simpleDateFormat = SimpleDateFormatHolder.formatFor("yyyy-MM");
		}

		try {
			return simpleDateFormat.parse(source);
		} catch (ParseException e) {
			log.error(MessageFormat.format("Parse date string:[{0}] with timezone:[{1}] and format:[{2}] failed!",
					source, timezone,
					DEFAULT_FORMAT));
		}
		return null;
	}

	/**
	 * 根据指定的format, 指定的时区解析日期字符串
	 * 
	 * @param source
	 * @param format
	 * @return Date
	 */
	public static Date parse(String source, String format, TimeZone timezone) {
		if (isBlank(source)) {
			return null;
		}
		Objects.requireNonNull(format);
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(format, timezone);
		try {
			return simpleDateFormat.parse(source);
		} catch (ParseException e) {
			log.error(MessageFormat.format("Parse date string:[{0}] with timezone:[{1}] and format:[{2}] failed!",
					source, timezone,
					format));
		}
		return null;
	}

	/**
	 * 根据指定的format, 指定的时区解析日期字符串
	 * 
	 * @param source
	 * @param format
	 * @return Date
	 */
	public static Date parse(String source, String format, Locale locale) {
		if (isBlank(source)) {
			return null;
		}
		Objects.requireNonNull(format);
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(format, locale);
		try {
			return simpleDateFormat.parse(source);
		} catch (ParseException e) {
			log.error(MessageFormat.format("Parse date string:[{0}] with locale:[{1}] and format:[{2}] failed!",
					source, locale,
					format));
		}
		return null;
	}

	/**
	 * 采用"yyyy-MM-dd HH:mm:ss", 指定的时区和locale解析日期字符串
	 * 
	 * @param source
	 * @param format
	 * @return Date
	 */
	public static Date parse(String source, TimeZone timezone, Locale locale) {
		if (isBlank(source)) {
			return null;
		}
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(DEFAULT_FORMAT, timezone, locale);
		try {
			return simpleDateFormat.parse(source);
		} catch (ParseException e) {
			log.error(MessageFormat.format(
					"Parse date string:[{0}] with timezone:[{1}], locale:[{2}] and format:[{3}] failed!",
					source, timezone, locale, DEFAULT_FORMAT));
		}
		return null;
	}

	/**
	 * 根据指定的format, 指定的时区和locale解析日期字符串
	 * 
	 * @param source
	 * @param format
	 * @return Date
	 */
	public static Date parse(String source, String format, TimeZone timezone, Locale locale) {
		if (isBlank(source)) {
			return null;
		}
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(format, timezone, locale);
		try {
			return simpleDateFormat.parse(source);
		} catch (ParseException e) {
			log.error(MessageFormat.format(
					"Parse date string:[{0}] with timezone:[{1}], locale:[{2}] and format:[{3}] failed!",
					source, timezone, locale, format));
		}
		return null;
	}

	/**
	 * 从一种日期格式转换成另外一种日期格式 采用相同的TimeZone和相同的Locale
	 * 
	 * @param source
	 * @param sourceFormat
	 * @param targetFormat
	 * @return String
	 */
	public static String transform(String source, String sourceFormat, String targetFormat) {
		Objects.requireNonNull(source);
		Objects.requireNonNull(sourceFormat);
		Objects.requireNonNull(targetFormat);
		return format(parse(source, sourceFormat), targetFormat);
	}

	/**
	 * 从一种日期格式转换成另外一种日期格式 采用相同的TimeZone和指定的Locale
	 * 
	 * @param source
	 * @param sourceFormat
	 * @param targetFormat
	 * @return String
	 */
	public static String transform(String source, String sourceFormat, String targetFormat, Locale locale) {
		if (isBlank(source)) {
			return null;
		}
		Objects.requireNonNull(sourceFormat);
		Objects.requireNonNull(targetFormat);
		return format(parse(source, sourceFormat, locale), targetFormat, locale);
	}

	/**
	 * 采用"yyyy-MM-dd HH:mm:ss"格式化Date对象, 时区为"Asia/Chongqing", Locale为CHINA
	 * 
	 * @param Date
	 * @return String
	 */
	public static String format(Date date) {
		Objects.requireNonNull(date);
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(DEFAULT_FORMAT);
		return simpleDateFormat.format(date);
	}

	/**
	 * 根据指定的format格式化Date对象, 时区为"Asia/Chongqing", Locale为CHINA
	 * 
	 * @param Date
	 * @return String
	 */
	public static String format(Date date, String format) {
		Objects.requireNonNull(date);
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(format);
		return simpleDateFormat.format(date);
	}

	public static String format(Date date, String format, Locale locale) {
		Objects.requireNonNull(date);
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(format, locale);
		return simpleDateFormat.format(date);
	}

	/**
	 * 采用"yyyy-MM-dd HH:mm:ss", 根据指定的时区格式化Date对象
	 * 
	 * @param Date
	 * @return String
	 */
	public static String format(Date date, TimeZone timeZone) {
		Objects.requireNonNull(date);
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(DEFAULT_FORMAT, timeZone);
		return simpleDateFormat.format(date);
	}

	/**
	 * 采用"yyyy-MM-dd HH:mm:ss", 根据指定的时区格式化Date对象
	 * 
	 * @param source
	 * @return String
	 */
	public static String format(Date date, String format, TimeZone timeZone) {
		Objects.requireNonNull(date);
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(format, timeZone);
		return simpleDateFormat.format(date);
	}

	/**
	 * 采用"yyyy-MM-dd HH:mm:ss", 指定的时区和locale格式化Date对象
	 * 
	 * @param source
	 * @param format
	 * @return String
	 */
	public static String format(Date date, TimeZone timeZone, Locale locale) {
		Objects.requireNonNull(date);
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(DEFAULT_FORMAT, timeZone, locale);
		return simpleDateFormat.format(date);
	}

	/**
	 * 根据指定的format, 指定的时区和locale格式化Date对象
	 * 
	 * @param source
	 * @param format
	 * @return String
	 */
	public static String format(Date date, String format, TimeZone timeZone, Locale locale) {
		Objects.requireNonNull(date);
		Objects.requireNonNull(format);
		SimpleDateFormat simpleDateFormat = SimpleDateFormatHolder.formatFor(format, timeZone, locale);
		return simpleDateFormat.format(date);
	}

	/**
	 * 根据指定的format格式化LocalDate对象
	 * 
	 * @param Date
	 * @return String
	 */
	public static String format(LocalDate localDate, String format) {
		if (localDate == null) {
			return null;
		}
		return localDate.format(ofPattern(format));
	}

	/**
	 * 根据指定的format格式化LocalDateTime对象
	 * 
	 * @param Date
	 * @return String
	 */
	public static String format(LocalDateTime localDateTime, String format) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.format(ofPattern(format));
	}

	/**
	 * 用ISO 日期格式化 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static String format(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.format(ofPattern(FMT_ISO_DATETIME));
	}

	/**
	 * 采用"yyyy-MM-dd HH:mm:ss"格式将日期字符串从srcTimezone转换成destTimezone日期字符串
	 * 
	 * @param source
	 * @param srcTimezone
	 * @param destTimezone
	 * @return String
	 */
	public static String convert2TargetTimezone(String source, TimeZone srcTimezone, TimeZone destTimezone) {
		Objects.requireNonNull(source);
		Date srcDate = parse(source, srcTimezone);
		return format(srcDate, destTimezone);
	}

	/**
	 * 根据指定格式将日期字符串从srcTimezone转换成destTimezone日期字符串
	 * 
	 * @param source
	 * @param srcTimezone
	 * @param destTimezone
	 * @return String
	 */
	public static String convert2TargetTimezone(String source, String format, TimeZone srcTimezone,
			TimeZone destTimezone) {
		Objects.requireNonNull(source);
		Objects.requireNonNull(format);
		Date srcDate = parse(source, format, srcTimezone);
		return format(srcDate, format, destTimezone);
	}

	/**
	 * 根据指定格式将日期字符串从srcTimezone转换成destTimezone, destFormat日期字符串
	 * 
	 * @param source
	 * @param srcFormat
	 * @param destFormat
	 * @param srcTimezone
	 * @param destTimezone
	 * @return String
	 */
	public static String convert2TargetTimezone(String source, String srcFormat, String destFormat,
			TimeZone srcTimezone,
			TimeZone destTimezone) {
		Objects.requireNonNull(source);
		Objects.requireNonNull(srcFormat);
		Objects.requireNonNull(destFormat);
		Date srcDate = parse(source, srcFormat, srcTimezone);
		return format(srcDate, destFormat, destTimezone);
	}

	/**
	 * 在指定日期上+/-天数
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date datePlus(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, days);

		return calendar.getTime();
	}

	/**
	 * Check date is in daylight for Pacific Time zone. 检查是否处于夏令时
	 * 
	 * @param date
	 * @return boolean
	 */
	public static boolean isPacificInDaylight(Date date) {
		return PST.inDaylightTime(date);
	}

	/**
	 * Check date is in daylight for Pacific Time zone. 检查是否处于夏令时
	 * 
	 * @param source
	 * @param format
	 * @return boolean
	 */
	public static boolean isPacificInDaylight(String source, String format) {
		Date date = parse(source, format);
		return PST.inDaylightTime(date);
	}

	/**
	 * Check if it is in daylight for Pacific Time zone now. 检查是否处于夏令时
	 * 
	 * @return boolean
	 */
	public static boolean isPacificInDaylight() {
		return PST.inDaylightTime(new Date());
	}

	/**
	 * 是否是闰年
	 * 
	 * @param year 年份
	 * @return boolean
	 */
	public static boolean isLeapYear(int year) {
		GregorianCalendar calendar = new GregorianCalendar();
		return calendar.isLeapYear(year);
	}

	/**
	 * 获取指定日期当月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date lastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}

	/**
	 * 获取指定日期当月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date firstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * 获取一天开始时间 如 2014-12-12 00:00:00
	 * 
	 * @return
	 */
	public static Date getDayStart() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 获取一天结束时间 如 2014-12-12 23:59:59
	 * 
	 * @return
	 */
	public static Date getDayEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 获取某个日期的当月第一天
	 * 
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * 获取某个日期的当月第一天
	 * 
	 * @return
	 */
	public static LocalDate getFirstDayOfMonth(LocalDate localDate) {
		return LocalDate.of(localDate.getYear(), localDate.getMonthValue(), 1);
	}

	/**
	 * 获取当月第一天
	 * 
	 * @return
	 */
	public static LocalDate getFirstDayOfMonth() {
		LocalDate localDate = LocalDate.now();
		return localDate.withDayOfMonth(1);
	}

	/**
	 * 获取上月第一天
	 * 
	 * @return
	 */
	public static LocalDate getFirstDayOfLastMonth() {
		LocalDate localDate = LocalDate.now();
		YearMonth yearMonth = YearMonth.from(localDate);
		return yearMonth.minusMonths(1).atDay(1);
	}

	/**
	 * 获取某个日期的当月第一天
	 * 
	 * @return
	 */
	public static Date getFirstDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * 获取某个日期的当月最后一天
	 * 
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 0);
		return cal.getTime();
	}

	/**
	 * 获取某个日期的当月最后一天
	 * 
	 * @return
	 */
	public static LocalDate getLastDayOfMonth() {
		LocalDate localDate = LocalDate.now();
		return localDate.withDayOfMonth(localDate.lengthOfMonth());
	}

	/**
	 * 获取某个日期的当月最后一天
	 * 
	 * @return
	 */
	public static Date getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 0);
		return cal.getTime();
	}

	/**
	 * 获取上月最后一天
	 * 
	 * @return
	 */
	public static LocalDate getLastDayOfLastMonth() {
		LocalDate localDate = LocalDate.now();
		YearMonth yearMonth = YearMonth.from(localDate);
		return yearMonth.minusMonths(1).atEndOfMonth();
	}

	/**
	 * 获取N天前的日期
	 * 
	 * @return
	 */
	public static LocalDate getLastNDays(long days) {
		return LocalDate.now().minusDays(days);
	}

	/**
	 * 获取本年度的第一天
	 * 
	 * @return
	 */
	public static LocalDate getFirstDayOfYear() {
		LocalDate now = LocalDate.now();
		return LocalDate.of(now.getYear(), 1, 1);
	}

	/**
	 * 获取本年度的最后一天
	 * 
	 * @return
	 */
	public static LocalDate getLastDayOfYear() {
		LocalDate now = LocalDate.now();
		return LocalDate.of(now.getYear(), 12, 31);
	}

	/**
	 * 返回代表上个月的YearMonth
	 * 
	 * @return
	 */
	public static YearMonth lastMonth() {
		return YearMonth.from(LocalDate.now()).minusMonths(1);
	}

	/**
	 * 判断哪个日期在前 如果日期一在日期二之前，返回true,否则返回false
	 * 
	 * @param date1 日期一
	 * @param date2 日期二
	 * @return boolean
	 */
	public static boolean isBefore(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		if (c1.before(c2))
			return true;

		return false;
	}

	/**
	 * 返回yyyy-MM-dd HH:mm:ss格式字符串
	 * 
	 * @return
	 */
	public static String now() {
		return LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * 返回yyyy-MM-dd格式字符串
	 * 
	 * @return
	 */
	public static String nowShort() {
		return LocalDateTime.now().format(ofPattern("yyyy-MM-dd"));
	}

	/**
	 * 获取两个日期的时间差，可以指定年，月，周，日，时，分，秒
	 * 
	 * @param date1 第一个日期
	 * @param date2 第二个日期<font color="red">此日期必须在date1之后</font>
	 * @param type  DateUtils.Type.X的枚举类型
	 * @return long值
	 * @throws Exception
	 */
	public static long getDiff(Date date1, Date date2, Type type) {

		if (!isBefore(date1, date2)) {
			return -1;
		}

		long d = Math.abs(date1.getTime() - date2.getTime());
		switch (type) {
		case Year: {
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();

			cal1.setTime(date1);
			int year1 = cal1.get(Calendar.YEAR);
			int month1 = cal1.get(Calendar.MONTH);
			int day1 = cal1.get(Calendar.DAY_OF_MONTH);
			int hour1 = cal1.get(Calendar.HOUR_OF_DAY);
			int minute1 = cal1.get(Calendar.MINUTE);
			int second1 = cal1.get(Calendar.SECOND);

			cal2.setTime(date2);
			int year2 = cal2.get(Calendar.YEAR);
			int month2 = cal2.get(Calendar.MONTH);
			int day2 = cal2.get(Calendar.DAY_OF_MONTH);
			int hour2 = cal2.get(Calendar.HOUR_OF_DAY);
			int minute2 = cal2.get(Calendar.MINUTE);
			int second2 = cal2.get(Calendar.SECOND);

			int yd = year2 - year1;

			if (month1 > month2) {
				yd -= 1;
			} else {
				if (day1 > day2) {
					yd -= 1;
				} else {
					if (hour1 > hour2) {
						yd -= 1;
					} else {
						if (minute1 > minute2) {
							yd -= 1;
						} else {
							if (second1 > second2) {
								yd -= 1;
							}
						}
					}
				}
			}
			return (long) yd;
		}
		case Month: {
			// 获取年份差
			long year = getDiff(date1, date2, Type.Year);

			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();

			cal1.setTime(date1);
			int month1 = cal1.get(Calendar.MONTH);
			int day1 = cal1.get(Calendar.DAY_OF_MONTH);
			int hour1 = cal1.get(Calendar.HOUR_OF_DAY);
			int minute1 = cal1.get(Calendar.MINUTE);
			int second1 = cal1.get(Calendar.SECOND);

			cal2.setTime(date2);
			int month2 = cal2.get(Calendar.MONTH);
			int day2 = cal2.get(Calendar.DAY_OF_MONTH);
			int hour2 = cal2.get(Calendar.HOUR_OF_DAY);
			int minute2 = cal2.get(Calendar.MINUTE);
			int second2 = cal2.get(Calendar.SECOND);

			int md = (month2 + 12) - month1;

			if (day1 > day2) {
				md -= 1;
			} else {
				if (hour1 > hour2) {
					md -= 1;
				} else {
					if (minute1 > minute2) {
						md -= 1;
					} else {
						if (second1 > second2) {
							md -= 1;
						}
					}
				}
			}
			return (long) md + year * 12;
		}
		case Week: {
			return getDiff(date1, date2, Type.Day) / 7;
		}
		case Day: {
			long d1 = date1.getTime();
			long d2 = date2.getTime();
			return (int) ((d2 - d1) / (24 * 60 * 60 * 1000));
		}
		case Hour: {
			long d1 = date1.getTime();
			long d2 = date2.getTime();
			return (int) ((d2 - d1) / (60 * 60 * 1000));
		}
		case Minutes: {
			long d1 = date1.getTime();
			long d2 = date2.getTime();
			return (int) ((d2 - d1) / (60 * 1000));
		}
		case Seconds: {
			long d1 = date1.getTime();
			long d2 = date2.getTime();
			return (int) ((d2 - d1) / 1000);
		}
		default:
			return 0;
		}
	}

	/**
	 * 返回两个时间之间的差距，根据传入的ChronoUnit返回差距的单位，如years， months， days
	 * 
	 * @param fromDateTime
	 * @param toDateTime
	 * @param unit
	 * @return
	 */
	public static long getDiff(LocalDateTime fromDateTime, LocalDateTime toDateTime, ChronoUnit unit) {
		if (fromDateTime == null || toDateTime == null) {
			return 0l;
		}
		return unit.between(toDateTime, fromDateTime);
	}

	/**
	 * 返回相差多少年
	 * 
	 * @param fromDateTime
	 * @param toDateTime
	 * @return long
	 */
	public static long getYearsDiff(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
		return getDiff(fromDateTime, toDateTime, ChronoUnit.YEARS);
	}

	/**
	 * 返回相差多少月
	 * 
	 * @param fromDateTime
	 * @param toDateTime
	 * @return long
	 */
	public static long getMonthsDiff(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
		return getDiff(fromDateTime, toDateTime, ChronoUnit.MONTHS);
	}

	/**
	 * 返回相差多少天
	 * 
	 * @param fromDateTime
	 * @param toDateTime
	 * @return long
	 */
	public static long getDaysDiff(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
		return getDiff(fromDateTime, toDateTime, ChronoUnit.DAYS);
	}

	/**
	 * 返回给定日期在全年中处于第几周，从1开始
	 * 
	 * @param localDate
	 * @return
	 */
	public static int weekOfYear(LocalDate localDate) {
		TemporalField field = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
		int weekNumber = localDate.get(field);
		return weekNumber;
	}

	/**
	 * 日期字符串转LocalDate，根据日期模式自动匹配格式
	 * 
	 * @param source
	 * @return LocalDate
	 */
	public static LocalDate toLocalDate(String source) {
		Objects.nonNull(source);
		if (source.matches(PT_ISO_DATE)) {
			return LocalDate.parse(source, ofPattern(FMT_ISO_DATE));
		} else if (source.matches(PT_ISO_DATE_1)) {
			return LocalDate.parse(source, ofPattern(FMT_ISO_DATE_1));
		} else if (source.matches(PT_ISO_DATE_2)) {
			return LocalDate.parse(source, ofPattern(FMT_ISO_DATE_2));
		} else if (source.matches(PT_ISO_DATE_3)) {
			return LocalDate.parse(source, ofPattern(FMT_ISO_DATE_3));
		} else if (source.matches(PT_DATE_EN)) {
			return LocalDate.parse(source, ofPattern(FMT_DATE_FORMAT_EN));
		} else if (source.matches(PT_DATE_EN_1)) {
			return LocalDate.parse(source, ofPattern(FMT_DATE_FORMAT_EN_1));
		} else if (source.matches(PT_DATE_EN_1)) {
			return LocalDate.parse(source, ofPattern(FMT_DATE_FORMAT_EN_2));
		} else if (source.matches(PT_DATE_EN_3)) {
			return LocalDate.parse(source, ofPattern(FMT_DATE_FORMAT_EN_3));
		} else if (source.matches(PT_DATE_EN_5)) {
			return LocalDate.parse(source, ofPattern(FMT_DATE_FORMAT_EN_5));
		} else if (source.matches(PT_DATE_EN_6)) {
			return LocalDate.parse(source, ofPattern(FMT_DATE_FORMAT_EN_6));
		} else if (source.matches(PT_DATE_EN_7)) {
			return LocalDate.parse(source, ofPattern(FMT_DATE_FORMAT_EN_7));
		} else if (source.matches(PT_MONTH)) {
			return LocalDate.parse(source, ofPattern("yyyy-MM"));
		} else if (source.matches(PT_DATE_CONCISE)) {
			return LocalDate.parse(source, ofPattern("yyyyMMdd"));
		}

		try {
			return LocalDate.parse(source, DateTimeFormatter.ofPattern(FMT_DATE_FORMAT_EN_4, Locale.ENGLISH));
		} catch (DateTimeParseException e) {
		}
		try {
			return LocalDate.parse(source, DateTimeFormatter.ofPattern(FMT_DATE_FORMAT_EN_4));
		} catch (DateTimeParseException e) {
		}
		log.warn("{} does not match any LocalDate format! ", source);
		return null;
	}

	/**
	 * 将Date用系统默认时区转成LocalDate
	 * 
	 * @param date
	 * @return LocalDate
	 */
	public static LocalDate toLocalDate(Date date) {
		if (date == null) {
			return null;
		}
		Instant instant = date.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return localDateTime.toLocalDate();
	}

	/**
	 * 将Date用指定时区转成LocalDate
	 * 
	 * @param date
	 * @return LocalDate
	 */
	public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
		if (date == null) {
			return null;
		}
		Objects.nonNull(zoneId);
		Instant instant = date.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zoneId);
		return localDateTime.toLocalDate();
	}

	/**
	 * 将LocalDateTime用系统默认时区转成LocalDate
	 * 
	 * @param date
	 * @return LocalDate
	 */
	public static LocalDate toLocalDate(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.toLocalDate();
	}

	/**
	 * 将LocalDateTime用+8(东8区 Asia/Shanghai)时区转成LocalDate
	 * 
	 * @param LocalDateTime
	 * @return LocalDate
	 */
	public static LocalDate toLocalDateCTT(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.atZone(CTT).toLocalDate();
	}

	/**
	 * 将LocalDateTime用指定时区转成LocalDate
	 * 
	 * @param LocalDateTime
	 * @return LocalDate
	 */
	public static LocalDate toLocalDate(LocalDateTime localDateTime, ZoneId zoneId) {
		if (localDateTime == null) {
			return null;
		}
		return localDateTime.atZone(zoneId).toLocalDate();
	}

	/**
	 * 将Date用+8(东8区 Asia/Shanghai)时区转成LocalDate
	 * 
	 * @param date
	 * @return LocalDate
	 */
	public static LocalDate toLocalDateCTT(Date date) {
		if (date == null) {
			return null;
		}
		Instant instant = date.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, CTT);
		return localDateTime.toLocalDate();
	}

	/**
	 * 日期字符串转LocalDateTime，根据日期模式自动匹配格式
	 * 
	 * @param source
	 * @return LocalDateTime
	 */
	public static LocalDateTime toLocalDateTime(String source) {
		if (isBlank(source)) {
			return null;
		}
		if (source.matches(PT_ISO_DATETIME)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME));
		} else if (source.matches(PT_ISO_DATETIME_1)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_1));
		} else if (source.matches(PT_ISO_DATETIME_2)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_2));
		} else if (source.matches(PT_ISO_DATETIME_3)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_3));
		} else if (source.matches(PT_ISO_DATETIME_4)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_4));
		} else if (source.matches(PT_ISO_DATETIME_5)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_5));
		} else if (source.matches(PT_ISO_DATETIME_SHORT)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_SHORT));
		} else if (source.matches(PT_ISO_DATETIME_SHORT_1)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_SHORT_1));
		} else if (source.matches(PT_ISO_DATETIME_SHORT_2)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_SHORT_2));
		} else if (source.matches(PT_ISO_DATETIME_SHORT_3)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_SHORT_3));
		} else if (source.matches(PT_ISO_DATETIME_SHORT_4)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_SHORT_4));
		} else if (source.matches(PT_ISO_DATETIME_SHORT_5)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_SHORT_5));
		} else if (source.matches(PT_ISO_DATETIME_SHORT_6)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_SHORT_6));
		} else if (source.matches(PT_ISO_DATETIME_SHORT_7)) {
			return LocalDateTime.parse(source, ofPattern(FMT_ISO_DATETIME_SHORT_7));
		} else if (source.matches(PT_DATETIME_FORMAT_EN)) {
			return LocalDateTime.parse(source, ofPattern(FMT_DATETIME_FORMAT_EN));
		} else if (source.matches(PT_DATETIME_FORMAT_EN_1)) {
			return LocalDateTime.parse(source, ofPattern(FMT_DATETIME_FORMAT_EN_1));
		} else if (source.matches(PT_DATETIME_FORMAT_EN_2)) {
			return LocalDateTime.parse(source, ofPattern(FMT_DATETIME_FORMAT_EN_2));
		} else if (source.matches(PT_DATETIME_FORMAT_EN_3)) {
			return LocalDateTime.parse(source, ofPattern(FMT_DATETIME_FORMAT_EN_3));
		} else if (source.matches(PT_DATETIME_FORMAT_EN_4)) {
			return LocalDateTime.parse(source, ofPattern(FMT_DATETIME_FORMAT_EN_4));
		} else if (source.matches(PT_DATETIME_FORMAT_EN_5)) {
			return LocalDateTime.parse(source, ofPattern(FMT_DATETIME_FORMAT_EN_5));
		} else if (source.matches(PT_DATETIME_FORMAT_EN_6)) {
			return LocalDateTime.parse(source, ofPattern(FMT_DATETIME_FORMAT_EN_6));
		} else if (source.matches(PT_DATETIME_FORMAT_EN_7)) {
			return LocalDateTime.parse(source, ofPattern(FMT_DATETIME_FORMAT_EN_7));
		} else if (source.matches(PT_DATETIME_FORMAT_EN_8)) {
			return LocalDateTime.parse(source, ofPattern(FMT_DATETIME_FORMAT_EN_8));
		} else if (source.matches(PT_DATETIME_FORMAT_EN_9)) {
			return LocalDateTime.parse(source, ofPattern(FMT_DATETIME_FORMAT_EN_9));
		} else if (source.matches(PT_HTTP_DATE_TIME_HEADER_PATTERN)) {
			SimpleDateFormat format = SimpleDateFormatHolder.formatFor(FMT_HTTP_DATE_HEADER_FORMAT, Locale.US);
			try {
				return toLocalDateTime(format.parse(source));
			} catch (ParseException e) {
				log.error("解析日期字符串出错", source);
			}
		}
		log.warn("{} does not match any LocalDateTime format! ", source);
		return null;
	}

	/**
	 * 将Date用系统默认时区转成LocalDateTime
	 * 
	 * @param date
	 * @return LocalDateTime
	 */
	public static LocalDateTime toLocalDateTime(Date date) {
		if (date == null) {
			return null;
		}
		Instant instant = date.toInstant();
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	/**
	 * 将Date用指定时区转成LocalDateTime
	 * 
	 * @param date
	 * @return LocalDateTime
	 */
	public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
		if (date == null) {
			return null;
		}
		Objects.nonNull(zoneId);
		Instant instant = date.toInstant();
		return LocalDateTime.ofInstant(instant, zoneId);
	}

	/**
	 * 将Date用+8(东8区 Asia/Shanghai)时区转成LocalDateTime
	 * 
	 * @param date
	 * @return LocalDateTime
	 */
	public static LocalDateTime toLocalDateTimeCTT(Date date) {
		if (date == null) {
			return null;
		}
		Instant instant = date.toInstant();
		return LocalDateTime.ofInstant(instant, CTT);
	}

	/**
	 * 用系统默认时区将LocalDate转成LocalDateTime
	 * 
	 * @param localDate
	 * @return LocalDateTime
	 */
	public static LocalDateTime toLocalDateTime(LocalDate localDate) {
		long milis = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(milis), ZoneId.systemDefault());
	}

	/**
	 * 用指定的时区将LocalDate转成LocalDateTime
	 * 
	 * @param localDate
	 * @param zoneId
	 * @return LocalDateTime
	 */
	public static LocalDateTime toLocalDateTime(LocalDate localDate, ZoneId zoneId) {
		long milis = localDate.atStartOfDay(zoneId).toInstant().toEpochMilli();
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(milis), zoneId);
	}

	/**
	 * 用+8(东8区 Asia/Shanghai)将LocalDate转成LocalDateTime
	 * 
	 * @param localDate
	 * @param zoneId
	 * @return LocalDateTime
	 */
	public static LocalDateTime toLocalDateTimeCTT(LocalDate localDate, ZoneId zoneId) {
		long milis = localDate.atStartOfDay(CTT).toInstant().toEpochMilli();
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(milis), CTT);
	}

	/**
	 * 用默认时区将LocalDateTime转成1970-1-1 00:00:00以来的毫秒数
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static long toEpochMilis(String source) {
		LocalDateTime localDateTime = toLocalDateTime(source);
		return toEpochMilis(localDateTime);
	}

	/**
	 * 用指定时区将LocalDateTime转成1970-1-1 00:00:00以来的毫秒数
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static long toEpochMilis(String source, ZoneId zoneId) {
		LocalDateTime localDateTime = toLocalDateTime(source);
		return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
	}

	/**
	 * 用默认时区将LocalDateTime转成1970-1-1 00:00:00以来的毫秒数
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static long toEpochMilis(LocalDateTime localDateTime) {
		Objects.nonNull(localDateTime);
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	/**
	 * 用指定时区将LocalDateTime转成1970-1-1 00:00:00以来的毫秒数
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static long toEpochMilis(LocalDateTime localDateTime, ZoneId zoneId) {
		Objects.nonNull(localDateTime);
		return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
	}

	/**
	 * 用用+8(东8区 Asia/Shanghai)将LocalDateTime转成1970-1-1 00:00:00以来的毫秒数
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static long toEpochMilisCTT(LocalDateTime localDateTime, ZoneId zoneId) {
		Objects.nonNull(localDateTime);
		return localDateTime.atZone(CTT).toInstant().toEpochMilli();
	}

	/**
	 * 用默认时区将LocalDate转成1970-1-1 00:00:00以来的毫秒数
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static long toEpochMilis(LocalDate localDate) {
		Objects.nonNull(localDate);
		return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	/**
	 * 用指定时区将LocalDate转成1970-1-1 00:00:00以来的毫秒数
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static long toEpochMilis(LocalDate localDate, ZoneId zoneId) {
		Objects.nonNull(localDate);
		return localDate.atStartOfDay(zoneId).toInstant().toEpochMilli();
	}

	/**
	 * 用+8(东8区 Asia/Shanghai)将LocalDate转成1970-1-1 00:00:00以来的毫秒数
	 * 
	 * @param localDateTime
	 * @return
	 */
	public static long toEpochMilisCTT(LocalDate localDate) {
		Objects.nonNull(localDate);
		return localDate.atStartOfDay(CTT).toInstant().toEpochMilli();
	}

	/**
	 * 失去为东8区
	 * 
	 * @param localDateTime
	 * @return Date
	 */
	public static Date toDate(LocalDateTime localDateTime) {
		ZoneId zone = ZoneOffset.ofHours(8);
		Instant instant = localDateTime.atZone(zone).toInstant();
		return Date.from(instant);
	}

	public static Date toDate(LocalDateTime localDateTime, ZoneId zone) {
		Instant instant = localDateTime.atZone(zone).toInstant();
		return Date.from(instant);
	}

	/**
	 * 检查source是否介于previous和next之间，两边的边界都包含
	 * <ul>
	 * 	<li>如果source为null， 返回false
	 * 	<li>如果previous==null，next!=null， 则检查source.isBefore(next)
	 * 	<li>如果previous!=null，next==null, 则检查source.isAfter(previous)
	 * 	<li>如果previous!=null && next != null, 则检查
	 * @param source
	 * @param previous
	 * @param next
	 * @return
	 * @on
	 */
	public static boolean isBetween(LocalDate source, LocalDate previous, LocalDate next) {
		if (source == null) {
			return false;
		}

		if (previous == null && next != null) {
			return source.isBefore(next) || source.isEqual(next);
		}

		if (previous != null && next == null) {
			return source.isAfter(previous) || source.isEqual(previous);
		}

		return source.compareTo(previous) >= 0 && source.compareTo(next) <= 0;
	}

	/**
	 * 检查source是否介于previous和next之间，两边的边界都包含
	 * <ul>
	 * 	<li>如果source为null， 返回false
	 * 	<li>如果previous==null，next!=null， 则检查source.isBefore(next)
	 * 	<li>如果previous!=null，next==null, 则检查source.isAfter(previous)
	 * 	<li>如果previous!=null && next != null, 则检查
	 * @param source
	 * @param previous
	 * @param next
	 * @return
	 * @on
	 */
	public static boolean isBetween(LocalDateTime source, LocalDateTime previous, LocalDateTime next) {
		if (source == null) {
			return false;
		}

		if (previous == null && next != null) {
			return source.isBefore(next) || source.isEqual(next);
		}

		if (previous != null && next == null) {
			return source.isAfter(previous) || source.isEqual(previous);
		}

		return source.compareTo(previous) >= 0 && source.compareTo(next) <= 0;
	}

	/**
	 * 使用系统默认时区检查localDate与系统当前日期是否在同一个月 如果localDate为null则返回false
	 * 
	 * @param localDate
	 * @return boolean
	 */
	public static boolean isCurrentMonth(LocalDate localDate) {
		if (localDate == null) {
			return false;
		}

		LocalDate now = LocalDate.now();
		return Year.from(localDate).equals(Year.from(now)) && Month.from(localDate) == Month.from(now);
	}

	/**
	 * 返回当前是一年的第几月，从1到12
	 * 
	 * @return int
	 */
	public static int currentMonth() {
		LocalDate now = LocalDate.now();
		return now.getMonthValue();
	}

	public static boolean isWeekend(LocalDate localDate) {
		if (localDate == null) {
			return false;
		}

		DayOfWeek dayOfWeek = localDate.getDayOfWeek();
		return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
	}

	/**
	 * 两者任意一个为null则返回false 否则检查prev比next日期要晚
	 * 
	 * @param prev
	 * @param next
	 * @return
	 */
	public static boolean gt(LocalDate prev, LocalDate next) {
		if (prev == null || next == null) {
			return false;
		}
		return prev.compareTo(next) > 0;
	}

	/**
	 * 两者任意一个为null则返回false 否则检查prev比next日期要晚或者是同一天
	 * 
	 * @param prev
	 * @param next
	 * @return
	 */
	public static boolean gte(LocalDate prev, LocalDate next) {
		if (prev == null || next == null) {
			return false;
		}
		return prev.compareTo(next) >= 0;
	}

	/**
	 * 两者任意一个为null则返回false 否则检查prev比next日期要早
	 * 
	 * @param prev
	 * @param next
	 * @return
	 */
	public static boolean lt(LocalDate prev, LocalDate next) {
		if (prev == null || next == null) {
			return false;
		}
		return prev.compareTo(next) < 0;
	}

	/**
	 * 两者任意一个为null则返回false 否则检查prev比next日期要早或者是同一天
	 * 
	 * @param prev
	 * @param next
	 * @return
	 */
	public static boolean lte(LocalDate prev, LocalDate next) {
		if (prev == null || next == null) {
			return false;
		}
		return prev.compareTo(next) <= 0;
	}

	/**
	 * 两个都为null返回true 
	 * 一个为null，返回false 
	 * 调用两者的equals方法
	 * 
	 * @param prev
	 * @param next
	 * @return
	 * @on
	 */
	public static boolean eq(LocalDateTime prev, LocalDateTime next) {
		if (prev == null && next == null) {
			return true;
		}

		if (prev == null) {
			return false;
		}

		if (next == null) {
			return false;
		}

		return prev.equals(next);
	}

	/**
	 * 两个都为null返回true 
	 * 一个为null，返回false 
	 * 调用两者的equals方法
	 * 
	 * @param prev
	 * @param next
	 * @return
	 * @on
	 */
	public static boolean eq(LocalDate prev, LocalDate next) {
		if (prev == null && next == null) {
			return true;
		}

		if (prev == null) {
			return false;
		}

		if (next == null) {
			return false;
		}

		return prev.equals(next);
	}

	/**
	 * nullsafe日期比较，日期早的在前，null在前
	 * 
	 * @param prev
	 * @param next
	 * @return
	 */
	public static Comparator<LocalDateTime> comparatorAsc() {
		return (prev, next) -> {
			if (prev == null) {
				return -1;
			}
			if (next == null) {
				return -1;
			}
			return prev.compareTo(next);
		};
	}

	/**
	 * nullsafe日期比较，日期早的在后，null在后
	 * 
	 * @param prev
	 * @param next
	 * @return
	 */
	public static Comparator<LocalDateTime> comparatorDesc() {
		return (prev, next) -> {
			if (prev == null) {
				return 1;
			}
			if (next == null) {
				return 1;
			}
			return next.compareTo(prev);
		};
	}

	private static Period getPeriod(LocalDateTime dob, LocalDateTime now) {
		return Period.between(dob.toLocalDate(), now.toLocalDate());
	}

	private static long[] getTime(LocalDateTime dob, LocalDateTime now) {
		LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), dob.getHour(),
				dob.getMinute(), dob.getSecond());
		Duration duration = Duration.between(today, now);

		long seconds = duration.getSeconds();

		long hours = seconds / SECONDS_PER_HOUR;
		long minutes = ((seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
		long secs = (seconds % SECONDS_PER_MINUTE);

		return new long[] { hours, minutes, secs };
	}
}
