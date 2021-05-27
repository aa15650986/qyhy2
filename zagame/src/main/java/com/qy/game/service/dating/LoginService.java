package com.qy.game.service.dating;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface LoginService {

    /**
     * 根据注册手机号码，获取玩家信息
     *
     * @param tel
     * @return
     */
    JSONObject getUserByTel(String tel, boolean isUpdateLoginTime, String ip);

    /**
     * 获取ip地址
     *
     * @param ip
     */
    String getIpAddresses(String ip);

    /**
     * 新增用户
     *
     * @param account
     * @param name
     * @param tel
     * @param lv
     * @param score
     * @param ip
     * @param isAuthentication
     * @return
     */
    boolean registerUser(String account, String name, String sex, String tel, String password, int lv, int roomcard, int coins, int score, String ip, int isAuthentication, String uuid, String headimg, String openid, String platform);

    /**
     * 新增用户
     *
     * @param account
     * @param name
     * @param tel
     * @param lv
     * @param score
     * @param ip
     * @param isAuthentication
     * @return
     */
    boolean registerUserWithPar(String account, String name, String sex, String tel, String password, int lv, int roomcard, int coins,
                                int score, String ip, int isAuthentication, String uuid, String headimg, String openid, String platform, String parUserId);


    /**
     * 更改玩家密码
     *
     * @param userId
     * @param pass
     * @return
     */
    boolean updateZaUserPass(long userId, String pass);

    /**
     * 获取分享记录
     *
     * @param userId
     * @param type
     * @return
     */
    JSONArray getAppShareRec(Long userId, int type, String statTime, String endTime);


    /**
     * 获取系统配置
     *
     * @return
     */
    JSONObject getSysBaseSet();

    /**
     * 插入分享记录
     *
     * @param userId
     * @param type
     * @return
     */
    boolean addShareRec(String userId, String type, String obj, String count, String platform);

    /**
     * 获取游戏配置
     *
     * @return
     */
    JSONObject getAPPGameSetting();

    /**
     * 获取道具扣除记录
     *
     * @param userId
     * @param doType 操作类型：1-洗牌  2-抽水
     * @param gid
     * @param roomid
     * @param roomNo
     * @return
     */
    JSONArray getAppObjRec(Long userId, int doType, String gid, String roomid, String roomNo);

    /**
     * 插入分享记录
     *
     * @param userId
     * @param type
     * @return
     */
    boolean addAppUserdeductionRec(Long userId, int type, int doType, double sum, String gid, String roomid, String roomNo, String memo, String platform);

    /**
     * 获取玩家信息
     *
     * @param account
     * @return
     */
    JSONObject zauserByAccount(String account);

    /**
     * 获取房间信息
     *
     * @return
     */
    JSONObject roomByRoomNo(String roomNo);

    /**
     * 获取玩家信息
     *
     * @param id
     * @return
     */
    JSONObject zauserById(String id);

    /**
     * 获取用户信息
     *
     * @return
     */
    JSONObject baseuserByUnionid(String unionid);

    /**
     * 获取代理信息
     *
     * @return
     */
    JSONObject baseproxyByCode(String code);

    /**
     * 修改用户上级代理
     *
     * @return
     */
    boolean changeUerParProxy(String userId, String proxyID);

    /**
     * 添加邀请记录
     *
     * @param parUserId
     * @param parProxyId
     * @param userId
     * @param memo
     * @return
     */
    boolean addInviteRec(String parUserId, String parProxyId, String userId, String memo);

    /**
     * 更新za_users 的 memo字段
     *
     * @param userId
     * @param memo
     * @return
     */
    boolean updateZaUserMemo(long userId, String memo);

    /**
     * 更新za_gamerooms的 status状态
     *
     * @return
     */
    boolean updateRoomSta(String roomNo, String status);

    /**
     * 随机生成标识码-金皇冠openid
     *
     * @param length
     * @return
     */
    String getUoid(int length);

    /**
     * 金皇冠手机注册跟新UnionId openid
     *
     * @param userId
     * @return
     */
    boolean updZauserUnionIdandOpenId(long userId, String uoid);

    /**
     * 金库密码默认
     *
     * @param userId
     * @return
     */
    boolean updZauserSafe(long userId, String safe);

    /**
     * 同步baseusers  金皇冠
     *
     * @param userCode
     * @param nickName
     * @param userTel
     * @param rank
     * @param recUserId
     * @param recUsers
     * @param unionID
     * @param openID
     * @return
     */
    boolean insertBaseUser(String userCode, String nickName, String userTel, String rank, String recUserId, String recUsers, String unionID, String openID);

    /**
     * 保存推广记录
     *
     * @param parId
     * @param userId
     * @param memo
     * @return
     */
    boolean insertInviteRec(String parId, String userId, String memo);

    /**
     * 计算当前ip已注册的玩家数
     *
     * @param ip
     * @return
     */
    int getRegiserIpUserCount(String ip, String starTime, String endTime);

    /**
     * 根据机器码查询用户数据
     *
     * @param jqm
     * @return
     */
    JSONObject gologin_jqm(String jqm);

    /**
     * 更新用户的机器码
     *
     * @param id
     * @param jqm
     */
    void upgologin_jqm(String id, String jqm);
}
