package com.qy.game.service.impl;

import com.qy.game.db.DBUtil;
import com.qy.game.service.dating.LoginService;
import com.qy.game.utils.DateUtils;
import com.qy.game.utils.Dto;
import com.qy.game.utils.IpUtils;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.bean.PageUtil;
import com.qy.game.ssh.dao.SSHUtilDao;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Transactional
@Component
@Service
public class LoginServiceImpl implements LoginService {
    private final static Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
    @Resource
    private SSHUtilDao sshUtilDao;

    @Override
    public JSONObject getUserByTel(String tel, boolean isUpdateLoginTime, String ip) {
        String sql = "SELECT id,gulidId,`name`,account,`password`,tel,sex,headimg,lv,coins,openid,unionid,`uuid`,`status`,isAuthentication,safe,vip,luck,yuanbao,roomcard,score,ip,safeprice,`area` FROM za_users where tel=?";
        JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{tel}));
        if (Dto.isNull(user)) {
            return null;
        }
        if (isUpdateLoginTime) {
            String area = user.get("area") == null ? "" : user.getString("area");
            if (ip != null) {
                area = getIpAddresses(ip);
                logger.info(">>>area>>>>" + area);
            }

            //生成新的uuid
            user.put("uuid", UUID.randomUUID().toString());
            //重置登录时间
            user.put("logintime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            //提交更新到数据库
            sql = "update za_users set `area`=?,ip=?,logintime=?,uuid=? where id=?";
            sshUtilDao.updObjectBySQL(sql, new Object[]{area, ip, user.getString("logintime"), user.getString("uuid"), user.getLong("id")});
        }
        user.remove("openID");
        user.remove("logintime");

        return user;
    }

    /**
     * 获取ip地址
     *
     * @param ip
     */
    @Override
    public String getIpAddresses(String ip) {
        String sql = "SELECT`province`,`city`FROM ip_address WHERE`ip_start_long`<= ? AND`ip_end_long`>= ?";
        System.out.println(sql);
        long ipLong = IpUtils.ipToLong(ip);
        JSONObject ipAddress = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{ipLong, ipLong}));
        if (ipAddress != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(ipAddress.get("province")).append(ipAddress.get("city"));
            return sb.toString();
        }
        return null;
    }

    @Override
    public boolean registerUser(String account, String name, String sex, String tel, String password, int lv, int roomcard, int coins, int score, String ip, int isAuthentication, String uuid, String headimg, String openid, String platform) {

        // 玩家地区
        String area = getIpAddresses(ip);
        String sql = "insert into za_users(account,name,sex,tel,password,lv,roomcard,coins,score,ip,createtime,status,isAuthentication,uuid,area,headimg,openid,platform,registerIp,registerCoins) " +
                "values(" + account + ",'" + name + "','" + sex + "','" + tel + "','" + password + "'," + lv + "," + roomcard + "," + coins + "," + score + ",'" + ip + "','" + Timestamp.valueOf(TimeUtil.getNowDate()) + "',1,0,'" + uuid + "','" + area + "','" + headimg + "','" + openid + "','" + platform + "','" + ip + "'," + coins + ")";

//		Object[] obj = {account,name,sex,tel,password,lv,roomcard,coins,score,ip,TimeUtil.getNowDate(),1,0,uuid,area,headimg,openid,platform,ip,coins};
        boolean result = sshUtilDao.updObjectBySQL(sql, new Object[]{});

        return result;
    }

    @Override
    public boolean registerUserWithPar(String account, String name, String sex, String tel, String password, int lv, int roomcard, int coins,
                                       int score, String ip, int isAuthentication, String uuid, String headimg, String openid, String platform, String parUserId) {

        // 玩家地区
        String area = getIpAddresses(ip);
        // 父级用户不存在不添加
        String getParUserSql = "select id from za_users where id=?";
        Object parUserInfo = sshUtilDao.getObjectBySQL(getParUserSql, new Object[]{parUserId});
        if (Dto.isNull(parUserInfo)) {
            return registerUser(account, name, sex, tel, password, lv, roomcard, coins, score, ip, isAuthentication, uuid, headimg, openid, platform);
        }
        String parUsers = "$" + parUserId + "$";
        String sql = "insert into za_users(account,name,sex,tel,password,lv,roomcard,coins,score,ip,createtime,status,isAuthentication,uuid,area,"
                + "headimg,openid,platform,registerIp,registerCoins,parUserId,parUsers) " +
                "values(" + account + ",'" + name + "','" + sex + "','" + tel + "','" + password + "'," + lv + "," + roomcard + "," + coins + "," + score + ",'" + ip + "','"
                + Timestamp.valueOf(TimeUtil.getNowDate()) + "',1,0,'" + uuid + "','" + area + "','" + headimg + "','" + openid + "','" + platform + "','"
                + ip + "'," + coins + "," + Long.valueOf(parUserId) + ",'" + parUsers + "')";

        boolean result = sshUtilDao.updObjectBySQL(sql, new Object[]{});

        return result;
    }

    @Override
    public boolean updateZaUserPass(long userId, String pass) {
        String sql = "update za_users set password=? where id=?";
        boolean result = sshUtilDao.updObjectBySQL(sql,
                new Object[]{pass, userId});

        return result;
    }

    @Override
    public JSONArray getAppShareRec(Long userId, int type, String statTime, String endTime) {
        String sql = "SELECT id FROM share_rec WHERE userId=? AND type=? AND createTime>=? AND createTime<=?";

        JSONArray data = JSONArray.fromObject(DBUtil.getObjectListBySQL(sql, new Object[]{userId, type, statTime, endTime}));
        return data;
    }

    @Override
    public JSONObject getSysBaseSet() {
        //String sql="SELECT appShareFrequency,appShareCircle,appShareFriend,cardname,coinsname,yuanbaoname,appShareObj FROM sys_base_set WHERE id=1";
        String sql = "SELECT card_name,coin_name,score_name,yuanbao_name FROM operator_systemsetting WHERE id=1";
        Object[] params = new Object[]{};
        JSONObject data = JSONObject.fromObject(JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params)));
        return data;
    }

    @Override
    public boolean addShareRec(String userId, String type, String obj, String count, String platform) {
        String sql = "insert into share_rec (userId,resource,type,createTime,obj,count,platform) values (?,?,?,?,?,?,?)";
        Object[] param = new Object[]{userId, "1", type, DateUtils.gettimestamp(), obj, count, platform};
        boolean result = sshUtilDao.updObjectBySQL(sql, param);

        return result;
    }

    @Override
    public JSONObject getAPPGameSetting() {
        String sql = "SELECT has_shuffle,shuffle_quantity,binding_reward,share_circle,share_frequency,share_friend,share_prop FROM operator_appsetting WHERE id=1";
        //String sql="SELECT isXipai,xipaiLayer,xipaiCount,xipaiObj,bangData FROM app_game_setting WHERE id=1";
        Object[] params = new Object[]{};
        JSONObject data = JSONObject.fromObject(JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params)));
        return data;
    }

    @Override
    public JSONArray getAppObjRec(Long userId, int doType, String gid, String roomid, String roomNo) {

        String sql = "SELECT id FROM za_userdeduction WHERE userid=? AND gid=? AND roomid=? AND roomNo=? AND doType=?";
        //JSONArray data=JSONArray.fromObject(DBUtil.getObjectListBySQL(sql, new Object[]{userId,gid,roomid,roomNo,doType}));
        JSONArray data = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{userId, gid, roomid, roomNo, doType}, new PageUtil()));
        return data;
    }

    @Override
    public boolean addAppUserdeductionRec(Long userId, int type, int doType,
                                          double sum, String gid, String roomid, String roomNo, String memo, String platform) {
        String sql = "insert into za_userdeduction (userid,roomid,roomNO,gid,type,sum,doType,creataTime,memo,platform) values (?,?,?,?,?,?,?,NOW(),?,?)";
        Object[] obj = new Object[]{userId, roomid, roomNo, gid, type, sum, doType, memo, platform};
        if (Dto.stringIsNULL(memo)) {
            sql = "insert into za_userdeduction (userid,roomid,roomNO,gid,type,sum,doType,creataTime,platform) values (?,?,?,?,?,?,?,NOW(),?)";
            obj = new Object[]{userId, roomid, roomNo, gid, type, sum, doType, platform};
        }
        boolean result = sshUtilDao.updObjectBySQL(sql, obj);

        return result;
    }

    @Override
    public JSONObject zauserByAccount(String account) {
        String sql = "SELECT id,roomcard,coins,yuanbao,password FROM za_users where account=?";
        JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{account}));
        return user;
    }

    @Override
    public JSONObject roomByRoomNo(String roomNo) {
        String sql = "SELECT status,id,user_id0,user_id1,user_id2,user_id3,user_id4,user_id5,user_id6,user_id7" +
                ",user_id8,user_id9 FROM za_gamerooms where room_no=? ORDER BY id desc ";
        JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{roomNo}));
        return data;
    }

    @Override
    public JSONObject zauserById(String id) {
        String sql = "SELECT platform,roomcard,id,coins,yuanbao,unionid,memo,unionid FROM za_users where id=?";
        JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{id}));
        return user;
    }

    @Override
    public JSONObject baseuserByUnionid(String unionid) {
        String sql = "SELECT proxyID,id,recUsers FROM base_users WHERE unionID=?";
        JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{unionid}));
        return user;
    }

    @Override
    public JSONObject baseproxyByCode(String code) {
        String sql = "SELECT sta,id FROM base_proxy WHERE proxyCode=?";
        JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{code}));
        return user;
    }

    @Override
    public boolean addInviteRec(String parUserId, String parProxyId, String userId, String memo) {
        String sql = "insert into proxy_invite_rec (parUserId,parProxyId,userId,createTime,memo) values (?,?,?,?,?)";
        Object[] obj = new Object[]{parUserId, parProxyId, userId, DateUtils.gettimestamp(), memo};
        boolean result = sshUtilDao.updObjectBySQL(sql, obj);

        return result;
    }

    @Override
    public boolean changeUerParProxy(String userId, String proxyID) {
        String sql = "UPDATE base_users SET proxyID=? WHERE id=?";
        boolean result = sshUtilDao.updObjectBySQL(sql,
                new Object[]{proxyID, userId});
        sql = "SELECT parUserID FROM base_users WHERE id=?";
        JSONObject banguser = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{userId}));
        if (!banguser.containsKey("parUserID") || Dto.stringIsNULL(banguser.getString("parUserID"))) {
            sql = "SELECT unionID FROM base_proxy WHERE id=?";
            JSONObject proxy = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{proxyID}));
            JSONObject user = baseuserByUnionid(proxy.getString("unionID"));
            if (!Dto.isObjNull(user)) {
                String parUsers = "$" + user.getString("id") + "$";
                if (user.containsKey("parUserID") && !Dto.stringIsNULL(user.getString("parUserID"))) {
                    parUsers = user.getString("parUsers") + user.getString("id") + "$";
                }
                sql = "UPDATE base_users SET parUserID=? ,parUsers=? WHERE id=?";
                sshUtilDao.updObjectBySQL(sql, new Object[]{user.getString("id"), parUsers, userId});
            }
        }


        return result;
    }

    @Override
    public boolean updateZaUserMemo(long userId, String memo) {
        String sql = "update za_users set memo=? where id=?";
        boolean result = sshUtilDao.updObjectBySQL(sql,
                new Object[]{memo, userId});

        return result;
    }

    @Override
    public boolean updateRoomSta(String roomNo, String status) {
        String sql = "update za_gamerooms set `status`=? where room_no=?";
        boolean result = sshUtilDao.updObjectBySQL(sql,
                new Object[]{status, roomNo});

        return result;
    }

    @Override
    public boolean updZauserUnionIdandOpenId(long userId, String uoid) {
        String sql = "UPDATE za_users SET unionid=?,openid=? WHERE id=?";
        boolean result = sshUtilDao.updObjectBySQL(sql,
                new Object[]{uoid, uoid, userId});

        return result;
    }

    /**
     * 根据长度随机生成
     * 26英文字和0~9随机生成
     *
     * @param length
     * @return entNum
     */
    public static String getMarkCode(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //String charOrNum ="num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = 65;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    @Override
    public String getUoid(int length) {
        String mark = getMarkCode(length);
        if (!mark.startsWith("0")) {
            String sql = "select id from za_users where unionid=?";
            if (JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{mark})) != null) {
                mark = getMarkCode(length);
            }
        } else {
            mark = getMarkCode(length);
        }
        return mark;
    }

    @Override
    public boolean insertBaseUser(String userCode, String nickName,
                                  String userTel, String rank, String recUserId, String recUsers,
                                  String unionID, String openID) {
        String sql = "INSERT INTO base_users (userCode,nickName,userTel,userBal,extractBalVip,frozenBalVip,noExtractVip,extractBalLs,frozenBalLs,noExtractBalLs,rank,recUserId,recUsers,unionID,openID,roomCards,coins,score,yuanbao,createtime) " +
                "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] obj = new Object[]{userCode, nickName, userTel, 0, 0, 0, 0, 0, 0, 0, rank, recUserId, recUsers, unionID, openID, 0, 0, 0, 0, new Date()};
        boolean result = sshUtilDao.updObjectBySQL(sql, obj);

        return result;
    }

    @Override
    public boolean updZauserSafe(long userId, String safe) {
        String sql = "update za_users set safe=? where id=?";
        boolean result = sshUtilDao.updObjectBySQL(sql,
                new Object[]{safe, userId});

        return result;
    }


    @Override
    public boolean insertInviteRec(String parId, String userId, String memo) {
        String sql = "INSERT proxy_invite_rec (parUserId,userId,createTime,memo) VALUES (?,?,NOW(),?)";
        Object[] obj = new Object[]{parId, userId, memo};
        boolean result = sshUtilDao.updObjectBySQL(sql, obj);

        return result;
    }


    @Override
    public int getRegiserIpUserCount(String ip, String starTime, String endTime) {
        String sql = "select IFNULL(COUNT(id),0) AS allcount from za_users where registerIp=? AND createtime>=? AND createtime<=? ";

        JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{ip, starTime, endTime}));
        return data.getInt("allcount");
    }


    @Override
    public JSONObject gologin_jqm(String jqm) {
        // TODO Auto-generated method stub
        String sql = "select id,gulidId,name,account,password,tel,sex,headimg,lv,coins,openid,unionid,uuid,status,isAuthentication,safe,vip,luck,safeprice from za_users where Machine=?";
        Object[] objects = {jqm};
        JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
        if (!Dto.isNull(data)) {
            data.remove("openid");
            data.remove("logintime");
            data.element("isMachine", 1);
        } else {
            sql = "select id,gulidId,name,account,password,tel,sex,headimg,lv,coins,openid,unionid,uuid,status,isAuthentication,safe,vip,luck,safeprice from za_users where Machine=?";
            Object[] objects2 = {jqm + "$"};
            data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects2));
            if (!Dto.isNull(data)) {
                data.remove("openid");
                data.remove("logintime");
                data.element("isMachine", 0);//代表有机器码但不自动登陆
            }
        }

        return data;
    }


    @Override
    public void upgologin_jqm(String id, String jqm) {
        // TODO Auto-generated method stub
        Object[] objects = {jqm, id};
        String sql2 = "update za_users set Machine=null where Machine=? and id!=?";
        sshUtilDao.updObjectBySQL(sql2, objects);
        String sql = "update za_users set Machine=? where id=?";
        sshUtilDao.updObjectBySQL(sql, objects);
    }

}
