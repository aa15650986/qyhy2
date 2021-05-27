package com.qy.game.server.dating;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qy.game.service.UserService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.qy.game.service.dating.FundRechargeServerBiz;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.utils.Dto;
import com.qy.game.utils.HttpReqUtil;
import com.qy.game.utils.MD5;

@Controller
public class GoldShopService {

	@Resource
	private GoldShopBizServer gsbiz;
	@Resource
	private FundRechargeServerBiz fbiz;
	@Resource
	private UserService userService;

	/**
	 * 金币场
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getzamall")
	public void getzamall(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String type = request.getParameter("type");
		// String platform=request.getParameter("platform");
		String domainstr = request.getServerName();
		String url = gsbiz.getdomain(domainstr);
		JSONObject con = new JSONObject();
		con.element("type", type).element("platform", Dto.stringIsNULL(url) ? null : url);
		JSONArray data = gsbiz.getzamall(con);
		String basePath = userService.getSokcetInfo().get("headimgUrl");
		for (int i = 0; i < data.size(); i++) {
			JSONObject dat = data.getJSONObject(i);
			if (dat.containsKey("logo")) {
				dat.element("logo", basePath + dat.getString("logo"));
			}
		}
		Dto.printMsg(response, data.toString());

	}

	/**
	 * 兑换
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getexchange")
	public void getexchange(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String type = request.getParameter("type");
		String platform = request.getParameter("platform");

		JSONObject con = new JSONObject();
		con.element("type", type).element("platform", platform);
		JSONArray data = gsbiz.getexchange(con);
		String basePath = userService.getSokcetInfo().get("headimgUrl");
		for (int i = 0; i < data.size(); i++) {
			JSONObject dat = data.getJSONObject(i);
			if (dat.containsKey("logo")) {
				dat.element("logo", basePath + dat.getString("logo"));
			}
		}
		Dto.printMsg(response, data.toString());

	}

	/**
	 * 查询兑换记录 * @param request
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getexchangerec")
	public void getexchangerec(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String id = request.getParameter("id");

		JSONObject con = new JSONObject();
		con.element("userId", id);
		JSONArray data = gsbiz.getexchangerec(con);
		Dto.printMsg(response, data.toString());

	}

	/**
	 * 进行兑换
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("gmexchange.json")
	public void gmexchange(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject msg = new JSONObject();
		String domainstr = request.getServerName();
		String url = gsbiz.getdomain(domainstr);
		try {
			String id = request.getParameter("id");
			String zeid = request.getParameter("zaid");
			String text = request.getParameter("text");
			String uuid = request.getParameter("uuid");
			if (!Dto.stringIsNULL(text)) {
				text = URLDecoder.decode(text, "utf-8");
				JSONObject exchangeInfo = JSONObject.fromObject(text);
				if (exchangeInfo.containsKey("tel") && !Dto.stringIsNULL(exchangeInfo.getString("tel"))) {
					if (Pattern.matches(Dto.REGEX_MOBILE, exchangeInfo.getString("tel"))) {
						JSONObject con = new JSONObject();
						con.element("id", id).element("zeid", zeid).element("uuid", uuid)
								.element("text", Dto.stringIsNULL(text) ? "" : text).element("platform", url);
						String sj = fbiz.upbasezf(con);
						msg.element("code", "1").element("msg", sj);
					} else {
						msg.element("code", "0").element("msg", "请输入正确的手机号");
					}
				} else {
					msg.element("code", "0").element("msg", "请输入手机号");
				}
			} else {
				msg.element("code", "0").element("msg", "请输入正确的信息");
			}
		} catch (Exception e) {
			msg.element("code", "0").element("msg", "网络错误，请稍后兑换");
		}
		Dto.printMsg(response, msg.toString());

		/* 已被替换掉 - 开始 */
		/*
		 * JSONObject msg=new JSONObject(); String domainstr=request.getServerName();
		 * String url=gsbiz.getdomain(domainstr); try { String
		 * id=request.getParameter("id"); String zeid=request.getParameter("zaid");
		 * String text=request.getParameter("text"); if(!Dto.stringIsNULL(text)) text=
		 * new String(text.getBytes(), "utf-8"); JSONObject con=new JSONObject();
		 * con.element("id", id) .element("zeid", zeid) .element("text",
		 * Dto.stringIsNULL(text)?"":text) .element("platform", url); String
		 * sj=fbiz.upbasezf(con); msg.element("code", "1") .element("msg", sj); } catch
		 * (Exception e) { // TODO: handle exception msg.element("code", "0")
		 * .element("msg", "网络错误，请稍后兑换"); } Dto.printMsg(response,msg.toString());
		 */

