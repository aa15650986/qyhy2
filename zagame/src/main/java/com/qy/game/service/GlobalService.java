package com.qy.game.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface GlobalService {


    /**
     * 获取微信分享内容
     *
     * @param gid      游戏id
     * @param type     分享类型
     * @param platform
     * @return
     */
    JSONObject getWXShare(int gid, int type, String platform);

    /**
     * 获取玩家战绩
     *
     * @param uid
     * @param gid
     * @return
     */
    JSONArray getUserGameLogList(long uid, int gid);


    /**
     * 获取玩家战绩（战绩汇总）
     *
     * @param uid
     * @param gid
     * @return
     */
    JSONArray getUserGameList(long uid, int gid);

    /**
     * 获取玩家战绩（战绩汇总）
     *
     * @param uid
     * @param gid
     * @return
     */
    JSONArray getUserGameList3(long uid, int gid, String type);

    /**
     * 获取游戏记录
     *
     * @param gid    游戏编号
     * @param roomNo 房间号
     * @return
     */
    JSONObject getGameLog(int gid, String roomNo);


    /**
     * 根据游戏记录ID获取游戏记录信息
     *
     * @param glog_id
     * @return
     */
    JSONObject getGameLogById(long glog_id);

    /**
     * 获取活动列表
     *
     * @return
     */
    JSONArray getActivityList();


    /**
     * 获取活动详情
     *
     * @param id
     * @return
     */
    JSONObject getActivityDetail(long id);


    /**
     * 获取全局配置信息
     *
     * @return
     */
    JSONObject getGlobalInfo(String url);


    /**
     * 获取应用对应版本的配置信息
     *
     * @param version
     * @return
     */
    JSONObject getVersionInfo(String version);

    /**
     * 获取应用对应版本的配置信息(新)
     *
     * @param version
     * @return
     */
    JSONObject getVersionInfo_new(String platform);

    /**
     * 获取单条用户游戏记录
     *
     * @param version
     * @return
     */
    JSONObject getuserlogsInfo(long id);

    /**
     * 获取单条房间游戏记录
     *
     * @param version
     * @return
     */
    JSONObject getgamelogsInfo(long id);

    /**
     * 获取当前版本号
     *
     * @return
     */
    JSONObject getGlobalVersion(String platform);

    /**
     * 获取战绩详情
     *
     * @param roonid
     * @return
     */
    JSONArray getzausergamelogs(String roonid);

    /**
     * 获取总决算游戏记录
     *
     * @param version
     * @return
     */
    JSONObject getuserlogs2(long id);

    /**
     * 获取十三水结算数据
     *
     * @param version
     * @return
     */
    JSONObject getJesuanSSS(long id);

    /**
     * 获取玩家战绩
     *
     * @param uid
     * @param gid
     * @return
     */
    JSONArray getUserGameLogList3(long uid, int gid, String type);

    /**
     * 获取创建房间的属性
     *
     * @param type
     * @param val
     * @return
     */
    String getzagame(String type, String val, String gid, String platform);


    /**
     * 获取金皇冠配置信息
     *
     * @return
     */
    JSONObject getGlodSysSetting();

    /**
     * 获取游戏的全部信息
     *
     * @return
     */
    JSONArray getgames();

}
