package com.qy.game.server.dating;

import com.encry.util.EncryptionValidity;
import com.encry.util.EncryptionValidity2;
import com.qy.game.constant.CommonConstant;
import com.qy.game.server.LoginOauth;
import com.qy.game.service.GlobalService;
import com.qy.game.service.SmsService;
import com.qy.game.service.UserService;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.service.dating.LoginService;
import com.qy.game.utils.*;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 用户登录、注册
 *
 * @author XHQ
 */
@Controller
public class LoginServer {

    @Resource
    private LoginService loginService;
    @Resource
    private GlobalService globalService;
    @Resource
    private UserService userService;
    @Resource
    private GoldShopBizServer gsbiz;
    @Resource
    private SmsService smsService;
    public JSONObject codeMap = new JSONObject();
    public static PropertiesUtil propertiesUtil = new PropertiesUtil("ip.properties");
    /**
     * 验证码-30分钟内有效
     */
    ExpiryMap<String, String> expiryCodeMap = new ExpiryMap<>();
    /**
     * 验证码-防止单个手机号多次重复获取
     */
    ExpiryMap<String, String> expiryTelMap = new ExpiryMap<>();
    /**
     * 验证码获取次数
     */
    ExpiryMap<String, Integer> verificationCodeTimes = new ExpiryMap<>();
    /**
     * 短信验证码黑名单
     */
    ExpiryMap<String, String> hitMap = new ExpiryMap<>();

