package com.csii.pp.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
//import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csii.pe.common.util.CsiiUtils;

/**
 * author X
 * 常用的关于日期和字符串处理的工具类
 */
public class MiscUtil {

	private static Logger logger = LoggerFactory.getLogger(MiscUtil.class);

	/**
	 * 默认日期模式:yyyyMMdd
	 */
	public static final String DEFAULT_DATE_PATTERN = "yyyyMMdd";
	/**
	 * 默认日期时间模式:yyyyMMddHHmmss
	 */
	public static final String DEFAULT_TIME_PATTERN = "yyyyMMddHHmmss";
	
	/**
	 * 默认日期时间模式:yyyy-MM-dd HH:mm:ss
	 */
	public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 默认日期时间模式:yyyy-MM-dd HH:mm:ss
	 */
	public static final String DAY_PATTERN = "yyyy-MM-dd";

	/**
	 * 根据日期-时间模式获取对应的格式化和解析日期的具体类
	 * 
	 * @param pattern 日期-时间模式
	 * @return
	 */
	public static DateFormat getDateFormat(String pattern) {
		return new SimpleDateFormat(pattern);
	}

	public static String getLocalHostName() {
		try {
			InetAddress inetaddress = InetAddress.getLocalHost();
			return inetaddress.getHostName();
		} catch (UnknownHostException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 获取一个对象的string形式并去除空格
	 */
	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	public static java.sql.Date getDate() {
		return new java.sql.Date(System.currentTimeMillis());
	}

	public static Timestamp getCurrentDateTime() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static String toStringAndTrim(Object object) {
		if (object == null) {
			return "";
		} else {
			return object.toString().trim();
		}
	}

	/**
	 * 
	 * 比较两个对象是否相等
	 * 
	 * @param firstStr
	 * 
	 * @param secondStr
	 * 
	 * @return
	 * 
	 */
	public static boolean trimAndEquals(Object firstStr, Object secondStr) {
		if ((firstStr == null) && (secondStr == null)) {
			return true;
		} else if ((firstStr == null) || (secondStr == null)) {
			return false;
		} else {
			return toStringAndTrim(firstStr).equals(toStringAndTrim(secondStr));
		}
	}

	/**
	 * 判断输入的字符串是否是空
	 * 
	 * @param inStr 输入字符串
	 * 
	 * @return
	 * 
	 */
	public static boolean isNullOrEmpty(String inStr) {
		return ((inStr == null) || (inStr.trim().length() == 0));
	}

	/**
	 * 判断输入的字符串是否是空
	 * 
	 * @param object 输入字符串
	 * 
	 * @return
	 * 
	 */
	public static boolean isNullOrEmpty(Object object) {
		return (object == null) || (object.toString().trim().length() == 0);
	}

	/***
	 * 将字符串转化为Date
	 * 字符串格式 yyyymmdd
	 * 
	 * @param date
	 * @return
	 */
	public static Date calStringToDate(Object date) {
		String dateStr = MiscUtil.toStringAndTrim(date);

		if (MiscUtil.isNullOrEmpty(dateStr)) {
			return null;
		}

		dateStr = dateStr.replaceAll("-", "");

		return new Date(CsiiUtils.bocmDateToCal(dateStr).getTimeInMillis());
	}

	public static Date calStringToDateTime(String datetime) {
		String dateStr = MiscUtil.toStringAndTrim(datetime);

		if (MiscUtil.isNullOrEmpty(dateStr)) {
			return null;
		}

		dateStr = dateStr.replaceAll("-", "");

		return new Date(convertStrToCal(dateStr).getTimeInMillis());
	}

	public static Timestamp calStringToTimestamp(String datetime) {
		String dateStr = MiscUtil.toStringAndTrim(datetime);

		if (MiscUtil.isNullOrEmpty(dateStr)) {
			return null;
		}

		dateStr = dateStr.replaceAll("-", "");

		return new Timestamp(convertStrToCal(dateStr).getTimeInMillis());
	}

	private static Calendar convertStrToCal(String datetime) {
		int year = Integer.parseInt(datetime.substring(0, 4));
		int month = Integer.parseInt(datetime.substring(4, 6)) - 1;
		int date = Integer.parseInt(datetime.substring(6, 8));
		int hour = Integer.parseInt(datetime.substring(8, 10));
		int min = Integer.parseInt(datetime.substring(10, 12));
		int sec = Integer.parseInt(datetime.substring(12, 14));

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month, date, hour, min, sec);
		return calendar;
	}

	public static Date parseDate(Object date) {
		String dateStr = MiscUtil.toStringAndTrim(date);

		if (MiscUtil.isNullOrEmpty(dateStr)) {
			return null;
		}

		dateStr = dateStr.replaceAll("-", "");

		try {

			return new Date(getDateFormat(DEFAULT_DATE_PATTERN).parse(dateStr).getTime());

		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}

	}

	public static Date putStringToDate(Object date) {
		String dateStr = MiscUtil.toStringAndTrim(date);

		if (MiscUtil.isNullOrEmpty(dateStr)) {
			return null;
		}

		dateStr = dateStr.replaceAll("-", "");

		try {
			SimpleDateFormat DATE_FORMATE = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			return new Date(DATE_FORMATE.parse(dateStr).getTime());

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static java.util.Date parseString2Date(Object date) {
		String dateStr = MiscUtil.toStringAndTrim(date);

		if (MiscUtil.isNullOrEmpty(dateStr)) {
			return null;
		}

		dateStr = dateStr.replaceAll("-", "");

		try {

			return new java.util.Date(getDateFormat(DEFAULT_DATE_PATTERN).parse(dateStr).getTime());

		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}

	}

	/**
	 * 以输入日期为准，向前滚动需要的月份后返回日期的格式字符串 返回日期格式 yyyyMMdd
	 *
	 * @param beginDate:需要滚动的基准日期格式为yyyyMMdd
	 * @param months:向前滚动的月份
	 *
	 */
	public static String rolMonth(String beginDate, int months) {

		if (isNullOrEmpty(beginDate)) {
			beginDate = CsiiUtils.getCurrentDate();
		}
		Calendar ca = CsiiUtils.bocmDateToCal(beginDate);
		ca.set(Calendar.MONTH, (ca.get(Calendar.MONTH) - months));
		return CsiiUtils.calToBocmDate(ca);

	}

	public static Date rolMonth(Date inputDate, int months) {
		String ret = rolMonth(getDateFormat(DEFAULT_DATE_PATTERN).format(inputDate), months);
		return new Date(CsiiUtils.bocmDateToCal(ret).getTimeInMillis());
	}

	/**
	 * 以输入时间为准，向前滚动需要的天数后返回日期的格式字符串 返回日期格式 yyyyMMdd
	 * 
	 * @param days
	 *            :向前滚动的天数
	 * @param inputDate
	 *            :需要滚动的基准日期格式为yyyyMMdd
	 * 
	 */
	public static String rolDate(String inputDate, long days) {
		java.util.Date inday = CsiiUtils.bocmDateToCal(inputDate).getTime();
		long l = inday.getTime();
		long rol = l - (days * 24 * 60 * 60 * 1000);
		java.util.Date rolDay = new java.util.Date(rol);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(rolDay);
		int i = calendar.get(Calendar.YEAR);
		int j = calendar.get(Calendar.MONTH) + 1;
		int k = calendar.get(Calendar.DATE);
		return "" + i + (j >= 10 ? "" + j : "0" + j) + (k >= 10 ? "" + k : "0" + k);
	}

	public static Date rolDate(Date inputDate, long days) {
		String ret = rolDate(getDateFormat(DEFAULT_DATE_PATTERN).format(inputDate), days);
		return new Date(CsiiUtils.bocmDateToCal(ret).getTimeInMillis());
	}

	// public static boolean isPeriodDay(Date date, String periodType) {
	//
	// return isPeriodDay(DATE_FORMATE.format(date), periodType);
	//
	// }

	/**
	 * 以输入时间为准，判断该日期是否是星期日
	 * 
	 * @param date:日期
	 * 
	 */
	public static boolean isWeekDay(String date) {
		Calendar tmpCal = CsiiUtils.bocmDateToCal(date);
		return tmpCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	public static boolean isWeekDay(Date date) {
		return isWeekDay(getDateFormat(DEFAULT_DATE_PATTERN).format(date));
	}

	/**
	 * 以输入时间为准，判断该日期是每半月的末
	 * 
	 * @param date:日期
	 * 
	 */
	public static boolean isHalfMonthEnd(String date) {

		Calendar tmpCal = CsiiUtils.bocmDateToCal(date);

		return isMonthEnd(date) || (tmpCal.get(Calendar.DAY_OF_MONTH) == 15);

	}

	public static boolean isHalfMonthEnd(Date date) {
		return isHalfMonthEnd(getDateFormat(DEFAULT_DATE_PATTERN).format(date));
	}

	/**
	 * 以输入时间为准，判断该日期是每个月的月末
	 * 
	 * @param date:日期
	 * 
	 */
	public static boolean isMonthEnd(String date) {
		Calendar tmpCal = CsiiUtils.bocmDateToCal(date);
		return tmpCal.get(Calendar.DAY_OF_MONTH) == tmpCal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static boolean isMonthEnd(Date date) {
		return isMonthEnd(getDateFormat(DEFAULT_DATE_PATTERN).format(date));
	}

	/**
	 * 以输入时间为准，判断该日期是每个季的季末
	 * 
	 * @param date:日期
	 * 
	 */
	public static boolean isSeasonEnd(String date) {

		if (isMonthEnd(date)) {
			Calendar tmpCal = CsiiUtils.bocmDateToCal(date);
			int month = tmpCal.get(Calendar.MONTH);
			if ((Calendar.MARCH == month) || (Calendar.JUNE == month) || (Calendar.SEPTEMBER == month) || (Calendar.DECEMBER == month)) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}

	}

	public static boolean isSeasonEnd(Date date) {

		return isSeasonEnd(getDateFormat(DEFAULT_DATE_PATTERN).format(date));

	}

	/**
	 * 以输入时间为准，判断该日期是每半年的结尾
	 * 
	 * @param date:日期
	 * 
	 */
	public static boolean isHalfYearEnd(String date) {

		if (isMonthEnd(date)) {
			Calendar tmpCal = CsiiUtils.bocmDateToCal(date);
			int month = tmpCal.get(Calendar.MONTH);
			if ((Calendar.JUNE == month) || (Calendar.DECEMBER == month)) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}

	}

	public static boolean isHalfYearEnd(Date date) {
		return isHalfYearEnd(getDateFormat(DEFAULT_DATE_PATTERN).format(date));

	}

	/**
	 * 以输入时间为准，判断该日期是每年的年末
	 * 
	 * @param date:日期
	 * 
	 */
	public static boolean isYearEnd(String date) {
		if (isMonthEnd(date)) {
			Calendar tmpCal = CsiiUtils.bocmDateToCal(date);
			int month = tmpCal.get(Calendar.MONTH);
			if (Calendar.DECEMBER == month) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	public static boolean isYearEnd(Date date) {
		return isYearEnd(getDateFormat(DEFAULT_DATE_PATTERN).format(date));
	}

	/**
	 * 以输入周期类型为准，判断该类型是否是合法的手续费周期
	 * 
	 * @param period:周期类型
	 * 
	 */
	public static boolean isValidFeePeriod(String period) {
		if (MiscUtil.isNullOrEmpty(period)) {
			return false;
		}
		return true;
	}

	/**
	 * 以指定分隔符拆分输入的字符串并根据传入的清楚标志判断是否清除空格
	 * 
	 * @param toSplit:待拆分的字符串
	 * @param delimiter:分隔符
	 * @param trim:是否需要清除空格的标志
	 * 
	 */
	public static String[] split(String toSplit, char delimiter, boolean trim) {
		if (toSplit == null) {
			return null;
		}
		int len = toSplit.length();
		if (len == 0) {
			return null;
		}
		List list = new ArrayList();
		int i = 0, start = 0;
		boolean match = false;
		while (i < len) {

			if (toSplit.charAt(i) == delimiter) {
				if (match) {
					if (trim) {
						list.add(toStringAndTrim(toSplit.substring(start, i)));
					} else {
						list.add(toSplit.substring(start, i));
					}
				}
				start = ++i;
				match = true;
				continue;
			}
			match = true;
			i++;
		}
		if (match && (toSplit.charAt(len - 1) != '|')) {
			if (trim) {
				list.add(toStringAndTrim(toSplit.substring(start, i)));
			} else {
				list.add(toSplit.substring(start, i));
			}
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * 以指定分隔符拆分输入的字符串并清除空格
	 * 
	 * @param toSplit:待拆分的字符串
	 * @param delimiter:分隔符
	 * 
	 */
	public static String[] split(String toSplit, char delimiter) {

		return split(toSplit, delimiter, true);
	}

	/**
	 * 把二进制的串转化为十六进制的字符串
	 * Creation date: (00-6-9 17:06:35)
	 * 
	 * @return java.lang.String
	 * @param inbuf byte[]
	 */
	public static String byteToHex(byte[] inbuf) {

		int i;
		String byteStr;
		StringBuffer strBuf = new StringBuffer();

		for (i = 0; i < inbuf.length; i++) {

			byteStr = Integer.toHexString(inbuf[i] & 0x00ff);
			if (byteStr.length() != 2) {
				strBuf.append('0').append(byteStr);
			} else {
				strBuf.append(byteStr);
			}

		}

		return new String(strBuf);
	}

	/**
	 * 将十六进制的字符串转化为二进制的串
	 * Creation date: (00-6-9 17:06:35)
	 * 
	 * @return java.lang.String
	 * @param inbuf byte[]
	 */
	public static byte[] hexToByte(String inbuf) {
		int len = inbuf.length() / 2;
		byte outbuf[] = new byte[len];

		for (int i = 0; i < len; i++) {
			String tmpbuf = inbuf.substring(i * 2, (i * 2) + 2);
			outbuf[i] = (byte) Integer.parseInt(tmpbuf, 16);
		}
		return outbuf;
	}

	/**
	 * 将日期格式化为字符串 yyyyMMdd
	 * 
	 * @param date
	 * @return
	 * @modified by x at 2009-10-13
	 */
	public static String dateToString(java.util.Date date) {
		if (date == null) {
			return "";
		}
		return getDateFormat(DEFAULT_DATE_PATTERN).format(date);
	}
	
	/**
	 * 将日期格式化为字符串 yyyyMMdd
	 * 
	 * @param date
	 * @return
	 * @modified by x at 2009-10-13
	 */
	public static String dateToDayString(java.util.Date date) {
		if (date == null) {
			return "";
		}
		return getDateFormat(DAY_PATTERN).format(date);
	}

	/**
	 * 将日期格式化为字符串 yyyyMMddhhmmss
	 * 
	 * @param date
	 * @return
	 */
	public static String timeToString(Date date) {
		if (date == null) {
			return "";
		}
		return getDateFormat(DEFAULT_TIME_PATTERN).format(date);
	}
	
	public static String timeToString1(Date date) {
		if (date == null) {
			return "";
		}
		return getDateFormat(TIME_PATTERN).format(date);
	}

	/**
	 * 将时间戳格式化为字符串 yyyyMMddhhmmss
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String timeToString(Timestamp timestamp) {

		if (timestamp == null) {
			return "";
		}
		Date dateTime = new Date(timestamp.getTime());
		return timeToString(dateTime);
	}
	
	public static String timeToString1(Timestamp timestamp) {

		if (timestamp == null) {
			return "";
		}
		Date dateTime = new Date(timestamp.getTime());
		return timeToString1(dateTime);
	}

	/**
	 * 
	 * 将时间(包括带时间戳格式的)取出年月日，
	 * 不足10位则转换成10位格式，再转换成Date类型
	 * 
	 */
	public static Date to10LengthToDate(Object obj) {
		String date = MiscUtil.toStringAndTrim(obj);

		if (isNullOrEmpty(obj)) {
			// return calStringToDate("1899-01-01");
			return null;
		}

		date = date.substring(0, date.indexOf(" "));

		String yyyy = date.substring(0, date.indexOf("-"));
		String MM = date.substring(date.indexOf("-") + 1, date.lastIndexOf("-"));
		if (MM.length() < 2) {
			MM = "0" + MM;
		}
		String dd = date.substring(date.lastIndexOf("-") + 1, date.length());
		if (dd.length() < 2) {
			dd = "0" + dd;
		}

		date = yyyy + "-" + MM + "-" + dd;

		return calStringToDate(date);
	}

	/**
	 * 取报文长度算法，转换成4位数字，左补0
	 * 
	 * @param len
	 * @return
	 */
	public static String getNumstr(int len) {
		StringBuffer length = new StringBuffer();
		String temp = String.valueOf(len);
		for (int i = 0; i < (4 - temp.length()); i++) {
			length.append("0");
		}
		length.append(String.valueOf(len));
		return length.toString();
	}

	public static String trimDateSeparator(Object dateTime) {
		return dateTime.toString().replaceAll(" ", "").replaceAll(":", "");
	}

	/***
	 * Timestamp、Date类型的时间转换
	 * 字符串格式 yyyyMMdd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String formatString(Object date) {

		try {
			DateFormat df = getDateFormat("yyyyMMddHHmmss");
			return df.format(date);
		} catch (Exception e) {
			// e.printStackTrace();
			return "";
		}
	}

	public static BigDecimal getPlainAmount(String plain) {
		if (plain == null) {
			return null;
		}

		String[] plainArray = plain.split("[|]");

		if (plainArray == null) {
			return null;
		}
		for (String transAmount : plainArray) {
			if ("TranAmt".equals(transAmount.split("=")[0])) {
				if (transAmount.split("=")[1] != null) {
					return new BigDecimal(transAmount.split("=")[1]);
				}
			}
		}
		return null;
	}

	public static Map parseStringToMap(String data, String token) {
		String PROPERTY_DELIMER = "=";
		String name = null;
		String value = null;
		int pos = 0;

		if (data == null) {
			return null;
		}

		StringTokenizer tokenizer = new StringTokenizer(data, token);
		Map props = new HashMap();

		if (tokenizer.countTokens() == 0) {
			return null;
		}

		while (tokenizer.hasMoreTokens()) {
			String element = tokenizer.nextToken();

			pos = element.indexOf(PROPERTY_DELIMER);
			if (pos != -1) {
				name = element.substring(0, pos);
				value = element.substring(pos + 1);
				name = URLDecoder.decode(name);
				value = URLDecoder.decode(value);
				props.put(name, value);
			}
		}
		return props;
	}

	/***
	 * 取当前的系统日期
	 * 字符串格式 yyyyMMdd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentTime() {

		SimpleDateFormat DATE_FORMATE = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		return DATE_FORMATE.format(new Date(System.currentTimeMillis()));

	}

	/***
	 * 取当前的系统日期
	 * 字符串格式 yyyyMMddHHmmss
	 * 
	 * @return
	 */
	public static String getCurrentTimeString() {

		return getDateFormat(DEFAULT_TIME_PATTERN).format(new Date(System.currentTimeMillis()));

	}

	/**
	 * 检查字符串中是否包含指定的字符串。如果字符串为<code>null</code>，将返回<code>false</code>。
	 * 
	 * <pre>
	 * StringUtil.contains(null, *)     = false
	 * StringUtil.contains(*, null)     = false
	 * StringUtil.contains("", "")      = true
	 * StringUtil.contains("abc", "")   = true
	 * StringUtil.contains("abc", "a")  = true
	 * StringUtil.contains("abc", "z")  = false
	 * </pre>
	 *
	 * @param str 要扫描的字符串
	 * @param searchStr 要查找的字符串
	 *
	 * @return 如果找到，则返回<code>true</code>
	 */
	public static boolean contains(String str, String searchStr) {
		if ((str == null) || (searchStr == null)) {
			return false;
		}

		return str.indexOf(searchStr) >= 0;
	}

	/**
	 * 根据结算周期计算出具体结算日期
	 * 
	 * @param period
	 * @param date
	 * @return
	 */
	public static Date getSettlementDate(String period, Date date) {
		if (isNullOrEmpty(period)) {
			return date;
		}
		switch (Integer.parseInt(period)) {
		case 0:// 实时
			return date;
		case 1:// 日
			return date;
		case 2:// 周
			return MiscUtil.getWeekEnd(date);
		case 3:// 半月
			return MiscUtil.getHalfMonthDate(date);
		case 4:// 月
			return MiscUtil.getMonthEnd(date);
		case 5:// 季
			return MiscUtil.getSeasonEnd(date);
		case 6:// 半年
			return MiscUtil.getHalfYearEnd(date);
		case 7:// 年
			return MiscUtil.getYearEnd(date);
		default:
			return date;
		}

	}

	/**
	 * 获取这个时间的年末
	 * 
	 * @param date
	 * @return
	 */
	public static Date getYearEnd(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		cal.add(Calendar.MONTH, 11 - month);
		return calMonthEnd(cal);
	}

	/**
	 * 获取这个时间半年末
	 * 
	 * @param date
	 * @return
	 */
	public static Date getHalfYearEnd(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		if (month < 6) {
			cal.add(Calendar.MONTH, 5 - month);
		} else {
			cal.add(Calendar.MONTH, 11 - month);
		}
		return calMonthEnd(cal);

	}

	/**
	 * 获取这个时间的季末 第一季度：1-3；第二季度：4-6；第三季度：7-9；第四季度：10-23；
	 * 
	 * @param date
	 * @return
	 */
	public static Date getSeasonEnd(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int month = cal.get(Calendar.MONTH);
		if (month < 3) {
			cal.add(Calendar.MONTH, 2 - month);
		} else if (month < 6) {
			cal.add(Calendar.MONTH, 5 - month);
		} else if (month < 9) {
			cal.add(Calendar.MONTH, 8 - month);
		} else {
			cal.add(Calendar.MONTH, 11 - month);
		}
		return calMonthEnd(cal);
	}

	private static Date calMonthEnd(Calendar cal) {
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.DAY_OF_YEAR, -1);
		return cal.getTime();
	}

	/**
	 * 获取这个时间的月末
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonthEnd(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return calMonthEnd(cal);
	}

	/**
	 * 获取这个时间的半月日期
	 * 
	 * @param date
	 * @return
	 */

	public static java.util.Date getHalfMonthDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, 15);
		if (date.after(cal.getTime())) {
			return MiscUtil.getMonthEnd(date);
		}
		return cal.getTime();
	}

	/**
	 * 获取这个时间的周日, 按中国星期的顺序：星期一,星期二,星期三,星期四,星期五,星期六,星期日
	 * 
	 * @param date
	 * @return
	 */
	public static Date getWeekEnd(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK);
		if (w == 1) {
			return date;
		} else {
			cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + (8 - w));
			return cal.getTime();
		}
	}

	/**
	 * 获取这个时间标准格式
	 * 
	 * @param datetime
	 * @return
	 */
	public static String getDateTime(String datetime) {
		if (isNullOrEmpty(datetime) || (datetime.trim().length() != 6)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();

		sb.append(" ").append(datetime.subSequence(0, 2)).append(":").append(datetime.subSequence(2, 4)).append(":").append(datetime.subSequence(4, 6));
		return sb.toString();
	}

	public static BigDecimal getAmount(String amount) {
		if (isNullOrEmpty(amount)) {
			return new BigDecimal("0");
		}
		return new BigDecimal(amount);
	}

	public static Date calStringToDateTimes(String datetime) {
		String dateStr = MiscUtil.toStringAndTrim(datetime);

		if (MiscUtil.isNullOrEmpty(dateStr)) {
			return null;
		}

		dateStr = dateStr.replaceAll("-", "");
		dateStr = dateStr.replaceAll(" ", "");
		dateStr = dateStr.replaceAll(":", "");

		return new Date(convertStrToCal(dateStr).getTimeInMillis());
	}

	/**
	 * 根据渠道号获取相应的付款帐户类型
	 * 
	 * @param channelId 渠道号
	 * @return
	 */

	/***
	 * 将Date转化为字符串 字符串格式 yyyymmdd
	 * 
	 * @param date
	 * @return
	 * @author WZJ
	 */
	public static String calDateToString(Object date) {
		String dateStr = MiscUtil.toStringAndTrim(date);

		if (MiscUtil.isNullOrEmpty(dateStr)) {
			return null;
		}

		dateStr = dateStr.replaceAll("-", "");

		return new SimpleDateFormat("yyyyMMdd").format(new Date(CsiiUtils.bocmDateToCal(dateStr).getTimeInMillis()));
	}

	public static String MultiplyHundred(BigDecimal amount) {
		if (amount == null) {
			return "";
		}
		return ((amount).multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_FLOOR).toString());

	}

	public static String DivideHundred(Object amount) {
		if (amount == null) {
			return "";
		}
		return ((new BigDecimal((String) amount))).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public static String getStrMsg(byte[] bytes, String encoding) {
		String str = "";
		try {
			if ((bytes != null) && (bytes.length > 0)) {
				str = new String(bytes, encoding);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		return str;
	}

	public static String getRandom() {
		String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < 24; ++i) {
			int number = random.nextInt(36);// [0,62)

			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	public static String sign(Map map, String key) {
		Set<Entry<String, String>> entry1 = WxUtil.sortedmap(map);
		Iterator<Entry<String, String>> it = entry1.iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String k = entry.getKey();
			Object v = entry.getValue();
			if ((null != v) && !"".equals(v) && !"sign".equals(k) && !"key".equals(k) && !"spbill_create_ip".equals(k)) {
				sf.append(k + "=" + v + "&");
			}
		}
		sf.append("key=" + key);
		String stringA = sf.toString();
		System.out.println("plain is ========="+stringA);
		String sign = DESHelper.getFileDigest(stringA.getBytes());
		return sign;

	}
	//刷卡、扫码支付 不能去掉ip
	public static String sign1(Map map, String key) {
		Set<Entry<String, String>> entry1 = WxUtil.sortedmap(map);
		Iterator<Entry<String, String>> it = entry1.iterator();
		StringBuffer sf = new StringBuffer();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String k = entry.getKey();
			Object v = entry.getValue();
			if ((null != v) && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sf.append(k + "=" + v + "&");
			}
		}
		sf.append("key=" + key);
		String stringA = sf.toString();
		System.out.println("plain is ========="+stringA);
		String sign = DESHelper.getFileDigest(stringA.getBytes());
		return sign;

	}
}
