package com.qy.game.service.dating;

import net.sf.json.JSONObject;


public interface SignServer {

	/**
	 * 
	 * @return
	 */
	public JSONObject getzasignreward();
	
	/**
	 * 前往签到页面
	 * @param userID
	 * @return
	 */
	public JSONObject jumpUserSign(long userID,String platform);
	
	
	/**
	 * 点击签到
	 * @param userID
	 * @return
	 */
	public JSONObject saveUserSign(long userID,String platform);
	
	/**
	 * 判断是否签到
	 * @param userID
	 * @return
	 */
	public boolean isUserSign(long userID);
	
	/**
	 * 保存用户表信息
	 * @param zu
	 * @return
	 */
	public boolean saveZaUserlist(String id,int coinIn,int Roomcard,String platform);
	
	
	
	/**
	 * 查询用户是否领取奖励
	 * @param userID
	 * @return
	 */
	public JSONObject ifUserSign(long userID);
	
	
	
	/**
	 * 领取奖励
	 * @param userID
	 * @return
	 */
	public JSONObject userTakeSign(long userID,String platform);
	
	
	
	/**
	 * 保存用户领奖信息表
	 * @param data
	 * @param is
	 * @return
	 */
	public boolean saveZaUserSignlist(String data,int is,long userID);

	/**
	 * 判断是否能拿奖励
	 * @param id
	 * @return
	 */
	public String getsignrew(String id,String platform);
	
	/**
	 * 获取
	 * @param id
	 * @return
	 */
	public JSONObject getvip(String id);
}
