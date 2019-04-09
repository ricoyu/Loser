package com.loserico.regex;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.loserico.commons.utils.DateUtils;

public class RegexTest {

	@Test
	public void testExtractDomain() {
		//		String regex = "^(?:https?:\\/\\/)?(?:[^@\\n]+@)?(?:www\\.)?([^:\\/\\n]+)";
		String regex = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(\\#(.*))?";
		String domain = "http://admin.loserico.com";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(domain);
		if (matcher.matches()) {
			System.out.println(matcher.group(0));
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(matcher.group(3));
		}
	}

	@Test
	public void testDatePattern() {
		String regexFull = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
		String regexMedim = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$";
		String regexShort = "^\\d{4}-\\d{2}-\\d{2}$";
		assertTrue(Pattern.matches(regexFull, "2016-10-20 11:24:20"));
		assertTrue(Pattern.matches(regexMedim, "2016-10-20 11:24"));
		assertFalse(Pattern.matches(regexFull, "2016-10-20"));
		assertTrue(Pattern.matches(regexShort, "2016-10-20"));
		assertFalse(Pattern.matches(regexShort, "2016-10-20s"));
		assertFalse(Pattern.matches(regexShort, "201a-10-20s"));
	}

	@Test
	public void testFileSplit() {
		List<String> sources = asList("101-Alphabet @ East Coast- Techace data (Mar'17).xlsx=3813027222",
				"102-Alphabet @ Childcare - Techace data (Mar'17).xlsx=603-020272-001");
		Pattern pattern = Pattern.compile("(\\d+)-(.+)=([\\d-]+)");
		sources.forEach((source) -> {
			Matcher matcher = pattern.matcher(source);
			if (matcher.matches()) {
				System.out.println(matcher.group(1));
				System.out.println(matcher.group(2));
				System.out.println(matcher.group(3));
			}
		});
	}

	@Test
	public void testMobile() {
		//		Pattern pattern = Pattern.compile("^((13[4-9])|(147)|(15[0-2,7-9])|(17[8])|(18[2-4,7-8]))\\\\d{8}|(170[5])\\\\d{7}$");
		Pattern pattern = Pattern.compile("^(0|86|17951)?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$");
		List<String> mobiles = asList(
				"13913582189",
				"1391358219",
				"13413582189",
				"1341358219",
				"13513582189",
				"13613582189",
				"13713582189",
				"13813582189",
				"13913582189",
				"15013582189",
				"15113582189",
				"15213582189",
				"15713582189",
				"15813582189",
				"15913582189",
				"18713582189",
				"18813582189");
		mobiles.stream().forEach((mobile) -> {
			Matcher matcher = pattern.matcher(mobile);
			if (matcher.matches()) {
				//				System.out.println(mobile +" is a valid mobile");
			} else {
				System.out.println(mobile + " is not a valid mobile");
			}
		});
	}

	@Test
	public void testClassNamePattern() {
		String className = "Unable to find cn.mulberrylearning.entity.pims.Staff with id 123123123";
		Pattern pattern = Pattern.compile("^Unable to find\\s*.*\\.([a-zA-Z0-9_]+)\\s*with id .*");
		Matcher matcher = pattern.matcher(className);
		if (matcher.matches()) {
			System.out.println(matcher.group(1));
		} else {
			System.out.println("not match");
		}
	}

	@Test
	public void testUsernamePettern() {
		Pattern pattern = Pattern.compile("^[a-z0-9_-]{3,15}$");
		Matcher matcher = pattern.matcher("你好");
		System.out.println(matcher.matches());
	}
	
	@Test
	public void testEnglishName() {
//		System.out.println(Pattern.matches("[a-zA-Z_-]+", "aas s-d"));
//		System.out.println(Pattern.matches("[a-zA-Z_-]+", "-"));
//		System.out.println(Pattern.matches("[\\u3000-\\u301e\\ufe10-\\ufe19\\ufe30-\\ufe44\\ufe50-\\ufe6b\\uff01-\\uffee]+", "你好"));
//		System.out.println(Pattern.matches("[\\u3000-\\u301e\\ufe10-\\ufe19\\ufe30-\\ufe44\\ufe50-\\ufe6b\\uff01-\\uffee]+", "rico_yu"));
		
		Pattern pattern = Pattern.compile(".*[\\u4E00-\\u9FA5]+.*");
//		Pattern pattern = Pattern.compile("[\u3000-\u301e\ufe10-\ufe19\ufe30-\ufe44\ufe50-\ufe6b\uff01-\uffee]*");
		System.out.println(pattern.matcher("你好").matches());
		System.out.println(pattern.matcher("俞雪华").matches());
		System.out.println(pattern.matcher("彭魁1").matches());
		System.out.println(pattern.matcher("rico yu").matches());
	}
	
	@Test
	public void testSGDAmount() {
		//String s = "2OCBCSGSGXXX628423725001                      ZAINAL ARIFFIN BIN ABDUL RAZAK                                                                                                              SGD000000000000024000Sept 18 1st DD                     T1118298J                          COLLSept 18 1st DD                                                                                                                                                                                                                                                                          T1118298J           0                                 ";
		String s = "2OCBCSGSGXXX628423725001                      ZAINAL ARIFFIN BIN ABDUL RAZAK                                                                                                              SGD000000000000024001Sept 18 1st DD                     T1118298J                          COLLSept 18 1st DD                                                                                                                                                                                                                                                                          T1118298J           0                                 ";
		String regex = ".+SGD(\\d+)[A-Za-z]+.*";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		if (matcher.matches()) {
			System.out.println(matcher.group(0));
			String amountGroup = matcher.group(1);
			System.out.println(amountGroup);
			int precisionCount = 0;
			StringBuilder amount = new StringBuilder();
			for(int i = amountGroup.length() - 1; i>=0; i--) {
				if (precisionCount == 2) {
					amount.insert(0, ".");
				}
				amount.insert(0, amountGroup.charAt(i));
				precisionCount++;
			}
			System.out.println(amount);
			BigDecimal bigDecimal = new BigDecimal(amount.toString());
			System.out.println(bigDecimal);
		}
	}
	
	@Test
	public void testExtractReferenceStatus() {
		String s = "2OCBCSGSGXXX628423725001                      ZAINAL ARIFFIN BIN ABDUL RAZAK                                                                                                              SGD000000000000024001Sept 18 1st DD                     T1118298J                          COLLSept 18 1st DD                                                                                                                                                                                                                                                                          T1118298J           0                                 ";
		s = s.trim();
		String[] columns = s.split("\\s+");
		for (int i = 0; i < columns.length; i++) {
			String string = columns[i];
			System.out.println(string);
		}
	}
	
	@Test
	public void testExtractValueDate() {
		String s = "1CNORMAL    B001400212648UOVBSGSGXXXSGD4513061336                        NASCANS SEMBAWANG                                                                                                                           2018091320180917                                                                                                                                            SEPT 18 2ND DD  ODIS2                                                                                                                                                                                                                                 ";
		Pattern pattern = Pattern.compile(".*\\s+(\\d{8})(\\d{8})\\s+.*");
		Matcher matcher = pattern.matcher(s);
		if (matcher.matches()) {
			System.out.println(matcher.group(1));
			System.out.println(matcher.group(2));
			System.out.println(DateUtils.toLocalDate(matcher.group(2)));
		}
	}
	
	@Test
	public void testName() {
		String message = "Duplicate entry '4521-watch_app-ddd' for key 'user_id'";
		Pattern pattern = Pattern.compile("^Duplicate entry '(.+)' for key.*");
		Matcher matcher = pattern.matcher(message);
		if (matcher.matches()) {
			System.out.println(matcher.group(1));
		}
	}
}
