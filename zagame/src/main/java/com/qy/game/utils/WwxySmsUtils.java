package com.qy.game.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

/**
 * 微网信云
 *
 * @author 洪序铭
 * @date 2019/09/03 17:31
 */
public class WwxySmsUtils {

    private final static Logger logger = LoggerFactory.getLogger(WwxySmsUtils.class);
    /**
     * 模板ID
     */
    public static final String url = "http://sms.uninets.com.cn/Modules/Interface/http/IservicesBSJY.aspx";
    public static final String userid = "zhoan";
    public static final String pwd = "zhoan10086";

    public static void main(String[] args) {
        System.out.println(sendMsg("18159170807", null));
    }

    /**
     * 发送短信
     *
     * @param mobile 手机
     * @param name   昵称
     * @return String
     */
    public static String sendMsg(String mobile, String name) {
        if (name == null) name = "----";
        // 生成随机验证码
        Random rd = new Random();
        int number = rd.nextInt(899999) + 100000;
        //请求参数
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("flag=sendsms&loginname=").append(userid)
                    .append("&password=").append(pwd)
                    .append("&p=").append(mobile)
                    .append("&c=").append(
                    URLEncoder.encode(new StringBuilder().append("【").append(name).append("】").append("您的验证码是").append(number).append("。如非本人操作，请忽略本短信").toString(), "UTF-8")
            );
            net(sb.toString());

        } catch (Exception e) {
            logger.error("发送短信异常！！！", e);
        }
        return String.valueOf(number);
    }

    /**
     * 发送网络请求
     *
     * @throws Exception
     */
    public static void net(String urlParameters) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            logger.info(new StringBuilder().append("发送微网信云短信:").append(response.toString()).toString());
        } else {
            logger.error("发送微网信云短信异常！！！");
        }

    }

}
