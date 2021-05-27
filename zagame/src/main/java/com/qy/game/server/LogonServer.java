package com.qy.game.server;

import com.qy.game.constant.CommonConstant;
import com.qy.game.model.GameCircleMember;
import com.qy.game.server.dating.LoginServer;
import com.qy.game.service.GameCircleMemberService;
import com.qy.game.service.GlobalService;
import com.qy.game.service.UserService;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.utils.Dto;
import com.encry.util.EncryptionValidity;
import com.encry.util.EncryptionValidity2;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class LogonServer {

    @Resource
    private UserService userService;

    @Resource
    private GlobalService globalService;

    @Resource
    private GoldShopBizServer gsbiz;
    
    @Resource
    private GameCircleMemberService gcService;
    
   
    /**
     * 测试用户数据
     */


    private static String[] uuid = {"111111", "222222", "333333", "444444", "u1", "u2", "u3"};

    /**
     * 测试用户数组下标
     */
    private static int uuidIndex = 0;


    /**
     * 微信登录
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/wxLogin")
    public void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoginOauth login = new LoginOauth();

        login.check(request, response, globalService, gsbiz, userService);
    }

    /**
     * 微信登录(mob)
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/wxLogin_mob", method = RequestMethod.POST)
    public void login_mob(HttpServletRequest request, HttpServletResponse response,
                          @RequestParam String data,
                          @RequestParam(required = false) String version,
                          @RequestParam(required = false) String platform) throws IOException {
        LoginOauth login = new LoginOauth();
        System.out.println("微信登录(mob):" + data);
        try {
            login.wxLogin(request, data, globalService, gsbiz, userService);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String openid = JSONObject.fromObject(data).getJSONObject("data")
                .getJSONObject("user_info").getString("openid");
        this.login(request, response, openid, version, platform);
    }

    /**
     * 根据用户uuid（通过微信授权登录获取）获取用户信息
     *
     * @param openID
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(HttpServletRequest request, HttpServletResponse response, @RequestParam String openID
            , @RequestParam(required = false) String version, @RequestParam(required = false) String platform) throws IOException {
        String domainstr = request.getServerName();
        String url = gsbiz.getdomain(domainstr);
        System.out.println("version:" + version);
        System.out.println("platform:" + platform);
        //判断版本号更新游戏
        boolean result = false;
        if (!Dto.stringIsNULL(version) && !Dto.stringIsNULL(platform)) {
            if ("AKQP".equals(platform)
                    || "DDQP".equals(platform)
                    || "YOUJ".equals(platform)
                    || "HHMJ".equals(platform)
                    || "YQWNN".equals(platform)
                    || "JXQP".equals(platform)
                    || "BWMJ".equals(platform)
                    || "QMQP".equals(platform)
                    || "520".equals(platform)
                    || "XYQP".equals(platform)
                    || "YJQP".equals(platform)
                    || "MNMJ".equals(platform)
                    || "WJY".equals(platform)
                    || "DFBY".equals(platform)
                    || "MMQP".equals(platform)
                    || "HYQP".equals(platform)
                    || "MJQP".equals(platform)
                    || "SJYQP".equals(platform)
                    || "SFQP".equals(platform)
                    || "SDTQP".equals(platform)
                    || "SNSJQP".equals(platform)
            ) {
                JSONObject zaverinfo = globalService.getGlobalVersion(platform);
                System.out.println("zaverinfo:" + zaverinfo);
                if (!Dto.isObjNull(zaverinfo)) {
                    if (zaverinfo.getString("version").equals(version)) {
                        result = true;
                    }
                }
            } else {
                result = true;
            }
        }


        if (openID.equals("")) {

            if (uuidIndex >= uuid.length) {
                uuidIndex = 0;
            }
            openID = uuid[uuidIndex];
            uuidIndex++;
            result = true;//判断版本号更新游戏
        }

        JSONObject user = null;

        if (Arrays.asList(uuid).contains(openID)) {
            user = userService.getUserInfoByOpenID(openID, false, null);
        } else {
            String ip = "";
            if (request.getHeader("x-forwarded-for") == null) {
                ip = request.getRemoteAddr();
            } else {
                ip = request.getHeader("x-forwarded-for");
            }
            user = userService.getUserInfoByOpenID(openID, true, ip);
        }

        JSONObject jsonObject = new JSONObject();
        if (user == null) {
            jsonObject.put("msg", "用户登录失败");
            jsonObject.put("data", "");
            jsonObject.put("code", 0);
        } else {
            if (user.getInt("status") == 1) {
                jsonObject.put("msg", "用户登录成功");
                
                //生产token
                String token = user.getString("uuid");
                //缓存存入token 保存一周
//                RedisCacheUtil.setString(new StringBuilder().append(CacheKeyConstant.CIRCLE_TOKEN).append(":").append(user.get("id")).toString(), token, CacheKeyConstant.week);
                jsonObject.element(CommonConstant.USERS_TOKEN, token);
                jsonObject.element(CommonConstant.USERS_ID, user.get("id"));
//				String basePath="";
//				if("JHG".equals(platform))
//					basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/gameproxy/";
//				else
               
                String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
                user.put("headimg", "http://"+LoginServer.propertiesUtil.get("ip")+"/zagame"+ user.getString("headimg"));
                JSONObject data = new JSONObject();
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
                data.put("userinfo", user);
                JSONObject setting = userService.getUserSetting(user.getInt("id"));
                if (setting != null) {
                    data.put("setting", setting);
                }
                // 获取配置信息
                JSONObject globalinfo = globalService.getGlobalInfo(url);
                if (globalinfo != null) {

                    data.put("version", globalinfo.get("version"));
                } else {
                    data.put("version", 1);
                }
                //获取操作记录
               
                // 获取当前在线的游戏服务器
                Map socketMap = userService.getSokcetInfo();
                data.put("ip", socketMap.get("socketIp"));
                data.put("port", socketMap.get("socketPort"));

                jsonObject.put("data", data);
                jsonObject.put("code", 1);

                //将用户当前平台
                Dto.PLATFORM_MAP.put(user.getString("id"), platform);
            } else {
                jsonObject.put("code", 0);
                jsonObject.put("msg", "您当前账户状态异常，请联系客服");
            }

        }

        //判断版本号更新游戏
        if (!result) {
            jsonObject.put("msg", "当前版本过低，请下载并安装最新版本");
            jsonObject.put("data", "");
            jsonObject.put("code", 0);
        }

        Dto.returnJosnMsg(response, jsonObject);
    }


    /**
     * 获取用户最新信息
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/refreshUserInfo", method = RequestMethod.POST)
    public void refreshUserInfo(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam String openID) throws IOException {

        JSONObject user = userService.getUserInfoByOpenID(openID, false, null);
        if (user != null) {
            JSONObject obj = new JSONObject();
            obj.put("roomcard", user.get("roomcard"));
            obj.put("coins", user.get("coins"));
            obj.put("score", user.get("score"));
            Dto.returnJosnMsg(response, obj);
        }
    }

    /**
     * 获取用户最新信息2
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/reuserInfo", method = RequestMethod.POST)
    public void reuserInfo(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam String userid) throws IOException {
        String platform = request.getParameter("platform");
        JSONObject obj = new JSONObject();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
        if (!Dto.stringIsNULL(platform)) {
            if ("JHG".equals(platform))
                basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/gameproxy/";
        }
        JSONObject user = userService.getUserInfoByID(Long.valueOf(userid));
        if (!Dto.isObjNull(user)) {
            if (user.containsKey("headimg")) {
                user.element("headimg", basePath + user.getString("headimg"));
            }
            obj.put("user", user);
            obj.put("code", 1);
        } else {
            obj.put("code", 0);
        }
        Dto.returnJosnMsg(response, obj);

    }

    /**
     * 获取消息标题列表
     */
    @RequestMapping("/getMessageList")
    public void getMessageList(HttpServletResponse response) {

        JSONArray msgList = userService.getMessageList();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "");
        jsonObject.put("data", msgList);
        jsonObject.put("code", 1);
        Dto.returnJosnMsg(response, jsonObject);
    }

    /**
     * 根据消息id获取消息内容
     *
     * @param mid
     */
    @RequestMapping("/getMessageContent")
    public void getMessageContent(HttpServletResponse response, @RequestParam long mid) {

        JSONObject msgContent = userService.getMessageContent(mid);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "");
        jsonObject.put("data", msgContent);
        jsonObject.put("code", 1);
        Dto.returnJosnMsg(response, jsonObject);
    }

    /**
     * 获取用户设置(音量)
     *
     * @param uid
     */
    @RequestMapping(value = "/getUserSetting", method = RequestMethod.POST)
    public void getUserSetting(HttpServletResponse response, @RequestParam long uid) {

        JSONObject jsonObject = userService.getUserSetting(uid);
        JSONObject result = new JSONObject();
        if (jsonObject == null) {
            result.put("msg", "");
            result.put("data", "");
            result.put("code", 0);
        } else {
            result.put("msg", "");
            result.put("data", jsonObject);
            result.put("code", 1);
        }
        Dto.returnJosnMsg(response, result);
    }
    
    /**
    *  校验用户合法性
     * @param response
     * @param account
     * @param uid
     */
    @RequestMapping(value = "/getValidityRecord", method = RequestMethod.GET)
    @ResponseBody
    public Object getValidityRecord(HttpServletResponse response, @RequestParam String account ,@RequestParam String uid) {
        Object obj = EncryptionValidity.isValidity(account, uid);
        JSONArray array = JSONArray.fromObject(obj);
        return array.toString();
        
    }
    
    
    @RequestMapping(value = "/getValidityRecord2", method = RequestMethod.GET)
    @ResponseBody
    public Object getValidityRecord2(HttpServletResponse response, @RequestParam String account ,@RequestParam String uid) {
        Object obj = EncryptionValidity2.isValidity(account, uid);
        JSONArray array = JSONArray.fromObject(obj);
        return array.toString();
        
    }
    

    /**
     * 提交用户设置(音量)
     *
     * @param uid
     * @param uuid
     * @param music
     * @param musicOn
     * @param volum
     * @param volumOn
     */
    @RequestMapping(value = "/setUserSetting", method = RequestMethod.POST)
    public void setUserSetting(HttpServletResponse response, @RequestParam long uid, @RequestParam String uuid,
                               @RequestParam double music, @RequestParam int musicOn, @RequestParam double volum, @RequestParam int volumOn, @RequestParam int mjVoice) {

        // 检查uuid是否合法
        JSONObject uuidResult = userService.checkUUID(uid, uuid);
        // 如果不合法,返回提示信息
        if (uuidResult.getInt("code") == 0) {
            Dto.returnJosnMsg(response, uuidResult);
            return;
        }

        JSONObject settingJson = new JSONObject();
        settingJson.put("music", music);
        settingJson.put("musicOn", musicOn);
        settingJson.put("volum", volum);
        settingJson.put("volumOn", volumOn);
        settingJson.put("mjVoice", mjVoice);
        boolean i = userService.setUserSetting(uid, settingJson);
        JSONObject result = new JSONObject();
        if (i) {
            result.put("msg", "");
            result.put("data", "");
            result.put("code", 1);
        } else {
            result.put("msg", "修改失败");
            result.put("data", "");
            result.put("code", 0);
        }
        Dto.returnJosnMsg(response, result);
    }
    /**
     * 
     * @param response
     * @param count   机器人个数
     * @param circleId  联盟Id
     * @param account 	所属哪个合伙人
     * @param hp		初始体力
     */
    @RequestMapping(value = "/robot", method = RequestMethod.GET)
    public void rebot(HttpServletResponse response,@RequestParam int count,@RequestParam int circleId,@RequestParam String account,@RequestParam double hp) {
    	
    	List<String> robotIds = userService.getAllRoBotId();
    	String superUserCode = userService.getUserCodeByAccount(account);
    	for (String id : robotIds) {
    		String code = LoginOauth.creatorAccount(userService);
    		GameCircleMember gcm  = new GameCircleMember(circleId, Integer.parseInt(id), code, hp, superUserCode, 3, "HYQP", 1, 1, "2021-01-01", "2021-01-01");
    		try {
    			gcService.insertRoBotInfo(gcm);
			} catch (Exception e) {
				// TODO: handle exception
			}
    	}
    }
     
    @RequestMapping(value = "/setrobot",method = RequestMethod.GET)
    public void setRobot(HttpServletResponse response) {
    	List<String> list = userService.getAllRot();
    	System.out.println(list);
    }
    
   @RequestMapping(value = "/addRoomCard",method = RequestMethod.GET)
    public void addRoomCard(HttpServletResponse response,@RequestParam String account,@RequestParam int roomcard) {
    	System.out.println("=========="+account+"===="+roomcard+"==========");
    	boolean b = userService.updateUserAccount(account, roomcard, 0);
    	JSONObject result = new JSONObject();
    	if (b) {
    		result.put("msg", "修改钻石成功！");
        	result.put("code", 1);
		}else {
			result.put("msg", "修改钻石失败！");
        	result.put("code", 0);
		}
    	
    	 Dto.returnJosnMsg(response, result);
    }
    
    @RequestMapping(value = "/getAllUserk",method = RequestMethod.GET)
    public void realName(HttpServletResponse response) {
    	System.out.println("接收到消息");
    	JSONObject result = new JSONObject();
    	result.put("users", userService.getAllUserK());
    	Dto.returnJosnMsg(response, result); 
    }
    @RequestMapping(value = "/updateUserk",method = RequestMethod.GET)
    public void updateUserK(HttpServletResponse response,@RequestParam String account,@RequestParam int a) {
    	System.out.println("123123123123");
    	String operation = a==0?"关":"开";
    	Date date = new Date();
    	userService.insertMsg(account, operation, date); 
    	JSONObject result = new JSONObject();
    	result.put("msg", userService.updateUserK(a,account)?"成功":"失败");
    	Dto.returnJosnMsg(response, result);
    }
}
