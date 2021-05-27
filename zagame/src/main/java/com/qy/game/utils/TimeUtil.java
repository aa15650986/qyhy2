package com.qy.game.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * 时间处理工具类
 * @author nanoha
 *
 */
public class TimeUtil {

	private static SimpleDateFormat ymdhms=new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
	private static SimpleDateFormat ymd=new SimpleDateFormat("yyyy-MM-dd");
	private static Calendar calendar = Calendar.getInstance();

	/**
	 * 获取当前时间
	 * @return
	 */
	public static String getNowDate(){
		String time=ymdhms.format(new Date());
		return time;
	}
	/**
	 * 指定格式获取当前时间
	 * @param format
	 * @return
	 */
	public static String getNowDate(String format){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		String time=sdf.format(new Date());
		return time;
	}
	
	/**
	 * 获得月份+日期
	 * @return X月X日(String)
	 */
	public static String getMonAndDay()
	{
		String time=ymdhms.format(new Date());
		Timestamp timestamp=Timestamp.valueOf(time);
		calendar.setTime(timestamp);
		int month=calendar.get(Calendar.MONTH) + 1;
		int day=calendar.get(Calendar.DATE);
		return month+"月"+day+"日";
	}
	/**
	 * 获得当前月份的天数
	 */
	public static int getNewMMDay(){
		
		Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
		int day=aCalendar.getActualMaximum(Calendar.DATE);
		return day;
	}
	
	/**
	 * 将字符串转化为时间
	 * @param timestr
	 * @return
	 * @throws ParseException 
	 */
	public static String StrTsf(String timestr) throws ParseException
	{
		Date date=ymd.parse(timestr);
		return ymdhms.format(date);
	}
	
