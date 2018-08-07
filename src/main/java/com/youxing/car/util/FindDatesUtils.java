package com.youxing.car.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FindDatesUtils {
	
	public static void main(String[] args) throws Exception  
	 {  
	  
	  Calendar cal = Calendar.getInstance();  
	  String start = "2012-02-01";  
	  String end = "2012-03-02";  
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	  Date dBegin = sdf.parse(start);  
	  Date dEnd = sdf.parse(end);  
	 }  
	  
	 public static List<String> findDates(Date dBegin, Date dEnd)  {  
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	  List lDate = new ArrayList();  
	  lDate.add(sdf.format(dBegin));  
	  Calendar calBegin = Calendar.getInstance();  
	  // 使用给定的 Date 设置此 Calendar 的时间  
	  calBegin.setTime(dBegin);  
	  Calendar calEnd = Calendar.getInstance();  
	  // 使用给定的 Date 设置此 Calendar 的时间  
	  calEnd.setTime(dEnd);  
	  // 测试此日期是否在指定日期之后  
	  while (dEnd.after(calBegin.getTime()))  
	  {  
	   // 根据日历的规则，为给定的日历字段添加或减去指定的时间量  
	   calBegin.add(Calendar.DAY_OF_MONTH, 1);  
	   lDate.add(sdf.format(calBegin.getTime()));  
	  }  
	  return lDate;  
	 }  

}
