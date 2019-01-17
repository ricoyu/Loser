package com.loserico.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

	//IP
	public final static String IP = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
			+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
	//匹配全网IP的正则表达式
	//public static final String IP = "^((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))$";

	//匹配URL的正则表达式
	public static final String URL = "^(([hH][tT]{2}[pP][sS]?)|([fF][tT][pP]))\\:\\/\\/[wW]{3}\\.[\\w-]+\\.\\w{2,4}(\\/.*)?$";

	//邮箱
	public final static String EMAIL = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
	/**
	 * 匹配邮箱的正则表达式
	 * <br>"www."可省略不写
	 */
	//public static final String EMAIL = "^(www\\.)?\\w+@\\w+(\\.\\w+)+$";

	//数字
	public final static String NUMBER = "^[0-9]*$";

	//密码(由数字/大写字母/小写字母/标点符号组成，四种都必有，8位以上)
	public static final String PASSWORD = "(?=^.{8,}$)(?=.*\\d)(?=.*\\W+)(?=.*[A-Z])(?=.*[a-z])(?!.*\\n).*$";

	//汉字(字符)
	public static final String CHINESE = "^[\u4e00-\u9fa5]+$";
	//public static final String CHINESE = "^[\u4e00-\u9f5a]+$";

	//中文及全角标点符号(字符)
	public static final String CHINESE_MARK = "[\u3000-\u301e\ufe10-\ufe19\ufe30-\ufe44\ufe50-\ufe6b\uff01-\uffee]";

	//中国大陆手机号码
	public static final String MOBILE = "(\\+\\d+)?1[3458]\\d{9}$";

	/**
	 * 匹配手机号码的正则表达式
	 * <br>支持130——139、150——153、155——159、180、183、185、186、188、189号段
	 */
	//public static final String MOBILE = "^1{1}(3{1}\\d{1}|5{1}[012356789]{1}|8{1}[035689]{1})\\d{8}$";

	//电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
	public static final String PHONE = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";

	//中国大陆身份证号(15位或18位)
	//public static final String ID_CARD = "\\d{15}(\\d\\d[0-9xX])?";
	public static final String ID_CARD = "^(^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$)|(^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|"
			+ "(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[Xx])$)$";

	/**
	 * 匹配邮编的正则表达式
	 */
	public static final String ZIP_CODE = "^\\d{6}$";

	/**
	 * 判断是否是正确的IP地址
	 * @param ip
	 * @return boolean true,通过，false，没通过
	 */
	public static boolean isIp(String ip) {
		if (null == ip || "".equals(ip))
			return false;
		return ip.matches(IP);
	}

	/**
	 * 验证给定的字符串是否是URL，仅支持http、https、ftp
	 * @param string
	 * @return
	 */
	public static boolean isURL(String string) {
		return string.matches(URL);
	}

	/**
	 * 判断是否是正确的邮箱地址
	 * 
	 * @param email
	 * @return boolean true,通过，false，没通过
	 */
	public static boolean isEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		return email.matches(EMAIL);
	}

	/**
	 * 判断是否正整数
	 * 
	 * @param number
	 *            数字
	 * @return boolean true,通过，false，没通过
	 */
	public static boolean isNumber(String number) {
		if (null == number || "".equals(number))
			return false;
		return number.matches(NUMBER);
	}

	/**
	 * 判断几位小数(正数)
	 * 
	 * @param decimal
	 *            数字
	 * @param count
	 *            小数位数
	 * @return boolean true,通过，false，没通过
	 */
	public static boolean isDecimal(String decimal, int count) {
		if (null == decimal || "".equals(decimal))
			return false;
		String regex = "^(-)?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){" + count + "})?$";
		return decimal.matches(regex);
	}

	/** 
	 * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港）） 
	 * @param mobile 移动、联通、电信运营商的号码段 
	 *<p>移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡） 
	 *、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）</p> 
	 *<p>联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）</p> 
	 *<p>电信的号段：133、153、180（未启用）、189</p> 
	 * @return 验证成功返回true，验证失败返回false 
	 */
	public static boolean isMobile(String mobile) {
		return mobile.matches(MOBILE);
	}

	/** 
	 * 验证固定电话号码 
	 * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447 
	 * <br/><b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字， 
	 *  数字之后是空格分隔的国家（地区）代码。
	 * <br/><b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号—— 
	 * 对不使用地区或城市代码的国家（地区），则省略该组件。
	 * <br/><b>电话号码：</b>这包含从 0 到 9 的一个或多个数字 
	 * @return 验证成功返回true，验证失败返回false 
	 */
	public static boolean isPhone(String phone) {
		String regex = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
		return Pattern.matches(regex, phone);
	}

	/** 
	 * 验证中文 
	 * @param chinese 中文字符 
	 * @return 验证成功返回true，验证失败返回false 
	 */
	public static boolean isChinese(String chinese) {
		return chinese.matches(CHINESE);
	}

	/**
	 * 验证给定的字符串是否是身份证号
	 * <br>
	 * <br>身份证15位编码规则：dddddd yymmdd xx p 
	 * <br>dddddd：6位地区编码 
	 * <br>yymmdd：出生年(两位年)月日，如：910215 
	 * <br>xx：顺序编码，系统产生，无法确定 
	 * <br>p：性别，奇数为男，偶数为女
	 * <br>
	 * <br>
	 * <br>身份证18位编码规则：dddddd yyyymmdd xxx y 
	 * <br>dddddd：6位地区编码 
	 * <br>yyyymmdd：出生年(四位年)月日，如：19910215 
	 * <br>xxx：顺序编码，系统产生，无法确定，奇数为男，偶数为女 
	 * <br>y：校验码，该位数值可通过前17位计算获得
	 * <br>前17位号码加权因子为 Wi = [ 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 ]
	 * <br>验证位 Y = [ 1, 0, 10, 9, 8, 7, 6, 5, 4, 3, 2 ]
	 * <br>如果验证码恰好是10，为了保证身份证是十八位，那么第十八位将用X来代替 校验位计算公式：Y_P = mod( ∑(Ai×Wi),11 )
	 * <br>i为身份证号码1...17 位; Y_P为校验码Y所在校验码数组位置
	 * @param idCard
	 * @return
	 */
	public static boolean isIdCard(String idCard) {
		return idCard.matches(ID_CARD);
	}

	/**
	 * 验证给定的字符串是否是邮编
	 * @param string
	 * @return
	 */
	public static boolean isZipCode(String string) {
		return string.matches(ZIP_CODE);
	}

	/**
	 * 判断是否含有特殊字符
	 * 
	 * @param text
	 * @return boolean true,通过，false，没通过
	 */
	public static boolean hasSpecialChar(String text) {
		if (null == text || "".equals(text))
			return true;
		if (text.replaceAll("[a-z]*[A-Z]*\\d*-*_*\\s*", "").length() == 0) {
			// 如果不包含特殊字符
			return false;
		}
		return true;
	}

	/** 
	 * <pre> 
	 * 获取网址 URL 的一级域名 
	 * http://www.zuidaima.com/share/1550463379442688.htm ->> zuidaima.com 
	 * </pre> 
	 *  
	 * @param url 
	 * @return 
	 */
	public static String getDomain(String url) {
		Pattern p = Pattern.compile("(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
		// 获取完整的域名  
		// Pattern p=Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);  
		Matcher matcher = p.matcher(url);
		matcher.find();
		return matcher.group();
	}
}