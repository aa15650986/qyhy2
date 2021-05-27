package com.qy.game.server.dating;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qy.game.service.dating.FundRechargeServerBiz;
import com.qy.game.utils.Dto;
import com.qy.game.utils.MathDelUtil;



@Controller
public class FundRechargeService {
	@Resource
	private FundRechargeServerBiz fbiz;
	
	/**
	 * 进行支付
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("sysbalpayment")
	public void sysbalpayment(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String id=request.getParameter("id");
		String price=request.getParameter("price");
		JSONObject money=fbiz.getsysbalrec(id);
		String id2="";
		if(money.getDouble("money")-Double.valueOf(price)<0){
			Double price2=MathDelUtil.halfUpWithSmall(Double.valueOf(price)-money.getDouble("money"),2);
			fbiz.addsysbalreccz(id, String.valueOf(price2));
			id2="成功";
			/* id2=fbiz.addsysbalreczf(id, "-"+price);*/
		}else{
			 /*id2=fbiz.addsysbalreczf(id, "-"+price);*/
			id2="成功";
		}
		Dto.printMsg(response, id2);
	}
	
	
	/**
	 * 进行充值
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("addsysbalrec")
	public String addsysbalrec(HttpServletRequest request,HttpServletResponse response){
		
		try {
			JSONObject data=JSONObject.fromObject(request.getParameter("data"));
			fbiz.addsysbalreccz(data.getString("id"), data.getString("price"));
			return "充值成功";
		} catch (Exception e) {
			// TODO: handle exception
			return "充值失败";
		}

	}
}
