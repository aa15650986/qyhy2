package com.qy.game.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface HService {

    /**
     * 获取基础数据
     *
     * @return
     */
    JSONObject gosysybaserec();

    /**
     * 开启管理栏目
     *
     * @param id
     */
    void upbaseismage(String id);

    /**
     * 关闭管理栏目
     */
    void closemage(String id);

    /**
     * 增加发送房卡的
     *
     * @param id
     * @param price
     */
    String addusersendroom(String id, String price);

    /**
     * 进入领取别人发送的房卡
     *
     * @param id
     * @return
     */
    JSONObject getusersendroom(String id);

    /**
     * 领取别人发的房卡
     *
     * @param id
     * @param userid
     */
    JSONObject addsyssendroom(String id, JSONObject user);

    /**
     * 查看领取和发送的房卡记录
     *
     * @param id
     * @return
     */
    JSONObject getusersysroom(String id);

    /**
     * 邀请好友的加入
     *
     * @param id
     * @param userID
     */
    JSONObject addcswxyqhy(String id, String userID);

    /**
     * 查看申请加入的
     *
     * @param id
     * @return
     */
    JSONArray gozamembr(String id);

    /**
     * 审核好友的加入
     *
     * @param id
     * @param sta
     */
    void upzamembr(String id, String sta);

    /**
     * 转移时看成员
     *
     * @param id
     * @return
     */
    JSONArray tozamembr(String id);

    /**
     * 进行房卡的转移
     *
     * @param id
     * @param userid
     * @param shu
     */
    void upzamemroom(String id, String userid, String shu);

    /**
     * 查看创建房间的记录
     *
     * @param id
     * @return
     */
    JSONArray H5gamerooms(String id);

    /**
     * 查询当前进去房间的人要不要审核
     *
     * @param roon
     * @param id
     * @return
     */
    JSONObject H5member(String roon, String id);

    /**
     * 房主同意加入房间
     *
     * @param roon
     * @param id
     */
    void H5addmember(String roon, String id);

    /**
     * 查询金币排行
     *
     * @param param
     * @return
     */
    JSONArray getUserListForTopCoin(JSONObject param);

    /**
     * 获得当前可用的游戏列表
     *
     * @return
     */
    JSONArray getCanUseGameList(int sta);

    /**
     * 获得游戏胜率统计
     *
     * @param param
     * @return
     */
    JSONObject getGameCountByGid(JSONObject param);

    /**
     * 检查用户是否被房主邀请
     *
     * @param ownerID
     * @param userID
     * @return
     */
    boolean isVisited(Long ownerID, Long userID);


}
