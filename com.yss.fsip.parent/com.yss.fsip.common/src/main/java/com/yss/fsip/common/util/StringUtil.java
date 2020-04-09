package com.yss.fsip.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author LSP
 *
 */
public final class StringUtil {

	static Pattern numberPattern = Pattern.compile("^-?\\d+$");

	/**
	 * 默认的空值
	 */
	public static final String EMPTY = "";
	private static String[] EMPTY_ARGS = new String[0];

	/**
	 * 检查字符串是否为空
	 * 
	 * @param str 字符串
	 * @return
	 */
	public static boolean isEmpty(String str) {

		if (str == null) {
			return true;
		} else if (str.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查字符串是否为空
	 * 
	 * @param str 字符串
	 * @return
	 */
	public static boolean isNotEmpty(String str) {

		return !isEmpty(str);
	}

	/**
	 * @param str string
	 * @return 去掉首尾空格的字符
	 */
	public static String trim(String str) {

		if (str == null || str.trim().equals("")) {
			return "";
		}
		return str.trim();
	}

	/**
	 * 截取并保留标志位之前的字符串
	 * 
	 * @param str  字符串
	 * @param expr 分隔符
	 * @return
	 */
	public static String substringBefore(String str, String expr) {

		if (isEmpty(str) || expr == null) {
			return str;
		}
		if (expr.length() == 0) {
			return EMPTY;
		}
		int pos = str.indexOf(expr);
		if (pos == -1) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * 截取并保留标志位之后的字符串
	 * 
	 * @param str  字符串
	 * @param expr 分隔符
	 * @return
	 */
	public static String substringAfter(String str, String expr) {

		if (isEmpty(str)) {
			return str;
		}
		if (expr == null) {
			return EMPTY;
		}
		int pos = str.indexOf(expr);
		if (pos == -1) {
			return EMPTY;
		}
		return str.substring(pos + expr.length());
	}

	/**
	 * 截取并保留最后一个标志位之前的字符串
	 * 
	 * @param str  字符串
	 * @param expr 分隔符
	 * @return
	 */
	public static String substringBeforeLast(String str, String expr) {

		if (isEmpty(str) || isEmpty(expr)) {
			return str;
		}
		int pos = str.lastIndexOf(expr);
		if (pos == -1) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * 截取并保留最后一个标志位之后的字符串
	 * 
	 * @param str
	 * @param expr 分隔符
	 * @return
	 */
	public static String substringAfterLast(String str, String expr) {

		if (isEmpty(str)) {
			return str;
		}
		if (isEmpty(expr)) {
			return EMPTY;
		}
		int pos = str.lastIndexOf(expr);
		if (pos == -1 || pos == (str.length() - expr.length())) {
			return EMPTY;
		}
		return str.substring(pos + expr.length());
	}

	/**
	 * 把字符串按分隔符转换为数组
	 * 
	 * @param string 字符串
	 * @param expr   分隔符
	 * @return
	 */
	public static String[] stringToArray(String string, String expr) {

		return string.split(expr);
	}

	/**
	 * 去除字符串中的空格
	 * 
	 * @param str
	 * @return
	 */
	public static String noSpace(String str) {

		str = str.trim();
		str = str.replace(" ", "_");
		return str;
	}

	/**
	 * 功能描述：对给定的数字长度不够的在其前面补0
	 * 
	 * @param date 给定的数字
	 * @param len  需要的长度
	 * @return 返回：返回给定长度的字符串
	 */
	public static String addZero(int date, int len) {

		String str = String.valueOf(date);
		StringBuffer sb = new StringBuffer("");
		while (str.length() < len) {
			sb.append("0");
			len--;
		}
		sb.append(str);
		return sb.toString();
	}

	/**
	 * 功能描述：人民币转成大写
	 * 
	 * @param value 需要转换的金额
	 * @return String 转换后的字符串
	 */
	public static String rmbToBigString(double value) {

		char[] hunit = { '拾', '佰', '仟' }; // 段内位置表示
		char[] vunit = { '万', '亿' }; // 段名表示
		char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示
		long midVal = (long) (value * 100); // 转化成整形
		if (midVal < 10) {
			return digit[Integer.parseInt(midVal + "")] + "分";
		}
		String valStr = String.valueOf(midVal); // 转化成字符串

		int n = valStr.length() - 2;
		String head = valStr.substring(0, n); // 取整数部分
		String rail = valStr.substring(n); // 取小数部分

		String prefix = ""; // 整数部分转化的结果
		String suffix = ""; // 小数部分转化的结果
		// 处理小数点后面的数
		if (rail.equals("00")) { // 如果小数部分为0
			suffix = "整";
		} else {
			suffix = digit[rail.charAt(0) - '0'] + "角" + digit[rail.charAt(1) - '0'] + "分"; // 否则把角分转化出来
		}
		// 处理小数点前面的数
		char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
		char zero = '0'; // 标志'0'表示出现过0
		byte zeroSerNum = 0; // 连续出现0的次数
		for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
			int idx = (chDig.length - i - 1) % 4; // 取段内位置
			int vidx = (chDig.length - i - 1) / 4; // 取段位置
			if (chDig[i] == '0') { // 如果当前字符是0
				zeroSerNum++; // 连续0次数递增
				if (zero == '0') { // 标志
					zero = digit[0];
				} else if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
					prefix += vunit[vidx - 1];
					zero = '0';
				}
				continue;
			}
			zeroSerNum = 0; // 连续0次数清零
			if (zero != '0') { // 如果标志不为0,则加上,例如万,亿什么的
				prefix += zero;
				zero = '0';
			}
			prefix += digit[chDig[i] - '0']; // 转化该数字表示
			if (idx > 0)
				prefix += hunit[idx - 1];
			if (idx == 0 && vidx > 0) {
				prefix += vunit[vidx - 1]; // 段结束位置应该加上段名如万,亿
			}
		}

		if (prefix.length() > 0)
			prefix += '圆'; // 如果整数部分存在,则有圆的字样
		return prefix + suffix; // 返回正确表示
	}

	/**
	 * 根据年、月、日计算星期.
	 * 
	 * @param year  年
	 * @param month 月
	 * @param day   日
	 * @return 对应的星期数
	 */
	public static int getWeekByDay(int year, int month, int day) {

		int e[] = new int[] { 0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5 };
		int w = (day - 1 + e[month - 1] + year + (year >> 2) - year / 100 + year / 400);
		if (month < 3 && ((year & 3) == 0 && year % 100 != 0 || year % 400 == 0) && year != 0) {
			--w;
		}
		w %= 7;
		return w;
	}

	/**
	 * 判断字符串的编码
	 * 
	 * @param str 待判断编码的字符串
	 * @return 编码格式名称
	 */
	public static String getEncoding(String str) {

		String encode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				return encode;
			}
		} catch (Exception exception3) {
		}
		return "";
	}

