package com.yss.fsip.common.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期工具类.
 * @author LSP
 *
 */
public final class DateUtil {
	
	private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

	// 格式：年－月－日 小时：分钟：秒
	public static final String FORMAT_ONE = "yyyy-MM-dd HH:mm:ss";

	// 格式：年－月－日 小时：分钟
	public static final String FORMAT_TWO = "yyyy-MM-dd HH:mm";

	// 格式：年月日 小时分钟秒
	public static final String FORMAT_THREE = "yyyyMMdd-HHmmss";

	// 格式：年－月－日
	public static final String LONG_DATE_FORMAT = "yyyy-MM-dd";

	// 格式：月－日
	public static final String SHORT_DATE_FORMAT = "MM-dd";

	// 格式：小时：分钟：秒
	public static final String LONG_TIME_FORMAT = "HH:mm:ss";

	// 格式：年-月
	public static final String MONTG_DATE_FORMAT = "yyyy-MM";

	// 年的加减
	public static final int SUB_YEAR = Calendar.YEAR;

	// 月加减
	public static final int SUB_MONTH = Calendar.MONTH;

	// 天的加减
	public static final int SUB_DAY = Calendar.DATE;

	// 小时的加减
	public static final int SUB_HOUR = Calendar.HOUR;

	// 分钟的加减
	public static final int SUB_MINUTE = Calendar.MINUTE;

	// 秒的加减
	public static final int SUB_SECOND = Calendar.SECOND;

