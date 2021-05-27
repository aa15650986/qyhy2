package com.qy.game.service.dating;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface GoldShopBizServer {

	/**
	 * 查询金币
	 * @param con
	 * @param pa
	 * @return
	 */
	public JSONArray getzamall(JSONObject con);
	
	/**
	 * 兑换
	 * @param con
	 * @param pa
	 * @return
	 */
	public JSONArray getexchange(JSONObject con);
	
	/**
	 * 兑换的记录
	 * @param con
	 * @param pa
	 * @return
	 */
	public JSONArray getexchangerec(JSONObject con);
	
	/**
	 * 支付完进行简介的修改
	 * @param id
	 * @param za
	 */
	public void upsyabaldes(String id,String text);
	
	/**
	 * 查询商城产品
	 * @param id
	 * @return
	 */
	public JSONObject getzamall(String id);
	
	/**
	 * 获取vip
	 * @return
	 */
	public JSONArray getsysvip();
	
	/**
	 * 获取vip
	 * @param id
	 * @return
	 */
	public JSONObject getsysvip(String id);
	
	/**
	 * 根据平台标识 获取平台信息
	 * @param platform
	 * @return
	 */
	public JSONObject getVersion(String platform);

	/**
	 * 根据id=1 获取平台信息
	 * @param platform
	 * @return
	 */
	public JSONObject getVersion1(int id);
	
	/**
	 * 根据网址查找平台号
	 * @param url
	 * @return
	 */
	public String getdomain(String url);
}
