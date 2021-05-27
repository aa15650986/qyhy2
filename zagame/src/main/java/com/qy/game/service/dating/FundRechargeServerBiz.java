package com.qy.game.service.dating;

import net.sf.json.JSONObject;

public interface FundRechargeServerBiz {

	/**
	 * 进行充值
	 * @param id
	 * @param price
	 */
	public void addsysbalreccz(String id,String price);
	
	/**
	 * 查询金额
	 * @param id
	 * @return
	 */
	public JSONObject getsysbalrec(String id);
	
	/**
	 * 
	 * @param id 用户id
	 * @param price 金额
	 * @param type 商城za 
	 * @param tyID 商城产品ID
	 * @return
	 */
	public String addsysbalreczf(String id,String price,String type,Long tyID);

	/**
	 * 兑换支付
	 * @param id
	 * @return
	 */
	public String upbasezf(JSONObject con);

	/**
	 * 兑换支付
	 * @param id
	 * @return
	 */
	public String upbasezf2(String id,String zaid,String text,String platform,String uuid);
	/**
	 * 创建订单
	 * @param obj
	 * @return
	 */
	public Boolean creataOrder(JSONObject obj);
	
	/**
	 * 创建订单2
	 * @param obj
	 * @return
	 */
	public boolean creataOrder2(JSONObject obj);
	
	/**
	 * 根据订单号查询 订单
	 * @param order
	 * @return
	 */
	public JSONObject getExchangeRec(String order);
	
	/**
	 * 根据订单号查询 订单
	 * @param order
	 * @return
	 */
	public JSONObject creataOrderText(String text);
	
	/**
	 * 根据id查询 订单
	 * @param order
	 * @return
	 */
	public JSONObject getExchangeRecById(String id);

	/**
	 * 根据订单ID 更新订单状态
	 * @param id
	 */
	public void updExchangeRec(Long id);
	
	/**
	 * 根据订单ID 更新订单实际支付金额
	 * @param id
	 */
	public void updExchangePayc(Double money,Long id);

	/**
	 * 根据 用户id  字段更新 数量
	 * @param id
	 * @param sum
	 * @param zz
	 */
	public void updZaUser(String id, double sum, String zz);

	/**
	 * 根据商品id获取商品详情
	 * @param id
	 * @return
	 */
	public JSONObject getZaMallById(long id);
	
	
	/**
	 * 更改用户VIP状态
	 * @param id
	 * @param type
	 * @return
	 */
	public boolean updateUserVip(long id,int type);
	
	
	/**
	 * 根据微信配置参数
	 * @param id
	 * @return
	 */
	public JSONObject getWechatSetting(long id);
}
