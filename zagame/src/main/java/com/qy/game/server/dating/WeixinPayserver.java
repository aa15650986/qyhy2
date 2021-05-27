package com.qy.game.server.dating;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qy.game.anysdk.PayNotify;
import com.qy.game.service.UserService;
import com.qy.game.service.dating.FundRechargeServerBiz;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.service.dating.LoginService;
import com.qy.game.utils.Dto;
import com.qy.game.utils.HttpReqUtil;
import com.qy.game.utils.wechat.Configure;
import com.qy.game.utils.wechat.WxPayApi;
import com.qy.game.utils.wechat.XMLParser;

@Controller
public class WeixinPayserver {

	@Resource
	private GoldShopBizServer gsbiz;
	@Resource
	private FundRechargeServerBiz fbiz;
	@Resource
	private UserService userService;
	@Resource 
	private LoginService loginService;
	
	private static String paramValues = "";
	/**
	 * 从通知参数里面获取到的签名值
	 */
	private static String originSign = "";
	
	/**
	 * 微信支付
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("pay/wxpay.json")
	public String wxpay(HttpServletRequest request, HttpServletResponse response) {

		String pay_no = request.getParameter("pay_no");
		System.out.println("---"+pay_no);
		if (!Dto.stringIsNULL(pay_no)) {

			JSONObject order=fbiz.getExchangeRecById(pay_no);
			if (order.getInt("status")==0) {

				JSONObject user=userService.getUserInfoByID(Long.valueOf(order.getLong("userId")));
				order.element("ip", request.getRemoteAddr());
				order.element("tradeType", "MWEB");
				order.element("openid", user.getString("openid"));

				String jsApiParameters = WxPayApi.wxpay(order);

				request.setAttribute("payOrder", order);
				request.setAttribute("jsApiParameters", jsApiParameters);
			}

		}

		return "work/pay/wx/wxpay";
	}


	/**
	 * 支付完成异步回调路径
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/wxpay_callback.json")
	public void callBack(HttpServletRequest request,HttpServletResponse response ) throws IOException{
		System.out.println("第三方微信支付完成异步回调路径");
		PayNotify paynotify = new PayNotify();
		JSONObject data = new JSONObject();
		
		paramValues = getValues(request);
		
		// 这是验签测试
		System.out.println("参考签名值: " + originSign + "\n");
		System.out.println("待签字符串: " + paramValues + "\n");
		System.out.println("计算得到的签名值: " + paynotify.getSign(paramValues) + "\n");
		if (paynotify.checkSign(paramValues, originSign)){
			System.out.println("验证签名成功\n");
		} else {
			System.out.println("验证签名失败");
			return;
		}

		JSONObject set=loginService.getSysBaseSet();

		Enumeration enu=request.getParameterNames();  
		while(enu.hasMoreElements()){  
			String paraName=(String)enu.nextElement();  
			System.out.println(paraName+": "+request.getParameter(paraName));  
			data.element(paraName+"", request.getParameter(paraName));
		}  
		//Enumeration enu=request.getParameterNames();  
//		while(enu.hasMoreElements()){  
//			String paraName=(String)enu.nextElement();  
//			System.out.println(paraName+": "+request.getParameter(paraName));  
//			data.element(paraName+"", request.getParameter(paraName));
//		}  
		
		//创建订单
		String id=data.getString("game_user_id");//用户id
		String zaid=data.getString("server_id");//商品ID
		String paytype="0";//0 微信  1支付宝
		String platform=data.getString("private_data");//平台标识
//		String platform="520";//平台标识

		System.out.println("user:"+id+",shop:"+zaid+",paytype:"+paytype+",platform:"+platform);

		//取得商品信息	
		JSONObject order=gsbiz.getzamall(zaid);
		//取得平台信息
		JSONObject ver=gsbiz.getVersion(platform);
		JSONObject user=userService.getUserInfoByAccount(id);
		if (!Dto.isObjNull(order)&&!Dto.isObjNull(ver)&&!Dto.isObjNull(user)) {

			String uporderCode=Dto.getorderCode();
			JSONObject obj=new JSONObject();
			obj.element("code", uporderCode);
			obj.element("objId", order.getInt("id"));
			obj.element("userid", user.getLong("id"));
			obj.element("title", order.getString("title"));
			obj.element("money", order.getDouble("payMoney"));
			
			/*if (order.getInt("type")==Dto.ZAMALL_TYPE_CARD) {
				obj.element("type",Dto.ZAMALLEXCHANGEREC_TYPE_BUYCARD);//房卡
			}else if (order.getInt("type")==Dto.ZAMALL_TYPE_CONIS){
				obj.element("type", Dto.ZAMALLEXCHANGEREC_TYPE_BUYCOINS);//金币
			}else if (order.getInt("type")==Dto.ZAMALL_TYPE_VIP){
				obj.element("type", Dto.ZAMALLEXCHANGEREC_TYPE_VIP);//VIP
			}else if (order.getInt("type")==Dto.ZAMALL_TYPE_DISCOUNT){
				obj.element("type", Dto.ZAMALLEXCHANGEREC_TYPE_DISCOUNT);//VIP
			}else if(order.getInt("type")==Dto.ZAMALL_TYPE_YUANBAO){
				obj.element("type", Dto.ZAMALLEXCHANGEREC_TYPE_YUANBAO);//元宝
			}*/
			obj.element("type", Dto.ZAMALLEXCHANGEREC_TYPE_APP);//type  用于记录是来自哪的订单
			obj.element("sum",order.getInt("denomination"));
			obj.element("memo",platform);
			obj.element("text",data.getString("order_id"));
			//判断订单是否存在
			if(!Dto.isObjNull(fbiz.creataOrderText(data.getString("order_id")))){
				System.out.println("请勿重复提交订单");
				return;
			}
			//long pay_id = fbiz.creataOrder2(obj);
			boolean creataOrder2 = fbiz.creataOrder2(obj);
			
