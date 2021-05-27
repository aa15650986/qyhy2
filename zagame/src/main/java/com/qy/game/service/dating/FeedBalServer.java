package com.qy.game.service.dating;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface FeedBalServer {

	/**
	 * 添加信息反馈
	 * @param con
	 */
	public void addsysfeedback(JSONObject con);
	
	/**
	 * 查看用户反馈
	 * @param id
	 * @return
	 */
	public JSONArray getfeedback(String id);
	/**
	 * 房卡转赠
	 * @param con
	 */
	public String upbaseroomcard(JSONObject con);
	
	/**
	 * 绑定电话
	 * @param con
	 */
	public JSONObject upuserBindphone(JSONObject con);
	
	/**
	 * 判断是否绑定过
	 * @param data
	 * @return
	 */
	public JSONObject cxsfbdg(JSONObject data);
	
}
