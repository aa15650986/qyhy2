package com.qy.game.server;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qy.game.service.UserService;

@Controller
@RequestMapping("/order")
public class OrderServer {
	
	@Resource
	private UserService userService;
	
	/**
	 * 支付接口
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/apppay")
	public void appPay(HttpServletRequest request, HttpServletResponse response) throws IOException{		
//		String pay_status = request.getParameter("pay_status");
//		String account = request.getParameter("user_id");
//		String product_count = request.getParameter("product_count");
//		String product_id = request.getParameter("product_id");
//		
//		System.out.println("=======苹果支付回调："+JSONObject.fromObject(request.getParameterMap()));
//		
//		if(pay_status!=null&&pay_status.equals("1")){
//			
//			int result = 0;
//			if(product_id.equals("com.cocos2d.akqp.zhoan.001")){ // 
//				
//				int roomcard = Integer.valueOf(product_count);
//				result = userService.updateUserAccount(account, roomcard, 0);
//			}else{
//				
//				int coins = Integer.valueOf(product_count) * 100;
//				result = userService.updateUserAccount(account, 0, coins);
//			}
//			
//			System.out.println("数据更新结果："+result);
//			
//		}else{
//			System.out.println("支付失败！");
//		}
		
		String account = request.getParameter("account");
		String type = request.getParameter("type");
		String count = request.getParameter("count");
		
		System.out.println("=======支付回调======："+JSONObject.fromObject(request.getParameterMap()));
		
		if(account!=null && type!=null && count!=null){
			
			boolean result = false;
			if(type.equals("0")){ 
				
				int roomcard = Integer.valueOf(count);
				result = userService.updateUserAccount(account, roomcard, 0);
			}else{
				
				int coins = Integer.valueOf(count);
				result = userService.updateUserAccount(account, 0, coins);
			}
			
			System.out.println("数据更新结果："+result);
			
		}
		
	}
	
	/**
	 * 支付接口
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/chuanrujinbi")
	public void chuanrujinbi(HttpServletRequest request, HttpServletResponse response) throws IOException{		
		
		String account = request.getParameter("account");
		String coins = request.getParameter("inp");
		boolean is = userService.chuanrujinbi(coins,account);
		System.out.println("jsonObject:"+is);
		
	}
	
	
	
}