	public static int getCharInStrCnt(String str, char findChar) {

		int cnt = 0;
		if (str != null) {
			char[] cs = str.toCharArray();
			for (char c : cs) {
				if (c == findChar) {
					cnt++;
				}
			}
		}
		return cnt;
	}

	/**
	 * 获取字符串的正则表达式
	 * 
	 * @param str 传入参数如：*ABC ABC* ?BC 等
	 * @return
	 */
	public static String getRegex(String str) {

		String regex = "*";
		if (str != null && !str.trim().equals("")) {
			regex = str;
		}
		regex = regex.replace('.', '#');
		regex = regex.replaceAll("#", "\\\\.");
		regex = regex.replace('*', '#');
		regex = regex.replaceAll("#", ".*");
		regex = regex.replace('?', '#');
		regex = regex.replaceAll("#", ".?");
		regex = "^" + regex + "$";
		return regex;
	}

	public static int toInt(Object obj) {
		if (obj == null || obj.toString().trim().equals("")) {
			return 0;
		}
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		} else if (obj instanceof String) {
			return Integer.parseInt(obj.toString());
		} else {
			throw new RuntimeException("对象【" + obj + "】不能转换成整型数据！");
		}

	}

	public static String toString(Object obj) {
		if (obj == null || obj.toString().trim().equals("")) {
			return "";
		}
		return obj.toString();

	}

	/**
	 * 格式化http地址.
	 * 
	 * @param url
	 * @return
	 */
	public static String formatHttpUrl(String url) {

		if (url == null) {
			return "";
		}
		StringBuffer str = new StringBuffer(url.length());
		url = url.trim();
		int dotIdx = url.indexOf("://");
		if (dotIdx > 0) {
			str.append(url.substring(0, dotIdx));
			str.append("://");
			url = url.substring(dotIdx + 3);
		}
		char[] arr = url.toCharArray();
		int reCnt = 0;
		int slashCnt = 0;
		for (char c : arr) {
			if (c == '\\') {
				slashCnt = 0;
				if (reCnt == 0)
					str.append('/');
				reCnt++;
			} else if (c == '/') {
				reCnt = 0;
				if (slashCnt == 0)
					str.append(c);
				slashCnt++;
			} else {
				reCnt = 0;
				slashCnt = 0;
				str.append(c);
			}
		}
		return str.toString();
	}

	public static boolean isNumber(Object obj) {
		return numberPattern.matcher(obj.toString()).find();
	}

	public static String formatMessage(String message, String... args) {

		if (message == null)
			return "";
		if (args == null || args.length == 0)
			args = EMPTY_ARGS;

		int length = message.length();
		int bufLen = length + (args.length * 5);

		StringBuffer buffer = new StringBuffer(bufLen < 0 ? 0 : bufLen);
		int defNum = -1;
		for (int i = 0; i < length; i++) {
			char c = message.charAt(i);
			switch (c) {
			case '{':
				int index = message.indexOf('}', i);
				if (index == -1) {
					buffer.append(c);
					break;
				}
				i++;
				if (i >= length) {
					buffer.append(c);
					break;
				}
				int number = -1;
				try {
					defNum++;
					String s = message.substring(i, index);
					if (s.trim().equals("")) {
						number = defNum;
					} else {
						number = Integer.parseInt(s);
					}
				} catch (NumberFormatException e) {
					number = defNum;
				}

				if (number >= args.length || number < 0) {
					buffer.append("<missing argument>");
					i = index;
					break;
				}
				buffer.append(args[number]);

				i = index;
				break;
			case '\'':
				int nextIndex = i + 1;
				if (nextIndex >= length) {
					buffer.append(c);
					break;
				}
				char next = message.charAt(nextIndex);
				if (next == '\'') {
					i++;
					buffer.append(c);
					break;
				}
				index = message.indexOf('\'', nextIndex);
				if (index == -1) {
					buffer.append(c);
					break;
				}
				buffer.append(message.substring(nextIndex, index));
				i = index;
				break;
			default:
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

	public static String decodeXss(String str) {
		if (str != null) {
			str = str.replaceAll("&lt;", "<");
			str = str.replaceAll("&gt;", ">");
			str = str.replaceAll("&amp;", "&");
			str = str.replaceAll("&nbsp;", " ");
		}
		return str;
	}

	/**
	 * 判断是否为数字（支持带小数和负号）
	 * 
	 * @param str
	 * @return true:是数字；false:不是数字
	 */
	public static boolean isNumeric_New(String str) {
		if (isEmpty(str)) {
			return false;
		} else {
			String regex = "^(-?\\d+)(\\.\\d+)?$";
			Pattern p = Pattern.compile(regex);
			Matcher matcher = p.matcher(str);
			return matcher.matches();
		}
	}

	/**
	 * 右补位(计算长度的时候以gbk为编码)
	 * 
	 * @param str
	 * @param len
	 * @param c
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String addOnRightGBK(String str, int len, char c) throws UnsupportedEncodingException {
		String tmpString = str;
		if (tmpString == null || tmpString.length() == 0) {
			tmpString = "";
		}
		int gbkLength = tmpString.getBytes("GBK").length;
		if (gbkLength >= len) {
			return tmpString;
		}
		char arr[] = new char[len - gbkLength];
		Arrays.fill(arr, 0, (len - gbkLength), c);
		return tmpString + String.valueOf(arr);
	}

	/**
	 * 
	 * 格式化等长字符串，加前缀
	 * 
	 * @param s    字符串
	 * @param flag 前缀
	 * @param len  长度
	 * @return
	 * @author PJ
	 * @throws UnsupportedEncodingException
	 */
	public static String stringGBKFormatLeft(String s, String flag, int len) throws UnsupportedEncodingException {
		if (s == null) {
			s = " ";
		}
		int add = len - s.getBytes("GBK").length;
		if (add > 0) {
			for (int i = 0; i < add; i++) {
				s = flag + s;
			}
		}
		return s;
	}

	/**
	 * 检查字符串是否为空
	 * 
	 * @param str 对象
	 * @return
	 */
	public static boolean isNotEmpty(Object str) {
		return !isEmpty(str);
	}

	/**
	 * 检查字符串是否为空
	 * 
	 * @param str 对象
	 * @return
	 */
	public static boolean isEmpty(Object str) {
		// 增加断言，避免开发使用错误的参数类型 wuyang
		assert !(str instanceof List) : "参数类型不能为List";
		assert !(str instanceof Map) : "参数类型不能为Map";
		if (str == null)
			return true;
		return str instanceof String || str instanceof StringBuffer ? StringUtil.isEmpty(String.valueOf(str)) : false;
	}

	/**
	 * 功能说明：从给定字符串的左侧截取子字符串，超长也不会报错
	 * 
	 * @param srt 给定字符串
	 * @param len 截取长度
	 * @return 子字符串
	 * @author wuyang
	 */
	public static final String left(String srt, int len) {
		if (len >= srt.length())
			return srt;
		return srt.substring(0, len);
	}

	/**
	 * 功能说明：从给定字符串的右侧截取子字符串
	 * 
	 * @param str 给定字符串
	 * @param len 截取长度
	 * @return 子字符串
	 * @author wuyang
	 */
	public static final String right(String str, int len) {
		if (len >= str.length())
			return str;
		return str.substring(str.length() - len);
	}

	/**
	 * 格式化等长字符串，加后缀
	 * 
	 * @param s    字符串
	 * @param flag 后缀
	 * @param len  长度
	 * @return String
	 * @author SYC
	 * @throws BizException 异常
	 */
	public static String stringFormatGBK(String s, String flag, int len) {
		String tmpString = s;
		if (tmpString == null) {
			tmpString = " ";
		}

		int add = 0;
		try {
			// 这里要转成gbk进行计算，否则对不齐cwt2013-11-25
			add = len - tmpString.getBytes("GBK").length;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		StringBuffer result = new StringBuffer(tmpString);
		if (add > 0) {
			for (int i = 0; i < add; i++) {
				result.append(flag);
			}
		}
		return result.toString();
	}

	/**
	 * 将字符串值转换成指定类型的对象
	 * 
	 * @param value 字符串值
	 * @param type  指定类型
	 * @return 类型的对象
	 * @throws ParseException 时间转换异常
	 */
	public static Object toObject(String value, String type) throws ParseException {
		Object reValue = value;
		if (isNotEmpty(value)) {
			if ("Double".equalsIgnoreCase(type)) {
				reValue = Double.valueOf(value);
			} else if ("Float".equalsIgnoreCase(type)) {
				reValue = Float.valueOf(value);
			} else if ("Long".equalsIgnoreCase(type)) {
				reValue = Long.valueOf(value);
			} else if ("Integer".equalsIgnoreCase(type)) {
				reValue = Integer.valueOf(value);
			} else if ("Boolean".equalsIgnoreCase(type)) {
				reValue = Boolean.valueOf(value);
			} else if ("BigDecimal".equalsIgnoreCase(type)) {
				reValue = new BigDecimal(value);
			} else if ("Date".equalsIgnoreCase(type)) {
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				reValue = sf.parse(value);
			}
		}
		return reValue;
	}

	/**
	 * 将小于长度的字符串格式化为（len）位（左补位），大于给定长度的原样返回
	 * 
	 * @param str 原字符串
	 * @param len 长度
	 * @param c   左补字符
	 * @return 格式化后的字符串
	 * @author wxy
	 * @date 2014-11-24
	 */
	public static String format(String str, int len, char c) {
		String tmpString = str;
		if (tmpString == null || tmpString.length() == 0) {
			tmpString = "";
		}
		if (tmpString.length() >= len) {
			return tmpString;
		}
		char arr[] = new char[len - tmpString.length()];
		Arrays.fill(arr, 0, (len - tmpString.length()), c);
		return String.valueOf(arr) + tmpString;
	}

	/**
	 * String数组去重
	 * 
	 * @param value 原数组
	 * @return 去重后的数组
	 * @author fkz
	 * @date 2015-4-18
	 */
	public static String[] removeRepeat(String[] value) {
		TreeSet<String> set = new TreeSet<String>();
		for (int i = 0; i < value.length; i++) {
			set.add(value[i]);
		}
		String[] result = new String[set.size()];
		return set.toArray(result);
	}

	/**
	 * String去重
	 * 
	 * @param value 原值
	 * @return 去重后的值
	 * @author fkz
	 * @date 2015-4-18
	 */
	public static String removeRepeat(String value) {

		return StringUtil.ArrayToString(StringUtil.removeRepeat(value.split(",")));
	}

	/**
	 * String[]转为，分割的字符串
	 * 
	 * @param types 类型集合
	 * @return String
	 * @author zhanglq
	 */
	public static String ArrayToString(String[] types) {

		StringBuffer sb = new StringBuffer();
		if (types != null && types.length > 0) {
			for (String str : types) {
				sb.append(str).append(",");
			}
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 判断是否为数字
	 * 
	 * @param str
	 * @return true:是数字；false:不是数字
	 * @author dongyeyun
	 * @date 2015-6-10
	 */
	public static boolean isNumeric(String str) {
		if (isEmpty(str)) {
			return false;
		} else {
			for (int i = 0; i < str.length(); i++) {
				if (!Character.isDigit(str.charAt(i))) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * 功能说明：检查字符串是否以比对字符集开头，如果以其中一个开头就返回true，否则返回false
	 * 
	 * @param str String 被检查字符串
	 * @param ary String[] 比对字符集
	 * @return boolean 是否以比对字符集开头
	 */
	public static boolean oneStart(String str, String arys) {
		if (isEmpty(arys))
			return false;
		String[] ary = arys.split(",");
		for (int i = 0; i < ary.length; i++) {
			if (str.startsWith(ary[i]))
				return true;
		}
		return false;
	}

	/**
	 * 
	 * 功能说明：检查字符串是否包含比对字符集，如果包含其中一个就返回true，否则返回false
	 * 
	 * @param str String 被检查字符串
	 * @param ary String[] 比对字符集
	 * @return boolean
	 */
	public static boolean oneOf(String str, String[] ary, boolean flag) {
		for (int i = 0; i < ary.length; i++) {
			if (flag) {
				if (str.equalsIgnoreCase(ary[i]))
					return true;
			} else {
				if (str.indexOf(ary[i]) > -1)
					return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * 重载方法
	 * 
	 * @param str
	 * @param ary
	 * @return
	 * @author chenwentao
	 * @date 2018-5-15
	 */
	public static boolean oneOf(String str, String[] ary) {
		return oneOf(str, ary, false);
	}

	/**
	 * 
	 * 功能说明：检查字符串是否包含比对字符集，如果包含其中一个就返回true，否则返回false
	 * 
	 * @param str  String 被检查字符串
	 * @param arys String 比对字符集(多个字符串用英文逗号间隔)
	 * @return boolean
	 */
	public static boolean oneOf(String str, String arys, boolean flag) {
		if ((arys == null || arys.trim().equalsIgnoreCase("")) && str != null && !str.trim().equalsIgnoreCase(""))
			return false;// 比对字符集为空直接返回false
		return oneOf(str, arys.split(","), flag);
	}

	/**
	 * 
	 * 功能说明：检查字符串是否包含比对字符集，如果包含其中一个就返回true，否则返回false
	 * 
	 * @param str  String 被检查字符串
	 * @param arys String 比对字符集(多个字符串用英文逗号间隔)
	 * @return boolean
	 */
	public static boolean oneOf(String str, String arys) {
		if ((arys == null || arys.trim().equalsIgnoreCase("")) && str != null && !str.trim().equalsIgnoreCase(""))
			return false;// 比对字符集为空直接返回false
		return oneOf(str, arys.split(","), false);
	}

	/**
	 * 
	 * 将堆栈信息转换为字符串
	 * 
	 * @param e
	 * @return
	 * @author chenwentao
	 * @date 2015-8-5
	 */
	public static String getStackMsg(Exception e) {
		StringBuffer sb = new StringBuffer();
		sb.append(e.getMessage());
		StackTraceElement[] stackArray = e.getStackTrace();
		for (int i = 0; i < stackArray.length; i++) {
			StackTraceElement element = stackArray[i];
			if (element.getClassName().startsWith("com.yss.acs")) {
				sb.append(e.getMessage() + " at " + element.getClassName() + "." + element.getMethodName() + "("
						+ element.getLineNumber() + "行)\n");

				break;
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * 将堆栈信息转换为字符串
	 * 
	 * @param e
	 * @return
	 * @author chenwentao
	 * @date 2015-8-5
	 */
	public static String getStackMsg(Throwable e) {

		StringBuffer sb = new StringBuffer();
		StackTraceElement[] stackArray = e.getStackTrace();
		for (int i = 0; i < stackArray.length; i++) {
			StackTraceElement element = stackArray[i];
			sb.append(element.toString() + "\n");
		}
		return sb.toString();
	}

	/**
	 * 根据两个日期时间返回时间段内的时间集合
	 * 
	 * @param beginDate
	 * @param endDate
	 * @author liuchunqi
	 * @return List
	 */
	public static List<Date> getDatesBetweenTwoDate(Date oneDate, Date twoDate) {
		Date beginDate; // 开始日期
		Date endDate; // 结束日期
		List<Date> lDate = new ArrayList<Date>();
		// 如果一个日期为null那直接返回另一个日期
		if (oneDate == null) {
			lDate.add(twoDate);
		} else if (twoDate == null) {
			lDate.add(oneDate);
		} else {
			// 判断两个日期的大小
			if (oneDate.compareTo(twoDate) == -1) {
				beginDate = oneDate;
				endDate = twoDate;
			} else {
				beginDate = twoDate;
				endDate = oneDate;
			}
			lDate.add(beginDate);// 把开始时间加入集合
			Calendar cal = Calendar.getInstance();
			// 使用给定的 Date 设置此 Calendar 的时间
			cal.setTime(beginDate);
			boolean bContinue = true;
			while (bContinue) {
				// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
				cal.add(Calendar.DAY_OF_MONTH, 1);
				// 测试此日期是否在指定日期之后
				if (endDate.after(cal.getTime())) {
					lDate.add(cal.getTime());
				} else {
					break;
				}
			}
			lDate.add(endDate);// 把结束时间加入集合
		}
		return lDate;
	}

	/**
	 * 右补位
	 * 
	 * @param str
	 * @param len
	 * @param c
	 * @return
	 */
	public static String addOnRight(String str, int len, char c) {
		String tmpString = str;
		if (tmpString == null || tmpString.length() == 0) {
			tmpString = "";
		}
		if (tmpString.length() >= len) {
			return tmpString;
		}
		char arr[] = new char[len - tmpString.length()];
		Arrays.fill(arr, 0, (len - tmpString.length()), c);
		return tmpString + String.valueOf(arr);
	}

	/**
	 * 通过正则表达式的方式获取字符串中指定字符的个数
	 * 
	 * @param str        指定的字符串
	 * @param patternStr 正则表达式字符串
	 * @return 指定字符的个数
	 */
	public static int pattern(String str, String patternStr) {
		// 根据指定的字符构建正则
		Pattern pattern = Pattern.compile(patternStr);
		// 构建字符串和正则的匹配
		Matcher matcher = pattern.matcher(str);
		int count = 0;
		// 循环依次往下匹配
		while (matcher.find()) { // 如果匹配,则数量+1
			count++;
		}
		return count;
	}

	/**
	 * 按分割符出现次数的位置截取字符串，截取舍掉第n次出现位置及之后的字符 如果 n<=0 或 不存在分隔符，则返回字符串本身 如果
	 * n>分隔符个数，则截取舍掉最后一个分隔符位置及之后的字符
	 * 
	 * @param str   指定的字符串
	 * @param split 分隔符
	 * @param n     从第n个分隔符截取
	 * @return
	 */
	public static String substringBySplit(String str, String split, int n) {
		int len = 0;
		for (int i = 0; i < n; i++) {
			int indexOf = str.indexOf(split, len);
			if (indexOf > -1) {
				len = indexOf + 1;
			}
		}
		return len > 0 ? str.substring(0, len - 1) : str;
	}

	/**
	 * 字符串转换为Ascii
	 * 
	 * @param value
	 * @return
	 */
	public static String stringToAscii(String value) {
		StringBuffer sbu = new StringBuffer();
		char[] chars = value.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (i == chars.length - 1) {
				sbu.append((int) chars[i]);
			} else {
				sbu.append((int) chars[i]).append(",");
			}
		}
		return sbu.toString();
	}

	/**
	 * Ascii转换为字符串
	 * 
	 * @param value
	 * @return
	 */
	public static String asciiToString(String value) {
		StringBuffer sbu = new StringBuffer();
		String[] chars = value.split(",");
		for (int i = 0; i < chars.length; i++) {
			sbu.append((char) Integer.parseInt(chars[i]));
		}
		return sbu.toString();
	}

	/**
	 * 判断是否包含英文字母
	 *
	 * @param str
	 * @return
	 */
	public static boolean judgeContainsStr(String str) {
		String regex=".*[a-zA-Z]+.*";
		Matcher m= Pattern.compile(regex).matcher(str);
		return m.matches();
	}

	/**
	* 模糊查询特殊字符串处理：<br/>
	*  / 改成 // <br/>
	*  _ 改成 /_ <br/>
	*  % 改成 /% <br/>
	* @Author: jingminy
	* @Date: 2020/2/28 9:54
	*/
	public static String processLikeQueryEscapeChar(String str) {
		if(isEmpty(str)) {
			return str;
		}
		return str.replaceAll("/", "//").replaceAll("_", "/_").replaceAll("%", "/%");
	}

	public static void main(String[] args) {
		System.out.println(rmbToBigString(0.09));
	}
}