			//if(pay_id>0){
			if(creataOrder2){
				System.out.println("创建订单成功");
			}else{
				System.out.println("创建订单成功");
			}
			//String pay_no = pay_id+"";
			//if( pay_no!=null && !"".equals(pay_no) ){
			if( creataOrder2 ){

				JSONObject za=fbiz.getExchangeRec(uporderCode);

				if(!Dto.isObjNull(JSONObject.fromObject(za))){
					String mallid=za.getString("objId");
					String userid=za.getString("userId");
					//取得商品信息	
					JSONObject mall=gsbiz.getzamall(za.getString("objId"));
					//查询订单
					//更新用户积分
					Map<String,String> map=new HashMap<String,String>();
					map.put("userId", za.getString("userId"));
					map.put("ordercode", uporderCode);

					if (za.getInt("status")==0) {
						if(mall.getInt("type")==Dto.ZAMALL_TYPE_YUANBAO) {
							//用户添加元宝
							fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "yuanbao");
							//map.put("type", "用户购买"+set.getString("yuanbaoname")+"："+za.getInt("sum")+"个");
						}else if(mall.getInt("type")==Dto.ZAMALL_TYPE_CARD) {
							//用户添加房卡
							fbiz.updZaUser(userid, za.getInt("sum"), "roomcard");
							//map.put("type", "用户购买"+set.getString("cardname")+"："+za.getInt("sum")+"张");
						}else if (mall.getInt("type")==Dto.ZAMALL_TYPE_CONIS){
							//用户添加金币
							fbiz.updZaUser(userid, za.getInt("sum"), "coins");
							//map.put("type", "用户购买"+set.getString("coinsname")+"："+za.getInt("sum")+"个");
						}/*else if (za.getInt("type")==Dto.ZAMALLEXCHANGEREC_TYPE_VIP){//用户购买VIP
							JSONObject mall= fbiz.getZaMallById(za.getLong("objId"));
							if (mall.getInt("type")==Dto.ZAMALLEXCHANGEREC_TYPE_VIP) {
								if (!Dto.stringIsNULL(mall.getString("coins"))&&mall.getInt("coins")!=0) {
									//用户添加金币
									fbiz.updZaUser(userid, mall.getInt("coins"), "coins");
								}
								if (!Dto.stringIsNULL(mall.getString("roomcard"))&&mall.getInt("roomcard")!=0) {
									//用户添加房卡
									fbiz.updZaUser(userid, mall.getInt("roomcard"), "roomcard");
								}
							}
							//更改玩家VIP状态
							fbiz.updateUserVip(Long.parseLong(userid), za.getInt("objId"));
							map.put("type", "购买VIP："+za.getInt("sum")+"天");
						}*/
						//支付成功更改订单状态
						fbiz.updExchangeRec(za.getLong("id"));
						System.out.println("购买成功,订单号："+uporderCode);

						String backMsg=HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null, "utf-8");
						System.out.println("更新用户积分返回参数："+backMsg);

					}
					request.setAttribute("payOrder", za);

				}
			}
			//返回给微信参数
			System.out.println("返回参数response：ok");
			Dto.printMsg(response, "ok");
		}
	}

	/*

	//第三方接受到的参数
    String order_id = request.getParameter("pay_status");
    String product_count = request.getParameter("product_count");
    String amount = request.getParameter("amount");
    String pay_status = request.getParameter("pay_status");
    String pay_time = request.getParameter("pay_time");
    String user_id = request.getParameter("user_id");
    String order_type = request.getParameter("order_type");
    String game_user_id = request.getParameter("game_user_id");
    String server_id = request.getParameter("server_id");
    String product_name = request.getParameter("product_name");
    String product_id = request.getParameter("product_id");
    String channel_product_id = request.getParameter("channel_product_id");
    String private_data = request.getParameter("private_data");
    String channel_number = request.getParameter("channel_number");
    String sign = request.getParameter("sign");
    String source = request.getParameter("source");
    String enhanced_sign = request.getParameter("enhanced_sign");
    String channel_order_id = request.getParameter("channel_order_id");
    String game_id = request.getParameter("game_id");
    String plugin_id = request.getParameter("plugin_id");
    String currency_type = request.getParameter("currency_type");
	 */
	/**
	 * 支付成功后微信通知
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/wxpay_notify.json")
	public void notify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("支付成功后微信异步通知");
		String inputLine = "";
		String notityXml = "";
		String resXml = "";

		try {
			while ((inputLine = request.getReader().readLine()) != null) {
				notityXml += inputLine;
			}
			request.getReader().close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> map = XMLParser.getMapFromXML(notityXml);


		if("SUCCESS".equals(map.get("return_code").toString())){

			//更新状态（订单状态）
			String outTradeNo = map.get("out_trade_no").toString();
			JSONObject order=fbiz.getExchangeRec(outTradeNo);

			if(order!=null){

				// 处理业务
				String url = request.getSession().getAttribute(Configure.getBASE_URL()) +"/wxpay_callback.html?ordercode=" + outTradeNo;
				HttpReqUtil.doGet(url, "", "UTF-8");

				//支付成功
				resXml = "<xml><return_code><![CDATA[SUCCESS]]></return_code>"
						+ "<return_msg><![CDATA[OK]]></return_msg></xml>";
			}else{
				resXml = "<xml><return_code><![CDATA[FAIL]]></return_code>"
						+ "<return_msg><![CDATA[报文为空]]></return_msg></xml>";
			}

		}else{
			resXml = "<xml><return_code><![CDATA[FAIL]]></return_code>"
					+ "<return_msg><![CDATA[报文为空]]></return_msg></xml>";
		}

		//返回给微信参数
		PrintWriter pw = response.getWriter();
		pw.write(resXml);
		pw.flush();
		pw.close();

	}
	

	/**
	 * 将参数名从小到大排序，结果如：adfd,bcdr,bff,zx
	 * 
	 * @param List<String> paramNames 
	 */
	public void sortParamNames(List<String> paramNames) {
			Collections.sort(paramNames, new Comparator<String>() {
				@Override
				public int compare(String str1,String str2) {
					return str1.compareTo(str2);
				}
			});
	}
	
	/**
	 * 从 HTTP请求参数 生成待签字符串, 此方法需要在 serverlet 下测试, 测试的时候取消注释, 引入该引入的类
	 */
	public String getValues(HttpServletRequest request){
		Enumeration<String> requestParams=request.getParameterNames();//获得所有的参数名
		List<String> params=new ArrayList<String>();
		while (requestParams.hasMoreElements()) {
			params.add(requestParams.nextElement());
		}
		sortParamNames(params);// 将参数名从小到大排序，结果如：adfd,bcdr,bff,zx
		String paramValues="";
		for (String param : params) {//拼接参数值
			if (param.equals("sign")) {
				originSign = request.getParameter(param);
				continue;
			}
			String paramValue=request.getParameter(param);
			if (paramValue!=null) {
				paramValues+=paramValue;
			}
		}
		
		return paramValues;
	}
	
}
