package com.qy.game.server.dating;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qy.game.service.dating.SignServer;
import com.qy.game.utils.Dto;
import com.qy.game.utils.TimeUtil;


@Controller
public class UserSignServer {

	@Resource
	private SignServer uss;
	/**
	 * 签到页面跳转
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("jumpSign")
	public void jumpUserSignLogin(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String userID = request.getParameter("userID");
		String platform=request.getParameter("platform");
		JSONObject zar=uss.getzasignreward();
		
		JSONObject json = new JSONObject();
		
		if (!Dto.stringIsNULL(userID)) {
			
			//JSONObject list = uss.saveUserSign(Long.valueOf(userID));
			
			JSONObject data = uss.jumpUserSign(Long.valueOf(userID),platform);
			//json.element("cdlist", list);
			json.element("list", data);
			json.element("sign", data.getString("sign"));
		}else {
			json.element("sign", 0);
		}
		json.element("zar", zar);
		if(Dto.stringIsNULL(platform))
			json.element("mengyue", TimeUtil.getNewMMDay());
		else if("DFBY".equals(platform))
			json.element("mengyue", 7);
//		json.element("firstnode", zar.getInt("firstnode"));
//		json.element("firstmoney", zar.getInt("firstmoney"));
//		json.element("lv1", zar.getInt("lv1"));
//		json.element("secnode", zar.getInt("secnode"));
//		json.element("secmoney", zar.getInt("secmoney"));
//		json.element("lv2", zar.getInt("lv2"));
//		json.element("thnode", zar.getInt("thnode"));
//		json.element("thmoney", zar.getInt("thmoney"));
//		json.element("lv3", zar.getInt("lv3"));
//		json.element("monthmoney", zar.getInt("monthmoney"));
//		json.element("lv4", zar.getInt("lv4"));
		
		Dto.printMsg(response, json.toString());
		
	}
	
	
	
	/**
	 * 用户签到
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("userSign")
	public void saveUserSignClick(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String userID = request.getParameter("userID");
		String platform=request.getParameter("platform");
		JSONObject data = uss.saveUserSign(Long.valueOf(userID),platform);
		
		Dto.printMsg(response, data.toString());
		
		//response.getWriter().println(data);
	}
	

	
	
	/**
	 * 领取签到奖励
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("takeSign")
	public void takeSignReward(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String userID = request.getParameter("userID");
		String platform=request.getParameter("platform"); 
		JSONObject data = new JSONObject();
		
		JSONObject json = uss.userTakeSign(Long.valueOf(userID),platform);
		if (json.getString("data").equals("1")) {
			data.element("msg", "成功领取");
			data.element("code", "1");
		}else if (json.getString("data").equals("-1")) {
			data.element("msg", "领取失败");
			data.element("code", "0");
			
		}else if (json.getString("data").equals("-2")) {
			data.element("msg", "您已领取奖励,请勿重复领取");
			data.element("code", "-1");
		}else if (json.getString("data").equals("2")) {
			data.element("msg", "签到次数不够");
			data.element("code", "2");
		}
		
		Dto.printMsg(response, data.toString());
	}
	
	/**
	 * 判断是否签到过
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("getisUserSign.json")
	public void getisUserSign(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		JSONObject msg=new JSONObject();
		try {
			Boolean t=uss.isUserSign(Long.valueOf(id));
			msg.element("msg", "程序错误")
				.element("code", t?1:0);
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("msg", "程序错误")
				.element("code", "0");
		}
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 判断是否能拿奖励
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("getsignrew.json")
	public void getsignrew(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		String platform=request.getParameter("platform");
		String da="0";
		try {
			 da=uss.getsignrew(id,platform);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		Dto.printMsg(response, da);
	}
}
