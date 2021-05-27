package com.qy.game.service;

import com.qy.game.model.User;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public interface UserService {


    /**
     * 保存用户信息
     *
     * @param user
     * @return
     */
    long insertUserInfo(User user);

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID
     * @return
     */
    JSONObject getUserInfoByID(long id);

    /**
     * 根据用户account获取用户信息
     *
     * @param account 用户account
     * @return
     */
    JSONObject getUserInfoByAccount(String account);

    /**
     * 验证用户账号是否可用
     *
     * @param account
     * @return
     */
    boolean checkUserAccount(String account);

    /**
     * 根据用户OpenID（通过微信授权登录获取）获取用户信息
     *
     * @param openID
     * @param ip
     * @return
     */
    JSONObject getUserInfoByOpenID(String openID, boolean isUpdateUUID, String ip);

    /**
     * 检查uuid是否合法
     *
     * @param uuid
     * @return
     */
    JSONObject checkUUID(long userID, String uuid);

    /**
     * 获取消息标题列表
     *
     * @return
     */
    JSONArray getMessageList();

    /**
     * 根据消息id获取消息内容
     *
     * @return
     */
    JSONObject getMessageContent(long messageID);

    /**
     * 根据用户id获取用户设置(音量)
     *
     * @param userID
     * @return
     */
    JSONObject getUserSetting(long userID);

    /**
     * 根据用户id提交用户设置(音量)
     *
     * @param userID
     * @param settingJson
     * @return
     */
    boolean setUserSetting(long userID, JSONObject settingJson);

    /**
     * 根据用户id获取用户战绩
     *
     * @param userID
     * @return
     */
    JSONObject getUserGameLogsByUserID(long userID);

    /**
     * 更新用户房卡
     *
     * @param unionId
     * @param roomCard
     * @return
     */
    boolean updateUserRoomCardByUnionId(String unionId, int roomCard);

    /**
     * 根据用户OpenID（通过微信授权登录获取）获取用户信息
     *
     * @param openID
     * @return
     */
    JSONObject getUserDatas(String openID);


    /**
     * 更新玩家账户信息（房卡、金币）
     *
     * @param account
     * @param roomcard
     * @param coins
     * @return
     */
    boolean updateUserAccount(String account, int roomcard, int coins);

    /**
     * 更新用户信息
     *
     * @param u
     * @return
     */
    boolean updateUserInfo(User u);

    /**
     * 通过用户openid更新用户的手机号码信息
     */
    boolean updateUserTel(String tel, long id);

    /**
     * 根据用户id查询用户商城消费情况
     */
    JSONArray getMallPayByUserId(long userId);

    /**
     * 根据用户id清空用户手机号码信息
     */
    boolean clearPhoneNumByUserId(long id);

    /**
     * 通过域名得到运营商mark
     */
    JSONObject getMarkByDoMain(String domain);

    /**
     * 更新user表中的operator
     */
    boolean updateOperatorMark(String mark, String openid);

    /**
     * 根据userid查询用户operatorMark
     */
    JSONObject getOperatorMarkByOpenId(String openid);

    /**
     * 更新玩家账户信息（房卡、金币）
     *
     * @param coins
     * @return
     */
    boolean chuanrujinbi(String coins, String account);

    /**
     * 苹果上架 购买接口
     *
     * @param obj
     * @return
     */
    boolean apleMall(JSONObject obj);

    /**
     * 根据unionId，平台号获取用户信息
     *
     * @param @param  unionId
     * @param @param  platform
     * @param @return
     * @return JSONObject
     * @throws
     * @date 2018年8月14日
     */
    JSONObject getUserInfoByUnionIdAndPlatform(String unionId, String platform);


    /**
     * 更新用户信息
     *
     * @param @param id
     * @param @param openId
     * @param @param name
     * @param @param headImg
     * @param @param area
     * @return void
     * @throws
     * @date 2018年8月14日
     */
    void updateUserInfo(long id, String openId, String name, String headImg, String area);

    /**
     * 获取ip地址
     *
     * @param ip
     */
    String getIpAddresses(String ip);

    /**
     * 检查查询
     */
    void getMySql();

    /**
     * 获取所有user  account
     *
     * @return
     */
    List<String> getUserAccountList();

    /**
     * 校验user  code 是否存在redis 里
     *
     * @param code
     * @return
     */
    boolean verifyUserCode(String code);

    /**
     * 获取socket 端口号
     *
     * @return socketIp和socketPort、headimgUrl
     */
    Map<String, String> getSokcetInfo();
    
    /**
     * 
     * @return
     */
    List<String> getAllRot();
    
    /**
     * 
     * @param user
     * @return
     */
    long insertUserInfo2(User user);
    
    List<String> getAllRoBotId();
    
    String getUserCodeByAccount(String account);
    
    JSONArray getAllUserK();
    
    boolean updateUserK(int a,String account);
    
    boolean insertMsg(String account,String operation,Date date);
    
    
}