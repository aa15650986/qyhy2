package com.qy.game.constant;

/**
 * redis key 添加key 请把所有项目的同步一下
 *
 * @author 洪序铭
 * @date 2019/08/08 15:09
 */
public class CacheKeyConstant {
    //过期时间(秒)--如 10分钟 RedisDto.minute*10
    public static final long minute = 60;//1分钟
    public static final long minute10 = 600;//10分钟
    public static final long minute30 = 1800;//30分钟
    public static final long hour = 3600;//1小时
    public static final long day = 86400;//1天
    public static final long week = 604800;//1周
    public static final long month = 2592000;//1月
    /**
     * The constant GAME_SETTING_NN.房间设置
     */
    public static final String GAME_SETTING_NN = "game_setting_nn";
    /**
     * The constant GAME_SETTING_SSS.房间设置
     */
    public static final String GAME_SETTING_SSS = "game_setting_sss";
    /**
     * The constant GAME_SETTING_ZJH.房间设置
     */
    public static final String GAME_SETTING_ZJH = "game_setting_zjh";
    /**
     * The constant GAME_SETTING_QZMJ.房间设置
     */
    public static final String GAME_SETTING_QZMJ = "game_setting_qzmj";
    /**
     * The constant GAME_SETTING_NAMJ.房间设置
     */
    public static final String GAME_SETTING_NAMJ = "game_setting_namj";
    /**
     * The constant GAME_INFO_BY_ID.
     */
    public static final String GAME_INFO_BY_ID = "game_info_by_id";
    /**
     * 游戏配置
     */
    public static final String APP_GAME_SETTING = "app_game_setting";
    /**
     * The constant VERIFICATION_MAP.
     */
    public static final String ROOM_INFO_BY_RNO = "room_info_by_rno";
    /**
     * The constant VERIFICATION_MAP.
     */
    public static final String VERIFICATION_MAP = "verification_map";
    /**
     * The constant VERIFICATION_SET.
     */
    public static final String VERIFICATION_SET = "verification_set";
    /**
     * The constant BIND_SET.
     */
    public static final String BIND_SET = "bind_set";
    /**
     * The constant GLOBAL_CLUB_NAME.
     */
    public static final String GLOBAL_CLUB_NAME = "global_club_name";
    /**
     * 游戏房间配置
     */
    public static final String ZA_GAMESETTING = "za_gamesetting";
    /**
     * 游戏配置规格名
     */
    public static final String ZA_GAMESETTING_RULE_NAME = "za_gamesetting_rule_name";
    /**
     * 用户token
     */
    public static final String CIRCLE_TOKEN = "circle_token";//俱乐部token

    /**
     * SocketIOClient_sessionId
     */
    public static final String SOCKETIOCLIENT_SESSIONID = "socketIOClient_sessionId";
    /**
     * socketIOClient_userId
     */
    public static final String SOCKETIOCLIENT_USERID = "socketIOClient_userId";

    /**
     * 系统全局配置
     */
    public static final String SYS_GLOBAL = "sys_global";

    /**
     * 亲友圈 信息
     */
    public static final String GAME_CIRCLE_INFO = "game_circle_info";

    /**
     * 手机号码
     */
    public static final String VERIFICATION_PHONE_SET = "verification_phone_set";

    /**
     * 手机号码
     */
    public static final String VERIFICATION_PHONE_MAP = "verification_phone_map";
    /**
     * 结算
     */
    public static final String SETTLE_ACCOUNTS = "settle_accounts";

    /**
     * 用户编号account
     */
    public static final String ZA_USERS_ACCOUNT = "za_users_account";

    /**
     * 用户编号set
     */
    public static final String ZA_USERS_ACCOUNT_SET = "za_users_account_set";

    /**
     * sys_socket
     */
    public static final String SYS_SOCKET = "sys_socket";
}
