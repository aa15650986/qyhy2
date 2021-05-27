package com.qy.game.remote.iservice;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.sf.json.JSONObject;

public interface IService extends Remote {  
	  
    /**
     * 加入注册服务
     * @param ip
     * @param port
     * @param serverName
     * @return
     * @throws RemoteException
     */
    public int joinServer(String ip, int port, String serverName) throws RemoteException;  
    
    
    /**
     * 游戏服务器心跳
     * @param serverName 游戏服务器名称
     * @throws RemoteException
     */
    public void heartBeat(String serverName) throws RemoteException;  

	/**
	 * 游戏结束结算-房间数
	 * @param roomNo
	 * @return
	 */
    public void settlementRoomNo(String roomNo) throws RemoteException;  
    
    /**
     * 游戏结束结算-房间数牌九
     * @param roomNo
     * @return
     */
    public void settlementRoomNo_PJ(String roomNo) throws RemoteException;  
    
	  /**
	   * 更改玩家日 币种记录（用于排行榜）
	   * @param userid
	   * @param coins
	   * @param win
	   * @param lose
	   * @param platform
	   * @param memo
	   */
    public void updateZaCoinsRec(Long userid,Integer coins,Integer type)throws RemoteException;
    
    /**
     * 玩家抽水
     * @param userIds 玩家id集合：[1,2]
     * @param roomNo
     * @param gid
     * @param sum 传入正整数 扣取数额
     * @param obj 1-房卡  2-金币
     * @throws RemoteException
     */
    public JSONObject pump(String userIds,String roomNo,String gid,String sum,String obj)throws RemoteException;
    
    /**
     * 玩家抽水带平台号
     * @param userIds 玩家id集合：[1,2]
     * @param roomNo
     * @param gid
     * @param sum 传入正整数 扣取数额
     * @param obj 1-房卡  2-金币
     * @throws RemoteException
     */
    public JSONObject pump_platform(String userIds,String roomNo,String gid,String sum,String obj,String platform)throws RemoteException;

    /**
     * 获取当前平台号
     */
    public String getPlatform(long id)throws RemoteException;
}