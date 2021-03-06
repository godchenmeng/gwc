/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.youxing.car.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 */
public class DateUtilsWeb extends org.apache.commons.lang.time.DateUtils {
	
	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" };
	
	public static String getFormatDate(Date date){
		return DateFormatUtils.format(date, "yyyy/MM/dd HH:mm:ss");
	}
	
	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd）
	 */
	public static String getDate() {
		return getDate("yyyy-MM-dd");
	}
	
	/**
	 * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String getDate(String pattern) {
		return DateFormatUtils.format(new Date(), pattern);
	}
	
	/**
	 * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
	 */
	public static String formatDate(Date date, Object... pattern) {
		String formatDate = null;
		if (pattern != null && pattern.length > 0) {
			formatDate = DateFormatUtils.format(date, pattern[0].toString());
		} else {
			formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
		}
		return formatDate;
	}
	
	/**
	 * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static String formatDateTime(Date date,String pattern) {
		return formatDate(date,pattern);
	}

	/**
	 * 得到当前时间字符串 格式（HH:mm:ss）
	 */
	public static String getTime() {
		return formatDate(new Date(), "HH:mm:ss");
	}

	/**
	 * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
	 */
	public static String getDateTime() {
		return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前年份字符串 格式（yyyy）
	 */
	public static String getYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串 格式（MM）
	 */
	public static String getMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串 格式（dd）
	 */
	public static String getDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串 格式（E）星期几
	 */
	public static String getWeek() {
		return formatDate(new Date(), "E");
	}
	
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" }
	 */
	public static Date parseDate(Object str) {
		if (str == null){
			return null;
		}
		try {
			return parseDate(str.toString(), parsePatterns);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime()-date.getTime();
		return t/(24*60*60*1000);
	}
	
    
	public static Date getDateStart(Date date) {
		if(date==null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date= sdf.parse(formatDate(date, "yyyy-MM-dd")+" 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static Date getDateEnd(Date date) {
		if(date==null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date= sdf.parse(formatDate(date, "yyyy-MM-dd") +" 23:59:59");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	public static String getMonday() {
		String monday;
	    Calendar cal = Calendar.getInstance();
	    cal.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天
	    cal.add(Calendar.DATE, -1*7);
	    cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
	    monday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	    return monday;
	}
	public static String getSunday() {
		String sunday;
	    Calendar cal = Calendar.getInstance();
	    cal.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天
	    cal.add(Calendar.DATE, -1*7);
	    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	    sunday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	    return sunday;
	}
	public static String getThisMonday() {
		String monday;
	    Calendar cal = Calendar.getInstance();
	    cal.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天
	    cal.add(Calendar.DATE, 0);
	    cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
	    monday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	    return monday;
	}
	public static String getThisSunday() {
		String sunday;
	    Calendar cal = Calendar.getInstance();
	    cal.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天
	    cal.add(Calendar.DATE,0);
	    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	    sunday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
	    return sunday;
	}
	public static String getfirstDayofMonth(){
	    String firstDayofMonth;
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.MONTH, -1);
	    calendar.set(Calendar.DAY_OF_MONTH, 1);
	    firstDayofMonth = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	    return firstDayofMonth;
	}
	public static String getfirstDayofMonthYears(){
	    String firstDayofMonth;
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.MONTH, -1);
	    calendar.set(Calendar.DAY_OF_MONTH, 1);
	    firstDayofMonth = new SimpleDateFormat("yyyy").format(calendar.getTime());
	    return firstDayofMonth;
	}
	public static String getfirstDayofMonths(){
	    String firstDayofMonth;
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.MONTH, -1);
	    calendar.set(Calendar.DAY_OF_MONTH, 1);
	    firstDayofMonth = new SimpleDateFormat("MM").format(calendar.getTime());
	    return firstDayofMonth;
	}
	public static String getlastDayofMonth(){
	    String lastDayofMonth;
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.DAY_OF_MONTH, 1); 
	    calendar.add(Calendar.DATE, -1);
	    lastDayofMonth = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	    return lastDayofMonth;
	}
	/** 
     * 根据年 月 获取对应的月份 天数 
     * */  
    public static int getDaysByYearMonth(int year, int month) {  
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }  
    public static long getHour(String date, String date1) throws ParseException {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf1.parse(date));
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(sdf1.parse(date1));
		long time = Math.round((calendar1.getTimeInMillis() - calendar.getTimeInMillis()) / (1000 * 60.0));
		return time;
	}
    /**
     * 
    * @author mars   
    * @date 2017年5月8日 上午9:12:39 
    * @Description: TODO(获取时间的毫秒数) 
    * @param @param time
    * @param @return
    * @param @throws ParseException    设定文件 
    * @return long    返回类型 
    * @throws
     */
    public static long getTime(String time) throws ParseException{
    	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf1.parse(time));
		return calendar.getTimeInMillis();
    }
    /**
     * 
    * @author mars   
    * @date 2017年5月8日 上午9:12:53 
    * @Description: TODO(毫秒数转时间) 
    * @param @param time
    * @param @return    设定文件 
    * @return String    返回类型 
    * @throws
     */
    public static String getLong2Date(Long time){
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(time);
    	return formatter.format(calendar.getTime());
    }
    
	public static boolean isCommonMonth(String start, String end) throws ParseException {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(sdf1.parse(start));
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(sdf1.parse(end));
		if (startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)) {
			if (startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH)) {
				return true;
			}
		}
		return false;
	}
	
	public static String setDay(String day, int da) throws ParseException {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(sdf1.parse(day));
		startCal.set(Calendar.DAY_OF_MONTH, da);
		return sdf1.format(startCal.getTime());
	}
	
	/**
	 * 
	 * @Function: DateUtils::getfirstDayofMonthToDate
	 * @Description: 获取当前月的第一天
	 * @return
	 * @throws ParseException
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月26日 下午5:36:45 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月26日     Banji Uoocent          v1.1.0               修改原因
	 */
	public static Date getfirstDayofMonthToDate() throws ParseException{
	    Date firstDayofMonth;
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.MONTH, 0);
	    calendar.set(Calendar.DAY_OF_MONTH, 1);
	    firstDayofMonth = getDateStart(new Date(getTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()))));
	    return firstDayofMonth;
	}
	
	/**
	 * 
	 * @Function: DateUtils::getlastDayofMonthToDate
	 * @Description: 获取当前月的最后一天
	 * @return
	 * @throws ParseException
	 * @version: v1.1.0
	 * @author: Banji Uoocent
	 * @date: 2017年5月26日 下午5:36:52 
	 *
	 * Modification History:
	 * Date         Author          Version            Description
	 *-------------------------------------------------------------
	 * 2017年5月26日     Banji Uoocent          v1.1.0               修改原因
	 */
	public static Date getlastDayofMonthToDate()throws ParseException{
	    Date lastDayofMonth;
	    Calendar calendar = Calendar.getInstance();
	    calendar.add(Calendar.MONTH, 1);
	    calendar.set(Calendar.DAY_OF_MONTH, 1); 
	    calendar.add(Calendar.DATE, -1);
	    lastDayofMonth = getDateEnd(new Date(getTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()))));
	    return lastDayofMonth;
	}

}