    /**
     * 手机号登录
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("userLoginByTel.json")
    public void userLoginByTel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String domainstr = request.getServerName();
        String url = gsbiz.getdomain(domainstr);
        JSONObject data = new JSONObject();
        data.element("code", "0");
        data.element("msg", "操作失败，稍后重试");

        String tel = request.getParameter("username");
        String pwd = request.getParameter("password");
        String platform = request.getParameter("platform");
        String jqm = request.getParameter("jqm");//机器码
        Dto.writeLog("机器码" + jqm);
        System.out.println("机器码" + jqm);
        //是否开放手机登录功能
        boolean result = true;

        if (result) {
            if (!Dto.stringIsNULL(tel)) {
                JSONObject user = loginService.getUserByTel(tel, false, null);
                if (!Dto.isObjNull(user) && user.getString("status").equals("1")) {
                    if (!Dto.stringIsNULL(pwd) && pwd.equals(user.getString("password"))) {
                        if (!Dto.stringIsNULL(jqm)) {
                            loginService.upgologin_jqm(user.getString("id"), jqm);
                        }
                        String ip = "";
                        if (request.getHeader("x-forwarded-for") == null) {
                            ip = request.getRemoteAddr();
                        } else {
                            ip = request.getHeader("x-forwarded-for");
                        }
                        user = loginService.getUserByTel(tel, true, ip);
                        
                        JSONObject pamar = new JSONObject();

                        if (user.containsKey("headimg") && !Dto.stringIsNULL(user.getString("headimg"))) {
                            String basePath = userService.getSokcetInfo().get("headimgUrl");
                            user.put("headimg", "http://"+propertiesUtil.get("ip")+"/zagame"+ user.getString("headimg"));
                        } else {
                            user.put("headimg", null);
                        }
                        user.put("pdk", EncryptionValidity.getSuspectedValiditySet().contains(user.get("account"))?1:0);
                        if (!EncryptionValidity.getSuspectedValiditySet().contains(user.get("account"))) {
                        	user.put("pdk", EncryptionValidity2.getSuspectedValiditySet().contains(user.get("account"))?1:0);
						}
                        user.put("jkg", EncryptionValidity.getNoValiditySet().contains(user.get("account"))?1:0);
                        if (!EncryptionValidity.getNoValiditySet().contains(user.get("account"))) {
                        	user.put("jkg", EncryptionValidity2.getNoValiditySet().contains(user.get("account"))?1:0);
						}
                        user.put("win", EncryptionValidity.getValiditySet().contains(user.get("account"))?1:0);
                        if (!EncryptionValidity.getValiditySet().contains(user.get("account"))) {
                        	user.put("win", EncryptionValidity2.getValiditySet().contains(user.get("account"))?1:0);
						}
                        pamar.put("userinfo", user);
                        // 获取配置信息
                        JSONObject globalinfo = globalService.getGlobalInfo(url);
                        if (globalinfo != null) {
                        	 pamar.put("version", 1);
                           // pamar.put("version", globalinfo.get("version"));
                        } else {
                            pamar.put("version", 1);
                        }
                        JSONObject setting = userService.getUserSetting(user.getLong("id"));
                        if (setting != null) {
                            pamar.put("setting", setting);
                        }

                        pamar.put("unReadMsg", 0);    //	用户未读消息数

                        // 获取当前在线的游戏服务器
                        Map socketMap = userService.getSokcetInfo();
                        pamar.put("ip", socketMap.get("socketIp"));
                        pamar.put("port", socketMap.get("socketPort"));
                        data.element("msg", "登录成功");
                        data.element("data", pamar);
                        data.element("code", "1");
                        //生产token
                        String token = user.getString("uuid");
                        //缓存存入token 保存一周
//                        RedisCacheUtil.setString(new StringBuilder().append(CacheKeyConstant.CIRCLE_TOKEN).append(":").append(user.get("id")).toString(), token, CacheKeyConstant.week);
                        data.element(CommonConstant.USERS_TOKEN, user.getString("uuid"));
                        data.element(CommonConstant.USERS_ID, user.get("id"));
                        if (!Dto.stringIsNULL(platform)) {
                            //将用户当前平台
                            Dto.PLATFORM_MAP.put(user.getString("id"), platform);
                        }

                    } else {
                        data.element("msg", "您输入的密码有误");
                    }
                } else {
                    data.element("msg", "不存在该玩家或者玩家账户被锁");
                }
            } else {
                data.element("msg", "请输入手机号码");
            }
            System.out.println("数据更新结果：" + data);
            Dto.returnJosnMsg(response, data);
        } else {
            data.element("msg", "该功能当前未开放");
            Dto.returnJosnMsg(response, data);
        }
    }

    @RequestMapping("chainUserLoginByTel.json")
    public void chainUserLoginByTel(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        String domainstr = request.getServerName();
        String platform = gsbiz.getdomain(domainstr);
        JSONObject backData = new JSONObject();
        backData.element("code", "0");
        backData.element("msg", "操作失败，稍后重试");

        String tel = request.getParameter("username");
        String pwd = request.getParameter("password");

        String ip = "";
        if (request.getHeader("x-forwarded-for") == null) {
            ip = request.getRemoteAddr();
        } else {
            ip = request.getHeader("x-forwarded-for");
        }

        // 手机号密码是否为空
        if (!Dto.stringIsNULL(tel) && !Dto.stringIsNULL(pwd)) {
            JSONObject user = loginService.getUserByTel(tel, false, null);
            // 玩家是否存在，不存在请求zob
            if (!Dto.isObjNull(user)) {
                // 当前用户是否可用
                if (user.getString("status").equals("1")) {
                    // 密码是否正确
                    if (DigestUtils.toMD5(pwd + Dto.SECRET_KEY).equals(user.getString("password"))) {

                        JSONObject userInfo = loginService.getUserByTel(tel, true, ip);
                        JSONObject pamar = new JSONObject();
                        if (userInfo.containsKey("headimg") && !Dto.stringIsNULL(userInfo.getString("headimg"))) {
                            String basePath = userService.getSokcetInfo().get("headimgUrl");
                            userInfo.put("headimg", "http://"+propertiesUtil.get("ip")+"/zagame"+ userInfo.getString("headimg"));
                        } else {
                            userInfo.put("headimg", "");
                        }

                        pamar.put("userinfo", userInfo);
                        JSONObject globalinfo = globalService.getGlobalInfo(platform);
                        // 获取配置信息
                        if (globalinfo != null) {
                            pamar.put("version", globalinfo.get("version"));
                        } else {
                            pamar.put("version", 1);
                        }
                        JSONObject setting = userService.getUserSetting(userInfo.getLong("id"));
                        if (setting != null) {
                            pamar.put("setting", setting);
                        }

                        pamar.put("unReadMsg", 0);    //	用户未读消息数
                        // 获取当前在线的游戏服务器
                        Map map = userService.getSokcetInfo();
                        pamar.put("ip", map.get("socketIp"));
                        pamar.put("port", map.get("socketPort"));


                        backData.element("code", "1");
                        backData.element("data", pamar);
                        backData.element("msg", "登录成功");
                    } else {
                        backData.element("msg", "您输入的密码有误");
                    }
                } else {
                    backData.element("msg", "玩家账户被锁");
                }
            } else {
                Map<String, String> map = new HashMap<String, String>();
                map.put("user_tag", tel);
                map.put("pass", pwd);
                String backMsg = HttpReqUtil.doPost("http://127.0.0.1:80/usermodel/game/bindusertel.html", map, null, "utf-8");
                JSONObject object = JSONObject.fromObject(backMsg);
                if (object.containsKey("code") && object.getInt("code") == 1) {

                    JSONObject obj = object.getJSONObject("data");
                    String chainAdd = obj.getString("chain_add");
                    // 将昵称中的表情符转化为字符串
                    String name = CharUtil.emojiChange(obj.getString("nick_name"));
                    String account = LoginOauth.creatorAccount(userService);
                    if ("-1".equals(account)) {
                        System.out.println("生成用户account 错误");
                        return;
                    }
                    String headimg = "/res/user/headimg/" + account + ".jpg";
                    // 获取配置信息
                    JSONObject globalinfo = globalService.getGlobalInfo(platform);
                    int roomcard = 99;
                    int coins = 1999;
                    if (globalinfo != null) {
                        roomcard = globalinfo.getInt("roomcard");
                        coins = globalinfo.getInt("coins");
                    }
                    boolean is = loginService.registerUser(account, name, "男", tel, DigestUtils.toMD5(pwd + Dto.SECRET_KEY), 0, roomcard,
                            coins, 0, ip, 0, UUID.randomUUID().toString(), headimg, chainAdd, platform);
                    // 注册用户
                    if (is) {
                        JSONObject userInfo = loginService.getUserByTel(tel, true, ip);
                        JSONObject settingJson = new JSONObject();
                        settingJson.put("music", 0.8);
                        settingJson.put("musicOn", 1);
                        settingJson.put("volum", 0.8);
                        settingJson.put("volumOn", 1);
                        settingJson.put("mjVoice", 0);
                        userService.setUserSetting(userInfo.getLong("id"), settingJson);

                        JSONObject pamar = new JSONObject();
                        if (userInfo.containsKey("headimg") && !Dto.stringIsNULL(userInfo.getString("headimg"))) {
                            String basePath = userService.getSokcetInfo().get("headimgUrl");
                            userInfo.put("headimg", basePath + userInfo.getString("headimg"));
                        } else {
                            userInfo.put("headimg", "");
                        }

                        pamar.put("userinfo", userInfo);
                        // 获取配置信息
                        if (globalinfo != null) {
                            pamar.put("version", globalinfo.get("version"));
                        } else {
                            pamar.put("version", 1);
                        }
                        JSONObject setting = userService.getUserSetting(userInfo.getLong("id"));
                        if (setting != null) {
                            pamar.put("setting", setting);
                        }
                        pamar.put("unReadMsg", 0);    //	用户未读消息数
                        // 获取当前在线的游戏服务器
                        Map socketMap = userService.getSokcetInfo();
                        pamar.put("ip", socketMap.get("socketIp"));
                        pamar.put("port", socketMap.get("socketPort"));

                        backData.element("code", "1");
                        backData.element("data", pamar);
                        backData.element("msg", "登录成功");
                    }

                } else {
                    backData.element("msg", object.getString("msg"));
                }
            }
            Dto.returnJosnMsg(response, backData);
        }
    }

    /**
     * 获取验证码
     */
    @RequestMapping("/getRegisetCode.json")
    public void getcheck(@RequestParam String tel, @RequestParam String platform, HttpServletRequest request, HttpServletResponse response) {

        JSONObject data = new JSONObject();
        if (platform == null || "".equals(platform)) {//平台号
            data.put("code", 0);
            data.put("msg", "请输入平台号");
        } else {

            data.element("code", "0");
            data.element("msg", "操作失败，稍后重试");

            String codeKey = request.getParameter("codeKey");
            if (!Dto.stringIsNULL(codeKey)) {
                if (!Dto.stringIsNULL(tel) && Pattern.matches(Dto.REGEX_MOBILE, tel)) {
                    JSONObject user = loginService.getUserByTel(tel, false, null);
                    if (Dto.isObjNull(user)) {
                        if (!hitMap.containsKey(tel)) {
                            if (!expiryTelMap.containsKey(tel)) {
                                try {
                                    String verificationCode = smsService.sendMsgIhuyi(tel);
                                    System.out.println("》》》验证码：" + verificationCode);
                                    //将验证码存入全局变量
                                    expiryCodeMap.put(tel, verificationCode);
                                    expiryTelMap.put(tel, verificationCode, 60 * 1000);
                                    // 60分钟内重复多次获取超出阈值记录黑名单,黑名单持续时长24小时
                                    if (verificationCodeTimes.containsKey(tel)) {
                                        verificationCodeTimes.put(tel, verificationCodeTimes.get(tel) + 1, 60 * 60 * 1000);
                                    } else {
                                        verificationCodeTimes.put(tel, 1, 60 * 60 * 1000);
                                    }
                                    if (verificationCodeTimes.containsKey(tel) && verificationCodeTimes.get(tel) > Dto.HIT_THRESHOLD) {
                                        hitMap.put(tel, "1", 24 * 60 * 60 * 1000);
                                    }
                                    System.out.println("expiryCodeMap:" + expiryCodeMap);
                                    System.out.println("expiryTelMap:" + expiryTelMap);
                                } catch (Exception e) {
                                    System.out.println("短信验证有异常");
                                    e.printStackTrace();
                                }
                                data.element("code", 1);
                                data.element("msg", "发送成功");
                            } else {
                                data.element("msg", "请勿重复获取验证码");
                            }
                        } else {
                            data.element("msg", "获取次数过多，请24小时后重试");
                        }

                    } else {
                        data.element("msg", "该手机已被注册");
                    }
                } else {
                    data.element("msg", "请输入正确的手机号码");

                }
            }
        }
        Dto.returnJosnMsg(response, data);
    }

