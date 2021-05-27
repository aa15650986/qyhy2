package com.qy.game.service.impl;

import com.qy.game.db.DBUtil;
import com.qy.game.model.GameServer;
import com.qy.game.service.GameService;
import com.qy.game.utils.DateUtils;
import com.qy.game.utils.Dto;
import com.qy.game.utils.MathDelUtil;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.bean.PageUtil;
import com.qy.game.ssh.dao.SSHUtilDao;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
@Service
@Transactional
public class GameServiceImpl implements GameService {

    @Resource
    SSHUtilDao sshUtilDao;

    @Override
    public int joinGameServer(String ip, int port, String serverName) {

        GameServer gameServer = new GameServer(serverName, ip, port);

        // 保存到数据库

        // 加到服务器队列
//		LVSServer.gameServers.add(gameServer);

        System.out.println(serverName + ":" + ip + ":" + port + "加入队列");

        return 1;
    }

    @Override
    public JSONArray getAllGameList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JSONObject getRoomInfoByRno(int gid, String roomNo, Integer status) {

        String sql;
        Object[] params;
        String tj = "user_id0,user_id1,user_id2,user_id3,user_id4,user_id5,game_index,game_count,base_info";
        if (status != null) {

            sql = "select " + tj + " from za_gamerooms where status=? and  room_no=? and game_id=? order by id desc";
            params = new Object[]{status, roomNo, gid};
        } else {
            sql = "select " + tj + " from za_gamerooms where room_no=? and game_id=? order by id desc";
            params = new Object[]{roomNo, gid};
        }
        //return DBUtil.getObjectBySQL(sql, params);
        return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
    }

