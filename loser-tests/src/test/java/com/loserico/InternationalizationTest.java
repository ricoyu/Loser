package com.loserico;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * http://www.baeldung.com/java-8-localization?utm_source=drip&utm_medium=email&utm_campaign=Latest+article+about+Java+%E2%80%93+on+Baeldung
 * 
 * Internationalization is a process of preparing an application to support various
 * linguistic, regional, cultural or political-specific data. It is an essential
 * aspect of any modern multi-language application.
 * 
 * For further reading, we should know that there’s a very popular abbreviation
 * (probably more popular than the actual name) for internationalization – i18n due to
 * the 18 letters between ‘i’ and ‘n’.
 * 
 * It’s crucial for present-day enterprise programs to serve people from different
 * parts of the world or multiple cultural areas. Distinct cultural or language
 * regions don’t only determine language-specific descriptions but also currency,
 * number representation and even divergent date and time composition.
 * 
 * For instance, let’s focus on country-specific numbers. They have various decimal
 * and thousand separators:
 * 
 * 102,300.45 (United States)
 * 102 300,45 (Poland)
 * 102.300,45 (Germany)
 * There are different date formats as well:
 * 
 * Monday, January 1, 2018 3:20:34 PM CET (United States)
 * lundi 1 janvier 2018 15 h 20 CET (France).
 * 2018年1月1日 星期一 下午03时20分34秒 CET (China)
 * What’s more, different countries have unique currency symbols:
 * 
 * £1,200.60 (United Kingdom)
 * € 1.200,60 (Italy)
 * 1 200,60 € (France)
 * $1,200.60 (United States)
 * An important fact to know is that even if countries have the 
 * same currency and currency symbol – like France and Italy – the position of their currency symbol could be different.
 * 
 * <p>
 * Copyright: Copyright (c) 2018-04-15 10:29
 * <p>
 * Company: DataSense
 * <p>
 * @author Rico Yu	ricoyu520@gmail.com
 * @version 1.0 
 * @on
 */
public class InternationalizationTest {

	/**
	 * Fields
	 * We have already learned that Locale consists of language code, country code, and variant. 
	 * There are two more possible fields to set: script and extensions.
	 * 
	 * Let’s have a look through a list of fields and see what the rules are:
	 * 
	 * Language 		can be an ISO 639 alpha-2 or alpha-3 code or registered language subtag.
	 * Region (Country) is ISO 3166 alpha-2 country code or UN numeric-3 area code.
	 * Variant 			is a case-sensitive value or set of values specifying a variation of a Locale.
	 * Script 			must be a valid ISO 15924 alpha-4 code.
	 * Extensions 		is a map which consists of single character keys and String values.
	 * @on
	 */
	@Test
	public void testLocaleFields() {
		Locale locale = new Locale.Builder()
				.setLanguageTag("fr")
				.setRegion("CA")
				.setVariant("POSIX")
				.setScript("Latn")
				.build();
		//The String representation of the above Locale is fr_CA_POSIX_#Latn.
		System.out.println(locale);

		/*
		 * It’s good to know that setting ‘variant’ may be a little bit tricky as
		 * there’s no official restriction on variant values, although the setter
		 * method requires it to be BCP-47 compliant.
		 * 
		 * Otherwise, it will throw IllformedLocaleException.
		 * 
		 * In the case where we need to use value that doesn’t pass validation, we can
		 * use Locale constructors as they don’t validate values.
		 */
		Locale localeZH = new Locale.Builder()
				.setLanguageTag("zh")
				.setRegion("CN")
				//A valid variant must be a String of 5 to 8 alphanumerics or single numeric followed by 3 alphanumerics. 
				//We can only apply “UNIX” to the variant field only via constructor as it doesn’t meet those requirements.
				.setVariant("ZHONG")
				.build();
		System.out.println(localeZH);
	}

	/**
	 * Locale has three constructors:
	 * 
	 * new Locale(String language)
	 * new Locale(String language, String country)
	 * new Locale(String language, String country, String variant)
	 * 
	 * However, there’s one drawback of using constructors to create Locale objects – we can’t set extensions and script fields.
	 * @on
	 */
	@Test
	public void testLocaleConstructors() {
		Locale locale = new Locale("pl", "PL", "UNIX");
		System.out.println(locale);
	}

	/**
	 * This is probably the simplest and the most limited way of getting Locales. The
	 * Locale class has several static constants which represent the most popular
	 * country or language:
	 */
	@Test
	public void testLocaleConstants() {
		Locale china = Locale.CHINA;
		Locale chinese = Locale.CHINESE;
		Locale simplifiedChinese = Locale.SIMPLIFIED_CHINESE;
		Locale traditionalChinese = Locale.TRADITIONAL_CHINESE;
		asList(china, chinese, simplifiedChinese, traditionalChinese).forEach(System.out::println);
	}

	/**
	 * Another way of creating Locale is calling the static factory method
	 * forLanguageTag(String languageTag). This method requires a String that meets
	 * the IETF BCP 47 standard.
	 */
	@Test
	public void testLanguageTag() {
		Locale uk = Locale.forLanguageTag("en-UK");
		System.out.println(uk);

		Locale zh = Locale.forLanguageTag("zh-CN");
		System.out.println(zh);
	}