	static final String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四",
			"星期五", "星期六" };

	@SuppressWarnings("unused")
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public DateUtil() {
	}

	/**
	 * 把符合日期格式的字符串转换为日期类型
	 * 
	 * @param dateStr
	 * @return
	 */
	public static java.util.Date stringtoDate(String dateStr, String format) {
		Date d = null;
		SimpleDateFormat formater = new SimpleDateFormat(format);
		try {
			formater.setLenient(false);
			d = formater.parse(dateStr);
		} catch (Exception e) {
			// log.error(e);
			d = null;
		}
		return d;
	}

	/**
	 * 把符合日期格式的字符串转换为日期类型
	 */
	public static java.util.Date stringtoDate(String dateStr, String format,
			ParsePosition pos) {
		Date d = null;
		SimpleDateFormat formater = new SimpleDateFormat(format);
		try {
			formater.setLenient(false);
			d = formater.parse(dateStr, pos);
		} catch (Exception e) {
			d = null;
		}
		return d;
	}

	/**
	 * 把日期转换为字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToString(java.util.Date date, String format) {
		String result = "";
		SimpleDateFormat formater = new SimpleDateFormat(format);
		try {
			result = formater.format(date);
		} catch (Exception e) {
			// log.error(e);
		}
		return result;
	}

	/**
	 * 获取当前时间的指定格式
	 * 
	 * @param format
	 * @return
	 */
	public static String getCurrDate(String format) {
		return dateToString(new Date(), format);
	}

	/**
	 * 获取当前时间
	 *
	 * @return
	 */
	public static Date getCurrTime() {
		return new Date();
	}

	/**
	 * 
	 * @param dateStr
	 * @param amount
	 * @return
	 */
	public static String dateSub(int dateKind, String dateStr, int amount) {
		Date date = stringtoDate(dateStr, FORMAT_ONE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(dateKind, amount);
		return dateToString(calendar.getTime(), FORMAT_ONE);
	}

	/**
	 * 两个日期相减
	 * 
	 * @param firstTime
	 * @param secTime
	 * @return 相减得到的秒数
	 */
	public static long timeSub(String firstTime, String secTime) {
		long first = stringtoDate(firstTime, FORMAT_ONE).getTime();
		long second = stringtoDate(secTime, FORMAT_ONE).getTime();
		return second - first;
	}

	/**
	 * 获得某月的天数
	 * 
	 * @param year
	 *            int
	 * @param month
	 *            int
	 * @return int
	 */
	public static int getDaysOfMonth(String year, String month) {
		        int days = 0;
		        if (month.equals("1") || month.equals("3") || month.equals("5")
		                || month.equals("7") || month.equals("8") || month.equals("10")
		                || month.equals("12")) {
		            days = 31;
		        } else if (month.equals("4") || month.equals("6") || month.equals("9")
		                || month.equals("11")) {
		            days = 30;
		        } else {
		            if ((Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 100 != 0)
		                    || Integer.parseInt(year) % 400 == 0) {
		                days = 29;
		            } else {
		                days = 28;
		            }
		        }
		 
		        return days;
		    }

	/**
	 * 获取某年某月的天数
	 * 
	 * @param year
	 *            int
	 * @param month
	 *            int 月份[1-12]
	 * @return int
	 */
	public static int getDaysOfMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获得当前日期
	 * 
	 * @return int
	 */
	public static int getToday() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DATE);
	}

	/**
	 * 获得当前月份
	 * 
	 * @return int
	 */
	public static int getToMonth() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获得当前年份
	 * 
	 * @return int
	 */
	public static int getToYear() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 返回日期的天
	 * 
	 * @param date
	 *            Date
	 * @return int
	 */
	public static int getDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DATE);
	}

	/**
	 * 返回日期的年
	 * 
	 * @param date
	 *            Date
	 * @return int
	 */
	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 返回日期的月份，1-12
	 * 
	 * @param date
	 *            Date
	 * @return int
	 */
	public static int getMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 计算两个日期相差的天数，如果date2 > date1 返回正数，否则返回负数
	 * 
	 * @param date1
	 *            Date
	 * @param date2
	 *            Date
	 * @return long
	 */
	public static long dayDiff(Date date1, Date date2) {
		return (date2.getTime() - date1.getTime()) / (1000*24*60*60);
	}

	/**
	 * 比较两个日期的年差
	 * 
	 * @param befor
	 * @param after
	 * @return
	 */
	public static int yearDiff(String before, String after) {
		Date beforeDay = stringtoDate(before, LONG_DATE_FORMAT);
		Date afterDay = stringtoDate(after, LONG_DATE_FORMAT);
		return getYear(afterDay) - getYear(beforeDay);
	}

	/**
	 * 比较指定日期与当前日期的差
	 * 
	 * @param befor
	 * @param after
	 * @return
	 */
	public static int yearDiffCurr(String after) {
		Date beforeDay = new Date();
		Date afterDay = stringtoDate(after, LONG_DATE_FORMAT);
		return getYear(beforeDay) - getYear(afterDay);
	}

	/**
	 * 比较指定日期与当前日期的差
	 * 
	 * @param before
	 * @return
	 * @author chenyz
	 */
	public static long dayDiffCurr(String before) {
		Date currDate = DateUtil.stringtoDate(currDay(), LONG_DATE_FORMAT);
		Date beforeDate = stringtoDate(before, LONG_DATE_FORMAT);
		return (currDate.getTime() - beforeDate.getTime()) / 00;

	}

	/**
	 * 获取每月的第一周
	 * 
	 * @param year
	 * @param month
	 * @return
	 * @author chenyz
	 */
	public static int getFirstWeekdayOfMonth(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.SATURDAY); // 星期天为第一天
		c.set(year, month - 1, 1);
		return c.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取每月的最后一周
	 * 
	 * @param year
	 * @param month
	 * @return
	 * @author chenyz
	 */
	public static int getLastWeekdayOfMonth(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.SATURDAY); // 星期天为第一天
		c.set(year, month - 1, getDaysOfMonth(year, month));
		return c.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获得当前日期字符串，格式"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @return
	 */
	public static String getCurrent() {
		String temp_str="";
	    Date dt = new Date();
	    //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    temp_str=sdf.format(dt);
	    return temp_str;
	}

	/**
	 * 获得当前日期字符串，格式"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @return
	 */
	public static String getNow() {
		Calendar today = Calendar.getInstance();
		return dateToString(today.getTime(), FORMAT_ONE);
	}

	/**
	 * 根据生日获取星座
	 * 
	 * @param birth
	 *            YYYY-mm-dd
	 * @return
	 */
	public static String getAstro(String birth) {
		if (!isDate(birth)) {
			birth = "0" + birth;
		}
		if (!isDate(birth)) {
			return "";
		}
		int month = Integer.parseInt(birth.substring(birth.indexOf("-") + 1,
				birth.lastIndexOf("-")));
		int day = Integer.parseInt(birth.substring(birth.lastIndexOf("-") + 1));
		String s = "魔羯水瓶双鱼牡羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯";
		int[] arr = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };
		int start = month * 2 - (day < arr[month - 1] ? 2 : 0);
		return s.substring(start, start + 2) + "座";
	}

	/**
	 * 判断日期是否有效,包括闰年的情况
	 * 
	 * @param date
	 *            YYYY-mm-dd
	 * @return
	 */
	public static boolean isDate(String date) {
		StringBuffer reg = new StringBuffer(
				"^((\\d{2}(([68][])|([79][26]))-?((((0?");
		reg.append("[78])|(1[02]))-?((0?[1-9])|([1-2][0-9])|(3[01])))");
		reg.append("|(((0?[])|(11))-?((0?[1-9])|([1-2][0-9])|(30)))|");
		reg.append("(0?2-?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([68][12");
		reg.append("79])|([79][89]))-?((((0?[78])|(1[02]))");
		reg.append("-?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[])|(11))");
		reg.append("-?((0?[1-9])|([1-2][0-9])|(30)))|(0?2-?((0?[");
		reg.append("1-9])|(1[0-9])|(2[0-8]))))))");
		Pattern p = Pattern.compile(reg.toString());
		return p.matcher(date).matches();
	}

	/**
	 * 取得指定日期过 months 月后的日期 (当 months 为负数表示指定月之前);
	 * 
	 * @param date
	 *            日期 为null时表示当天
	 * @param month
	 *            相加(相减)的月数
	 */
	public static Date nextMonth(Date date, int months) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}

	/**
	 * 取得指定日期过 day 天后的日期 (当 day 为负数表示指日期之前);
	 * 
	 * @param date
	 *            日期 为null时表示当天
	 * @param month
	 *            相加(相减)的月数
	 */
	public static Date nextDay(Date date, int day) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.add(Calendar.DAY_OF_YEAR, day);
		return cal.getTime();
	}

	/**
	 * 取得距离今天 day 日的日期
	 * 
	 * @param day
	 * @param format
	 * @return
	 * @author chenyz
	 */
	public static String nextDay(int day, String format) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, day);
		return dateToString(cal.getTime(), format);
	}

	/**
	 * 取得指定日期过 day 周后的日期 (当 day 为负数表示指定月之前)
	 * 
	 * @param date
	 *            日期 为null时表示当天
	 */
	public static Date nextWeek(Date date, int week) {
		Calendar cal = Calendar.getInstance();
		if (date != null) {
			cal.setTime(date);
		}
		cal.add(Calendar.WEEK_OF_MONTH, week);
		return cal.getTime();
	}

	/**
	 * 获取当前的日期(yyyy-MM-dd)
	 */
	public static String currDay() {
		return DateUtil.dateToString(new Date(), DateUtil.LONG_DATE_FORMAT);
	}

	/**
	 * 获取昨天的日期
	 * 
	 * @return
	 */
	public static String befoDay() {
		return befoDay(DateUtil.LONG_DATE_FORMAT);
	}

	/**
	 * 根据时间类型获取昨天的日期
	 * 
	 * @param format
	 * @return
	 * @author chenyz
	 */
	public static String befoDay(String format) {
		return DateUtil.dateToString(DateUtil.nextDay(new Date(), -1), format);
	}

	/**
	 * 获取明天的日期
	 */
	public static String afterDay() {
		return DateUtil.dateToString(DateUtil.nextDay(new Date(), 1),
				DateUtil.LONG_DATE_FORMAT);
	}

	/**
	 * 取得当前时间距离0/1/1的天数
	 * 
	 * @return
	 */
	public static int getDayNum() {
		int daynum = 0;
		GregorianCalendar gd = new GregorianCalendar();
		Date dt = gd.getTime();
		GregorianCalendar gd1 = new GregorianCalendar(0, 1, 1);
		Date dt1 = gd1.getTime();
		daynum = (int) ((dt.getTime() - dt1.getTime()) / (24 * 60 * 60 * 0));
		return daynum;
	}

	/**
	 * getDayNum的逆方法(用于处理Excel取出的日期格式数据等)
	 * 
	 * @param day
	 * @return
	 */
	public static Date getDateByNum(int day) {
		GregorianCalendar gd = new GregorianCalendar(0, 1, 1);
		Date date = gd.getTime();
		date = nextDay(date, day);
		return date;
	}

	/** 针对yyyy-MM-dd HH:mm:ss格式,显示yyyymmdd */
	public static String getYmdDateCN(String datestr) {
		if (datestr == null)
			return "";
		if (datestr.length() < 10)
			return "";
		StringBuffer buf = new StringBuffer();
		buf.append(datestr.substring(0, 4)).append(datestr.substring(5, 7))
				.append(datestr.substring(8, 10));
		return buf.toString();
	}

	/**
	 * 获取本月第一天
	 * 
	 * @param format
	 * @return
	 */
	public static String getFirstDayOfMonth(String format) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		return dateToString(cal.getTime(), format);
	}

	/**
	 * 获取本月最后一天
	 * 
	 * @param format
	 * @return
	 */
	public static String getLastDayOfMonth(String format) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return dateToString(cal.getTime(), format);
	}
	
	/**
	 * 类似vb的CDate函数，自动分析sDate，如格式正常，返回日期，否则报错。
	 * 注意这里只能处理单纯日期，不处理时间，年份正常范围在0-99和1000－9999 仅解析用/-.间隔的日期
	 */
	public static Date toDate(String sDate)  {
		int jj;
		char ss = 0, cc;
		String[] sss = { "-", "/", "." };
		String[] result;
		int kk, mm;
		final String emsg = "非法日期格式！";

		GregorianCalendar cl = null;

		// 检查分隔符
		for (jj = 0; jj < sss.length; jj++) {
			if (sDate.indexOf(sss[jj]) >= 0)
				break;
		}
		int len = sDate.length();
		if(jj < sss.length){
			ss = sss[jj].charAt(0);
		}else if(len!=8 && len!=6){
			throw new RuntimeException(emsg);
		}
		// 检查数字有效性即除了数字和分隔符，不应该再包括其它字符
		for (int i = 0; i < len ; i++) {
			cc = sDate.charAt(i);
			if (cc != ss && (cc < '0' || cc > '9'))
				throw new RuntimeException(emsg);
		}
		
		if((len==8||len==6) && ss == 0){
			if(len==8){
    			jj = Integer.parseInt(sDate.substring(0,4));
        		kk = Integer.parseInt(sDate.substring(4,6));
        		mm = Integer.parseInt(sDate.substring(6,8));
			}else{
				jj = Integer.parseInt(sDate.substring(0,2));
        		kk = Integer.parseInt(sDate.substring(2,4));
        		mm = Integer.parseInt(sDate.substring(4,6));
			}
		}else{
    		ss = sss[jj].charAt(0);
 
    		// 劈开，获取3个数字
    		result = sDate.split("\\"+sss[jj], -1); // 检查全部，包括空的元素，用0会忽略空
    		if (result.length != 3){
    			throw new RuntimeException(emsg);
    		}
    		jj = Integer.parseInt(result[0]);
    		kk = Integer.parseInt(result[1]);
    		mm = Integer.parseInt(result[2]);
		}

		// 判断是否符合一种日期格式
		// 1、y/M/d格式
		if (isValidDate(jj, kk, mm))
			cl = new GregorianCalendar(jj < 30 ? jj + 2000
					: (jj <= 99 ? jj + 1900 : jj), kk - 1, mm);
		else {
			if (mm < 30)
				mm += 2000;
			else if (mm <= 99)
				mm += 1900;
			// 2、M/d/y格式
			if (isValidDate(mm, jj, kk))
				cl = new GregorianCalendar(mm, jj - 1, kk);
			// 3、d/M/y格式
			else if (isValidDate(mm, kk, jj))
				cl = new GregorianCalendar(mm, kk - 1, jj);
			else
				throw new RuntimeException(emsg);
		}
		return cl.getTime();
	}
	/**
	 * 判断年月日是否在正常范围 年份正常范围在0-99和1000－9999
	 */
	public static boolean isValidDate(int year, int month, int day) {
		GregorianCalendar cl;

		if (year < 0 || (year > 99 && (year < 1000 || year > 9999)))
			return false;
		if (year < 30)
			year += 2000;
		else if (year <= 99)
			year += 1900;

		if (month < 1 || month > 12)
			return false;

		cl = new GregorianCalendar(year, month - 1, 1); // 参数月份从0开始所以减一
		if (day < cl.getActualMinimum(Calendar.DAY_OF_MONTH) || day > cl.getActualMaximum(Calendar.DAY_OF_MONTH))
			return false;

		return true;
	}
	
	public static String formatDate(Date date,String format) {
	    return new SimpleDateFormat(format).format(date);
	}	
	
	
	/**
	 * 根据传入日期获取本周最后一日 方法详细说明，包括用途、注意事项、举例说明等。
	 * 
	 * @param current_day
	 * @return
	 * @author qixu
	 * @date 2018-1-13
	 */
	public static final String getLastDayOfWeek(String current_day) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		String returnStrDate = "";
		try {
			date = dateFormat.parse(current_day);
			Calendar cDate = Calendar.getInstance();
			cDate.setFirstDayOfWeek(Calendar.MONDAY);
			cDate.setTime(date);
			Calendar lastDate = Calendar.getInstance();
			lastDate.setFirstDayOfWeek(Calendar.MONDAY);
			lastDate.setTime(date);
			if (cDate.get(Calendar.WEEK_OF_YEAR) == 1 && cDate.get(Calendar.MONTH) == 11) {
				lastDate.set(Calendar.YEAR, cDate.get(Calendar.YEAR) + 1);
			}
			int typeNum = cDate.get(Calendar.WEEK_OF_YEAR);
			lastDate.set(Calendar.WEEK_OF_YEAR, typeNum);
			lastDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			returnStrDate = new SimpleDateFormat("yyyy-MM-dd").format(lastDate.getTime());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return returnStrDate;
	}

	/**
	 * 返回当前是第几季度
	 * 
	 * @return int 当前是第几季度
	 */
	@SuppressWarnings("deprecation")
	public static final int getCurrentSeason() {

		int season = 0;
		Timestamp now = new Timestamp(System.currentTimeMillis());
		int month = now.getMonth();
		if (month >= 1 && month <= Integer.parseInt("3")) {
			season = 1;
		} else if (month >= Integer.parseInt("4") && month <= Integer.parseInt("6")) {
			season = Integer.parseInt("2");
		} else if (month >= Integer.parseInt("7") && month <= Integer.parseInt("9")) {
			season = Integer.parseInt("3");
		} else if (month >= Integer.parseInt("10") && month <= Integer.parseInt("12")) {
			season = Integer.parseInt("4");
		}
		return season;
	}

	/**
	 * 返回当前月是所处季度的第几个月
	 * 
	 * @return int 当前月是所处季度的第几个月
	 */
	public static final int getMonthOfSeason() {

		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		return (month % Integer.parseInt("3") + 1);
	}

	/**
	 * 获取当前日期是本季度的第几个月 方法详细说明，包括用途、注意事项、举例说明等。
	 * 
	 * @param date
	 * @return
	 * @author qixu
	 * @date 2018-12-21
	 */
	public static final int getMonthOfSeason(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		return (month % Integer.parseInt("3") + 1);
	}

	/**
	 * 返回当前年份
	 * 
	 * @return int 年份值
	 */
	public static final int getCurrentYear() {

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		return year;
	}

	/**
	 * 返回当前月份
	 * 
	 * @return int 月份值
	 */
	public static final int getCurrentMonth() {

		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH) + 1;
		return month;
	}

	/**
	 * 返回当前日
	 * 
	 * @return int 当前日
	 */
	public static final int getCurrentDay() {

		Calendar cal = Calendar.getInstance();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	/**
	 * 返回Date型当前时间 2011/7/21 10:20:02
	 * 
	 * @return 当前时间
	 * @throws ParseException 异常
	 */
	public static Date getCurrentTime() {

		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_ONE);
		String systemTime = sdf.format(new Date());
		Date returnDate = null;
		try {
			returnDate = sdf.parse(systemTime);
		} catch (ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return returnDate;
	}

	/**
	 * 返回Timestamp类型的当前日期 2006-07-07
	 * 
	 * @return 当前日期
	 */
	public static Timestamp currentTime() {

		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 获取当前日期的字符串 2006-07-07
	 * 
	 * @return String 当前日期
	 */
	public static String getCurrentDate() {

		Timestamp d = currentTime();
		return d.toString().substring(0, Integer.parseInt("10"));
	}

	/**
	 * 根据指定天数返回指定日期之前或之后的日期
	 * 
	 * @param date 指定日期
	 * @param d    增加或减少的天数,取当前日期之后传入正数，取当前日期之前传入负数
	 * @return Date
	 */
	public static Date getCurrentDateBeforOrAfter(Date date, int d) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, d);
		return cal.getTime();
	}

	/**
	 * 返回指定Date类型日期的字符串
	 * 
	 * @param date 日期
	 * @return String
	 */
	public static String getStringDate(Date date) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	/**
	 * 返回指定Date类型日期的字符串
	 * 
	 * @param date 日期
	 * @return String
	 */
	public static String getStringDateYYYYMMDD(Date date) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(date);
	}

	/**
	 * 把给定的日字符串转换成Date类型
	 * 
	 * @param sdate 日期
	 * @return Date
	 * @throws ParseException 异常
	 */
	public static Date getStringToDate(String sdate) throws ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.parse(sdate);
	}

	/**
	 * 
	 * 把给定的日期字符串转换成Date类型
	 * 
	 * @param sdate  日期
	 * @param format 日期格式
	 * @return Date
	 * @throws ParseException 异常
	 */
	public static Date getStringToDate(String sdate, String dateFormatString) throws ParseException {

		SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
		return dateFormat.parse(sdate);
	}

	/**
	 * 根据给定日期 获取前一天(自然日)
	 * 
	 * @param date 日期
	 * @return Date
	 * @author zhanglq
	 */
	public static Date getPreDate(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}

	/**
	 * 根据指定格式格式化日期
	 * 
	 * @param format 格式
	 * @param date   日期
	 * @return String
	 * @author zhanglq
	 */
	public static String convertDateToString(String format, Date date) {

		String dateStr = null;
		if (date != null) {

			DateFormat df = new SimpleDateFormat(format);
			dateStr = df.format(date);
		}
		return dateStr;
	}

	/**
	 * 获取上个月第一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getFirstDayBM(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);// 当前月-1，即上月
		cal.set(Calendar.DATE, 1);// 上个月第一天
		return cal.getTime();
	}

	/**
	 * 获取指定月第一天
	 * 
	 * @param date   日期
	 * @param amount 指定第几个月
	 * @return Date
	 */
	public static Date getFirstDayBM(Date date, int amount) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, amount);// 指定月
		cal.set(Calendar.DATE, 1);// 第一天
		return cal.getTime();
	}

	/**
	 * 获取上个月最后一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getLastDayBM(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);// 当前月第一天
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	/**
	 * 获取本月最后一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getLastDayByM(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	/**
	 * 获取上个季度的第一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getFirstDayBQ(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		cal.set(Calendar.DATE, 1);// 设置为第一天
		if (month >= 1 && month <= Integer.parseInt("3")) {
			cal.add(Calendar.YEAR, -1);
			cal.set(Calendar.MONTH, Integer.parseInt("9"));
		} else if (month >= Integer.parseInt("4") && month <= Integer.parseInt("6")) {
			cal.set(Calendar.MONTH, 0);
		} else if (month >= Integer.parseInt("7") && month <= Integer.parseInt("9")) {
			cal.set(Calendar.MONTH, Integer.parseInt("3"));
		} else if (month >= Integer.parseInt("10") && month <= Integer.parseInt("12")) {
			cal.set(Calendar.MONTH, Integer.parseInt("6"));
		}
		return cal.getTime();
	}

	/**
	 * 获取上个季度最后一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getLastDayBQ(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		cal.set(Calendar.DATE, 1);// 设置为第一天
		if (month >= 1 && month <= Integer.parseInt("3")) {
			cal.set(Calendar.MONTH, 0);
		} else if (month >= Integer.parseInt("4") && month <= Integer.parseInt("6")) {
			cal.set(Calendar.MONTH, Integer.parseInt("3"));
		} else if (month >= Integer.parseInt("7") && month <= Integer.parseInt("9")) {
			cal.set(Calendar.MONTH, Integer.parseInt("6"));
		} else if (month >= Integer.parseInt("10") && month <= Integer.parseInt("12")) {
			cal.set(Calendar.MONTH, Integer.parseInt("9"));
		}
		cal.add(Calendar.DATE, -1);// 设置为最后一天
		return cal.getTime();
	}

	/**
	 * 获取上半年年第一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getFirstDayBHY(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		cal.set(Calendar.DATE, 1);// 设置为第一天
		if (month <= Integer.parseInt("6")) {
			cal.add(Calendar.YEAR, -1);
			cal.set(Calendar.MONTH, Integer.parseInt("6"));
		} else {
			cal.set(Calendar.MONTH, 0);
		}
		cal.set(Calendar.DATE, 1);
		return cal.getTime();
	}

	/**
	 * 获取上半年年最后一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getLastDayBHY(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		if (month <= Integer.parseInt("6")) {
			cal.add(Calendar.YEAR, -1);
			cal.set(Calendar.MONTH, Integer.parseInt("12"));
		} else {
			cal.set(Calendar.MONTH, Integer.parseInt("6"));
		}
		cal.set(Calendar.DATE, 1);// 设置为第一天
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	/**
	 * 获取上一年第一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getFirstDayBY(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, -1);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DATE, 1);
		return cal.getTime();
	}

	/**
	 * 获取上一年最后一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getLastDayBY(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, 0);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	/**
	 * 获取当前年最后一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getLastDay(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, 1);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, 0);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}


	/**
	 * 功能说明：计算两个日期之间相差的天数
	 * 
	 * @param dDate1 ：较小日期
	 * @param dDate2 ：较大日期
	 * @return 相差天数
	 */
	public static int dateDiff(java.util.Date dDate1, java.util.Date dDate2) {
		if (dDate1 == null || dDate2 == null) {
			return 0;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(LONG_DATE_FORMAT);
		try {
			dDate1 = sdf.parse(sdf.format(dDate1));
			dDate2 = sdf.parse(sdf.format(dDate2));
		} catch (ParseException e) {
			logger.error("计算两日期天数之差出错：", e);
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dDate1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(dDate2);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 功能说明：通过只算年份和月份，获取月份差，不计四舍五入
	 * 
	 * @param dDate1 Date：起始月日期
	 * @param dDate2 Date：终止月日期
	 * @return int：dDate2-dDate1的月份差
	 */
	public static int monthDiff(java.util.Date dDate1, java.util.Date dDate2) {
		int year, month;
		GregorianCalendar cld = new GregorianCalendar();

		cld.setTime(dDate2);
		year = cld.get(Calendar.YEAR);
		month = cld.get(Calendar.MONTH);

		cld.setTime(dDate1);
		year -= cld.get(Calendar.YEAR);
		month -= cld.get(Calendar.MONTH);

		return year * 12 + month;
	}

	/**
	 * 计算两个日期之前的年份差
	 * 
	 * @param date
	 * @param bigDate
	 * @return
	 */
	public static int yearDiff(java.util.Date date, java.util.Date bigDate) {
		GregorianCalendar cld = new GregorianCalendar();
		cld.setTime(bigDate);
		int bigYear = cld.get(Calendar.YEAR);

		cld.setTime(date);
		int year = cld.get(Calendar.YEAR);

		return bigYear - year;
	}

	/**
	 * 计算两个日期相差的月份，第三个参数为false的时候，直接算月份查，不含年份差*12
	 * 
	 * @param date
	 * @param bigDate
	 * @param containsYear
	 * @return
	 */
	public static int monthDiff(java.util.Date date, java.util.Date bigDate, boolean containsYear) {
		if (containsYear) {
			return monthDiff(date, bigDate);
		}
		GregorianCalendar cld = new GregorianCalendar();
		cld.setTime(bigDate);
		int bigMonth = cld.get(Calendar.MONTH);
		cld.setTime(date);
		int month = cld.get(Calendar.MONTH);
		return bigMonth - month;
	}

	/**
	 * 计算两个日期相差的天数，第三个参数为false的时候，直接算天数差。
	 * 
	 * @param date
	 * @param bigDate
	 * @param containsYearAndMonth
	 * @return
	 */
	public static int dateDiff(java.util.Date date, java.util.Date bigDate, boolean containsYearAndMonth) {
		if (containsYearAndMonth) {
			return dateDiff(date, bigDate);
		}
		GregorianCalendar cld = new GregorianCalendar();
		cld.setTime(bigDate);
		int bigD = cld.get(Calendar.DATE);
		cld.setTime(date);
		int d = cld.get(Calendar.DATE);
		return bigD - d;
	}

	/**
	 * 功能说明：判断闰年
	 * 
	 * @param year 传入得年份
	 * @return 是否为闰年;
	 */
	public static boolean isLeapYear(int year) {
		return new GregorianCalendar().isLeapYear(year);
	}

	/**
	 * 功能说明：返回指定日期区间段呢包含2月29日的个数。
	 * 
	 * @param sDate：起始日期（含）
	 * @param eDate：结束日期（含）
	 * @return 2月29日的个数
	 * @throws Exception
	 */
	public static int getLeapYears(Date sDate, Date eDate)  {
		int leapDays = 0;
		Date dDate;
		for (int i = getYear(sDate); i <= getYear(eDate); i++) {
			if (isLeapYear(i)) {
				dDate = toDate(i + "-02-29");
				if (dateDiff(dDate, sDate) <= 0 && dateDiff(dDate, eDate) >= 0) {
					leapDays++;
				}
			}
		}
		return leapDays;
	}

	/**
	 * 功能说明：依据类型返回日期中的元素
	 */
	public static int getDateItems(Date dDate, int field) {
		GregorianCalendar cl = new GregorianCalendar();
		cl.setTime(dDate);

		return cl.get(field);
	}

	/**
	 * 功能说明：返回给定日期为星期几的标识位
	 * 
	 * @param dDate 日期
	 * @return 星期几标识位 2：星期一 3：星期二 4：星期三 5：星期四 6：星期五 7：星期六 1：星期日
	 */
	public static int getWeekDay(Date dDate) {
		return getDateItems(dDate, Calendar.DAY_OF_WEEK);
	}

	/**
	 * 功能说明：返回给定日期当年的第多少天
	 * 
	 * @param dDate 日期
	 * @return int 比如2017-5-26，返回146
	 * 
	 */
	public static int getYearDays(Date dDate) {
		return getDateItems(dDate, Calendar.DAY_OF_YEAR);
	}

	/**
	 * 功能说明：返回给定日期当年天数
	 * 
	 * @param dDate 日期
	 * @return int 年天数；平年365，闰年366。
	 * 
	 */
	public static int getYearDayss(Date dDate) {
		return isLeapYear(getYear(dDate)) ? 366 : 365;
	}

	/**
	 * 取得指定日期的下一个月
	 * 
	 * @param date 指定日期。
	 * @return 指定日期的下一个月
	 */
	public static java.util.Date getNextMonth(java.util.Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.MONTH, 1);
		return gc.getTime();
	}

	/**
	 * 取得指定日期的下一日
	 * 
	 * @param date 指定日期。
	 * @return 指定日期的下一日
	 */
	public static java.util.Date getNextDate(java.util.Date date) {
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		gc.add(Calendar.DATE, 1);
		return gc.getTime();
	}

	/**
	 * 获取月份第一天的日期 例如：date = 2011-1-23 getMonthFirstDay(date) = 2011-1-1
	 */
	public static Date getMonthFirstDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
		calendar.set(year, month, day);
		return calendar.getTime();
	}

	/**
	 * 获取月份最后一天的日期 例如：date = 2011-1-23 getMonthLastDay(date) = 2011-1-31
	 */
	public static Date getMonthLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(year, month, day);
		return calendar.getTime();
	}

	/**
	 * 获取当前月份的所在季度的最后一天
	 * 
	 * @param ywDate 计提日
	 * @param ywDate
	 * @return
	 * @throws ACSFundaccException
	 */
	public static Date getQuarterEndDay(Date date)  {
		Date quarterDay = null;
		int m = getMonth(date);
		int s = (int) Math.ceil((m - 1) / 3) + 1; // 季度
		String year = String.valueOf(getYear(date));
		switch (s) {
		case 1:
			quarterDay = toDate(year + "-03-31");
			break;
		case 2:
			quarterDay = toDate(year + "-06-30");
			break;
		case 3:
			quarterDay = toDate(year + "-09-30");
			break;
		case 4:
			quarterDay = toDate(year + "-12-31");
			break;
		}
		return quarterDay;
	}

	/**
	 * 获取当前月份的所在季度的第一天
	 * 
	 * @param ywDate 计提日
	 * @param ywDate
	 * @return
	 * @throws ACSFundaccException
	 */
	public static Date getQuarterFirstDay(Date date)  {
		Date quarterDay = null;
		int m = getMonth(date);
		int s = (int) Math.ceil((m - 1) / 3) + 1; // 季度
		String year = String.valueOf(getYear(date));
		switch (s) {
		case 1:
			quarterDay = toDate(year + "-01-01");
			break;
		case 2:
			quarterDay = toDate(year + "-04-01");
			break;
		case 3:
			quarterDay = toDate(year + "-07-01");
			break;
		case 4:
			quarterDay = toDate(year + "-10-01");
			break;
		}
		return quarterDay;
	}

	/**
	 * 获取给定日期的当前年份的第一天 例如：date = 2011-1-23 getYearFirstDay(date) = 2011-01-01
	 */
	public static Date getYearFirstDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		return calendar.getTime();
	}

	/**
	 * 获取给定日期的当前年份的最后一天 例如：date = 2011-1-23 getYearLastDay(date) = 2011-12-31
	 */
	public static Date getYearLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		return calendar.getTime();
	}

	/**
	 * 功能说明：返回月末日
	 * 
	 * @param year  年
	 * @param month 月
	 * @return 指定年、月的月末日
	 */
	public static int endOfMonth(int year, int month) {
		return new GregorianCalendar(year, month - 1, 1).getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取永久日期(29991231)
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static Date getForeverDate() throws ParseException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		return sf.parse("29991231");
	}

	/**
	 * 转换为sqldate
	 * 
	 * @param fdate
	 * @return
	 */
	public static java.sql.Date toSqlDate(Date fdate) {
		return new java.sql.Date(fdate.getTime());
	}

	/**
	 * 转换为timestamp
	 * 
	 * @param fdate
	 * @return
	 */
	public static Timestamp toTimeStamp(Date fdate) {
		return new Timestamp(fdate.getTime());
	}

	/**
	 * 功能说明：加减年，根据日期向前或向后推算N年
	 * 
	 * @param dDate 日期
	 * @param years 年份数 正整数或负整数
	 * @return 返回增加年份后的日期对象
	 */
	public static final Date addYear(Date dDate, int years) {
		return addDate(dDate, years, Calendar.YEAR);
	}

	/**
	 * 功能说明：加减月，根据日期向前或向后推算N个月
	 * 
	 * @param dDate  日期
	 * @param months 月份数 正整数或负整数
	 * @return 返回增加月份后的日期对象
	 */
	public static final Date addMonth(Date dDate, int months) {
		return addDate(dDate, months, Calendar.MONTH);
	}

	/**
	 * 
	 * 日期加减方法
	 * 
	 * @param dDate
	 * @param days
	 * @return
	 * @author chenwentao
	 * @date 2015-7-11
	 */
	public static final Date addDay(Date dDate, int days) {
		return addDate(dDate, days, Calendar.DAY_OF_MONTH);
	}

	/**
	 * 功能说明：加减日期
	 * 
	 * @param 日期对象
	 * @param           field指定是年、月、日
	 * @param amount是数量
	 * @return 修改后的日期
	 */
	public static Date addDate(Date dDate, int amount, int field) {
		GregorianCalendar cl = new GregorianCalendar();
		cl.setTime(dDate);
		cl.add(field, amount);

		return cl.getTime();
	}

	/**
	 * 获取某年某月的最后一天
	 * 
	 * @param:@param year
	 * @param:@param month
	 * @param:@return
	 * @return:String
	 */
	public static final String getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// 设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastDayOfMonth = sdf.format(cal.getTime());

		return lastDayOfMonth;
	}

	/**
	 * 获取连天中最大的一天，精确到天
	 * 
	 * @param dDate1 日期1
	 * @param dDate2 日期2
	 * @return
	 * @author linht
	 * @date 2015-11-21
	 */
	public static Date maxDate(Date dDate1, Date dDate2) {
		if (dDate1 == null) {
			return dDate2;
		} else if (dDate2 == null) {
			return dDate1;
		}
		if (dateDiff(dDate1, dDate2) > 0) {
			return dDate2;
		} else {
			return dDate1;
		}
	}

	/**
	 * 获取数组中的最大日期，精确到天
	 * 
	 * @param dates 日期数组
	 * @return
	 * @author linht
	 * @date 2015-11-21
	 */
	public static Date maxDate(Date[] dates) {
		if (dates == null || dates.length == 0) {
			return null;
		}
		if (dates.length == 1) {
			return dates[0];
		}
		Date max = dates[0];
		for (int i = 0; i < dates.length - 1; i++) {
			max = maxDate(max, dates[i + 1]);
		}
		return max;
	}

	/**
	 * 获取连天中最小的一天，精确到天
	 * 
	 * @param dDate1 日期1
	 * @param dDate2 日期2
	 * @return
	 * @author wuyang
	 * @date 2018-9-28
	 */
	public static Date minDate(Date dDate1, Date dDate2) {
		if (dDate1 == null) {
			return dDate2;
		} else if (dDate2 == null) {
			return dDate1;
		}
		if (dateDiff(dDate1, dDate2) < 0) {
			return dDate2;
		} else {
			return dDate1;
		}
	}

	/**
	 * 获取数组中的最小日期，精确到天
	 * 
	 * @param dates 日期数组
	 * @return
	 * @author wuyang
	 * @date 2018-9-28
	 */
	public static Date minDate(Date[] dates) {
		if (dates == null || dates.length == 0) {
			return null;
		}
		if (dates.length == 1) {
			return dates[0];
		}
		Date min = dates[0];
		for (int i = 0; i < dates.length - 1; i++) {
			min = minDate(min, dates[i + 1]);
		}
		return min;
	}

	/**
	 * 获取当前季度最后一天
	 * 
	 * @param date 日期
	 * @return Date
	 */
	public static Date getLastDayOfSeason(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH) + 1;
		cal.set(Calendar.DATE, 1);// 设置为第一天
		if (month >= 1 && month <= Integer.parseInt("3")) {
			cal.set(Calendar.MONTH, 3);
		} else if (month >= Integer.parseInt("4") && month <= Integer.parseInt("6")) {
			cal.set(Calendar.MONTH, Integer.parseInt("6"));
		} else if (month >= Integer.parseInt("7") && month <= Integer.parseInt("9")) {
			cal.set(Calendar.MONTH, Integer.parseInt("9"));
		} else if (month >= Integer.parseInt("10") && month <= Integer.parseInt("12")) {
			cal.set(Calendar.MONTH, Integer.parseInt("0"));
			cal.add(Calendar.YEAR, 1);// 按季最后一个季度，最后一天减前一天是差了一年，需要加回来。
		}
		cal.add(Calendar.DATE, -1);// 设置为最后一天
		return cal.getTime();
	}

	/**
	 * 获取指定日期的指定格式的日期字符串
	 * 
	 * 方法详细说明，包括用途、注意事项、举例说明等。
	 * 
	 * @param date
	 * @param format 例如：yyyy-MM-dd或者yyyy-MM-等形式
	 * @return
	 * @author guochaolong
	 * @date 2016-3-20
	 */
	public static String getYearAndMonth(Date date, String format) {
		String result = "";
		SimpleDateFormat formater = new SimpleDateFormat(format);
		result = formater.format(date);
		return result;
	}

	/**
	 * 
	 * 返回日期所在季度 20160802 add by gcl
	 * 
	 * @return int 当前是第几季度
	 */
	public static final int getSeason(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int currentMonth = c.get(Calendar.MONTH) + 1;
		int currentSeacon = 0;
		if (currentMonth >= 1 && currentMonth <= 3)
			currentSeacon = 1;
		else if (currentMonth >= 4 && currentMonth <= 6)
			currentSeacon = 2;
		else if (currentMonth >= 7 && currentMonth <= 9)
			currentSeacon = 3;
		else if (currentMonth >= 10 && currentMonth <= 12)
			currentSeacon = 4;
		return currentSeacon;
	}

	/**
	 * 获取上一年的日期 20160802 add by gcl
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, -1);
		return cal.getTime();
	}

	/**
	 * 
	 * 获取非标产品30/360的计息区间，计算太麻烦，再此实现规则调用好调整
	 * 
	 * @return
	 * @param offeringDate 非标计息起始日
	 * @param ywDate       业务日期
	 * @author fkz
	 * @
	 * @date 2016-10-19
	 */
	public static Map<String, Date> getIntervalOfNStandard(Date offeringDate, Date ywDate)  {
		Map<String, Date> rsMap = new HashMap<String, Date>();
		int dateDiff = dateDiff(offeringDate, toDate(
				getLastDayOfMonth(getYear(offeringDate), getMonth(offeringDate))));
		int offeringDay = getDay(offeringDate);
		if (getDay(ywDate) >= offeringDay || (dateDiff(ywDate,
				toDate(getLastDayOfMonth(getYear(ywDate), 2)))) == 0
				&& offeringDay > getDay(ywDate)) {
			ywDate = toDate(
					getYear(ywDate) + "-" + getMonth(addMonth(ywDate, 1)) + "-01");
		}
		if (dateDiff == 0) {
			rsMap.put("intervalBegin",
					toDate(getLastDayOfMonth(getYear(addMonth(ywDate, -1)),
							getMonth(addMonth(ywDate, -1)))));
			rsMap.put("intervalEnd",
					addDay(
							toDate(
									getLastDayOfMonth(getYear(ywDate), getMonth(ywDate))),
							-1));
		} else {
			// 当前月天数
			int currentDays = DateUtil.getDaysOfMonth(getYear(ywDate), getMonth(ywDate));
			// 上月天数
			int preDays = DateUtil.getDaysOfMonth(getYear(addMonth(ywDate, -1)),
					getMonth(addMonth(ywDate, -1)));

			if (offeringDay > preDays) {
				rsMap.put("intervalBegin",
						toDate(getLastDayOfMonth(getYear(addMonth(ywDate, -1)),
								getMonth(addMonth(ywDate, -1)))));
			} else {
				rsMap.put("intervalBegin", toDate(getYear(addMonth(ywDate, -1)) + "-"
						+ getMonth(addMonth(ywDate, -1)) + "-" + offeringDay));
			}

			if (offeringDay > currentDays) {
				rsMap.put("intervalEnd",
						addDay(toDate(
								getLastDayOfMonth(getYear(ywDate), getMonth(ywDate))),
								-1));
			} else {
				rsMap.put("intervalEnd",
						addDay(
								toDate(getYear(rsMap.get("intervalBegin")) + "-"
										+ (getMonth(rsMap.get("intervalBegin")) + 1) + "-" + offeringDay),
								-1));
			}
		}

		// 判断日期是否在这个区间
//		if (dateDiff(rsMap.get("intervalBegin"), ywDate) >= 0) {
//
//		}
		return rsMap;
	}

	/**
	 * 获取债券期间剩余天数中所含闰年的个数，如果业务日期在2-29之后，或者止息日在2-29之前，则不统计业务日期所在闰年和止息日所在闰年
	 * 
	 * @param date1 业务日期
	 * @param date2 截止日期
	 * @param zqdm  债券代码，便于跟踪
	 * @return
	 * @author yds
	 * @throws ParseException
	 * @date 2017-3-6
	 */
	public static int getRngs(Date busDate, Date endDate, String zqdm) throws ParseException {
		int sum = 0;
		// 获取当年年份
		int curyear = getYear(busDate);
		// 如果业务日期<业务年份2-29,sum+1;
		if (366 == getCurYearDays(curyear)
				&& dateDiff(busDate, getStringToDate(curyear + "-02-29")) > 0) {
			sum = sum + 1;
		}
		// 获取截止年份
		int endyear = getYear(endDate);
		// 循环处理，当遇到年天数是366的sum+1
		for (int i = curyear + 1; i < endyear; i++) {
			if (366 == getCurYearDays(i)) {
				sum = sum + 1;
			}
		}
		// 如果计息截止日>截止年份2-29，sum+1
		if (366 == getCurYearDays(endyear)
				&& dateDiff(endDate, getStringToDate(endyear + "-02-29")) < 0) {
			sum = sum + 1;
		}
		return sum;
	}

	/**
	 * 功能说明：返回给定日期当年天数
	 * 
	 * @param year 传入年份
	 * @return int 年天数；平年365，闰年366。
	 * 
	 */
	public static int getCurYearDays(int year) {
		return new GregorianCalendar().isLeapYear(year) ? 366 : 365;
	}

	/**
	 * 
	 * @author LiKai
	 * @version 1.0.0,2018-4-24
	 * @since 1.0.0,2018
	 */

	// 日期转化为大小写
	public static String dateToUpper(Date date) {

		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		int year = ca.get(Calendar.YEAR);
		int month = ca.get(Calendar.MONTH) + 1;
		int day = ca.get(Calendar.DAY_OF_MONTH);
		return numToUpper(year) + "年" + monthToUppder(month) + "月" + dayToUppder(day) + "日";
	}

	/**
	 * 
	 * @author LiKai
	 * @version 1.0.0,2018-4-24
	 * @since 1.0.0,2018
	 */
	// 将数字转化为大写
	public static String numToUpper(int num) {

		// String u[] = {"零","壹","贰","叁","肆","伍","陆","柒","捌","玖"};
		String u[] = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
		char[] str = String.valueOf(num).toCharArray();
		StringBuffer rstr = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			rstr.append(u[Integer.parseInt(String.valueOf(str[i]))]);
		}
		return rstr.toString();
	}

	/**
	 * 
	 * @author LiKai
	 * @version 1.0.0,2018-4-24
	 * @since 1.0.0,2018
	 */
	// 月转化为大写
	public static String monthToUppder(int month) {

		if (month < 10) {
			return numToUpper(month);
		} else if (month == 10) {
			return "十";
		} else {
			return "十" + numToUpper(month - 10);
		}
	}

	/**
	 * 
	 * @author LiKai
	 * @version 1.0.0,2018-4-24
	 * @since 1.0.0,2018
	 */
	// 日转化为大写
	public static String dayToUppder(int day) {

		if (day < 20) {
			return monthToUppder(day);
		} else {
			char[] str = String.valueOf(day).toCharArray();
			if (str[1] == '0') {
				return numToUpper(Integer.parseInt(String.valueOf(str[0]))) + "十";
			} else {
				return numToUpper(Integer.parseInt(String.valueOf(str[0]))) + "十"
						+ numToUpper(Integer.parseInt(String.valueOf(str[1])));
			}
		}
	}

	/**
	 * 计算两个日期之间的差值
	 * 
	 * @param date1 较小日期
	 * @param date2 较大日期
	 * @author lianghongwu
	 */
	public static long[] getDiffTime(Date date1, Date date2) {
		long l = date2.getTime() - date1.getTime();
		long day = l / (24 * 60 * 60 * 1000);
		long hour = l / (60 * 60 * 1000) - day * 24;
		long min = (l / (60 * 1000) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long[] arr = new long[4];
		arr[0] = day;
		arr[1] = hour;
		arr[2] = min;
		arr[3] = s;
		return arr;
	}

	/**
	 * 通过一个原时区日期时间，获取另外一个指定时区的日期时间
	 * 
	 * @param date         原时区 日期
	 * @param format       格式化
	 * @param sourceString 原时区
	 * @param targetString 目标时区
	 * @return 目标时区 时间
	 * 
	 * @author twh 2018-07-06
	 */
	public static String getDateByTimeZone(Date date, String format, String sourceString, String targetString) {

		DateFormat formatter = new SimpleDateFormat(format);

		// 原时区
		TimeZone sourceTimeZone = TimeZone.getTimeZone(sourceString);
		// 目标时区
		TimeZone targetTimeZone = TimeZone.getTimeZone(targetString);

		Long targetTime = date.getTime() - sourceTimeZone.getRawOffset() + targetTimeZone.getRawOffset();

		Date date2 = new Date(targetTime);

		return formatter.format(date2);

	}

	/**
	 * 获取半年最后一天
	 * 
	 * @param ywDate 日期
	 * @param ywDate
	 * @return
	 * @throws SOFAException
	 * @throws Exception
	 */
	public static Date getHalfLastDay(Date date) {
		Date halfDay = null;
		int m = DateUtil.getMonth(date);
		int s = (m - 1) / 6 + 1; // 季度
		String year = String.valueOf(DateUtil.getYear(date));
		if (s == 1) {
			halfDay = DateUtil.toDate(year + "-06-30");
		} else if (s == 2) {
			halfDay = DateUtil.toDate(year + "-12-31");
		}
		return halfDay;
	}

	/**
	 * 
	 * String类型转化为java.sql.Date类型
	 */
	public static java.sql.Date toSqlDate(String dDate, String format) {
		return toSqlDate(dDate, format, null);
	}

	/**
	 * 
	 * String类型转化为java.sql.Date类型
	 */
	public static java.sql.Date toSqlDate(String dDate, String format, Locale local) {
		GregorianCalendar cl = new GregorianCalendar();
		try {
			if (local == null) {
				cl.setTime(new SimpleDateFormat(format).parse(dDate));
			} else {
				cl.setTime(new SimpleDateFormat(format, local).parse(dDate));
			}
			// 年份要控制一下
			int year = cl.get(Calendar.YEAR);
			if (year < 1000 || year > 9999)
				throw new RuntimeException("BASE_991P \n\t年份必须在1000－9999之间");
			return new java.sql.Date(cl.getTime().getTime());
		} catch (ParseException pe) {
			throw new RuntimeException("BASE_991P");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static void main( String[] args ) throws Exception {		
		System.out.println("---"+DateUtil.toDate("2000-10-11"));
		System.out.println("---"+DateUtil.toDate("20001011"));
		System.out.println("---"+DateUtil.toDate("001011"));
		System.out.println("---"+DateUtil.toDate("2000.10.11"));
		System.out.println("---"+DateUtil.toDate("2000/10/11"));
		System.out.println("---"+DateUtil.toDate("2000/1/1"));
		System.out.println("---"+DateUtil.toDate("2000-1-1"));
		System.out.println("---"+DateUtil.toDate("00-10-11"));
		System.out.println(timeSub("2017-08-22 22:33:30", "2017-08-21 22:33:30"));
	}
		
}
