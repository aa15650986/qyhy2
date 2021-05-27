package com.qy.game.service;

import net.sf.json.JSONObject;

/**
 * 短信接口
 */
public interface SmsService {
    /**
     * 根据短信类型获取短信信息
     *
     * @param type 1 互亿
     * @return
     */
    JSONObject getSysSmsByType(int type);

    /**
     * 互亿短信发送
     *
     * @param mobile 手机号码
     * @return
     */
    String sendMsgIhuyi(String mobile);

}
