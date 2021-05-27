package com.qy.game.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

public class JsSdkUtils {
public static JSONObject getReadyParameter(HttpServletRequest request,String url) {
		
		//获取session中的token时间参数
		String tokenTime=(String) request.getSession().getAttribute(Dto.WECHATACCESSTOKENTIME);
		String token=(String) request.getSession().getAttribute(Dto.WECHATACCESSTOKEN);
		//System.out.println("》》token111《《："+token);
		//判断accesstoken 过期
		Dto.writeLog("微信token"+token);
		if (Dto.stringIsNULL(token)||!isAccessTokenExpire(request)) {
			Dto.writeLog("微信token为空");
			System.out.println("<<重新获取Access_Token>>");
			JSONObject tokenObj=Doweixin. getAccessToken();	//	获取token
			System.out.println("获取的totken"+tokenObj);
			
			if(!tokenObj.containsKey("access_token")){
				JSONObject data=new JSONObject();
				data.element("timestamp", "");
				data.element("nonceStr", "");
				data.element("signature", "");
				return data;
			}
			
			token=tokenObj.getString("access_token");
			request.getSession().setAttribute(Dto.WECHATACCESSTOKEN, token);
			request.getSession().setAttribute(Dto.WECHATACCESSTOKENTIME, TimeUtil.getNowDate());
		}
		
		//token=(String) request.getSession().getAttribute(Dto.WECHATACCESSTOKEN);
		//System.out.println("》》token《《："+token);
		//获取jsapi_ticket
		JSONObject jsapi=JsSdkUtils.getJsapiTicket(token);
		Dto.writeLog("获取微信的jsapi"+jsapi);
		if (!Dto.isNull(jsapi)&&jsapi.getString("errcode").equals("0")) {
			System.out.println(jsapi.toString());
			String jsapi_ticket = jsapi.getString("ticket"); 
			
			String timestamp = JsSdkUtils.create_timestamp();//时间戳
			String nonceStr = JsSdkUtils.create_nonce_str();//随机串
			String signature =getSignature(jsapi_ticket, timestamp, nonceStr, url);
			
			JSONObject data=new JSONObject();
			data.element("timestamp", timestamp);
			data.element("nonceStr", nonceStr);
			data.element("signature", signature);
			return data;
		}
		else {
			return null;
		}
		
	}
	
	/**
	 * 判断accesstoken 是否过期
	 * @param request true为已过期 false 未过期
	 * @return
	 */
	public static boolean isAccessTokenExpire(HttpServletRequest request) {
		//获取session中的token时间参数
		String tokenTime=(String) request.getSession().getAttribute(Dto.WECHATACCESSTOKENTIME);
		Dto.writeLog("获取session中的token时间参数"+tokenTime);
		if (!Dto.stringIsNULL(tokenTime)) {
			System.out.println("前一次获取token时间:"+tokenTime);
			//获取token过期时间
			String timeString=TimeUtil.addHoursBaseOnNowTime(tokenTime, 2, "yyyy-MM-dd HH:mm:ss");
			Dto.writeLog("token过期时间:"+timeString);

			//判断token是否过期 true为已过期 false 未过期
			boolean is=TimeUtil.isLatter(tokenTime,TimeUtil.getNowDate());
			if (is) {
				Dto.writeLog("token已过期");
			}
			return is;
		}
		return false;
	}

	/**
	 * 根据access_token获取jsapi_ticket
	 * @return
	 */
	public static JSONObject getJsapiTicket(String token) {
		
		System.out.println("access_token---："+token);
		//公众号申请jsapi_ticket的地址
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+token+"&type=jsapi";
		//url = url.replace("ACCESS_TOKEN", token);
		String result = HttpTookit.doGet(url, "UTF-8", false);
		JSONObject json = JSONObject.fromObject(result);
		System.out.println("根据access_token获取jsapi_ticket:"+json.toString());
		return json;
	}
	
	/**
	 * 获取JS-SDK Signature  
	 * @param token
	 * @param url
	 * @return
	 */
    public static String getSignature(String jsapi_ticket,String timestamp,String nonceStr,String url){  
        
    	String str = "jsapi_ticket=" + jsapi_ticket +"&noncestr=" + nonceStr +"&timestamp=" + timestamp +"&url=" + url;
        String signature = getSha1(str); //签名 
        //System.out.println("》》signature《《："+signature);
        return signature;  
    }
	
    /**
     * Sha1签名  
     * @param str
     * @return
     */
    public static String getSha1(String str) {  
    	String signature = ""; //签名 
    	try {
        	MessageDigest reset = MessageDigest.getInstance("SHA-1");
        	reset.update(str.getBytes("utf-8"));
        	            byte[] hash=reset.digest();
        	            Formatter formatter = new Formatter();
        	            for(byte b:hash){
        	           	formatter.format("%02x", b);
        	           	} 
        	            signature = formatter.toString();
        	            formatter.close();
        	} catch (NoSuchAlgorithmException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        	} catch (UnsupportedEncodingException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        	}   
    	return signature;
    }  
    
    public static String create_nonce_str() {
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

    public static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
}