    @Override
    public JSONObject getRoomInfo(long gid, String roomNo) {
        String user = "user_id0,user_icon0,user_name0,user_score0,user_id1,user_icon1,user_name1,user_score1,user_id2,user_icon2,user_name2,user_score2," +
                " user_id3,user_icon3,user_name3,user_score3,user_id4,user_icon4,user_name4,user_score4,user_id5,user_icon5,user_name5,user_score5," +
                " user_id6,user_icon6,user_name6,user_score6,user_id7,user_icon7,user_name7,user_score7,user_id8,user_icon8,user_name8,user_score8," +
                " user_id9,user_icon9,user_name9,user_score9 ";
        String sql = "select status,id,base_info,roomtype,level,roomtype,createtime," + user + " from za_gamerooms where  room_no=? and game_id=? order by id desc";
        Object[] params = new Object[]{roomNo, gid};
        //return DBUtil.getObjectBySQL(sql, params);
        return TimeUtil.objectToJSONObject(JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params)));
    }

    @Override
    public JSONArray getAllRoomListByGameID(long gameID) {//没找到应用的地方

        String sql = "select * from za_gamerooms where status>=0 and game_id=?";
        Object[] params = new Object[]{gameID};
        //return DBUtil.getObjectListBySQL(sql, params);
        PageUtil pageUtil = null;
        return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params, pageUtil));
    }

    @Override
    public JSONArray getAllRoomListByGameID(long gameID, String level) {

        String sql = "select id from za_gamerooms where status>=0 and game_id=? and level=?";
        Object[] params = new Object[]{gameID, level};
        //return DBUtil.getObjectListBySQL(sql, params);
        PageUtil pageUtil = null;
        return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params, pageUtil));
    }

    @Override
    public JSONArray getGameSetting(long gameID, String platform) {
        String tj = " zg.is_open,zg.opt_key,zg.opt_name,zg.opt_val,zg.id,zg.game_id,zg.is_mul,zg.is_use,zg.sort,zga.name as gameName";
        // 根据platform获取选项信息
        if (platform != null && !platform.equals("")) {

            String sql = "select " + tj + " from za_gamesetting zg,za_games zga where zg.game_id=? and zg.memo=? and zg.is_use=1 and zga.id=zg.game_id order by zg.sort, zg.id";
            //return DBUtil.getObjectListBySQL(sql, new Object[]{gameID, platform});
            return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{gameID, platform}, 1, 10));
        }

        // 根据游戏ID取默认选项信息
        String sql = "select " + tj + " from za_gamesetting zg,za_games zga where zg.game_id=? and zg.is_use=1 and zga.id=zg.game_id order by zg.id";
        //return DBUtil.getObjectListBySQL(sql, new Object[]{gameID});
        return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{gameID}, 1, 10));

    }

    @Override
    public JSONObject createGameRoom(GameServer gameServer, JSONObject base_info, long userID, long gameID, String roomNo) {

        // 根据用户id获取需要的用户信息
        String sql = "select headimg,name,score from za_users where id=?";
        Object[] params = new Object[]{userID};
        //JSONObject user = DBUtil.getObjectBySQL(sql, params);
        JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
        if (roomNo == null) {

            // 生成一个房间号(room_no)
            boolean check = false;
            while (!check) {
                roomNo = MathDelUtil.getRandomStr(6);
                sql = "select count(id) as count from za_gamerooms where status>=0 and  room_no=? order by id desc";
                params = new Object[]{roomNo};
                //if (DBUtil.getObjectBySQL(sql, params).getInt("count") == 0)
                if (JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params)).getInt("count") == 0)
                    check = true;
            }
        }

        // 初始化游戏局数
        int turn = 0;
        if (base_info.containsKey("turn")) {
            turn = base_info.getJSONObject("turn").getInt("turn");
        }

        // 扣房卡类型（扣房主、AA）
        int paytype = 0;
        if (base_info.containsKey("paytype")) {
            paytype = base_info.getInt("paytype");
        }

        System.out.println("房间号：" + roomNo + ", 局数：" + turn);
        //roomtype   房间类型（0：房卡，1：金币）
        sql = "insert into za_gamerooms(roomtype,server_id,game_id,room_no,base_info,createtime,user_id0,user_icon0,user_name0,user_score0,ip,port,status,game_count,paytype,fangzhu) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        params = new Object[]{0, gameServer.getId(), gameID, roomNo, base_info.toString(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), userID, user.get("headimg"),
                user.get("name"), user.get("score"), gameServer.getIp(), gameServer.getPort(), 0, turn, paytype, userID};
        // 提交新的房间信息到数据库 返回受影响行数
        //int i = DBUtil.executeUpdateBySQL(sql, params);
        //if (i > 0) {
        boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, params);
        if (updObjectBySQL) {

            sql = "select ip,port,room_no,game_id,id from za_gamerooms where  room_no=? order by id desc";
            params = new Object[]{roomNo};
            //JSONObject roominfo = DBUtil.getObjectBySQL(sql, params);
            JSONObject roominfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
            return roominfo;
        }
        return null;
    }


    @Override
    public JSONObject createGameRoomYNHB(GameServer gameServer, JSONObject base_info, long userID, long gameID, String roomNo) {

        // 根据用户id获取需要的用户信息
        String sql = "select headimg,name,score from za_users where id=?";
        Object[] params = new Object[]{userID};
        //JSONObject user = DBUtil.getObjectBySQL(sql, params);
        JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
        if (roomNo == null) {

            // 生成一个房间号(room_no)
            boolean check = false;
            while (!check) {
                roomNo = MathDelUtil.getRandomStr(6);
                sql = "select count(id) as count from za_gamerooms where status>=0 and  room_no=? order by id desc";
                params = new Object[]{roomNo};
                //if (DBUtil.getObjectBySQL(sql, params).getInt("count") == 0)
                if (JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params)).getInt("count") == 0)
                    check = true;
            }
        }

        // 初始化游戏局数
        int turn = 0;
        if (base_info.containsKey("turn")) {
            turn = base_info.getJSONObject("turn").getInt("turn");
        }

        // 扣房卡类型（扣房主、AA）
        int paytype = 0;
        if (base_info.containsKey("paytype")) {
            paytype = base_info.getInt("paytype");
        }

        System.out.println("房间号：" + roomNo + ", 局数：" + turn);
        //roomtype   房间类型（0：房卡，1：金币,2代开房间，3元宝）
        sql = "insert into za_gamerooms(roomtype,server_id,game_id,room_no,base_info,createtime,user_id0,user_icon0,user_name0,user_score0,ip,port,status,game_count,paytype) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        params = new Object[]{3, gameServer.getId(), gameID, roomNo, base_info.toString(), new Date(), userID, user.get("headimg"),
                user.get("name"), user.get("score"), gameServer.getIp(), gameServer.getPort(), 0, turn, paytype};
        // 提交新的房间信息到数据库 返回受影响行数
        //int i = DBUtil.executeUpdateBySQL(sql, params);
        //if (i > 0) {
        boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, params);
        if (updObjectBySQL) {
            sql = "select ip,port,room_no,game_id,id from za_gamerooms where  room_no=? order by id desc";
            params = new Object[]{roomNo};
            //JSONObject roominfo = DBUtil.getObjectBySQL(sql, params);
            JSONObject roominfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
            return roominfo;
        }
        return null;
    }


    @Override
    public JSONObject createGameRoomAll(GameServer gameServer, JSONObject base_info, long userID, long gameID, String roomNo) {

        // 根据用户id获取需要的用户信息
        String sql = "select headimg,name,score from za_users where id=?";
        Object[] params = new Object[]{userID};
        //JSONObject user = DBUtil.getObjectBySQL(sql, params);
        JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
        if (roomNo == null) {

            // 生成一个房间号(room_no)
            boolean check = false;
            while (!check) {
                roomNo = MathDelUtil.getRandomStr(6);
                sql = "select count(id) as count from za_gamerooms where status>=0 and  room_no=? order by id desc";
                params = new Object[]{roomNo};
                //if (DBUtil.getObjectBySQL(sql, params).getInt("count") == 0)
                if (JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params)).getInt("count") == 0)
                    check = true;
            }
        }

        // 初始化游戏局数
        int turn = 0;
        if (base_info.containsKey("turn")) {
            turn = base_info.getJSONObject("turn").getInt("turn");
        }

        // 扣房卡类型（扣房主、AA）
        int paytype = 0;
        if (base_info.containsKey("paytype")) {
            paytype = base_info.getInt("paytype");
        }

        // 是否允许陌生人看到
        int open = 0;
        if (base_info.containsKey("open")) {
            open = base_info.getInt("open");
        }
        if (gameID == 4) {
            sql = "select setting from za_games where id=?";
            params = new Object[]{4};
            //JSONObject gameinfo = DBUtil.getObjectBySQL(sql, params);
            JSONObject gameinfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
            base_info.element("maxplayer", JSONObject.fromObject(gameinfo.getString("setting")).getInt("maxplayer"));
        }
        System.out.println("房间号：" + roomNo + ", 局数：" + turn);
        //roomtype   房间类型（0：房卡，1：金币,2代开房间，3元宝）
        sql = "insert into za_gamerooms(roomtype,server_id,game_id,room_no,base_info,createtime,user_id0,user_icon0,user_name0,user_score0,ip,port,status,game_count,paytype,open) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        params = new Object[]{3, gameServer.getId(), gameID, roomNo, base_info.toString(), new Date(), userID, user.get("headimg"),
                user.get("name"), user.get("score"), gameServer.getIp(), gameServer.getPort(), 0, turn, paytype, open};
        // 提交新的房间信息到数据库 返回受影响行数
        //int i = DBUtil.executeUpdateBySQL(sql, params);
        //if (i > 0) {
        boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, params);
        if (updObjectBySQL) {
            sql = "select ip,port,room_no,game_id,id from za_gamerooms where  room_no=? order by id desc";
            params = new Object[]{roomNo};
            //JSONObject roominfo = DBUtil.getObjectBySQL(sql, params);
            JSONObject roominfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
            return roominfo;
        }
        return null;
    }

    @Override
    public JSONObject createGameRoomdk(GameServer gameServer, JSONObject base_info, long userID, long gameID, String roomNo) {

        // 根据用户id获取需要的用户信息
        String sql = "select headimg,name,score from za_users where id=?";
        Object[] params = new Object[]{userID};
        //JSONObject user = DBUtil.getObjectBySQL(sql, params);
        JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
        if (roomNo == null) {

            // 生成一个房间号(room_no)
            boolean check = false;
            while (!check) {
                roomNo = MathDelUtil.getRandomStr(6);
                sql = "select count(id) as count from za_gamerooms where status>=0 and  room_no=? order by id desc";
                params = new Object[]{roomNo};
                //if (DBUtil.getObjectBySQL(sql, params).getInt("count") == 0)
                if (JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params)).getInt("count") == 0)
                    check = true;
            }
        }

        // 初始化游戏局数
        int turn = 0;
        if (base_info.containsKey("turn")) {
            turn = base_info.getJSONObject("turn").getInt("turn");
        }

        // 扣房卡类型（扣房主、AA）
        int paytype = 0;
        if (base_info.containsKey("paytype")) {
            paytype = base_info.getInt("paytype");
        }

        System.out.println("房间号：" + roomNo + ", 局数：" + turn);
        //roomtype   房间类型（0：房卡，1：金币）
        if (base_info.containsKey("gametime") && base_info.getJSONObject("gametime").getInt("time") > 0) {

            String stoptime = TimeUtil.changeTime2(new Date(), 0, 0, base_info.getJSONObject("gametime").getInt("time"));
            System.out.println("代开房间截止时间stoptime：" + stoptime);
            sql = "insert into za_gamerooms(roomtype,server_id,game_id,room_no,base_info,createtime,ip,port,status,game_count,paytype,fangzhu,stoptime) "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            params = new Object[]{2, gameServer.getId(), gameID, roomNo, base_info.toString(), new Date()
                    , gameServer.getIp(), gameServer.getPort(), 0, turn, paytype, userID, DateUtils.str3Timestamp(stoptime)};
        } else {
            sql = "insert into za_gamerooms(roomtype,server_id,game_id,room_no,base_info,createtime,ip,port,status,game_count,paytype,fangzhu) "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?)";
            params = new Object[]{2, gameServer.getId(), gameID, roomNo, base_info.toString(), new Date()
                    , gameServer.getIp(), gameServer.getPort(), 0, turn, paytype, userID};
        }
        // 提交新的房间信息到数据库 返回受影响行数
        //int i = DBUtil.executeUpdateBySQL(sql, params);
        //if (i > 0) {
        boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, params);
        if (updObjectBySQL) {


            sql = "select ip,port,room_no,game_id,id from za_gamerooms where  room_no=? order by id desc";
            params = new Object[]{roomNo};
            //JSONObject roominfo = DBUtil.getObjectBySQL(sql, params);
            JSONObject roominfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));

            //扣除房卡
            int roomcard = base_info.getJSONObject("gametime").getInt("AANum") * base_info.getInt("player");
            //扣除数据库用户游戏房卡数--(房卡扣除规则：用户下完完整一局后才扣除房间总房卡数)
            String sql4 = "update za_users set roomcard=roomcard-? where id=?";
            //DBUtil.executeUpdateBySQL(sql4, new Object[]{roomcard, userID});
            sshUtilDao.updObjectBySQL(sql4, new Object[]{roomcard, userID});
            //扣除房卡记录
            String sql1 = "insert into za_userdeduction(userid,roomid,gid,roomNo,type,sum,creataTime) values(?,?,?,?,?,?,?)";
            //DBUtil.executeUpdateBySQL(sql1, new Object[]{userID,roominfo.getInt("id"),roominfo.getInt("game_id"), roomNo,2,-roomcard, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())});
            sshUtilDao.updObjectBySQL(sql1, new Object[]{userID, roominfo.getInt("id"), roominfo.getInt("game_id"), roomNo, 2, -roomcard, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())});

            return roominfo;
        }
        return null;
    }

    @Override
    public JSONObject checkIsExistRoom(long gameID, long userID) {

//		String sql = "select ip,port,room_no,game_id,id from za_gamerooms where roomtype!=1 and status>=0 and game_id=? and (user_id0=? or user_id1=? " +
//				"or user_id2=? or user_id3=? or user_id4=? or user_id5=? or user_id6=? or user_id7=? or user_id8=? or user_id9=?) order by createtime limit 1 ";
        String sql = "select ip,port,room_no,game_id,id from za_gamerooms where status>=0 and game_id=? and (user_id0=? or user_id1=? " +
                "or user_id2=? or user_id3=? or user_id4=? or user_id5=? or user_id6=? or user_id7=? or user_id8=? or user_id9=?) order by createtime limit 1 ";
        Object[] params = new Object[]{gameID, userID, userID, userID, userID, userID, userID, userID, userID, userID, userID};
        //return DBUtil.getObjectBySQL(sql, params);
        return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
    }

    @Override
    public JSONObject checkIsExistRoom(long userID) {

//		String sql = "select zg.ip,zg.port,zg.room_no,zg.game_id,zg.id from za_gamerooms zg LEFT JOIN za_games zg1 ON zg1.id=zg.game_id  where zg.roomtype!=1 and zg.status>=0 and (zg.user_id0=? or zg.user_id1=? or zg.user_id2=? " +
//				"or zg.user_id3=? or zg.user_id4=? or zg.user_id5=? or zg.user_id6=? or zg.user_id7=? or zg.user_id8=? or zg.user_id9=?) and zg1.status=1 LIMIT 1 ";
        String sql = "select zg.ip,zg.port,zg.room_no,zg.game_id,zg.id from za_gamerooms zg LEFT JOIN za_games zg1 ON zg1.id=zg.game_id  where zg.status>=0 and (zg.user_id0=? or zg.user_id1=? or zg.user_id2=? " +
                "or zg.user_id3=? or zg.user_id4=? or zg.user_id5=? or zg.user_id6=? or zg.user_id7=? or zg.user_id8=? or zg.user_id9=?) and zg1.status=1 LIMIT 1 ";
        Object[] params = new Object[]{userID, userID, userID, userID, userID, userID, userID, userID, userID, userID};
        //return DBUtil.getObjectBySQL(sql, params);
        return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
    }

    @Override
    public JSONObject checkIsExistRoom(String roomNo, long gameID, long userID) {

//		String sql = "select ip,port,room_no,game_id,id from za_gamerooms where roomtype!=1 and status>=0 and game_id=? and room_no=? and (user_id0=? or user_id1=? or user_id2=? or user_id3=? or user_id4=? or user_id5=?)";
        String sql = "select ip,port,room_no,game_id,id from za_gamerooms where status>=0 and game_id=? and room_no=? and (user_id0=? or user_id1=? or user_id2=? or user_id3=? or user_id4=? or user_id5=?)";
        Object[] params = new Object[]{gameID, roomNo, userID, userID, userID, userID, userID, userID};
        //return DBUtil.getObjectBySQL(sql, params);
        return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
    }

    @Override
    public JSONObject checkIsExistRoom_BR(long userID) {

        //判断用户是否在百人场
        String sql = "select room_no from za_usergameroom where status>=0 and user_id=?";
        Object[] params = new Object[]{userID};
        JSONObject userroom = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
        if (!Dto.isObjNull(userroom)) {
            String roomNo = userroom.getString("room_no");
            sql = "select zg.ip,zg.port,zg.room_no,zg.game_id,zg.id from za_gamerooms zg LEFT JOIN za_games zg1 ON zg1.id=zg.game_id  where zg.status>=0 and zg.room_no=? and zg1.status=1 LIMIT 1 ";
            params = new Object[]{roomNo};
            return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
        } else {
            return null;
        }
    }

    @Override
    public JSONObject joinRoom(long userID, long gameID, String room_no) {

        JSONObject result = new JSONObject();

        // 检测用户是否在其他房间中（后台检测）
        JSONObject roomdata = checkIsExistRoom(userID);
        if (!Dto.isObjNull(roomdata)) {
            result.put("code", 1);
            result.put("data", roomdata);
            return result;
        }

        //wph修改 h5游戏不需要检查房间是否存在 为了改动尽量小 把加入的功能独立出来
        return doJoinRoom(userID, gameID, room_no);
    }

    @Override
    public JSONObject getUsers(long userID) {
        String sql = "select roomcard,coins,score,id,yuanbao from za_users where id=?";
        //return DBUtil.getObjectBySQL(sql, new Object[]{userID});
        return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{userID}));
    }

    @Override
    public JSONObject getQuickJoin(GameServer gameServer, long userID, long gameID, int coins, String option, String level) {


        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        StringBuffer sql = new StringBuffer();
        // 检查玩家是否已在房间中
        sql.append("select ip,port,room_no,game_id,id from za_gamerooms where roomtype = 1 and status >=0 and game_id = ? and level=? and ");
        if (gameID == 1 || gameID == 4) {
            sql.append(" ( user_id0 = ? or user_id1 = ? or user_id2 = ? or user_id3 = ? or user_id4 = ? or user_id5 = ?) ");
            //data = DBUtil.getObjectBySQL(sql.toString(), new Object[]{gameID,level,userID,userID,userID,userID,userID,userID});
            data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql.toString(), new Object[]{gameID, level, userID, userID, userID, userID, userID, userID}));
        } else if (gameID == 2) {
            sql.append(" ( user_id0 = ? or user_id1 = ? or user_id2 = ? ) ");
            //data = DBUtil.getObjectBySQL(sql.toString(), new Object[]{gameID,level,userID,userID,userID});
            data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql.toString(), new Object[]{gameID, level, userID, userID, userID}));
        } else if (gameID == 5) {
            sql.append(" ( user_id0 = ? or user_id1 = ? or user_id2 = ? or user_id3 = ?  ) ");
            //data = DBUtil.getObjectBySQL(sql.toString(), new Object[]{gameID,level,userID,userID,userID,userID});
            data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql.toString(), new Object[]{gameID, level, userID, userID, userID, userID}));
        } else if (gameID == 6) {
            sql.append(" ( user_id0 = ? or user_id1 = ? or user_id2 = ? or user_id3 = ? or user_id4 = ? or user_id5 = ? or user_id6=? or user_id7=? or user_id8=? or user_id9=?) ");
            //data = DBUtil.getObjectBySQL(sql.toString(), new Object[]{gameID,level,userID,userID,userID,userID,userID,userID,userID,userID,userID,userID});
            data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql.toString(), new Object[]{gameID, level, userID, userID, userID, userID, userID, userID, userID, userID, userID, userID}));
        }

        // 根据gid搜索该游戏类型是否有空的位置
        if (Dto.isObjNull(data)) {

            StringBuffer sql2 = new StringBuffer();
            sql2.append("select room_no from za_gamerooms where roomtype = 1 and status >=0 and game_id = ? and level=? and ");

            if (gameID == 1 || gameID == 4) {
                // 循环查找房间内1~6号位的空位
                sql2.append(" ( user_id0 = 0 or user_id1 = 0 or user_id2 = 0 or user_id3 = 0 or user_id4 = 0 or user_id5 = 0) ");
            } else if (gameID == 2) {
                // 循环查找房间内1~3号位的空位
                sql2.append(" ( user_id0 = 0 or user_id1 = 0 or user_id2 = 0 ) ");
            } else if (gameID == 5) {
                // 循环查找房间内1~4号位的空位（牌九）
                sql2.append("( user_id0 = 0 or user_id1 = 0 or user_id2 = 0 or user_id3 = 0 ) ");
            } else if (gameID == 6) {
                // 循环查找房间内1~10号位的空位
                sql2.append(" ( user_id0 = 0 or user_id1 = 0 or user_id2 = 0 or user_id3 = 0 or user_id4 = 0 or user_id5 = 0 or user_id6 = 0 or user_id7 = 0 or user_id8 = 0 or user_id9 = 0) ");
            }

            //JSONArray array = DBUtil.getObjectListBySQL(sql2.toString(), new Object[]{gameID,level});
            PageUtil pageUtil = null;
            JSONArray array = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql2.toString(), new Object[]{gameID, level}, pageUtil));
            if (array.size() > 0) {
                int num = Dto.getRandom(0, array.size());
                String roomNo = array.getJSONObject(num).getString("room_no");
                JSONObject roominfo = joinRoom(userID, gameID, roomNo);
                result.element("code", 1);
                result.element("data", roominfo.getJSONObject("data"));
            } else {
                // 创建房间
                JSONObject roomData = createGamePriceRoom(gameServer, userID, gameID, null, 1, coins, option, level);
                result.element("code", 1);
                result.element("data", roomData);
            }
        } else {
            result.element("code", 1);
            result.element("data", data);
        }
        return result;
    }

    @Override
    public JSONObject getQuickJoinBR(GameServer gameServer, long userID, long gameID, int coins, String option, String level) {

        JSONObject result = new JSONObject();
        //
        String sql = "select room_no from za_usergameroom where status>=0 and game_id=? and user_id=?";
        Object[] params = new Object[]{gameID, userID};
        //JSONObject userroom = DBUtil.getObjectBySQL(sql, params);
        JSONObject userroom = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));

        // 已在房间直接返回信息
        if (!Dto.isObjNull(userroom)) {
            JSONObject roomData = getRoomInfo((int) gameID, userroom.getString("room_no"));
            result.element("code", 1);
            result.element("data", roomData);
        } else {

            // 根据用户id获取需要的用户信息
            sql = "select headimg,name,score from za_users where id=?";
            params = new Object[]{userID};
            //JSONObject user = DBUtil.getObjectBySQL(sql, params);
            JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));

            JSONArray roomArray = getAllRoomListByGameID(gameID, level);

            // 存在房间，加入房间
            if (roomArray.size() > 0) {

                JSONObject gamerooms = roomArray.getJSONObject(0);
                sql = "insert into za_usergameroom(game_id,room_no,user_id,user_icon,user_name,user_score,status) values(?,?,?,?,?,?,?)";
                params = new Object[]{gameID, gamerooms.getString("room_no"), userID, user.get("headimg"), user.get("name"), user.get("score"), 0};
                //DBUtil.executeUpdateBySQL(sql, params);
                sshUtilDao.updObjectBySQL(sql, params);
                result.element("code", 1);
                result.element("data", gamerooms);
            } else {

                // TODO 创建一个新的房间

                String roomNo = "";
                // 生成一个房间号(room_no)
                boolean check = false;
                while (!check) {
                    roomNo = MathDelUtil.getRandomStr(16);
                    sql = "select count(id) as count from za_gamerooms where status>=0 and room_no=?";
                    params = new Object[]{roomNo};
                    //if (DBUtil.getObjectBySQL(sql, params).getInt("count") == 0)
                    if (JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params)).getInt("count") == 0)
                        check = true;
                }

                String baseinfo = "";
                JSONObject base_info = new JSONObject();

                if (option == null) {
                    if (gameID == 11) {
                        JSONArray array = new JSONArray();
                        for (int i = 1; i <= 5; i++) {
                            JSONObject obj = new JSONObject();
                            obj.put("name", 10 * i);
                            obj.put("val", 10 * i);
                            array.add(obj);
                        }
                        base_info.element("baseNum", array);
                        base_info.element("type", 1);
                        base_info.element("player", 6);
                    }
                    baseinfo = base_info.toString();
                } else {
                    base_info = JSONObject.fromObject(option);
                    baseinfo = option;
                }

                //roomtype   房间类型（0：房卡，1：金币）
                sql = "insert into za_gamerooms(roomtype,game_coins,server_id,game_id,room_no,base_info,createtime,ip,port,status,game_count,level) "
                        + "values(?,?,?,?,?,?,?,?,?,?,?,?)";
                params = new Object[]{1, coins, gameServer.getId(), gameID, roomNo, baseinfo, new Date(), gameServer.getIp(), gameServer.getPort(), 0, 0, level};
                // 提交新的房间信息到数据库 返回受影响行数
                //int i = DBUtil.executeUpdateBySQL(sql, params);
                //if (i > 0) {
                boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, params);
                if (updObjectBySQL) {

                    sql = "select ip,port,room_no,game_id,id from za_gamerooms where room_no=? order by id desc";
                    params = new Object[]{roomNo};
                    //JSONObject roominfo = DBUtil.getObjectBySQL(sql, params);
                    JSONObject roominfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));

                    // 插入一条新的数据
                    sql = "insert into za_usergameroom(game_id,room_no,user_id,user_icon,user_name,user_score,status) values(?,?,?,?,?,?,?)";
                    params = new Object[]{gameID, roominfo.getString("room_no"), userID, user.get("headimg"), user.get("name"), user.get("score"), 0};
                    //DBUtil.executeUpdateBySQL(sql, params);
                    sshUtilDao.updObjectBySQL(sql, params);
                    result.element("code", 1);
                    result.element("data", roominfo);
                }
            }
        }
        return result;
    }

    @Override
    public synchronized JSONObject getQuickJoinBJL(GameServer gameServer, long userID, long gameID) {

        String roomNo = "";
        JSONObject result = new JSONObject();
        //判断有没有房间存在，没有的话，创建房间
        String sql = "select room_no from za_gamerooms where status>=0 and game_id=? and roomtype=1 ";
        Object[] params = new Object[]{gameID};
        //JSONArray roomArray = DBUtil.getObjectListBySQL2(sql, params);
        PageUtil pageUtil = null;
        JSONArray roomArray = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params, pageUtil));
        //判断有没有房间存在，没有的话，创建房间
        sql = "select id from za_games where status=1 and id=?";
        params = new Object[]{gameID};
        //JSONArray games = DBUtil.getObjectListBySQL2(sql, params);
        JSONArray games = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params, pageUtil));
        if (games.size() <= 0) {
            System.out.println("百家乐房间1");
            result.put("code", 0);
            result.put("msg", "游戏暂未开放");
            result.put("data", "");
            return result;
        }

        //{"turn":10,"min":10,"max":1100,"upzhuang":10000,"baseNum":[10,20,30,50,110],"minxiazhu":100,"gamecount":20}
        if (roomArray.size() == 0) {
            Map<String, String> map = Dto.PLATFORM_MAP;
            String platform = map.get(userID + "");
            System.out.println("百家乐加入房间参数gameID:" + gameID + "-platform:" + platform);
            //创建房间
            sql = "select opt_key,opt_val from za_gamesetting where is_use = 1 and game_id=? and memo = ?";
            Object[] param2 = new Object[]{gameID, platform};
            //JSONArray setArray = DBUtil.getObjectListBySQL2(sql, param2);
            JSONArray setArray = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, param2, pageUtil));

            if (setArray.size() == 0) {
                System.out.println("百家乐房间2");
                result.put("code", 0);
                result.put("msg", "游戏暂未开放");
                result.put("data", "");
                return result;
            }

            JSONObject base_info = new JSONObject();
            for (int i = 0; i < setArray.size(); i++) {
                JSONObject da = setArray.getJSONObject(i);
                base_info.element(da.getString("opt_key"), JSONArray.fromObject(da.getString("opt_val")).getJSONObject(0).getString("val"));


            }
            System.out.println("新建房间base_infor:" + base_info);
            // 生成一个房间号(room_no)
            boolean check = false;
            while (!check) {
                roomNo = MathDelUtil.getRandomStr(6);
                sql = "select count(id) as count from za_gamerooms where status>=0 and  room_no=? order by id desc";
                params = new Object[]{roomNo};
                //if (DBUtil.getObjectBySQL(sql, params).getInt("count") == 0)
                if (JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params)).getInt("count") == 0)
                    check = true;
            }

            sql = "insert into za_gamerooms(roomtype,server_id,game_id,room_no,base_info,createtime,ip,port,status,game_count,paytype) "
                    + "values(?,?,?,?,?,?,?,?,?,?,?)";
            params = new Object[]{1, gameServer.getId(), gameID, roomNo, base_info.toString(), new Date()
                    , gameServer.getIp(), gameServer.getPort(), 0, base_info.getInt("turn"), 0};
            System.out.println("params:" + params);
            //int i = DBUtil.executeUpdateBySQL(sql, params);
            //if (i <= 0) {
            boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, params);
            if (!updObjectBySQL) {
                System.out.println("百家乐房间3");
                result.put("code", 0);
                result.put("msg", "游戏暂未开放");
                result.put("data", "");
                return result;
            }

        } else {
            roomNo = roomArray.getJSONObject(0).getString("room_no");
        }


        sql = "select room_no from za_usergameroom where status>=0 and game_id=? and user_id=? and room_no = ? ";
        params = new Object[]{gameID, userID, roomNo};
        //JSONObject userroom = DBUtil.getObjectBySQL(sql, params);
        JSONObject userroom = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));

        // 已在房间直接返回信息
        if (!Dto.isObjNull(userroom)) {
            JSONObject roomData = getRoomInfo((int) gameID, userroom.getString("room_no"));
            result.element("code", 1);
            result.element("data", roomData);
        } else {

            // 根据用户id获取需要的用户信息
            sql = "select headimg,name,score from za_users where id=?";
            params = new Object[]{userID};
            //JSONObject user = DBUtil.getObjectBySQL(sql, params);
            JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));

            String sql2 = "select ip,port,room_no,game_id,id from za_gamerooms where status>=0 and game_id=?";
            Object[] params2 = new Object[]{gameID};
            //JSONObject roomData = DBUtil.getObjectBySQL(sql2, params2);
            JSONObject roomData = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, params2));
            // 存在房间，加入房间
            if (!Dto.isObjNull(roomData)) {

                sql = "insert into za_usergameroom(game_id,room_no,user_id,user_icon,user_name,user_score,status) values(?,?,?,?,?,?,?)";
                params = new Object[]{gameID, roomData.getString("room_no"), userID, user.get("headimg"), user.get("name"), user.get("score"), 0};
                //DBUtil.executeUpdateBySQL(sql, params);
                sshUtilDao.updObjectBySQL(sql, params);
                result.element("code", 1);
                result.element("data", roomData);
            } else {
                System.out.println("百家乐房间4");
                result.put("code", 0);
                result.put("msg", "游戏暂未开放");
                result.put("data", "");
            }
        }
        return result;
    }

    @Override
    public JSONObject createGamePriceRoom(GameServer gameServer, long userID, long gameID, String roomNo, int roomtype, int coins, String option, String level) {

        // 根据用户id获取需要的用户信息
        String sql = "select headimg,name,score from za_users where id=?";
        Object[] params = new Object[]{userID};
        //JSONObject user = DBUtil.getObjectBySQL(sql, params);
        JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
        if (roomNo == null) {

            // 生成一个房间号(room_no)
            boolean check = false;
            while (!check) {
                roomNo = MathDelUtil.getRandomStr(16);
                sql = "select count(id) as count from za_gamerooms where status>=0 and room_no=?";
                params = new Object[]{roomNo};
                //if (DBUtil.getObjectBySQL(sql, params).getInt("count") == 0)
                if (JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params)).getInt("count") == 0)
                    check = true;
            }
        }

        String baseinfo = "";
        JSONObject base_info = new JSONObject();

        if (option == null) {
            if (gameID == 1) {
                JSONArray array = new JSONArray();
                for (int i = 1; i <= 6; i++) {
                    JSONObject obj = new JSONObject();
                    obj.put("name", 10 * i);
                    obj.put("val", 10 * i);
                    array.add(obj);
                }
                base_info.element("baseNum", array);
                base_info.element("type", 1);
                base_info.element("player", 6);
            } else if (gameID == 2) {
                base_info.element("baseNum", coins);
                base_info.element("multiple", 100);
                base_info.element("goldcoins", 2000);
            } else if (gameID == 4) {
                base_info.element("type", 0);
                base_info.element("player", 6);
                base_info.element("turn", 5);
                base_info.element("color", 0);
                base_info.element("goldcoins", 100);
            } else if (gameID == 5) {
                JSONArray array = new JSONArray();
                array.add("10");
                array.add("20");
                array.add("30");
                array.add("40");
                array.add("50");
                array.add("60");
                base_info.element("baseNum", array);
                base_info.element("multiple", 10000);
            } else if (gameID == 6) {
                JSONArray array = new JSONArray();
                array.add("1");
                array.add("5");
                array.add("50");
                array.add("100");
                array.add("500");
                base_info.element("baseNum", array);
                base_info.element("player", 5);
                base_info.element("di", 1);
                base_info.element("goldcoins", 20);
                base_info.element("maxcoins", 20);
            }
            baseinfo = base_info.toString();
        } else {
            base_info = JSONObject.fromObject(option);
            baseinfo = option;
        }

        //roomtype   房间类型（0：房卡，1：金币）
        sql = "insert into za_gamerooms(roomtype,game_coins,server_id,game_id,room_no,base_info,createtime,user_id0,user_icon0,user_name0,user_score0,ip,port,status,game_count,level) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        params = new Object[]{roomtype, coins, gameServer.getId(), gameID, roomNo, baseinfo, new Date(), userID, user.get("headimg"),
                user.get("name"), user.get("score"), gameServer.getIp(), gameServer.getPort(), 0, 0, level};
        // 提交新的房间信息到数据库 返回受影响行数
        //int i = DBUtil.executeUpdateBySQL(sql, params);
        //if (i > 0) {
        boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, params);
        if (updObjectBySQL) {

            sql = "select ip,port,room_no,game_id,id from za_gamerooms where room_no=? order by id desc";
            params = new Object[]{roomNo};
            //JSONObject roominfo = DBUtil.getObjectBySQL(sql, params);
            JSONObject roominfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));

            // 将没用的座位置为-1
            if (!Dto.isObjNull(base_info) && base_info.containsKey("player")) {
                // 玩家人数
                int player = base_info.getInt("player");
                int maxCount = 10;
                if (player < maxCount) { // 当玩家少于6人时

                    params = new Object[maxCount - player + 1];
                    StringBuffer sqlsb = new StringBuffer("update za_gamerooms set ");

                    for (int j = player; j < maxCount; j++) {
                        sqlsb.append("user_id" + j + "=? ");
                        if (j != maxCount - 1) {
                            sqlsb.append(",");
                        }
                        params[j - player] = -1;
                    }
                    sqlsb.append("where id=?");
                    params[maxCount - player] = roominfo.getLong("id");
                    //DBUtil.executeUpdateBySQL(sqlsb.toString(), params);
                    sshUtilDao.updObjectBySQL(sqlsb.toString(), params);
                }
            }

            return roominfo;
        }
        return null;
    }

    @Override
    public JSONArray getGameGoldSetting(long gid, String platform) {

        String sql = "select di,goldcoins,player,`option`,memo,online from za_gamegoldsetting where game_id=? and platform=? and is_use=1 order by sort, id";
        //return DBUtil.getObjectListBySQL(sql, new Object[]{gid, platform});
        PageUtil pageUtil = null;
        return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{gid, platform}, pageUtil));
    }

    @Override
    public JSONArray getdaikaiRoomList(long userID, Timestamp stoptime) {
        //获取代开房间列表
        String sql = " select status,base_info,game_id,room_no,user_name0,user_name1,user_name2,user_name3,user_name4,user_name5 from za_gamerooms where roomtype=2 and fangzhu = ? and (stoptime > ? or stoptime is null) and status=0 LIMIT 20 ";
        //return DBUtil.getObjectListBySQL(sql, new Object[]{userID,stoptime});
        PageUtil pageUtil = null;
        return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{userID, stoptime}, pageUtil));
    }

    @Override
    public JSONArray getdaikaiRoomListgid(long userID, Timestamp stoptime, int gid) {
        //获取代开房间列表
        String sql = " select status,base_info,game_id,room_no,user_name0,user_name1,user_name2,user_name3,user_name4,user_name5 from za_gamerooms where roomtype=2 and fangzhu = ? and game_id = ? and (stoptime > ? or stoptime is null) and status=0 LIMIT 20 ";
        //return DBUtil.getObjectListBySQL(sql, new Object[]{userID,gid,stoptime});
        PageUtil pageUtil = null;
        return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{userID, gid, stoptime}, pageUtil));
    }

    @Override
    public JSONArray getyuanbaoRoomList(long gid, String url) {
        //获取元宝房间列表
        JSONArray array = new JSONArray();
        PageUtil pageUtil = null;
        String tj = "base_info,room_no,user_id0,user_id1,user_id2,user_id3,user_id4,user_id5,user_id6,user_id7,user_id8,user_id9 ";
        if (gid == -1) {
            String sql = " select " + tj + " from za_gamerooms where roomtype=3 and status=0 and open=1 ";
            //array = DBUtil.getObjectListBySQL(sql, new Object[]{});
            array = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{}, pageUtil));
        } else {
            String sql = " select " + tj + " from za_gamerooms where roomtype=3 and status=0 and game_id = ? and open=1 ";
            //array = DBUtil.getObjectListBySQL(sql, new Object[]{gid});
            array = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{gid}, pageUtil));
        }

        String sql2 = "select  opt_val,opt_key from za_gamesetting where game_id=? and memo=? AND opt_key='type'";
        Object[] objects = {gid, url};
        //JSONObject object=DBUtil.getObjectBySQL(sql2, objects);
        JSONObject object = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects));
        //获取人数未满的人
        JSONArray arr = new JSONArray();
        for (int index = 0; index < array.size(); index++) {
            JSONObject roominfo = array.getJSONObject(index);
            roominfo.element("iszs", "0");//前端进行判断有没有展示的
            //JSONObject base_info = roominfo.getJSONObject("base_info");
            JSONObject base_info = JSONObject.fromObject(roominfo.getString("base_info"));
            for (int j = 0; j < JSONArray.fromObject(object.getString("opt_val")).size(); j++) {
                if (JSONArray.fromObject(object.getString("opt_val")).getJSONObject(j).getString("val").equals(base_info.getString("type"))) {
                    roominfo.element("fytype", JSONArray.fromObject(object.getString("opt_val")).getJSONObject(j).getString("name"));
                }
            }


            int shu = 0;//房间人数
            if (roominfo.getInt("user_id0") >= 1)
                shu++;
            if (roominfo.getInt("user_id1") >= 1)
                shu++;
            if (roominfo.getInt("user_id2") >= 1)
                shu++;
            if (roominfo.getInt("user_id3") >= 1)
                shu++;
            if (roominfo.getInt("user_id4") >= 1)
                shu++;
            if (roominfo.getInt("user_id5") >= 1)
                shu++;
            if (roominfo.getInt("user_id6") >= 1)
                shu++;
            if (roominfo.getInt("user_id7") >= 1)
                shu++;
            if (roominfo.getInt("user_id8") >= 1)
                shu++;
            if (roominfo.getInt("user_id9") >= 1)
                shu++;
            roominfo.element("renshu", shu);

            arr.add(roominfo);


        }
        return arr;
    }

    @Override
    public JSONObject doJoinRoom(long userID, long gameID, String room_no) {
        JSONObject result = new JSONObject();
        String tj = "ip,port,room_no,game_id,id,stoptime,roomtype,base_info,user_id0,user_id1,user_id2,user_id3,user_id4,user_id5," +
                " user_id6,user_id7,user_id8,user_id9 ";
        // 查找相应游戏id下的房间号
        String sql = "select " + tj + " from za_gamerooms where game_id=? and  room_no=? and status=0 order by id desc";
        Object[] params = new Object[]{gameID, room_no};
        if (gameID == -1) {
            sql = "select " + tj + " from za_gamerooms where  room_no=? and status=0 order by id desc";
            params = new Object[]{room_no};
        }
        //JSONObject roominfo = DBUtil.getObjectBySQL(sql, params);
        JSONObject roominfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));

        if (Dto.isNull(roominfo)) {
            result.put("msg", "房间号不存在");
            result.put("data", "");
            result.put("code", 0);
            return result;
        }

        //代开房间判断是否到期
        if (roominfo.containsKey("stoptime") && !Dto.stringIsNULL(roominfo.getString("stoptime"))
                && roominfo.getInt("roomtype") == 2) {

            TimeUtil.transTimeStamp(roominfo, "yyyy-MM-dd HH:mm:ss", "stoptime");
            System.out.println("进入代开房间重开方法roomInfo2：" + roominfo);

            String stoptime = roominfo.getString("stoptime");
            String newtime = DateUtils.getTimestamp().toString();
            System.out.println("进入代开房间重开方法stoptime：" + stoptime + ":newtime" + newtime);
            boolean res = TimeUtil.isLatter(newtime, stoptime);
            if (res) {
                //到期、关闭房间
                sql = "update za_gamerooms set status=?  where room_no=? ";
                params = new Object[]{-1, room_no};
                //DBUtil.executeUpdateBySQL(sql, params);
                sshUtilDao.updObjectBySQL(sql, params);
                result.put("msg", "房间号不存在");
                result.put("data", "");
                result.put("code", 0);
                return result;
            }
        }


        // 循环查找房间内1~3号位的第一个空位
        int targetNum = -1;

        //cec斗地主新增修改(原因：斗地主只需要3个位置)
        if (gameID == 2 || roominfo.getInt("game_id") == 2) {
            // 循环查找房间内1~3号位的第一个空位
            for (int i = 0; i < 3; i++) {
                long user_id = roominfo.getLong("user_id" + Integer.toString(i));
                if (user_id == 0) {
                    targetNum = i;
                    break;
                }
            }

        } else if (gameID == 5 || roominfo.getInt("game_id") == 5) {

            //JSONObject base_info = roominfo.getJSONObject("base_info");
            //if(base_info!=null&&base_info.containsKey("player")){
            JSONObject base_info = JSONObject.fromObject(roominfo.getString("base_info"));
            if (!Dto.isObjNull(base_info) && base_info.containsKey("player")) {
                // 玩家人数
                int player = base_info.getInt("player");
                for (int i = 0; i < player; i++) {
                    long user_id = roominfo.getLong("user_id" + i);
                    if (user_id == 0) {
                        targetNum = i;
                        break;
                    }
                }
            }

        } else if (gameID == 3 || roominfo.getInt("game_id") == 3) { // 麻将游戏

            //JSONObject base_info = roominfo.getJSONObject("base_info");
            //if(base_info!=null&&base_info.containsKey("player")){
            JSONObject base_info = JSONObject.fromObject(roominfo.getString("base_info"));
            if (!Dto.isObjNull(base_info) && base_info.containsKey("player")) {
                // 玩家人数
                int player = base_info.getInt("player");
                if (roominfo.getLong("user_id0") == 0) {
                    targetNum = 0;
                } else if (player == 2) { // 两人场，坐对面 0，2
                    targetNum = 2;
                } else {

                    for (int i = 0; i < player; i++) {
                        long user_id = roominfo.getLong("user_id" + i);
                        if (user_id == 0) {
                            targetNum = i;
                            break;
                        }
                    }
                }
            }
        } else if (gameID == 8 || roominfo.getInt("game_id") == 8) {
            // 红包
            return doJoinRoomHongbao(roominfo, userID, gameID, room_no);
        } else if (gameID == 4 || roominfo.getInt("game_id") == 4) {
            //JSONObject base_info = roominfo.getJSONObject("base_info");
            //if(base_info!=null&&base_info.containsKey("player")){
            JSONObject base_info = JSONObject.fromObject(roominfo.getString("base_info"));
            if (!Dto.isObjNull(base_info) && base_info.containsKey("player")) {
                // 玩家人数
                int player = base_info.getInt("player");

                if (base_info.containsKey("maxplayer")) {
                    player = base_info.getInt("maxplayer");
                }
                for (int i = 0; i < player; i++) {
                    long user_id = roominfo.getLong("user_id" + i);
                    if (user_id == 0) {
                        targetNum = i;
                        break;
                    }
                }
            }
        } else {

            //JSONObject base_info = roominfo.getJSONObject("base_info");
            //if(base_info!=null&&base_info.containsKey("player")){
            JSONObject base_info = JSONObject.fromObject(roominfo.getString("base_info"));
            if (!Dto.isObjNull(base_info) && base_info.containsKey("player")) {
                // 玩家人数
                int player = base_info.getInt("player");
                for (int i = 0; i < player; i++) {
                    long user_id = roominfo.getLong("user_id" + i);
                    if (user_id == 0) {
                        targetNum = i;
                        break;
                    }
                }
            }
        }

        // 房间内没有空位
        if (targetNum < 0) {
            result.put("msg", "房间已满");
            result.put("data", "");
            result.put("code", 0);
            return result;
        }

        //元宝场-----判断房间是否是元宝场，如果是元宝场，就直接跳到元宝场的方法
        if (roominfo.containsKey("roomtype") && roominfo.getInt("roomtype") == 3) {
            // 元宝加入房间
            return doJoinRoomyuanbao(roominfo, userID, gameID, room_no, targetNum, JSONObject.fromObject(roominfo.getString("base_info")));
        }

        /**
         * 判断用户房卡不足
         * cec 2017-10-05 17:30
         */
        JSONObject baseinfo = JSONObject.fromObject(roominfo.getString("base_info"));
        if (baseinfo.containsKey("paytype") && "1".equals(baseinfo.getString("paytype"))) {
            String usql = "select roomcard from za_users where id=?";
            //JSONObject userdata=DBUtil.getObjectBySQL(usql, new Object[]{userID});
            JSONObject userdata = JSONObject.fromObject(sshUtilDao.getObjectBySQL(usql, new Object[]{userID}));
            if (baseinfo.getJSONObject("turn").getInt("AANum") > userdata.getInt("roomcard")) {
                result.put("msg", "房卡不足");
                result.put("data", "");
                result.put("code", 0);
                return result;
            }
        }


        // 房间内有空位,加入房间,更新到数据库
        sql = "select name,headimg,score from za_users where id=?";
        //JSONObject userinfo = DBUtil.getObjectBySQL(sql, new Object[] { userID });
        JSONObject userinfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{userID}));
        roominfo.put("user_id" + targetNum, userID);
        roominfo.put("user_icon" + targetNum, userinfo.get("headimg"));
        roominfo.put("user_name" + targetNum, userinfo.get("name"));
        roominfo.put("user_score" + targetNum, userinfo.get("score"));
        sql = "update za_gamerooms set " + "user_id" + targetNum + "=?," + "user_icon" + targetNum + "=?," + "user_name"
                + targetNum + "=?," + "user_score" + targetNum + "=? " + "where room_no=? and user_id" + targetNum
                + "=0";
        params = new Object[]{userID, userinfo.get("headimg"), userinfo.get("name"), userinfo.get("score"), room_no};
        //int i = DBUtil.executeUpdateBySQL(sql, params);
        //if (i > 0) {
        boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, params);
        if (updObjectBySQL) {
            result.put("msg", "");
            result.put("data", roominfo);
            result.put("code", 1);
            return result;
        } else {
            result.put("msg", "加入房间失败");
            result.put("data", "");
            result.put("code", 0);
            return result;
        }
    }

    /**
     * 红包加入房间-百人
     *
     * @param
     * @return
     */
    public JSONObject doJoinRoomHongbao(JSONObject roominfo, long userID, long gameID, String room_no) {
        JSONObject result = new JSONObject();

        // 循环查找房间内1~3号位的第一个空位
        int targetNum = -1;

        JSONObject base_info = JSONObject.fromObject(roominfo.getString("base_info"));
        //if(base_info!=null&&base_info.containsKey("player")){
        if (!Dto.isObjNull(base_info) && base_info.containsKey("player")) {
            if (base_info.getInt("type") == 0) {//传统限制人数

                // 玩家人数
                int player = base_info.getJSONObject("player").getInt("number");
                for (int i = 0; i < player; i++) {
                    long user_id = roominfo.getLong("user_id" + i);
                    if (user_id == 0) {
                        targetNum = i;
                        break;
                    }
                }

            } else if (base_info.getInt("type") == 1) {//布雷，不限人数
                // 玩家人数
                targetNum = 999;
                int player = base_info.getJSONObject("player").getInt("number");
                for (int i = 0; i < player; i++) {
                    long user_id = roominfo.getLong("user_id" + i);
                    if (user_id == 0) {
                        targetNum = i;
                        break;
                    }
                }
            }
        }

        // 房间内没有空位
        if (targetNum < 0) {
            result.put("msg", "房间已满");
            result.put("data", "");
            result.put("code", 0);
            return result;
        }
        if (targetNum == 999) {
            result.put("msg", "");
            result.put("data", roominfo);
            result.put("code", 1);
            return result;
        } else {
            Object[] params = new Object[]{gameID, room_no};
            // 房间内有空位,加入房间,更新到数据库
            String sql = "select name,headimg,score,yuanbao from za_users where id=?";
            //JSONObject userinfo = DBUtil.getObjectBySQL(sql, new Object[] { userID });
            JSONObject userinfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{userID}));


            //判断金币是否足够
            if (base_info.containsKey("roomCoins") && userinfo.getDouble("yuanbao") < base_info.getDouble("roomCoins")) {
                result.put("msg", "金币不足");
                result.put("data", "");
                result.put("code", 0);
                return result;
            }


            roominfo.put("user_id" + targetNum, userID);
            roominfo.put("user_icon" + targetNum, userinfo.get("headimg"));
            roominfo.put("user_name" + targetNum, userinfo.get("name"));
            roominfo.put("user_score" + targetNum, userinfo.get("score"));
            sql = "update za_gamerooms set " + "user_id" + targetNum + "=?," + "user_icon" + targetNum + "=?," + "user_name"
                    + targetNum + "=?," + "user_score" + targetNum + "=? " + "where room_no=? and user_id" + targetNum
                    + "=0";
            params = new Object[]{userID, userinfo.get("headimg"), userinfo.get("name"), userinfo.get("score"), room_no};
            //int i = DBUtil.executeUpdateBySQL(sql, params);
            //if (i > 0) {
            boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, params);
            if (updObjectBySQL) {
                result.put("msg", "");
                result.put("data", roominfo);
                result.put("code", 1);
                return result;
            } else {
                result.put("msg", "加入房间失败");
                result.put("data", "");
                result.put("code", 0);
                return result;
            }
        }
    }


    /**
     * 元宝加入房间
     *
     * @param
     * @return
     */
    public JSONObject doJoinRoomyuanbao(JSONObject roominfo, long userID, long gameID, String room_no, int targetNum, JSONObject base_info) {
        JSONObject result = new JSONObject();
        System.out.println("进入元宝场加入房间方法--------->");

        Object[] params = new Object[]{gameID, room_no};
        // 房间内有空位,加入房间,更新到数据库
        String sql = "select name,headimg,score,yuanbao from za_users where id=?";
        //JSONObject userinfo = DBUtil.getObjectBySQL(sql, new Object[] { userID });
        JSONObject userinfo = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{userID}));

        //判断金币是否足够
        if (base_info.containsKey("enterYB") && userinfo.getDouble("yuanbao") < base_info.getDouble("enterYB")) {
            result.put("msg", "元宝不足");
            result.put("data", "");
            result.put("code", 0);
            return result;
        }

        roominfo.put("user_id" + targetNum, userID);
        roominfo.put("user_icon" + targetNum, userinfo.get("headimg"));
        roominfo.put("user_name" + targetNum, userinfo.get("name"));
        roominfo.put("user_score" + targetNum, userinfo.get("score"));
        sql = "update za_gamerooms set " + "user_id" + targetNum + "=?," + "user_icon" + targetNum + "=?," + "user_name"
                + targetNum + "=?," + "user_score" + targetNum + "=? " + "where room_no=? and user_id" + targetNum
                + "=0";
        params = new Object[]{userID, userinfo.get("headimg"), userinfo.get("name"), userinfo.get("score"), room_no};
        //int i = DBUtil.executeUpdateBySQL(sql, params);
        //if (i > 0) {
        boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, params);
        if (updObjectBySQL) {
            result.put("msg", "");
            result.put("data", roominfo);
            result.put("code", 1);
            return result;
        } else {
            result.put("msg", "加入房间失败");
            result.put("data", "");
            result.put("code", 0);
            return result;
        }
    }

    @Override
    public JSONArray getAllGame(int status) {//没找到应用的
        String sql = "select * from za_games where status = ? order by id asc ";
        return DBUtil.getObjectListBySQL(sql, new Object[]{status});
    }


    @Override
    public JSONArray getRoomDescByGameId(long gameId) {//h5的，但在页面上找不到应用的就先放这
        // TODO Auto-generated method stub
        String sql = "select * from za_gamerooms where game_id=?";
        return DBUtil.getObjectListBySQL(sql, new Object[]{gameId});
    }


    @Override
    public JSONObject getRoomSettingByRoomId(String roomNo) {
        String sql = "select game_id,base_info,user_id0 from za_gamerooms where room_no=?";
        //return DBUtil.getObjectBySQL(sql, new Object[]{roomNo});
        return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{roomNo}));
    }

    @Override
    public JSONArray getUserAllGameList(String userId) {
        String sql = "select room_no,game_id,status,user_icon0,user_icon1,user_icon2,user_icon3,user_icon4,user_icon5,user_icon5user_id0,user_name0,base_info,createtime from za_gamerooms where (user_id0=? or user_id1=? or user_id2=? or user_id3=? or user_id4=? or user_id5=?) order by id desc";
        Object[] params = new Object[]{userId, userId, userId, userId, userId, userId};
        //return DBUtil.getObjectListBySQL(sql, params);
        PageUtil pageUtil = null;
        return TimeUtil.listToJSONArray(JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params, pageUtil)));
    }


    @Override
    public JSONArray getRightNowPlay(String userId) {
        // TODO Auto-generated method stub
        String sql = "select room_no,game_id,status,user_icon0,user_icon1,user_icon2,user_icon3,user_icon4,user_icon5,user_icon5user_id0,user_name0,base_info,createtime from za_gamerooms where status=0  and (user_id0=? or user_id1=? or user_id2=? or user_id3=? or user_id4=? or user_id5=?) order by id desc";
        Object[] params = new Object[]{userId, userId, userId, userId, userId, userId};
        //return DBUtil.getObjectListBySQL(sql, params);
        PageUtil pageUtil = null;
        return TimeUtil.listToJSONArray(JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params, pageUtil)));
    }

    @Override
    public JSONArray getPlayByMe(String userId) {
        String sql = "select room_no,game_id,status,user_icon0,user_icon1,user_icon2,user_icon3,user_icon4,user_icon5,user_icon5user_id0,user_name0,base_info,createtime from za_gamerooms where   user_id0=? order by id desc";
        Object[] params = new Object[]{userId};
        //return DBUtil.getObjectListBySQL(sql, params);
        PageUtil pageUtil = null;
        return TimeUtil.listToJSONArray(JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params, pageUtil)));
    }

    @Override
    public JSONArray getMallList() {
        String sql = "select denomination,logo,payMoney from za_mall order by id desc";
        Object[] params = new Object[]{};
        //return DBUtil.getObjectListBySQL(sql,params);
        PageUtil pageUtil = null;
        return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params, pageUtil));
    }

    @Override
    public JSONArray getEndPlay(String userId) {
        String sql = "select room_no,game_id,status,user_icon0,user_icon1,user_icon2,user_icon3,user_icon4,user_icon5,user_icon5user_id0,user_name0,base_info,createtime from za_gamerooms where status=-1 or status=-2 and (user_id0=? or user_id1=? or user_id2=? or user_id3=? or user_id4=? or user_id5=?)order by id desc";
        Object[] params = new Object[]{userId};
        //return DBUtil.getObjectListBySQL(sql,params);
        PageUtil pageUtil = null;
        return TimeUtil.listToJSONArray(JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params, pageUtil)));
    }

    @Override
    public JSONObject getTimesetting(long gid) {
        String sql = "select description,explanation from za_arena where gid=? ";
        Object[] params = new Object[]{gid};
        //return DBUtil.getObjectBySQL(sql,params);
        return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
    }

    @Override
    public JSONObject getza_games(String gid) {
        // TODO Auto-generated method stub
        String sql = "select status from za_games where id=?";
        Object[] objects = {gid};
        //JSONObject data=DBUtil.getObjectBySQL(sql, objects);
        //return data;
        JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
        return data;
    }


}
