package com.qy.game.service.dating;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public interface MessageService {
	
	/**
	 * 获取游戏活动
	 * @return
	 */
	public JSONArray getActivities(String platform);
	
	/**
	 * 获取游戏公告
	 * @return
	 */
	public JSONArray getNotice(String platform);
	
	/**
	 * 获取弹出公告
	 * @return
	 */
	public JSONObject getEjectMessage(String platform);
	
	/**
	 * 获取滚动公告
	 * @return
	 */
	public JSONArray getRollNotice(String platform);
	
	/**
	 * 获取轮播图
	 * @return
	 */
	public JSONArray getRollImg(String platform);
	
	/**
	 * 获取公告详情
	 * @param id
	 * @return
	 */
	public JSONObject getNoticeInfo(Long id);
	
	/**
	 * 获取活动详情
	 * @param id
	 * @return
	 */
	public JSONObject getActivityInfo(Long id);
	
	/**
	 * 获取用户未读消息
	 * @param id
	 * @return
	 */
	public JSONArray getUserMsg(Long id,String platform);
	
	/**
	 * 获取用户消息
	 * @param id
	 * @return
	 */
	public JSONObject getFeedBackInfo(Long id);
	
	/**
	 * 更改用户消息状态
	 * @param id
	 * @param sta
	 * @return
	 */
	public boolean setFeedBackStatus(Long id,int sta);

	/**
	 * 获取用户排行信息
	 * @param id
	 * @param sta
	 * @return
	 */
	public JSONArray getZaCoinsPaihang(String type);
	
	/**
	 * 获取用户月排行信息
	 * @param id
	 * @param sta
	 * @return
	 */
	public JSONArray getYuepaihangbang(String type);
	
	/**
	 * 获取单条用户排行
	 * @param id
	 * @param sta
	 * @return
	 */
	public JSONArray getyueCoinsPaihang(String userid);
	
	/**
	 * 获取单条用户排行
	 * @param id
	 * @param sta
	 * @return
	 */
	public JSONArray getZhouCoinsPaihang(String userid);
	/**
	 * 获取用户排行信息 山弹头
	 * @return
	 */
	public JSONArray getZaCoinsPaihang2(String platform);
	/**
	 * 获取用户排行信息 山弹头
	 * @return
	 */
	public JSONArray getZaCoinsPaihang3(int end);
	/**
	 * 获取用户排行信息 山弹头
	 * @return
	 */
	public JSONArray getZaCoinsPaihang4();

	/**
	 * 获取用户排行信息 新
	 * @return
	 */
	public JSONArray getZaCoinsPaihang5(String platform);
	
	/**
	 * 获取用户消息元宝排行
	 * @param id
	 * @return
	 */
	public JSONObject getUserPai(Long id);

	/**
	 *  游戏中个用户删除消息
	 * @param id
	 * @param userid
	 */
	public void upZafeedbackCon(String id);
	
	/**
	 * 获取每周排行积分
	 * @param id
	 * @return
	 */
	public JSONArray getzhoupaihang(String type);
	
	/**
	 *获取竞技场清除的设置 
	 * @return
	 */
	public JSONArray getzaarena();

	

}
