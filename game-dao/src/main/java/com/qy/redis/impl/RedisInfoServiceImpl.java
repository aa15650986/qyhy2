

package com.qy.redis.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.SocketIOClient;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import com.qy.dao.DBUtil;
import com.qy.redis.RedisInfoService;
import com.qy.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RedisInfoServiceImpl implements RedisInfoService {
    private Logger log = LoggerFactory.getLogger(RedisInfoServiceImpl.class);
    @Resource
    private RedisService redisService;

    public RedisInfoServiceImpl() {
    }


    @Override
    public JSONArray getGameSetting(int var1, String var2, int var3) {
        return null;
    }

    @Override
    public void delGameSettingAll() {

    }

    @Override
    public SocketIOClient getSocketIOClientByUid(String var1) {
        return null;
    }

    @Override
    public boolean isOnline(String var1) {
        return false;
    }

    @Override
    public JSONObject getSysGlobalByKey(String var1, String var2) {
        return null;
    }

    @Override
    public JSONObject getGlobalByKey(String var1) {
        return null;
    }

    @Override
    public void delSysGlobalByKey(String var1, String var2) {

    }

    @Override
    public void delSysGlobal() {

    }

    @Override
    public JSONObject getGameCircleInfoByid(String var1) {
        return null;
    }

    @Override
    public void delGameCircleInfoByid(String var1) {

    }

    @Override
    public JSONObject getGameInfoById(int var1) {
        return null;
    }

    @Override
    public void delGameInfoById(int var1) {

    }

    @Override
    public void delGameInfoAll() {

    }

    @Override
    public long summaryTimes(String var1, String var2) {
        return 0;
    }

    @Override
    public void insertSummary(String var1, String var2) {

    }

    @Override
    public void delSummary(String var1, String var2) {

    }

    @Override
    public void delSummaryAll() {

    }

    @Override
    public int getAppTimeSetting(String var1, int var2, String var3) {
        return 0;
    }

    @Override
    public void delAppTimeSettingAll() {

    }

    @Override
    public List<String> getTeaInfoAllCode() {
        return null;
    }

    @Override
    public void delTeaInfoAllCode() {

    }

    @Override
    public boolean verifyTeaCode(String var1) {
        return false;
    }

    @Override
    public boolean teaRoomOpen(String var1) {
        return false;
    }

    @Override
    public void delTeaRoomOpen(String var1) {

    }

    @Override
    public void subExistCreateRoom(String var1) {

    }

    @Override
    public void addExistCreateRoom(String var1, int var2) {

    }

    @Override
    public boolean isExistCreateRoom(String var1, boolean var2) {
        return false;
    }

    @Override
    public void delExistCreateRoom(String var1) {

    }

    @Override
    public JSONObject getSysInfo(String var1) {
        return null;
    }

    @Override
    public boolean idempotent(String var1) {
        return false;
    }

    @Override
    public void delTeaSys() {

    }

    @Override
    public boolean getStartStatus(String var1, String var2) {
        return false;
    }

    @Override
    public JSONArray getGameSssSpecialSetting() {
        return null;
    }

    @Override
    public void delGameSssSpecialSetting() {

    }

    @Override
    public boolean getHasControl(String var1) {
        return false;
    }

    @Override
    public void delHasControl() {

    }

    @Override
    public long createCircle(String var1) {
        return 0;
    }

    @Override
    public String getCreateCircle(String var1) {
        return null;
    }

    @Override
    public void delCreateCircle(String var1) {

    }

    @Override
    public void delAllCreateCircle() {

    }

    @Override
    public Map<String, String> getSocketInfo() {
        HashMap map = new HashMap();

        try {
            String key = "sys_socket";
            JSONArray data = null;
            Object s = this.redisService.queryValueByKey(key);
            if (null != s) {
                data = JSON.parseArray(JSONArray.toJSON(s).toString());
            }

            if (null == data) {
                data = DBUtil.getObjectListBySQL("SELECT * FROM sys_socket s WHERE s.`is_deleted`=0", new Object[0]);
                if (null == data || data.size() == 0) {
                    return map;
                }

                this.redisService.insertKey(key, String.valueOf(data), 600L);
            }

            int i = (int)((double)data.size() * Math.random());
            if (data.getJSONObject(i).containsKey("socket_ip") && data.getJSONObject(i).containsKey("socket_port") && data.getJSONObject(i).containsKey("headimg_url") && data.getJSONObject(i).containsKey("headimg_url")) {
                map.put("socketIp", data.getJSONObject(i).getString("socket_ip"));
                map.put("socketPort", data.getJSONObject(i).getString("socket_port"));
                map.put("headimgUrl", data.getJSONObject(i).getString("headimg_url"));
                map.put("payUrl", data.getJSONObject(i).getString("pay_url"));

            }
        } catch (Exception var6) {
            this.log.info("server获取sys_socket异常{}", var6);
        }

        return map;
    }

    @Override
    public void delSokcetInfo() {

    }

    @Override
    public JSONObject getAttachGameLog(Object var1) {
        return null;
    }

    @Override
    public long readMessageRepeatCheck(long var1) {
        return 0;
    }

    @Override
    public void delReadMessageRepeatCheck(long var1) {

    }

    @Override
    public void delAllReadMessageRepeatCheck() {

    }
}
