package com.qy.game.service;

import net.sf.json.JSONObject;

public interface MiscellService {

    /**
     * 获取转盘的基础数据
     *
     * @return
     */
    JSONObject systurntab(String id, String platform);

    /**
     * 进行转盘
     *
     * @param id
     * @return
     */
    JSONObject delturntabmeny(String id, String platform);

    /**
     * 金黄冠福袋分享完获取的钱
     *
     * @param id
     */
    void JHGfd(String id, String type);

    /**
     * 金黄冠获取每天的分享金币和总的分享金币
     *
     * @param id
     */
    JSONObject JHGcosin(String id);

    /**
     * 获取微招代理的微信好
     *
     * @return
     */
    JSONObject zdlwase(String url);

    /**
     * 获取用户头像
     *
     * @return
     */
    JSONObject gouserimg();

    /**
     * 更新用户头像
     *
     * @param id
     * @param img
     */
    void JHGupuserimg(String id, String img);

    /**
     * 获取游戏的滚动条
     *
     * @return
     */
    JSONObject getmessage9();

    /**
     * 把机器码清空
     *
     * @param id
     */
    void upMachinenull(String id);

    /**
     * 检查福袋分享完有没有插入数据
     *
     * @param id
     * @return
     */
    String czJHGfd(String id);
}
