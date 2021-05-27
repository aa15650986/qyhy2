package com.qy.game.server;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.qy.game.service.UserAuthService;
import com.qy.game.utils.Dto;



/**
 * 加盟申请
 * @author tpq
 *
 */


@Controller
@RequestMapping("/join")
public class UserJoinServer {
	
	@Resource
	private UserAuthService authService;
	
	
	@RequestMapping("/userJoinSQ")
	public void userJoinUs(HttpServletRequest reques, HttpServletResponse response, 
			@RequestParam long userid,@RequestParam String username,@RequestParam String usertel){
		
		JSONObject data = new JSONObject();
		
		if (userid > 0) {
			try {
				username = new String(username.getBytes("iso8859-1"),"utf-8");
				 data = authService.insertUserJoin(userid, username, usertel);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				data.element("code", "0")
				.element("msg", "操作失败");
			}
		}
		
		Dto.returnJosnMsg(response, data);
		
		
	}
	

}