		/* 已被替换掉 - 结束 */
	}

	// 兑换进行兑换2-没有微信的
	@RequestMapping("gmexchange2.json")
	public void gmexchang2e(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject msg = new JSONObject();
		try {
			String id = request.getParameter("id");
			String zaid = request.getParameter("zaid");
			String text = request.getParameter("text");
			String platform = request.getParameter("platform");
			String uuid = request.getParameter("uuid");

			if (!Dto.stringIsNULL(text)) {
				text = URLDecoder.decode(text, "utf-8");
				JSONObject exchangeInfo = JSONObject.fromObject(text);
				if (exchangeInfo.containsKey("tel") && !Dto.stringIsNULL(exchangeInfo.getString("tel"))) {
					if (Pattern.matches(Dto.REGEX_MOBILE, exchangeInfo.getString("tel"))) {
						String sj = fbiz.upbasezf2(id, zaid, text, platform, uuid);
						msg.element("code", "1").element("msg", sj);
					} else {
						msg.element("code", "0").element("msg", "请输入正确的手机号");
					}
				} else {
					msg.element("code", "0").element("msg", "请输入手机号");
				}
			} else {
				msg.element("code", "0").element("msg", "请输入正确的信息");
			}
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0").element("msg", "网络错误，请稍后兑换");
		}
		Dto.printMsg(response, msg.toString());
	}

	/**
	 * 进行商城的购买
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	/* @RequestMapping("gmzamall.json") */
	public void gmzamall(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject msg = new JSONObject();
		String msg1 = "";

		try {
			String id = request.getParameter("id");
			String zaid = request.getParameter("zaid");
			/*
			 * String paytype=request.getParameter("paytype");
			 * 
			 * if (paytype.equals("1")) { paytype="aliwap";//支付宝 }else{
			 * paytype="wxhtml";//微信 }
			 */

			JSONObject za = gsbiz.getzamall(zaid);

			String uporderCode = Dto.getorderCode();

			JSONObject obj = new JSONObject();
			obj.element("code", uporderCode);
			obj.element("objId", za.getInt("id"));
			obj.element("userid", id);
			obj.element("title", za.getString("title"));
			obj.element("money", za.getDouble("payMoney"));
			if (za.getInt("type") == Dto.ZAMALL_TYPE_CARD) {
				obj.element("type", Dto.ZAMALL_TYPE_CARD);// 房卡
			} else if (za.getInt("type") == Dto.ZAMALL_TYPE_CONIS) {
				obj.element("type", Dto.ZAMALL_TYPE_CONIS);// 金币
			} /*
				 * else if (za.getInt("type")==Dto.ZAMALL_TYPE_VIP){ obj.element("type",
				 * Dto.ZAMALLEXCHANGEREC_TYPE_VIP);//VIP }else if
				 * (za.getInt("type")==Dto.ZAMALL_TYPE_DISCOUNT){ obj.element("type",
				 * Dto.ZAMALLEXCHANGEREC_TYPE_DISCOUNT);//VIP }
				 */
			obj.element("sum", za.getInt("denomination"));
			// obj.element("text", za.getString("des"));
			boolean is = fbiz.creataOrder(obj);

			if (is) {

				String str = MD5.MD5Encode(uporderCode + String.valueOf(za.getInt("payMoney") * 100) + "wxhtml"
						+ String.valueOf(System.currentTimeMillis() / 1000) + Dto.mch + MD5.MD5Encode(Dto.key));
				msg1 = "<html><head>" + "<script type='text/javascript'>"
						+ "location.href ='http://api.tellni.cn/waporder/order_add?mch=" + Dto.mch
						+ "&pay_type=wxhtml&order_id=" + uporderCode + "&time="
						+ String.valueOf(System.currentTimeMillis() / 1000) + "&money="
						+ String.valueOf(za.getInt("payMoney") * 100) + "&sign=" + str + "&extra=" + za.getString("id")
						+ "_" + id
						+ "&notify_url=http://app.akyule.cn/zagame/gmzamallBack.html&return_url=http://app.akyule.cn/zagame/gmzamallBackHm.html?order_id="
						+ uporderCode + "';" + "</script></head><body>请进行付款，静心等待！</body></html>";
			}

		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0").element("msg", "网络错误，请稍后购买");
		}

		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.getWriter().write(msg1);
		// Dto.printMsg(response,msg.toString());
	}

	/**
	 * 前端页面回调
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("gmzamallBackHm")
	public void gmzamallBackHtml(HttpServletRequest request, HttpServletResponse response) throws IOException {
		/*
		 * String order_id=request.getParameter("order_id"); String tt=""; if
		 * (!"".equals(order_id)) { JSONObject za=fbiz.getExchangeRec(order_id); String
		 * time=String.valueOf(System.currentTimeMillis()/1000); String
		 * str=MD5.MD5Encode(Dto.mch+za.getString("orderCode")+za.getInt("money")*100+
		 * "wxhtml"+time+MD5.MD5Encode(Dto.key)); String
		 * queryString="?mch="+Dto.mch+"&pay_type=wxhtml&order_id="+order_id+"&time="+
		 * time+"&money="+String.valueOf(za.getInt("money")*100)+"&sign="+str;
		 * 
		 * String msg=HttpReqUtil.doGet("http://api.tellni.cn/lqpay/showquery",
		 * queryString, "utf-8");
		 * 
		 * JSONObject backmsg=JSONObject.fromObject(msg);
		 * 
		 * if (backmsg.getBoolean("ok")) { tt="购买成功!祝你游戏愉快！"; }else{
		 * tt=backmsg.getString("msg"); } }
		 */
		String msg1 = "<html><head><script type='text/javascript'>window.close();</script></head><body></body></html>";
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.getWriter().write(msg1);
	}

	/**
	 * 付款异步回调
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws FileUploadException
	 */
	@RequestMapping("gmzamallBack")
	public void gmzamallBack(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException, IOException, FileUploadException {

		// 转型为MultipartHttpRequest
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

		// 获得上传的文件（根据前台的name名称得到上传的文件）
		String sign = multipartRequest.getParameter("sign");
		String time = multipartRequest.getParameter("time");
		String transactionId = multipartRequest.getParameter("transactionId");
		String orderNo = multipartRequest.getParameter("orderNo");
		String status = multipartRequest.getParameter("status");
		String pay_type = multipartRequest.getParameter("pay_type");
		String extra = multipartRequest.getParameter("extra");
		String money = multipartRequest.getParameter("money");
		String mch = multipartRequest.getParameter("mch");
		String order_id = multipartRequest.getParameter("order_id");
		String commodity_name = multipartRequest.getParameter("commodity_name");

		// System.out.println(sign);
		JSONObject receiveData = new JSONObject();
		receiveData.element("sign", sign);
		receiveData.element("time", time);
		receiveData.element("transactionId", transactionId);
		receiveData.element("sign", sign);
		receiveData.element("orderNo", orderNo);
		receiveData.element("status", status);
		receiveData.element("pay_type", pay_type);
		receiveData.element("extra", extra);
		receiveData.element("money", money);
		receiveData.element("mch", mch);
		receiveData.element("order_id", order_id);
		receiveData.element("commodity_name", commodity_name);

		// JSONObject receiveData=JSONObject.fromObject(result);

		if (yzsign(receiveData))// 验证签名
		{
			Dto.printMsg(response, "SUCCESS");

			if (status.equals("1")) {// 成功
				String mallid = extra.split("\\_")[0];
				String userid = extra.split("\\_")[1];
				// 查询订单
				JSONObject za = fbiz.getExchangeRec(order_id);
				// 取得商品信息
				JSONObject mall = gsbiz.getzamall(za.getString("objId"));
				// 更新用户积分
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", za.getString("userId"));
				map.put("ordercode", order_id);

				if (za.getInt("status") == 0) {
					if (mall.getInt("type") == Dto.ZAMALL_TYPE_CARD) {
						// 用户添加房卡
						fbiz.updZaUser(userid, za.getInt("sum"), "roomcard");
						// map.put("type", "用户购买房卡："+za.getInt("sum")+"张");
					} else if (mall.getInt("type") == Dto.ZAMALL_TYPE_CONIS) {
						// 用户添加金币
						fbiz.updZaUser(userid, za.getInt("sum"), "coins");
						// map.put("type", "用户购买金币："+za.getInt("sum")+"个");
					} /*
						 * else if (mall.getInt("type")==Dto.ZAMALLEXCHANGEREC_TYPE_VIP){//用户购买VIP
						 * JSONObject mall= fbiz.getZaMallById(za.getLong("objId")); if
						 * (mall.getInt("type")==Dto.ZAMALLEXCHANGEREC_TYPE_VIP) { if
						 * (!Dto.stringIsNULL(mall.getString("coins"))&&mall.getInt("coins")!=0) {
						 * //用户添加金币 fbiz.updZaUser(userid, mall.getInt("coins"), "coins"); } if
						 * (!Dto.stringIsNULL(mall.getString("roomcard"))&&mall.getInt("roomcard")!=0) {
						 * //用户添加房卡 fbiz.updZaUser(userid, mall.getInt("roomcard"), "roomcard"); } }
						 * //更改玩家VIP状态 fbiz.updateUserVip(Long.parseLong(userid), za.getInt("objId"));
						 * map.put("type", "购买VIP："+za.getInt("sum")+"天"); }
						 */
					// 支付成功更改订单状态
					fbiz.updExchangeRec(za.getLong("id"));
					System.out.println("购买成功：" + order_id);

					String backMsg = HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null,
							"utf-8");
					System.out.println("更新用户积分返回参数：" + backMsg);
				}
			} else if (status.equals("0")) {
				// 失败
				System.out.println("购买失败：" + order_id);
			}
		} else {

			Dto.printMsg(response, "ERROR");
		}
	}

	/**
	 * 签名验证
	 * 
	 * @param obj
	 * @return
	 */
	private Boolean yzsign(JSONObject obj) {
		String str = MD5.MD5Encode(obj.getString("order_id") + obj.getString("orderNo") + obj.getString("money")
				+ Dto.mch + "wxhtml" + obj.getString("time") + MD5.MD5Encode(Dto.key));

		boolean is = false;
		if (str.equals(obj.getString("sign"))) {
			is = true;
		}
		return is;
	}

	/*
	 * 获取vip
	 */
	@RequestMapping("getsysvip")
	public void getsysvip(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONArray list = gsbiz.getsysvip();
		String basePath = userService.getSokcetInfo().get("headimgUrl");
		for (int i = 0; i < list.size(); i++) {
			if (list.getJSONObject(i).containsKey("logo")) {
				String img = list.getJSONObject(i).getString("logo");
				list.getJSONObject(i).element("logo", basePath + img);
			}
		}
		Dto.printMsg(response, list.toString());
	}

	/**
	 * 购买vip
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("gmvip")
	public void gmvip(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject msg = new JSONObject();
		HttpServletRequest httpRequest = request;
		try {
			String id = request.getParameter("id");
			String zaid = request.getParameter("zaid");
			JSONObject za = gsbiz.getsysvip(zaid);
			String url = "http://127.0.0.1/" // 服务器地址
					+ httpRequest.getContextPath() // 项目名称
					+ "/sysbalpayment.html"; // 请求页面或其他地址
			Map<String, String> params = new HashMap<String, String>();
			params.put("id", id);
			params.put("price", String.valueOf(za.getString("price")));
			String type = HttpReqUtil.doPost(url, params, "", "UTF-8");
			if ("成功".equals(type)) {
				String id2 = fbiz.addsysbalreczf(id, "-" + za.getString("price"), "vip", za.getLong("id"));// 扣钱加用户
				gsbiz.upsyabaldes(id2, "您支付了" + za.getString("price") + "的" + za.getString("name"));
				msg.element("code", "1").element("msg", "购买成功");

			} else {
				msg.element("code", "0").element("msg", "网络错误，请稍后购买");

			}

		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0").element("msg", "网络错误，请稍后购买");
		}
		Dto.printMsg(response, msg.toString());
	}
}
