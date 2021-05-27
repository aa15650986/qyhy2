package com.qy.game.imple;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qy.game.service.UserService;
import com.qy.game.utils.Dto;
import com.qy.game.utils.HttpReqUtil;

/**
 * 游戏远程接口
 * 
 * @author nanoha
 *
 */
@Controller
@RequestMapping("/impl")
public class GameRemoteInterface {
	/*
	 * @Resource private UserService g_Service;
	 * 
	 * //请求域名 private String host=""; //订单查询接口地址 private String
	 * checkOrderUrl="/getUserChargeRec.json"; //异步通知回调地址 private String
	 * notifyUrl="/rechargeRoomCard_notify.json";
	 * 
	 *//**
		 * 新增房卡
		 * 
		 * @param request
		 * @param response
		 * @throws IOException
		 *//*
			 * @RequestMapping("/addroomcard") public void addRoomCard(HttpServletRequest
			 * request,HttpServletResponse response) throws IOException { String msg=""; int
			 * code=0;
			 * 
			 * //0.获得传入参数 String orderCode=request.getParameter("orderCode"); String
			 * unionId=request.getParameter("unionId");
			 * host=request.getParameter("baseUrl");
			 * 
			 * Dto.printMsg(response, new JSONObject().element("msg", "已成功接收到消息")
			 * .element("sta", "success").toString());
			 * 
			 * if(Dto.stringIsNULL(orderCode) || Dto.stringIsNULL(unionId))
			 * msg="请传入正确的订单号和unionId"; else { //1.根据用户unionId查询用户的订单信息 Map<String,String>
			 * map=new HashMap<String,String>(); map.put("orderCode", orderCode);
			 * map.put("unionId", unionId); String
			 * backMsg=HttpReqUtil.doPost(host+checkOrderUrl, map, null, "utf-8");
			 * 
			 * System.out.println("订单查询结果为"+backMsg);
			 * 
			 * //2.根据查询结果为用户房卡充值 try { JSONObject backObj=JSONObject.fromObject(backMsg);
			 * 
			 * if(backObj.getInt("isArrival")==0 && backObj.getInt("sta")==1){ String
			 * orderUnoinId=backObj.getString("unionId"); if(unionId.equals(orderUnoinId)){
			 * 
			 * //3.更新用户房卡数量 int num=backObj.getInt("roomCards"); boolean
			 * result=g_Service.updateUserRoomCardByUnionId(unionId, num); if(result){
			 * code=1; msg="操作成功"; }else msg="更新用户房卡信息失败，请稍后再试";
			 * 
			 * }else msg="unionId不匹配，请检查订单与传入参数"; }else msg="订单已到账，或尚未支付";
			 * 
			 * } catch (Exception e) { System.out.println("服务器500，操作失败"); code=-1; } }
			 * 
			 * //4.返回消息 Map<String,String> backMsg=new HashMap<String,String>();
			 * backMsg.put("code", String.valueOf(code)); backMsg.put("msg", msg);
			 * backMsg.put("chargeNum", orderCode); backMsg.put("unionId", unionId);
			 * System.out.println(" 返回游戏代理端的数据为"+backMsg.toString());
			 * HttpReqUtil.doPost(host+notifyUrl, backMsg, null, "utf-8"); }
			 */

}
