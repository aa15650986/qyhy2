package com.qy.game.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.qy.game.service.UserAuthService;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.utils.Dto;


/**
 * 实名认证
 * @author tpq
 *
 */




@Controller
@RequestMapping("/auth")
public class UserAuthServer {
	
	@Resource
	private UserAuthService authService;
	@Resource
	private GoldShopBizServer gsbiz;
	
	/**
	 * 用户实名认证提交
	 * @param reques
	 * @param response
	 * @param uid
	 * @param name
	 * @param idCard
	 */
	@RequestMapping("/userAuth")
	public void submitAuthLetter(HttpServletRequest reques, HttpServletResponse response, 
			@RequestParam long userid,@RequestParam String username,@RequestParam String usercard){
		String domainstr=reques.getServerName();
		String url=gsbiz.getdomain(domainstr);
		JSONObject data = new JSONObject();
		
		if (userid > 0) {
			try {
				username = URLDecoder.decode( username, "UTF-8" );
//				username = new String(username.getBytes("iso8859-1"),"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			JSONObject list = authService.insertUserAuth(userid, username, usercard,url);
			
			if (list.getString("status").equals("1")) {
				data.element("msg", "成功提交认证")
					.element("data", list)
					.element("code", "1")
					.element("status", "success");
			}else if (list.getString("status").equals("0")){
				data.element("msg", "提交失败，请稍后再试")
					.element("data", list)
					.element("code", "0")
					.element("status", "fail");
			}else if (list.getString("status").equals("-1")){
				data.element("msg", "请勿重复提交认证")
					.element("data", list)
					.element("code", "-1")
					.element("status", "fail");
		}
		}
		
		Dto.returnJosnMsg(response, data);
	}
	
	/**
	 * 获取用户实认证结果
	 * @param reques
	 * @param response
	 * @param uid
	 * @param name
	 * @param idCard
	 */
	@RequestMapping("/getuserAuths")
	public void getuserAuth(HttpServletRequest reques, HttpServletResponse response, 
			@RequestParam long userid){
			
		JSONObject data = authService.getUserAuth(userid);
		System.out.println(data);
		Dto.returnJosnMsg(response, data);
	}
	

}
