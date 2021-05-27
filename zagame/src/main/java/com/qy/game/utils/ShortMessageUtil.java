package com.qy.game.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;


public class ShortMessageUtil {
	/***
	 * 发送短信
	 * @param tel 手机号码
	 * @param code 验证码
	 * @return 发送状态      0>>>成功
	 * @throws UnsupportedEncodingException 
	 * */
	public static String getCode(String tel,HttpServletRequest request) throws UnsupportedEncodingException {
		Random rd=new Random();
		int co = rd.nextInt(899999)+100000;

		//【东东棋牌】验证码：%s。您正在验证东东棋牌游账号，验证码很重要，请勿泄露给他人
		//【东方博游】验证码：%s。您正在验证东方博游账号，验证码很重要，请勿泄露给他人。
		//【万金游网娱】验证码：%s。您正在验证万金游网娱账号，验证码很重要，请勿泄露给他人
		
		
	    System.out.println(co);
	    String msg=String.format("【棋牌游戏】验证码：%s。您正在验证棋牌游戏账号，验证码很重要，请勿泄露给他人。",co);
		Map<String,String> param=new HashMap<String, String>();
		param.put("userid", "549");
		param.put("account", "ZQR");
		param.put("password", "123456");
		param.put("mobile", tel);
		param.put("content", msg);
		param.put("sendTime", "");
		param.put("action", "send");
		param.put("extno", "");
		System.out.println(param.toString());
		String returnStr = HttpReqUtil.doPost("http://110.40.13.138:8088/sms.aspx", param, null, "utf-8");
		System.out.println(returnStr);
		
		return String.valueOf(co);
	}
	
	/**
	 * 系统时间的毫秒数
	 * 
	 * @return 系统时间的毫秒数
	 */
	public static long getMillis() {

		return new Date().getTime();
	}
	
}