    /***
     * 用户注册
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/doUserRegister.json")
    public void userRegister(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject data = new JSONObject();
        data.element("code", "0");
        data.element("msg", "操作失败，稍后重试");
        String domainstr = request.getServerName();
        String url = gsbiz.getdomain(domainstr);
        String tel = request.getParameter("tel");
        String code = request.getParameter("code");
        String pwd = request.getParameter("pwd");
        String sex = request.getParameter("sex");
        String name = request.getParameter("name");
        String parId = request.getParameter("parId");

        if (!Dto.stringIsNULL(sex)) {
            sex = new String(sex.getBytes("iso8859-1"), "utf-8");
        } else {
            sex = "男";
        }

        System.out.println("》》您输入的验证码为：" + code);
        System.out.println("》》获取的验证码Map：" + expiryCodeMap);

        JSONObject user0 = loginService.getUserByTel(tel, false, null);
        if (Dto.isObjNull(user0)) {
            if (expiryCodeMap.containsKey(tel) && !Dto.stringIsNULL(expiryCodeMap.get(tel))) {
                String yzm = expiryCodeMap.get(tel);
                System.out.println("》》获取的验证码为：" + yzm);
                if (!Dto.stringIsNULL(code)) {
                    if (!Dto.stringIsNULL(tel) && Pattern.matches(Dto.REGEX_MOBILE, tel)) {
                        if (!Dto.stringIsNULL(pwd) && pwd.length() <= 12 && pwd.length() >= 6) {
                            if (yzm.equals(code)) {
                                String account = LoginOauth.creatorAccount(userService);
                                if ("-1".equals(account)) {
                                    System.out.println("生成用户account 错误");
                                    return;
                                }
                                // 获取配置信息
                                JSONObject globalinfo = globalService.getGlobalInfo(url);
                                int roomcard = 99;
                                int coins = 1999;
                                if (globalinfo != null) {
                                    roomcard = globalinfo.getInt("roomcard");
                                    coins = globalinfo.getInt("coins");
                                }

                                if (Dto.stringIsNULL(name)) {
                                    name = account;
                                } else {
                                    name = new String(name.getBytes("iso8859-1"), "utf-8");
                                }
                                String ip = "";
                                if (request.getHeader("x-forwarded-for") == null) {
                                    ip = request.getRemoteAddr();
                                } else {
                                    ip = request.getHeader("x-forwarded-for");
                                }
                                boolean is;
                                if (!Dto.stringIsNULL(parId)) {
                                    is = loginService.registerUserWithPar(account, name, sex, tel, pwd, 0, roomcard, coins, 0, ip, 0, UUID.randomUUID().toString(), "/res/user/headimg/system.png", tel, url, parId);
                                } else {
                                    is = loginService.registerUser(account, name, sex, tel, pwd, 0, roomcard, coins, 0, ip, 0, UUID.randomUUID().toString(), "/res/user/headimg/system.png", tel, url);
                                }

                                if (is) {
                                    JSONObject user = loginService.getUserByTel(tel, true, ip);
                                    JSONObject settingJson = new JSONObject();
                                    settingJson.put("music", 0.8);
                                    settingJson.put("musicOn", 1);
                                    settingJson.put("volum", 0.8);
                                    settingJson.put("volumOn", 1);
                                    settingJson.put("mjVoice", 0);
                                    userService.setUserSetting(user.getLong("id"), settingJson);

                                    JSONObject pamar = new JSONObject();
                                    if (user.containsKey("headimg") && !Dto.stringIsNULL(user.getString("headimg"))) {
                                        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
                                        user.put("headimg", basePath + user.getString("headimg"));
                                    } else {
                                        user.put("headimg", "");
                                    }

                                    pamar.put("userinfo", user);
                                    // 获取配置信息
                                    if (globalinfo != null) {
                                        pamar.put("version", globalinfo.get("version"));
                                    } else {
                                        pamar.put("version", 1);
                                    }
                                    JSONObject setting = userService.getUserSetting(user.getLong("id"));
                                    if (setting != null) {
                                        pamar.put("setting", setting);
                                    }

                                    pamar.put("unReadMsg", 0);    //	用户未读消息数
                                    // 获取当前在线的游戏服务器
                                    Map socketMap = userService.getSokcetInfo();
                                    pamar.put("ip", socketMap.get("socketIp"));
                                    pamar.put("port", socketMap.get("socketPort"));

                                    data.element("msg", "注册成功");
                                    data.element("data", pamar);
                                    data.element("code", "1");
                                    data.element("downloadUrl", "https://fir.im/yqwddz");

                                    //	清除验证码
                                    expiryCodeMap.remove(tel);
                                    expiryTelMap.remove(tel);
                                }
                            } else {
                                data.element("msg", "您输入的手机验证码有误");
                            }
                        } else {
                            data.element("msg", "请输入6~12位的密码");
                        }
                    } else {
                        data.element("msg", "请输入正确的手机号码");
                    }
                } else {
                    data.element("msg", "请输入验证码");
                }
            } else {
                data.element("msg", "请先获取手机验证码");
            }
        } else {
            data.element("msg", "该手机号已被注册");
        }
        Dto.returnJosnMsg(response, data);
    }

    /**
     * 获取验证码
     */
    @RequestMapping("/getdxCode.json")
    public void getdxCode(@RequestParam String tel, @RequestParam String platform, HttpServletRequest request, HttpServletResponse response) {

        JSONObject data = new JSONObject();
        if (platform == null || "".equals(platform)) {//平台号
            data.put("code", 0);
            data.put("msg", "请输入平台号");
        } else {
            data.element("code", "0");
            data.element("msg", "操作失败，稍后重试");
            String id = request.getParameter("id");
            String codeKey = request.getParameter("codeKey");
            if (!Dto.stringIsNULL(codeKey)) {
                if (!Dto.stringIsNULL(tel) && Pattern.matches(Dto.REGEX_MOBILE, tel)) {
                    JSONObject user = loginService.getUserByTel(tel, false, null);
                    if (Dto.isObjNull(user) || (!Dto.stringIsNULL(id) && (id.equals(user.getString("id")) || "0".equals(id)))) {
                        if (!hitMap.containsKey(tel)) {
                            if (!expiryTelMap.containsKey(tel)) {
                                try {
                                    String yangzhengma = smsService.sendMsgIhuyi(tel);
                                    System.out.println("》》》验证码：" + yangzhengma);
                                    //将验证码存入全局变量
                                    expiryCodeMap.put(tel, yangzhengma);
                                    expiryTelMap.put(tel, yangzhengma, 60 * 1000);
                                    System.out.println("expiryCodeMap:" + expiryCodeMap);
                                    System.out.println("expiryTelMap:" + expiryTelMap);
                                    // 60分钟内重复多次获取超出阈值记录黑名单,黑名单持续时长24小时
                                    if (verificationCodeTimes.containsKey(tel)) {
                                        verificationCodeTimes.put(tel, verificationCodeTimes.get(tel) + 1, 60 * 60 * 1000);
                                    } else {
                                        verificationCodeTimes.put(tel, 1, 60 * 60 * 1000);
                                    }
                                    if (verificationCodeTimes.containsKey(tel) && verificationCodeTimes.get(tel) > Dto.HIT_THRESHOLD) {
                                        hitMap.put(tel, "1", 24 * 60 * 60 * 1000);
                                    }

                                } catch (Exception e) {
                                    System.out.println("短信验证有异常");
                                    e.printStackTrace();
                                }

                                data.element("code", 1);
                                data.element("msg", "发送成功");
                            } else {
                                data.element("msg", "请勿重复发送验证码");
                            }
                        } else {
                            data.element("msg", "获取次数过多，请于24小时后重试");
                        }
                    } else {
                        data.element("msg", "该手机已被注册");
                    }
                } else {
                    data.element("msg", "请输入正确的手机号码");

                }
            }
        }


        Dto.returnJosnMsg(response, data);
    }

