package com.loserico.utils;

import static java.time.format.DateTimeFormatter.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.loserico.commons.utils.DateUtils;

public class DateUtilsTest {

	@Test
	public void testPATTERN_RFC1123() {
		SimpleDateFormat rfcFormat = new SimpleDateFormat(DateUtils.PATTERN_RFC1036);
		System.out.println(rfcFormat.format(new Date()));

		SimpleDateFormat rfc1123Format = new SimpleDateFormat(DateUtils.PATTERN_RFC1123, Locale.US);
		System.out.println(rfc1123Format.format(new Date()));

		SimpleDateFormat ascFormat = new SimpleDateFormat(DateUtils.PATTERN_ASCTIME);
		System.out.println(ascFormat.format(new Date()));

		SimpleDateFormat isoFormat = new SimpleDateFormat(DateUtils.ISO_DATE_MILISECONDS);
		System.out.println(isoFormat.format(new Date()));
	}

	@Test
	public void testDEFAULT_TWO_DIGIT_YEAR_START() {
		TimeZone CST = TimeZone.getTimeZone("GMT+8");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(CST);
		calendar.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date date = calendar.getTime();
		System.out.println(date);
	}

	@Test
	public void testParse() {
		String dateString = "2016-05-24 18:00:00";
		String destDateString = DateUtils.convert2TargetTimezone(dateString, DateUtils.CHINA, DateUtils.GMT);
		System.out.println(destDateString);

		String dateString2 = "2016-05-24 8:00:00";
		String destDateString2 = DateUtils.convert2TargetTimezone(dateString2, DateUtils.CHINA, DateUtils.GMT);
		System.out.println(destDateString2);

		String dateString3 = "2016-05-24 7:59:59";
		String destDateString3 = DateUtils.convert2TargetTimezone(dateString3, DateUtils.CHINA, DateUtils.GMT);
		System.out.println(destDateString3);

		String dateString4 = "2016-05-24 00:00:00";
		String destDateString4 = DateUtils.convert2TargetTimezone(dateString4, DateUtils.GMT, DateUtils.CHINA);
		System.out.println(destDateString4);

		String dateString5 = "2016-05-24 7:59:59";
		String destDateString5 = DateUtils.convert2TargetTimezone(dateString5, DateUtils.DEFAULT_FORMAT,
				DateUtils.UTC_DATETIME_FORMAT, DateUtils.CHINA, DateUtils.GMT);
		System.out.println(destDateString5);
		
		String dateString6 = "2018-01-24T10:45:19.000";
		LocalDateTime localDateTime = LocalDateTime.parse(dateString6, ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
//		LocalDateTime localDateTime = DateUtils.toLocalDateTime(DateUtils.parse(dateString6, "yyyy-MM-dd'T'HH:mm:ss.SSS"));
		System.out.println(localDateTime);
	}
	
	@Test
	public void testLongShortMonthName() {
		LocalDate now = LocalDate.now();
		System.out.println(now.format(ofPattern("MM", Locale.ENGLISH)));
		System.out.println(now.format(ofPattern("MMM", Locale.ENGLISH)));
		System.out.println(now.format(ofPattern("MMMM", Locale.ENGLISH)));
		System.out.println(now.format(ofPattern("yy")));
		System.out.println(now.format(ofPattern("MMMyy", Locale.ENGLISH)));
	}
	
	@Test
	public void testToLocalDate() {
		LocalDate localDate = DateUtils.toLocalDate("2018/7/1");
		System.out.println(localDate);
		localDate = DateUtils.toLocalDate("2018/7/11");
		System.out.println(localDate);
		localDate = DateUtils.toLocalDate("2018/07/1");
		System.out.println(localDate);
		localDate = DateUtils.toLocalDate("2018/07/11");
		System.out.println(localDate);
	}
	
	@Test
	public void testToLocalDateTime() {
		LocalDateTime localDateTime = DateUtils.toLocalDateTime("2017/11/15 12:20:10");
		LocalDateTime localDateTime2 = DateUtils.toLocalDateTime("2017/11/15 2:20:10");
		System.out.println(localDateTime);
		System.out.println(localDateTime2);
	}

}