	public static Date StrTsfToDate(String timestr,String format) throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		Date date=sdf.parse(timestr);
		return date;
	}
	
	/**
	 * 转化查询结果中的对应timestamp值
	 * @param array
	 * @param key
	 * @return
	 */
	public static JSONArray transTimestamp(JSONArray array,String key)
	{
		for(int i=0;i<array.size();i++)
		{
			JSONObject tmpobj=array.getJSONObject(i);
			if(("null").equals(tmpobj.getString(key)))
				continue;
			else{
				JSONObject time=tmpobj.getJSONObject(key);
				Date nowDate=new Date();
				nowDate.setTime(time.getLong("time"));
				tmpobj.element(key, ymd.format(nowDate));
			}
		}
		return array;
	}
	
	public static JSONArray transTimestamp(JSONArray array,String key,String format)
	{
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		for(int i=0;i<array.size();i++)
		{
			JSONObject tmpobj=array.getJSONObject(i);
			if(("null").equals(tmpobj.getString(key)))
				continue;
			else{
				JSONObject time=tmpobj.getJSONObject(key);
				Date nowDate=new Date();
				nowDate.setTime(time.getLong("time"));
				tmpobj.element(key, sdf.format(nowDate));
			}
		}
		return array;
	}
	
	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @param sdf
	 * @return
	 */
	public static Date str2Date(String str, SimpleDateFormat sdf) {

		if (null == str || "".equals(str)) {
			return null;
		}
		Date date = null;
		try {
			date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static JSONArray transTimesArray(JSONArray array,String key,String format) throws ParseException{
		
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		for(int i=0;i<array.size();i++)
		{
			JSONObject tmpobj=array.getJSONObject(i);
			if(("null").equals(tmpobj.getString(key)))
				continue;
			else{
				tmpobj.element(key, sdf.format(str2Date(tmpobj.getString("createtime"), ymd)));
			}
		}
		return array;
	}
	
	
	public static JSONArray transTimestampToMd(JSONArray array,String key)
	{
		for(int i=0;i<array.size();i++)
		{
			JSONObject tmpobj=array.getJSONObject(i);
			if(("null").equals(tmpobj.getString(key)))
				continue;
			else{
				JSONObject time=tmpobj.getJSONObject(key);
				Date nowDate=new Date();
				nowDate.setTime(time.getLong("time"));
				calendar.setTime(nowDate);
				int month=calendar.get(Calendar.MONTH) + 1;
				int day=calendar.get(Calendar.DATE);
				tmpobj.element(key, month+"月"+day+"日");
			}			
		}
		return array;
	}
	
	/**
	 * timestamp
	 * @param oData
	 * @param sSdfFormat
	 * @param sKey
	 * @return
	 */
	public static JSONObject transTimeStamp(JSONObject oData,String sSdfFormat,String sKey)
	{
		if(!("null").equals(oData.getString(sKey)))
		{
			SimpleDateFormat format=new SimpleDateFormat(sSdfFormat);
			Date nowDate=new Date();
			JSONObject time=oData.getJSONObject(sKey);
			nowDate.setTime(time.getLong("time"));
			oData.element(sKey, format.format(nowDate));
		}		
		return oData;
	}
	
	/**
	 * 判断两个时间的大小
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean isLatter(String startTime,String endTime){
		calendar.setTime(Timestamp.valueOf(startTime));
		long start=calendar.getTimeInMillis();
		calendar.setTime(Timestamp.valueOf(endTime));
		long end=calendar.getTimeInMillis();
		return start>end?true:false;
	}
	
	/**
	 * 获得本周的周一或周日
	 * @param isMon
	 * @return
	 */
	public static String getMonOrSat(boolean isMon){
		dayCount(new Date());
		if(isMon)
			return ymd.format(calendar.getTime());
		else{
			calendar.add(Calendar.DATE, 6);  
			return ymd.format(calendar.getTime());
		}
			
	}	
	private static Calendar dayCount(Date time){
		calendar.setTime(time);
		int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天 
		if(1 == dayWeek) 
			calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek()-day);
		return calendar;
	}
	
	/**
	 * 获得两个日期间间隔的天数
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static String getDaysBetweenTwoTime(String time1,String time2)
	{
		 Calendar fromCalendar = Calendar.getInstance(); 
		 fromCalendar.setTime(Timestamp.valueOf(time1));
		 fromCalendar.set(Calendar.HOUR_OF_DAY, 0);  
         fromCalendar.set(Calendar.MINUTE, 0);  
         fromCalendar.set(Calendar.SECOND, 0);  
         fromCalendar.set(Calendar.MILLISECOND, 0); 
         Calendar toCalendar = Calendar.getInstance(); 
         toCalendar.setTime(Timestamp.valueOf(time2));  
         toCalendar.set(Calendar.HOUR_OF_DAY, 0);  
         toCalendar.set(Calendar.MINUTE, 0);  
         toCalendar.set(Calendar.SECOND, 0);  
         toCalendar.set(Calendar.MILLISECOND, 0);
         Long tempString = (Math.abs(toCalendar.getTime().getTime() - fromCalendar.getTime().getTime())) / (1000 * 60 * 60 * 24);
         return  String.valueOf(tempString);
	}
	/**
	 * 时间加几天，减几天
	 */
	public static Calendar changeTime(Date time,int year,int month,int days){
        Calendar c = Calendar.getInstance();  
        c.setTime(time); 
        c.add(Calendar.YEAR, year);//属性很多也有月等等，可以操作各种时间日期  
        c.add(Calendar.MONTH, month);
        c.add(Calendar.DATE, days);  
        return c;
	}
	/**
	 * 时间加几天，减几天
	 */
	public static String changeTime2(Date time,int year,int month,int days){
		Calendar c = Calendar.getInstance();  
		c.setTime(time); 
		c.add(Calendar.YEAR, year);//属性很多也有月等等，可以操作各种时间日期  
		c.add(Calendar.MONTH, month);
		c.add(Calendar.DATE, days);  
		return ymdhms.format(c.getTime());
	}
	/**
	 * 在指定日期基础上加任意小时数
	 * @param Time
	 * @param addCount
	 * @param format
	 * @return
	 */
	public static String addHoursBaseOnNowTime(String Time,int addCount,String format){
		calendar.setTime(Timestamp.valueOf(Time));
		calendar.add(Calendar.HOUR_OF_DAY, addCount);
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(calendar.getTime());
	}
	
	/**
	 * 在指定日期基础上加n个月
	 * @param Time
	 * @param addCount
	 * @return
	 */
	public static String addTimeBaseOnNowTime(String Time,int addCount,Timestamp times)
	{
		
		if(Time!=null&&!"".equals(Time)){
			calendar.setTime(Timestamp.valueOf(Time));
			}else{
				calendar.setTime(times);
			}
		
		calendar.add(Calendar.MONTH, addCount);
		return ymdhms.format(calendar.getTime());
	}
	
	
	/**
	 * list转jsonarray 时间格式设置
	 * @param list
	 * @return
	 */
	public static JSONArray listToJSONArray(List list){
		
		JsonConfig config=new JsonConfig();  
        //注册一个json对象的转换器,如果转换Timestamp类型，调用该转换器  
        config.registerJsonValueProcessor(Timestamp.class,new JsonValueProcessor() {  
            @Override
			public Object processObjectValue(String arg0, Object arg1, JsonConfig arg2) {  

            	if(arg1!=null){
            		
            		SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
            		Timestamp d=(Timestamp) arg1;  
            		return sd.format(d);  
            	}
            	return arg1;
                 
            }  
            @Override
			public Object processArrayValue(Object arg0, JsonConfig arg1) {  
                return null;  
            }  
        });  
        
		return JSONArray.fromObject(list, config);
	}
	
	
	/**
	 * object转jsonObject 时间设置
	 * @param obj
	 * @return
	 */
	public static JSONObject objectToJSONObject(Object obj){
		
		JsonConfig config=new JsonConfig();  
        //注册一个json对象的转换器,如果转换Timestamp类型，调用该转换器  
        config.registerJsonValueProcessor(Timestamp.class,new JsonValueProcessor() {  
            @Override
			public Object processObjectValue(String arg0, Object arg1, JsonConfig arg2) {  
                
            	if(arg1!=null){
            		
            		SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
            		Timestamp d=(Timestamp) arg1;  
            		return sd.format(d);  
            	}
            	return arg1;
                
            }  
            @Override
			public Object processArrayValue(Object arg0, JsonConfig arg1) {  
                return null;  
            }  
        });  
        
		return JSONObject.fromObject(obj, config);
	}
	
	public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }
	
	/**
	 * Timestamp 转换格式 
	 * @return
	 */
	public static JsonConfig Timestampzh(){
		
		JsonConfig config=new JsonConfig();  
		//注册一个json对象的转换器,如果转换Timestamp类型，调用该转换器  
		config.registerJsonValueProcessor(Timestamp.class,new JsonValueProcessor() {  
			@Override
			public Object processObjectValue(String arg0, Object arg1, JsonConfig arg2) {  
				SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				Timestamp d=(Timestamp) arg1;  
				return sd.format(d);  
			}  
			@Override
			public Object processArrayValue(Object arg0, JsonConfig arg1) {  
				return null;  
			}  
		});
		return config;  
	}
}
