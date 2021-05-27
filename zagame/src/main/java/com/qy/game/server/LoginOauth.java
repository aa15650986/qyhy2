package com.qy.game.server;

import com.qy.game.constant.CacheKeyConstant;
import com.qy.game.model.User;
import com.qy.game.service.GlobalService;
import com.qy.game.service.UserService;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.service.impl.UserServiceImpl;
import com.qy.game.utils.*;
import net.sf.json.JSONObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 登录验证处理类
 *
 * @version 1.0
 */
public class LoginOauth {

    //@Resource GoldShopServiceImpl gsbiz;
    @Resource 
    UserServiceImpl userService;

    /**
     * anysdk统一登录地址
     */
    private String loginCheckUrl = "http://oauth.anysdk.com/api/User/LoginOauth/";

    /**
     * connect time out
     *
     * @var int
     */
    private int connectTimeOut = 30 * 1000;

    /**
     * time out second
     *
     * @var int
     */
    private int timeOut = 30 * 1000;

    /**
     * user agent
     *
     * @var string
     */
    private static final String userAgent = "px v1.0";

    /**
     * 检查登录合法性及返回sdk返回的用户id或部分用户信息
     *
     * @param request
     * @param response
     * @param globalService
     * @return 验证合法 返回true 不合法返回 false
     */
    public boolean check(HttpServletRequest request, HttpServletResponse response, GlobalService globalService, GoldShopBizServer gsbiz, UserService userService) {

        try {
            Map<String, String[]> params = request.getParameterMap();
            //检测必要参数
            if (parametersIsset(params)) {
                sendToClient(response, "parameter not complete");
                return false;
            }

            String queryString = getQueryString(request);

            URL url = new URL(loginCheckUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", userAgent);
            conn.setReadTimeout(timeOut);
            conn.setConnectTimeout(connectTimeOut);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(queryString);
            writer.flush();
            tryClose(writer);
            tryClose(os);
            conn.connect();

            InputStream is = conn.getInputStream();
            String result = stream2String(is);
            // 微信登录处理
            wxLogin(request, result, globalService, gsbiz, userService);
            System.out.println(result);
            sendToClient(response, result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendToClient(response, "Unknown error!");
        return false;
    }

    /**
     * 处理微信用户登录信息
     *
     * @param request
     * @param result
     * @param globalService
     */
    protected void wxLogin(HttpServletRequest request, String result, GlobalService globalService, GoldShopBizServer gsbiz, UserService userService) throws Exception {
        //UserService userService = new UserServiceImpl();
        //GoldShopBizServer gsbiz=new GoldShopServiceImpl();
        System.out.println("执行微信登录方法");
    	JSONObject obj = JSONObject.fromObject(result);
        String domainstr = request.getServerName();

        String url = gsbiz.getdomain(domainstr);
        if (obj.getString("status").equals("ok")) {

            JSONObject data = obj.getJSONObject("data").getJSONObject("user_info");

            String openid = data.getString("openid");
            String unionid = data.containsKey("unionid") ? data.getString("unionid") : openid;
			/*//获取完整url
			String uri=request.getRequestURL().toString();
			System.out.println(uri);
			//通过完整url得到domain 
			URL url=new URL(uri);
			String domain=url.getHost();
			//通过domain得到运营商的mark
			String mark=userService.getMarkByDoMain(domain).toString();
			//更新数据
			userService.updateOperatorMark(mark, openid);
			System.out.println("成功更新信息");*/
            String ip = "";
            if (request.getHeader("x-forwarded-for") == null) {
                ip = request.getRemoteAddr();
            } else {
                ip = request.getHeader("x-forwarded-for");
            }

            JSONObject userObj = userService.getUserInfoByOpenID(openid, true, ip);
            if (userObj == null) {

                // 将昵称中的表情符转化为字符串
                String name = CharUtil.emojiChange(data.getString("nickname"));
                String sex = "";
                if (data.containsKey("gender")) {
                    data.element("sex", data.getString("gender"));
                }
                if (data.containsKey("sex")) {
                    if (data.getString("sex").equals("1")) {
                        sex = "男";
                    } else if (data.getString("sex").equals("2")) {
                        sex = "女";
                    }
                }
                String account = creatorAccount(userService);
                if ("-1".equals(account)) {
                    System.out.println("生成用户account 错误");
                    return;
                }
                String headimgurl = data.containsKey("headimgurl") ? data.getString("headimgurl") : data.containsKey("icon") ? data.getString("icon") : null;
                String headimg = "/res/user/headimg/" + account + ".jpg";
                String filePath = request.getServletContext().getRealPath("/") + headimg;
                // 保存微信头像
                boolean back = Dto.saveImage(headimgurl, filePath, null);
                // 压缩头像
                if (back) {
                    try {
                        String thumb = filePath.replace("headimg", "headimg110");
                        ImageCompressUtil.compressImage(new File(filePath), 110, 110, thumb, "jpg");
                        headimg = headimg.replace("headimg", "headimg110");
                    } catch (IOException e) {
                        System.out.println("压缩头像:" + e.getMessage());
                        e.getStackTrace();
                    }
                }
                if (null == headimgurl) {
                    headimg = "/res/user/headimg/123456789.jpg";
                }
                // 获取配置信息
                JSONObject globalinfo = globalService.getGlobalInfo(url);
                int roomcard = 99;
                int coins = 1999;
                if (globalinfo != null) {
                    roomcard = globalinfo.getInt("roomcard");
                    coins = globalinfo.getInt("coins");
                }
                // 地区
                String area = userService.getIpAddresses(ip);
                JSONObject userInfo = userService.getUserInfoByUnionIdAndPlatform(unionid, url);
                if (!Dto.isNull(userInfo)) {
                    userService.updateUserInfo(userInfo.getLong("id"), openid, name, headimg, area);
                } else {
                    User user = new User(0L, account, name, sex, headimg, "0", roomcard, coins, 0, openid, unionid, UUID.randomUUID().toString(), ip, area, url, 0);
                    long uid = userService.insertUserInfo(user);
                    if (uid > 0) {

                        JSONObject settingJson = new JSONObject();
                        settingJson.put("music", 0.8);
                        settingJson.put("musicOn", 1);
                        settingJson.put("volum", 0.8);
                        settingJson.put("volumOn", 1);
                        settingJson.put("mjVoice", 0);
                        userService.setUserSetting(uid, settingJson);
                    }
                }
            } else {
                //每次登录 、更新用户头像 昵称
                //新昵称
                String name = CharUtil.emojiChange(data.getString("nickname"));
                //新头像
                String headimgurl = data.containsKey("headimgurl") ? data.getString("headimgurl") : data.containsKey("icon") ? data.getString("icon") : null;
                String headimg = "/res/user/headimg/" + userObj.getString("account") + ".jpg";
                String filePath = request.getServletContext().getRealPath("/") + headimg;
                // 保存微信头像
                boolean back = Dto.saveImage(headimgurl, filePath, null);
                // 压缩头像
                if (back) {
                    try {
                        String thumb = filePath.replace("headimg", "headimg110");
                        ImageCompressUtil.compressImage(new File(filePath), 110, 110, thumb, "jpg");
                        headimg = headimg.replace("headimg", "headimg110");
                    } catch (IOException e) {
                        System.out.println("压缩头像:" + e.getMessage());
                        e.getStackTrace();
                    }
                }

                // 地区
                String area = userService.getIpAddresses(ip);
                User user = new User(0L, "", name, "", headimg, "0", 0, 0, 0, openid, unionid, UUID.randomUUID().toString(), ip, area, url, userObj.getLong("gulidId"));

                // 更新用户信息
                userService.updateUserInfo(user);

            }
        }
    }

    /**
     * 生成用户账号（随机生成一个8位数的账号）
     *
     * @param userService
     * @return
     */
    public static String creatorAccount(UserService userService) {

        List<String> array = userService.getUserAccountList();
        String code = "";
        boolean b = true;
        while (b) {
            code = MathDelUtil.getRandomStr(6);
            if (!array.contains(code) && !userService.verifyUserCode(code)) {
                b = false;
            }
        }
        if (code.length() == 6) {
            if (userService.checkUserAccount(code)) {
//                System.out.println(new StringBuilder().append("生成用户的编号:").append(code).append(" 原有编号:").append(array.toString()).toString());
                RedisCacheUtil.del(CacheKeyConstant.ZA_USERS_ACCOUNT);
                return code;
            } else {
                return creatorAccount(userService);
            }
        } else {
            code = "-1";
        }
        return code;
    }

//    public static String creatorAccount(UserService userService) {
//
//        Random rd = new Random();
//        int num = rd.nextInt(9000000) + 10000000;
//        String account = String.valueOf(num);
//        if (userService.checkUserAccount(account)) {
//
//            return account;
//        } else {
//            return creatorAccount(userService);
//        }
//    }


    public void setLoginCheckUrl(String loginCheckUrl) {
        this.loginCheckUrl = loginCheckUrl;
    }

    /**
     * 设置连接超时
     *
     * @param connectTimeOut
     */
    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    /**
     * 设置超时时间
     *
     * @param timeOut
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }


    /**
     * check needed parameters isset 检查必须的参数 channel
     * uapi_key：渠道提供给应用的app_id或app_key（标识应用的id）
     * uapi_secret：渠道提供给应用的app_key或app_secret（支付签名使用的密钥）
     *
     * @param params
     * @return boolean
     */
    private boolean parametersIsset(Map<String, String[]> params) {
        return !(params.containsKey("channel") && params.containsKey("uapi_key")
                && params.containsKey("uapi_secret"));
    }

    /**
     * 获取查询字符串
     *
     * @param request
     * @return
     */
    private String getQueryString(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + value + "&";
            }
        }
        queryString = queryString.substring(0, queryString.length() - 1);
        return queryString;
    }

    /**
     * 获取流中的字符串
     *
     * @param is
     * @return
     */
    private String stream2String(InputStream is) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tryClose(br);
        }
        return "";
    }

    /**
     * 向客户端应答结果
     *
     * @param response
     * @param content
     */
    private void sendToClient(HttpServletResponse response, String content) {
        response.setContentType("text/plain;charset=utf-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭输出流
     *
     * @param os
     */
    private void tryClose(OutputStream os) {
        try {
            if (null != os) {
                os.close();
                os = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭writer
     *
     * @param writer
     */
    private void tryClose(Writer writer) {
        try {
            if (null != writer) {
                writer.close();
                writer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭Reader
     *
     * @param reader
     */
    private void tryClose(Reader reader) {
        try {
            if (null != reader) {
                reader.close();
                reader = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
