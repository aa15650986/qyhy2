package com.qy.game.service.dating;

import net.sf.json.JSONObject;

public interface UserSafeServerBiz {

	/**\
	 * 检查保险柜
	 * @param id
	 * @return
	 */
	public JSONObject getbaseSafe(String id);
	
	/**
	 * 设置保险柜密码
	 * @param id
	 * @param tel
	 */
	public JSONObject upbaseSafe(String id,String pass,String tel);
	
	/**
	 * 保险柜存入
	 * @param id
	 * @param pass
	 */
	public void upbasesafeprice(String id,String pirce);
	
	/**
	 * 保险柜取出
	 * @param id
	 * @param pass
	 */
	public JSONObject upbasesafepricejin(JSONObject data);
	
	/**
	 * 修改保险柜密码
	 * @param data
	 */
	public JSONObject upbaseSafexg(JSONObject data);
	
	/**
	 * 修改保险柜密码
	 * @param id
	 * @param tel
	 * @return
	 */
	public JSONObject jcusertel(String id,String tel,String type);
	
	
}