	/**
	 * Even though we can create multiple combinations of Locale objects, we may not
	 * be able to use them.
	 * 
	 * An important note to be aware of is that the Locales on a platform are
	 * dependent on those that have been installed within the Java Runtime.
	 * 
	 * As we use Locales for formatting, the different formatters may have an even
	 * smaller set of Locales available that are installed in the Runtime.
	 * 
	 * Let’s check how to retrieve arrays of available locales:
	 * 
	 * After that, we can check whether our Locale resides among available Locales.
	 * 
	 * We should remember that the set of available locales is different for various
	 * implementations of the Java Platform and various areas of functionality.
	 * 
	 * The complete list of supported locales is available on the Oracle’s Java SE
	 * Development Kit webpage.
	 * @on
	 */
	@Test
	public void testAvailableLocales() {
		Locale[] numberFormatLocales = NumberFormat.getAvailableLocales();
		Locale[] dateFormatLocales = DateFormat.getAvailableLocales();
		Locale[] locales = Locale.getAvailableLocales();
		System.out.println("numberFormatLocales:");
		Stream.of(numberFormatLocales).forEach(System.out::println);
		System.out.println("dateFormatLocales:");
		Stream.of(dateFormatLocales).forEach(System.out::println);
		System.out.println("locales:");
		Stream.of(locales).forEachOrdered(System.out::println);
	}

	/**
	 * While working with localization, we might need to know what the default Locale
	 * on our JVM instance is. Fortunately, there’s a simple way to do that:
	 */
	@Test
	public void testDefaultLocale() {
		Locale defaultLocale = Locale.getDefault();
		System.out.println(defaultLocale);

		//Also, we can specify a default Locale by calling a similar setter method:
		Locale.setDefault(Locale.TRADITIONAL_CHINESE);
		//It’s especially relevant when we’d like to create JUnit tests that don’t depend on a JVM instance.
		System.out.println(Locale.getDefault());
	}

	/**
	 * This section refers to numbers and currencies formatters that should conform to
	 * different locale-specific conventions.
	 * 
	 * To format primitive number types (int, double) as well as their object
	 * equivalents (Integer, Double), we should use NumberFormat class and its static
	 * factory methods.
	 * 
	 * Two methods are interesting for us:
	 * 
	 * NumberFormat.getInstance(Locale locale) NumberFormat.getCurrencyInstance(Locale
	 * locale)
	 */
	@Test
	public void testNumberAndCurrency() {
		Locale usLocale = Locale.US;
		double number = 102300.456d;
		NumberFormat usNumberFormat = NumberFormat.getInstance(usLocale);
		assertEquals(usNumberFormat.format(number), "102,300.456");
		/*
		 * As we can see it’s as simple as creating Locale and using it to retrieve
		 * NumberFormat instance and formatting a sample number. We can notice that
		 * the output includes locale-specific decimal and thousand separators.
		 */
		System.out.println(usNumberFormat.format(number));

		BigDecimal numberCurrency = new BigDecimal(102_300.456d);
		NumberFormat usNumberCurrencyFormat = NumberFormat.getCurrencyInstance(usLocale);
		assertEquals(usNumberCurrencyFormat.format(numberCurrency), "$102,300.46");
		/*
		 * Formatting a currency involves the same steps as formatting a number. The
		 * only difference is that the formatter appends currency symbol and round
		 * decimal part to two digits.
		 */
		System.out.println(usNumberCurrencyFormat.format(numberCurrency));
	}

	/**
	 * Since Java 8 was introduced, the main class for localizing of dates and times
	 * is the DateTimeFormatter class. It operates on classes that implement
	 * TemporalAccessor interface, for example, LocalDateTime, LocalDate, LocalTime or
	 * ZonedDateTime. To create a DateTimeFormatter we must provide at least a
	 * pattern, and then Locale.
	 * 
	 * @on
	 */
	@Test
	public void testDateTimeFormatter() {
		Locale.setDefault(Locale.US);
		LocalDateTime localDateTime = LocalDateTime.of(2018, 1, 1, 10, 15, 50, 500);
		String pattern = "dd-MMMM-yyyy HH:mm:ss.SSS";

		DateTimeFormatter defaultTimeFormatter = DateTimeFormatter.ofPattern(pattern);
		DateTimeFormatter deTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.GERMANY);

		assertEquals(
				"01-January-2018 10:15:50.000",
				defaultTimeFormatter.format(localDateTime));
		assertEquals(
				"01-Januar-2018 10:15:50.000",
				deTimeFormatter.format(localDateTime));
		/*
		 * We can see that after retrieving DateTimeFormatter all we have to do is to
		 * call the format() method.
		 * 
		 * For a better understanding, we should familiarize with possible pattern
		 * letters.
		 * 
		 * Let’s look at letters for example:
		 * Symbol  	 Meaning                     Presentation      Examples
		 *   ------  -------                     ------------      -------
		 *    y       year-of-era                 year              2004; 04
		 *    M/L     month-of-year               number/text       7; 07; Jul; July; J
		 *    d       day-of-month                number            10
		 *  
		 *    H       hour-of-day (0-23)          number            0
		 *    m       minute-of-hour              number            30
		 *    s       second-of-minute            number            55
		 *    S       fraction-of-second          fraction          978
		 * @on
		 */
		System.out.println(defaultTimeFormatter.format(localDateTime));
		System.out.println(deTimeFormatter.format(localDateTime));

		/*
		 * In order to format LocalizedDateTime, we can use the
		 * ofLocalizedDateTime(FormatStyle dateTimeStyle) method and provide a
		 * predefined FormatStyle.
		 */
		ZoneId losAngelesTimeZone = TimeZone.getTimeZone("America/Los_Angeles").toZoneId();

		DateTimeFormatter localizedTimeFormatter = DateTimeFormatter
				.ofLocalizedDateTime(FormatStyle.FULL);
		String formattedLocalizedTime = localizedTimeFormatter.format(
				ZonedDateTime.of(localDateTime, losAngelesTimeZone));

		assertEquals("Monday, January 1, 2018 10:15:50 AM PST", formattedLocalizedTime);
		System.out.println(formattedLocalizedTime);
	}
}