    /***
     * 修改密码
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/updateZaUserPass.json")
    public void updateZaUserPass(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject data = new JSONObject();
        data.element("code", "0");
        data.element("msg", "操作失败，稍后重试");

        String tel = request.getParameter("tel");
        String code = request.getParameter("code");
        String pwd = request.getParameter("pwd");

        System.out.println("》》您输入的验证码为：" + code);

        String codeKey = request.getParameter("codeKey");

        if (!Dto.stringIsNULL(tel)) {
            JSONObject user = loginService.getUserByTel(tel, false, null);
            if (!Dto.isObjNull(user)) {
                if (!Dto.stringIsNULL(code)) {
                    if (expiryCodeMap.containsKey(tel) && !Dto.stringIsNULL(expiryCodeMap.get(tel))) {
                        String yzm = expiryCodeMap.get(tel);
                        System.out.println("》》获取的验证码为：" + yzm);
                        if (code.equals(yzm)) {
                            if (!Dto.stringIsNULL(pwd) && pwd.length() <= 12 && pwd.length() >= 6) {
                                boolean is = loginService.updateZaUserPass(user.getLong("id"), pwd);
                                if (is) {
                                    data.element("code", "1");
                                    data.element("msg", "密码修改成功");
                                }
                            } else {
                                data.element("msg", "请输入6~12位密码");
                            }

                        } else {
                            data.element("msg", "您输入的验证码有误");
                        }
                    }
                } else {
                    data.element("msg", "请获取验证码，并输入验证码");
                }
            } else {
                data.element("msg", "不存在该电话号码的玩家，请确认后重试");
            }
        } else {
            data.element("msg", "请输入手机号");
        }

        Dto.returnJosnMsg(response, data);
    }

    /***
     * 修改密码
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/updateZaUserPass2.json")
    public void updateZaUserPass2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject data = new JSONObject();
        data.element("code", "0");
        data.element("msg", "操作失败，稍后重试");
        String pwd = request.getParameter("pwd");
        String pwd2 = request.getParameter("pwd2");
        String account = request.getParameter("account");
        JSONObject user = loginService.zauserByAccount(account);
        if (user.getString("password").equals(pwd)) {
            if (!Dto.stringIsNULL(pwd2) && pwd2.length() <= 12 && pwd2.length() >= 6) {
                boolean is = loginService.updateZaUserPass(user.getLong("id"), pwd2);
                if (is) {
                    data.element("code", "1");
                    data.element("msg", "密码修改成功");
                }
            } else {
                data.element("msg", "请输入6~12位密码");
            }
        } else {
            data.element("msg", "旧密码不正确");
        }
        Dto.returnJosnMsg(response, data);
    }

    /**
     * 修改密码获取验证码
     */
    @RequestMapping("/getUpdatePassCode.json")
    public void getUpdatePassCode(@RequestParam String tel, @RequestParam String platform, HttpServletRequest request, HttpServletResponse response) {

        JSONObject data = new JSONObject();
        if (platform == null || "".equals(platform)) {//平台号
            data.put("code", 0);
            data.put("msg", "请输入平台号");
        } else {
            data.element("code", "0");
            data.element("msg", "操作失败，稍后重试");

            String codeKey = request.getParameter("codeKey");
            if (!Dto.stringIsNULL(codeKey)) {
                if (!Dto.stringIsNULL(tel) && Pattern.matches(Dto.REGEX_MOBILE, tel)) {
                    JSONObject user = loginService.getUserByTel(tel, false, null);
                    if (!Dto.isObjNull(user)) {
                        if (!hitMap.containsKey(tel)) {
                            if (!expiryTelMap.containsKey(tel)) {
                                try {
                                    String verificationCode = smsService.sendMsgIhuyi(tel);
                                    System.out.println("》》》验证码：" + verificationCode);
                                    //将验证码存入全局变量
                                    expiryCodeMap.put(tel, verificationCode);
                                    expiryTelMap.put(tel, verificationCode, 60 * 1000);
                                    System.out.println("expiryCodeMap:" + expiryCodeMap);
                                    System.out.println("expiryTelMap:" + expiryTelMap);
                                    // 60分钟内重复多次获取超出阈值记录黑名单,黑名单持续时长24小时
                                    if (verificationCodeTimes.containsKey(tel)) {
                                        verificationCodeTimes.put(tel, verificationCodeTimes.get(tel) + 1, 60 * 60 * 1000);
                                    } else {
                                        verificationCodeTimes.put(tel, 1, 60 * 60 * 1000);
                                    }
                                    if (verificationCodeTimes.containsKey(tel) && verificationCodeTimes.get(tel) > Dto.HIT_THRESHOLD) {
                                        hitMap.put(tel, "1", 24 * 60 * 60 * 1000);
                                    }

                                    data.element("data", verificationCode);
                                } catch (Exception e) {
                                    System.out.println("短信验证有异常");
                                    e.printStackTrace();
                                }

                                data.element("code", 1);
                                data.element("msg", "发送成功");
                            } else {
                                data.element("msg", "请勿重复获取验证码");
                            }
                        } else {
                            data.element("msg", "获取次数过多，请于24小时后重试");
                        }
                    } else {
                        data.element("msg", "不存在该手机号码的用户");
                    }
                } else {
                    data.element("msg", "请输入正确的手机号码");

                }
            }
        }


        Dto.returnJosnMsg(response, data);
    }

