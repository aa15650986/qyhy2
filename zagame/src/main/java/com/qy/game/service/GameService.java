package com.qy.game.service;

import com.qy.game.model.GameServer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.Timestamp;

public interface GameService {


    /**
     * 加入新的服务器
     *
     * @param ip
     * @param port
     * @param serverName
     * @return
     */
    int joinGameServer(String ip, int port, String serverName);


    /**
     * 获取游戏列表
     *
     * @return
     */
    JSONArray getAllGameList();


    /**
     * 根据房间号获取房间信息
     *
     * @param roomNo
     * @param status
     * @return
     */
    JSONObject getRoomInfoByRno(int gid, String roomNo, Integer status);

    /**
     * 根据房间号获取房间信息
     *
     * @param roomNo
     * @param gid
     * @return
     */
    JSONObject getRoomInfo(long gid, String roomNo);


    /**
     * 根据游戏ID获取所有房间列表
     *
     * @param gameID
     * @return
     */
    JSONArray getAllRoomListByGameID(long gameID);

    /**
     * 根据游戏ID获取所有房间列表
     *
     * @param gameID
     * @return
     */
    JSONArray getAllRoomListByGameID(long gameID, String level);

    /**
     * 根据游戏ID获取创建房间的设置信息
     *
     * @param gameID
     * @param platform
     * @return
     */
    JSONArray getGameSetting(long gameID, String platform);

    /**
     * 创建游戏房间 - 房卡场
     *
     * @param gameServer
     * @param userID
     * @param gameID
     * @param roomNo
     * @return
     */
    JSONObject createGameRoom(GameServer gameServer, JSONObject base_info, long userID, long gameID, String roomNo);

    /**
     * 创建游戏房间 - 房卡场-红包元宝
     *
     * @param gameServer
     * @param userID
     * @param gameID
     * @param roomNo
     * @return
     */
    JSONObject createGameRoomYNHB(GameServer gameServer, JSONObject base_info, long userID, long gameID, String roomNo);

    /**
     * 创建游戏房间 - 元宝场
     *
     * @param gameServer
     * @param userID
     * @param gameID
     * @param roomNo
     * @return
     */
    JSONObject createGameRoomAll(GameServer gameServer, JSONObject base_info, long userID, long gameID, String roomNo);

    /**
     * 创建游戏房间 - 代开房卡场
     *
     * @param gameServer
     * @param userID
     * @param gameID
     * @param roomNo
     * @return
     */
    JSONObject createGameRoomdk(GameServer gameServer, JSONObject base_info, long userID, long gameID, String roomNo);

    /**
     * 创建游戏房间 - 金币场
     *
     * @param gameServer
     * @param userID
     * @param gameID
     * @param roomNo
     * @return
     */
    JSONObject createGamePriceRoom(GameServer gameServer, long userID, long gameID, String roomNo, int roomtype, int coins, String option, String level);

    /**
     * 根据gameID和userID判断是否存在房间,是则返回房间信息,否则返回null
     *
     * @param gameID
     * @param userID
     * @return
     */
    JSONObject checkIsExistRoom(long gameID, long userID);

    /**
     * 根据userID判断是否存在房间,是则返回房间信息,否则返回null
     *
     * @param userID
     * @return
     */
    JSONObject checkIsExistRoom(long userID);

    /**
     * 根据房间号判断是否存在房间
     *
     * @param roomNo
     * @param gameID
     * @param userID
     * @return
     */
    JSONObject checkIsExistRoom(String roomNo, long gameID, long userID);

    /**
     * 根据userID判断是否存在百人场房间
     *
     * @param userID
     * @return
     */
    JSONObject checkIsExistRoom_BR(long userID);

    /**
     * 根据gameID和roomID找到房间并判断是否存在空位,是则加入房间并返回房间信息
     *
     * @param userID
     * @param gameID
     * @param roomID
     * @return
     */
    JSONObject joinRoom(long userID, long gameID, String roomID);

    /**
     * 根据gameID和roomID找到房间,跳过检查
     *
     * @param userID
     * @param gameID
     * @param roomID
     * @return
     */
    JSONObject doJoinRoom(long userID, long gameID, String roomID);

    /**
     * 获取玩家用的信息
     *
     * @param userID
     * @return
     */
    JSONObject getUsers(long userID);

    /**
     * 根据gid搜索该游戏类型是否有空间房间，
     * 如果有则返回前台提示访问加入房间，
     * 如果没有则提示前台访问创建房间接口
     *
     * @param userID
     * @param gameID
     * @param coins
     * @param option
     * @param level
     * @return
     */
    JSONObject getQuickJoin(GameServer gameServer, long userID, long gameID, int coins, String option, String level);

    /**
     * 百人场快速加入房间方法
     *
     * @param gameServer
     * @param userID
     * @param gameID
     * @param coins
     * @param option
     * @param level
     * @return
     */
    JSONObject getQuickJoinBR(GameServer gameServer, long userID, long gameID, int coins, String option, String level);

    /**
     * 百家乐-百人场快速加入房间方法
     *
     * @param gameServer
     * @param userID
     * @param gameID
     * @return
     */
    JSONObject getQuickJoinBJL(GameServer gameServer, long userID, long gameID);


    /**
     * 获取金币场游戏场次信息
     *
     * @param gid
     * @param platform
     * @return
     */
    JSONArray getGameGoldSetting(long gid, String platform);


    /**
     * 获取代开房间所有列表
     *
     * @param userID
     * @param stoptime
     * @return
     */
    JSONArray getdaikaiRoomList(long userID, Timestamp stoptime);

    /**
     * 获取代开房间个个游戏列表
     *
     * @param userID
     * @param stoptime
     * @param gid
     * @return
     */
    JSONArray getdaikaiRoomListgid(long userID, Timestamp stoptime, int gid);

    /**
     * 获取元宝列表
     *
     * @param gid
     * @param url
     * @return
     */
    JSONArray getyuanbaoRoomList(long gid, String url);

    /**
     * 获取游戏列表
     */
    JSONArray getAllGame(int status);

    /**
     * 根据gameid得到房间信息
     *
     * @param gameId
     * @return
     */
    JSONArray getRoomDescByGameId(long gameId);

    /**
     * 通过房间Id获取房间信息
     */
    JSONObject getRoomSettingByRoomId(String roomNo);

    /**
     * 根据用户Id查找用户所玩过的牌局列表
     */
    JSONArray getUserAllGameList(String userId);

    /**
     * 根据用户Id查找用户进行中的牌局
     */
    JSONArray getRightNowPlay(String userId);

    /**
     * 根据用户Id查找用户组建的牌局
     */

    JSONArray getPlayByMe(String userId);

    /**
     * 查看用户已结束的牌局
     */
    JSONArray getEndPlay(String userId);


    /**
     * 查询商城中的商品
     */
    JSONArray getMallList();

    /**
     * 打开竞技场isOpen为1开放0不开放
     */
    JSONObject getTimesetting(long gid);

    /**
     * 获取游戏的状态
     *
     * @param gid
     * @return
     */
    JSONObject getza_games(String gid);
}
