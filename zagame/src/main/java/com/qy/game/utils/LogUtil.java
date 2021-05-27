package com.qy.game.utils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

public class LogUtil {
	
	public static void main(String args[]) throws Exception {  
		
		LogUtil.info(LogUtil.class, "info");
    }  
	
	/**
	 * 消息打印
	 * @param clazz
	 * @param msg
	 */
	public static void print(String msg){
		
		Logger.getLogger(LogUtil.class).info(msg);
	}
	
	/**
	 * 普通消息
	 * @param clazz
	 * @param msg
	 */
	public static void info(Class clazz, String msg){
		
		Logger.getLogger(clazz).info(msg);
	}
	
	/**
	 * 错误消息
	 * @param clazz
	 * @param msg
	 */
	public static void error(Class clazz, String msg){
		
		Logger.getLogger(clazz).error(msg);
	}
	
	/**
	 * 警告消息
	 * @param clazz
	 * @param msg
	 */
	public static void warn(Class clazz, String msg){
		
		Logger.getLogger(clazz).warn(msg);
	}
	
	/**
	 * 调试消息
	 * @param clazz
	 * @param msg
	 */
	public static void debug(Class clazz, String msg){
		
		Logger.getLogger(clazz).debug(msg);
	}
	
}
