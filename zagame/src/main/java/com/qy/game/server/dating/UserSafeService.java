package com.qy.game.server.dating;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qy.game.service.dating.UserSafeServerBiz;
import com.qy.game.utils.Dto;

/**
 * 保险柜
 * @author ASUS
 *
 */
@Controller
public class UserSafeService {

	@Resource
	private LoginServer ls;
	
	@Resource
	private UserSafeServerBiz usbiz;
	
	/**
	 * 进入存入
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("getbaseSafe")
	public void addbaseSafe(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		JSONObject msg=usbiz.getbaseSafe(id);
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 设置保险柜密码
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("upbaseSafe.json")
	public void upbaseSafe(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		String pass=request.getParameter("pass");
		String tel=request.getParameter("tel");
		String code=request.getParameter("code");
		
		String codeKey=request.getParameter("codeKey");
		JSONObject dd=ls.codeMap;
		
		JSONObject msg=new JSONObject();
		if(Dto.isNull(dd)){
			msg.element("code", "0")
			.element("msg", "请获取验证码");
		}else if (dd.getString(codeKey).equals(code)) {
			try {
				msg=usbiz.upbaseSafe(id, pass,tel);
				
			} catch (Exception e) {
				// TODO: handle exception
				msg.element("code", "0")
				.element("msg", "网络错误,请稍后重试");
			}
		}else {
			msg.element("code", "0")
			.element("msg", "验证码错误");
		}	
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 保险柜存入
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("upbasesafepricejia.json")
	public void upbasesafeprice(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		String price=request.getParameter("price");
		JSONObject msg=new JSONObject();
		try {
			usbiz.upbasesafeprice(id, price);
			msg.element("code", "1")
				.element("msg", "存入成功");
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0")
			.element("msg", "网络错误,请稍后重试");
		}
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 取出
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("upbasesafepricejin")
	public void upbasesafepricejin(HttpServletRequest request,HttpServletResponse response) throws IOException{
			JSONObject msg=new JSONObject();
		try {
			JSONObject data=new JSONObject();
			data.element("id", request.getParameter("id"))
				.element("safe", request.getParameter("safe"))
				.element("price", request.getParameter("price"))
				.element("platform", request.getParameter("platform"));
		
			msg=usbiz.upbasesafepricejin(data);

		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0")
			.element("msg", "网络错误,请稍后重试");
		}
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 修改保险柜密码 修改password
	 * @throws IOException 
	 */
	@RequestMapping("upbaseSafexg")
	public void upbaseSafexg(HttpServletRequest request,HttpServletResponse response) throws IOException{
 		JSONObject msg=new JSONObject();
		try {
			JSONObject data=new JSONObject();
			data.element("id", request.getParameter("id"))
			.element("safe1", request.getParameter("safe1"))
			.element("safe2", request.getParameter("safe2"))
			.element("type", request.getParameter("type"));
			msg=usbiz.upbaseSafexg(data);
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0")
				.element("msg", "网络错误");
		}
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 找回手机号之前验证号码
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("jcusertel.json")
	public void jcusertel(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		String tel=request.getParameter("tel");
		String type=request.getParameter("type");
		JSONObject msg=new JSONObject();
		try {
			 msg=usbiz.jcusertel(id, tel,type);
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0")
			.element("msg", "网络错误");
		}
		Dto.printMsg(response, msg.toString());
	}
}
