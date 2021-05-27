package com.qy.game.utils;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *短信API服务调用示例代码 － 聚合数据
 */
public class JsmsUtils{

    private final static Logger logger = LoggerFactory.getLogger(JsmsUtils.class);

    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent =  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.1547.66 Safari/537.36";

    /**
     * 配置您申请的KEY
     */
    public static final String APPKEY ="59f49def764d83248b2dd9418dd0a0a9";

    /**
     * 发送短信
     */
    public static String sendMsg(String mobile){
    	// 生成随机验证码
    	Random rd=new Random();
		int number = rd.nextInt(899999)+100000;
        String result =null;
        //请求接口地址
        String url ="http://v.juhe.cn/sms/send";
        //请求参数
        Map<String, Object> params = new HashMap<String, Object>();
        //接收短信的手机号码
        params.put("mobile",mobile);
        //短信模板ID，请参考个人中心短信模板设置
        params.put("tpl_id","113597");
        String tpl_value = "#code#="+String.valueOf(number);
        //#code#=1234&#company#=聚合数据，变量名和变量值对。如果你的变量名或者变量值中带有#&=中的任意一个特殊符号，请先分别进行urlencode编码后再传递，<a href="http://www.juhe.cn/news/index/id/50" target="_blank">详细说明></a>
        params.put("tpl_value",tpl_value);
        //应用APPKEY(应用详细页查询)
        params.put("key",APPKEY);
        //返回数据的格式,xml或json，默认json
        params.put("dtype","json");

        try {
            result =net(url, params, "GET");
            JSONObject object = JSONObject.fromObject(result);
            if(object.getInt("error_code")==0){
                logger.info(object.get("result").toString());
            }else{
                logger.info(object.get("error_code")+":"+object.get("reason"));
            }
        } catch (Exception e) {
            logger.error("发送短信异常！！！",e);
        }
        return String.valueOf(number);
    }

    /**
     * 发送网络请求
     * @param strUrl 请求地址
     * @param params 请求参数
     * @param method 请求方法
     * @return  网络请求字符串
     * @throws Exception
     */
    public static String net(String strUrl, Map<String, Object> params,String method) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        String rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            if(method==null || method.equals("GET")){
                strUrl = strUrl+"?"+urlencode(params);
            }
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            if(method==null || method.equals("GET")){
                conn.setRequestMethod("GET");
            }else{
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
            }
            conn.setRequestProperty("User-agent", userAgent);
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            if (params!= null && method.equals("POST")) {
                try {
                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                    out.writeBytes(urlencode(params));
                } catch (Exception e) {
                    logger.error("发送短信异常！！！",e);
                }
            }
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
        } catch (IOException e) {
            logger.error("发送短信异常！！！",e);
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return rs;
    }

    /**
     * 将map型转为请求参数型
     * @param data
     * @return
     */
    public static String urlencode(Map<String,Object>data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                logger.error("发送短信异常！！！",e);
            }
        }
        return sb.toString();
    }
}