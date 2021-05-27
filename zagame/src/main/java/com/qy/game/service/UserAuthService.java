package com.qy.game.service;

import net.sf.json.JSONObject;


public interface UserAuthService {

    /**
     * 用户认证天添加
     *
     * @param userID
     * @param name
     * @param idCard
     * @return
     */
    JSONObject insertUserAuth(long userID, String name, String idCard, String platform);

    /**
     * 获取用户认证结果
     *
     * @param userID
     * @return
     */
    JSONObject getUserAuth(long userID);


    /**
     * 用户加盟添加
     *
     * @param userID
     * @param name
     * @param tel
     * @return
     */
    JSONObject insertUserJoin(long userID, String name, String tel);


}