    @RequestMapping("wxlogin")
    public void wxlogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String domainstr = request.getServerName();
        String url2 = gsbiz.getdomain(domainstr);
        JSONObject user = (JSONObject) request.getSession().getAttribute("userInfo");
        user.put("headimg", "http://"+propertiesUtil.get("ip")+"/zagame"+ user.getString("headimg"));
        JSONObject data = userService.getUserInfoByOpenID(user.getString("openid"), false, null);
        String url = request.getParameter("url");
        if (Dto.isNull(data)) {
            String account = LoginOauth.creatorAccount(userService);
            if ("-1".equals(account)) {
                System.out.println("生成用户account 错误");
                return;
            }
            JSONObject globalinfo = globalService.getGlobalInfo(url2);
            int roomcard = 99;
            int coins = 1999;
            if (globalinfo != null) {
                roomcard = globalinfo.getInt("roomcard");
                coins = globalinfo.getInt("coins");
            }
            boolean is = loginService.registerUser(account, user.getString("nickname"), null, null, null, 0, roomcard, coins, 0, null, 0, UUID.randomUUID().toString(), user.getString("headimgurl"), user.getString("openid"), url2);
            data = userService.getUserInfoByOpenID(user.getString("openid"), false, null);
        }
        request.getSession().setAttribute(Dto.LOGIN_USER, data);
        response.sendRedirect("/zagame" + url);
    }


    /***
     * 用户注册
     * 金皇冠，需同步表，故另写方法
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/doUserRegister_gold.json")
    public void userRegister_gold(HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject data = new JSONObject();
        data.element("code", "0");
        data.element("msg", "操作失败，稍后重试");
        String domainstr = request.getServerName();
        String url = gsbiz.getdomain(domainstr);
        String tel = request.getParameter("tel");
        String code = request.getParameter("code");
        String pwd = request.getParameter("pwd");
        String pwd2 = request.getParameter("pwd2");
        String sex = request.getParameter("sex");
        String name = request.getParameter("name");
        String parid = request.getParameter("parid");
        String recUsers = "0";
        String recUserId = "0";
        if (!Dto.stringIsNULL(sex)) {
            sex = new String(sex.getBytes("iso8859-1"), "utf-8");
        } else {
            sex = "男";
        }
        if (!Dto.stringIsNULL(parid)) {
            JSONObject pazu = loginService.zauserById(parid);
            if (!Dto.isObjNull(pazu)) {
                JSONObject pabu = loginService.baseuserByUnionid(pazu.getString("unionid"));
                if (!Dto.isObjNull(pabu)) {
                    recUserId = pabu.getString("id");
                    recUsers = pabu.getString("recUsers") + "$" + pabu.getString("id");
                }
            }
        }


        System.out.println("》》您输入的验证码为：" + code);
        System.out.println("》》获取的验证码Map：" + expiryCodeMap);

        String codeKey = request.getParameter("codeKey");
        if (expiryCodeMap.containsKey(tel) && !Dto.stringIsNULL(expiryCodeMap.get(tel))) {
            String verificationCode = expiryCodeMap.get(tel);
            System.out.println("》》获取的验证码为：" + verificationCode);
            if (!Dto.stringIsNULL(code)) {
                if (pwd.equals(pwd2)) {
                    if (!Dto.stringIsNULL(tel) && Pattern.matches(Dto.REGEX_MOBILE, tel)) {
                        if (!Dto.stringIsNULL(pwd) && pwd.length() <= 12 && pwd.length() >= 6) {
                            if (verificationCode.equals(code)) {
                                String account = LoginOauth.creatorAccount(userService);
                                if ("-1".equals(account)) {
                                    System.out.println("生成用户account 错误");
                                    return;
                                }
                                // 获取配置信息
                                JSONObject globalinfo = globalService.getGlobalInfo(url);
                                int roomcard = 0;
                                int coins = 0;
                                if (globalinfo != null) {
                                    roomcard = globalinfo.getInt("roomcard");
                                    coins = globalinfo.getInt("coins");
                                }

                                if (Dto.stringIsNULL(name)) {
                                    name = account;
                                } else {
                                    name = new String(name.getBytes("iso8859-1"), "utf-8");
                                }

                                String ip = "";
                                if (request.getHeader("x-forwarded-for") == null) {
                                    ip = request.getRemoteAddr();
                                } else {
                                    ip = request.getHeader("x-forwarded-for");
                                }

                                Boolean isRegister = true;
                                JSONObject glodset = globalService.getGlodSysSetting();
                                if (!Dto.stringIsNULL(glodset.getString("ipRegisterLimit"))) {
                                    JSONObject tr = JSONObject.fromObject(glodset.getString("ipRegisterLimit"));
                                    int everyday = tr.getInt("everyday");
                                    int total = tr.getInt("total");
                                    if (everyday != -1 && total != -1) {
                                        String starTime = DateUtils.getDate("yyyy-MM-dd") + " 00:00:00";
                                        String endTime = DateUtils.getDate("yyyy-MM-dd") + " 23:59:59";
                                        int todaycount = loginService.getRegiserIpUserCount(ip, starTime, endTime);
                                        starTime = "2000-01-01 00:00:00";
                                        int allcount = loginService.getRegiserIpUserCount(ip, starTime, endTime);
                                        if (todaycount >= everyday) {
                                            isRegister = false;
                                            data.element("msg", "今日IP可注册量已达顶，请明日再来！");
                                        } else if (allcount >= total) {
                                            isRegister = false;
                                            data.element("msg", "当前IP可注册量已达顶！");
                                        }
                                    }

                                }

                                if (isRegister) {
                                    boolean is = loginService.registerUser(account, name, sex, tel, pwd, 1, roomcard, coins, 0, ip, 0, UUID.randomUUID().toString(), "1", null, url);

                                    if (is) {

                                        JSONObject user = loginService.getUserByTel(tel, true, ip);
                                        JSONObject settingJson = new JSONObject();
                                        settingJson.put("music", 0.8);
                                        settingJson.put("musicOn", 1);
                                        settingJson.put("volum", 0.8);
                                        settingJson.put("volumOn", 1);
                                        settingJson.put("mjVoice", 0);
                                        userService.setUserSetting(user.getLong("id"), settingJson);

                                        JSONObject pamar = new JSONObject();
                                        if (user.containsKey("headimg") && !Dto.stringIsNULL(user.getString("headimg"))) {
                                            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
                                            user.put("headimg", basePath + user.getString("headimg"));
                                        } else {
                                            user.put("headimg", "");
                                        }

                                        pamar.put("userinfo", user);
                                        // 获取配置信息
                                        if (globalinfo != null) {
                                            pamar.put("version", globalinfo.get("version"));
                                        } else {
                                            pamar.put("version", 1);
                                        }
                                        JSONObject setting = userService.getUserSetting(user.getLong("id"));
                                        if (setting != null) {
                                            pamar.put("setting", setting);
                                        }

                                        pamar.put("unReadMsg", 0);    //	用户未读消息数

                                        data.element("msg", "注册成功");
                                        data.element("data", pamar);
                                        data.element("code", "1");

                                        //金皇冠特殊化操作，随机生成openid及unionid并同步base_user
                                        String mark = loginService.getUoid(18);
                                        loginService.updZauserUnionIdandOpenId(user.getLong("id"), mark);

                                        loginService.insertBaseUser(account, name, tel, "1", recUserId, recUsers, mark, mark);
                                        //设置默认密码888888
                                        loginService.updZauserSafe(user.getLong("id"), "888888");
                                        //生成推广二维码
                                        Map<String, String> map = new HashMap<String, String>();
                                        String backMsg = HttpReqUtil.doPost(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/gameproxy/getPlayerQR_gold.json?userId=" + user.getLong("id"), map, null, "utf-8");


                                        if (!recUserId.equals("0")) {
                                            //添加推广记录
                                            loginService.insertInviteRec(recUserId, user.getString("id"), "");
                                        }
                                        //	清除验证码
                                        expiryCodeMap.remove(codeKey);
                                        expiryTelMap.remove(codeKey);
                                    }
                                }
                            } else {
                                data.element("msg", "您输入的手机验证码有误");
                            }
                        } else {
                            data.element("msg", "请输入6~12位的密码");
                        }
                    } else {
                        data.element("msg", "请输入正确的手机号码");
                    }
                } else {
                    data.element("msg", "两次输入的密码不一致，请确认");
                }
            } else {
                data.element("msg", "请输入验证码");
            }
        } else {
            data.element("msg", "请先获取手机验证码");
        }


        Dto.returnJosnMsg(response, data);
    }


    @RequestMapping("/goLogin_JHG.html")
    public String goLogin_JHG(HttpServletResponse response, HttpServletRequest request) throws Exception {

        String parid = request.getParameter("userId");
        request.setAttribute("parid", parid);
        return "/work/item/tele";
    }

    @RequestMapping("/gologin_jqm.json")
    public void gologin_jqm(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String domainstr = request.getServerName();
        String url = gsbiz.getdomain(domainstr);
        JSONObject data = new JSONObject();
        data.element("code", "0");
        data.element("msg", "操作失败，稍后重试");
        String platform = request.getParameter("platform");
        String jqm = request.getParameter("jqm");//机器码
        JSONObject user = loginService.gologin_jqm(jqm);
        if (!Dto.isObjNull(user) && user.getString("status").equals("1")) {
            if (user.getInt("isMachine") == 1) {//代表用机器码登陆
                String ip = "";
                if (request.getHeader("x-forwarded-for") == null) {
                    ip = request.getRemoteAddr();
                } else {
                    ip = request.getHeader("x-forwarded-for");
                }
                user = loginService.getUserByTel(user.getString("tel"), true, ip);

                JSONObject pamar = new JSONObject();

                if (user.containsKey("headimg") && !Dto.stringIsNULL(user.getString("headimg"))) {
                    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
                    if (!Dto.stringIsNULL(platform)) {
                        if ("JHG".equals(platform))
                            basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/gameproxy/";
                    }

                    user.put("headimg", basePath + user.getString("headimg"));
                } else {
                    user.put("headimg", null);
                }

                pamar.put("userinfo", user);
                // 获取配置信息
                JSONObject globalinfo = globalService.getGlobalInfo(url);
                if (globalinfo != null) {

                    pamar.put("version", globalinfo.get("version"));
                } else {
                    pamar.put("version", 1);
                }
                JSONObject setting = userService.getUserSetting(user.getLong("id"));
                if (setting != null) {
                    pamar.put("setting", setting);
                }

                pamar.put("unReadMsg", 0);    //	用户未读消息数

                data.element("msg", "登录成功");
                data.element("data", pamar);
                data.element("code", "1");

                if (!Dto.stringIsNULL(platform)) {
                    //将用户当前平台
                    Dto.PLATFORM_MAP.put(user.getString("id"), platform);
                }
            } else {
                data.element("code", "0")
                        .element("tel", user.getString("tel"))
                        .element("password", user.getString("password"));
            }

        } else {
            data.element("msg", "登录失败");
            data.element("code", "0");
        }
        System.out.println("数据更新结果：" + data);
        Dto.returnJosnMsg(response, data);

    }
}
