package com.qy.game.server.dating;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.qy.game.model.KFTSMPayMode;
import com.qy.game.service.UserService;
import com.qy.game.service.dating.FundRechargeServerBiz;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.utils.DateUtils;
import com.qy.game.utils.Dto;
import com.qy.game.utils.HttpReqUtil;
import com.qy.game.utils.MD5;
import com.qy.game.utils.wechat.Configure;


/**
 * 第三方支付接口
 * @author hxq
 *2017.09.05
 */
@Controller
public class ThirdPayService {

	@Resource
	private GoldShopBizServer gsbiz;
	@Resource
	private FundRechargeServerBiz fbiz;

	@Resource
	private UserService userService;
	
	/**
	 * 进行商城的购买
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("gmzamall")
	public void zapaymall(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String msg1="";
		
		try {
			String id=request.getParameter("id");//用户id
			String zaid=request.getParameter("zaid");//商品ID
			String paytype=request.getParameter("paytype");//0 微信  1支付宝
			String platform=request.getParameter("platform");//平台标识
			
			System.out.println("user:"+id+",shop:"+zaid+",paytype:"+paytype+",platform:"+platform);
			
			//取得商品信息	
			JSONObject za=gsbiz.getzamall(zaid);
			//取得平台信息
			JSONObject ver=gsbiz.getVersion(platform);
			JSONObject user=userService.getUserInfoByID(Long.valueOf(id));
			
			if (!Dto.isObjNull(za)&&!Dto.isObjNull(ver)&&!Dto.isObjNull(user)) {
				
				
				String uporderCode=Dto.getorderCode();
				JSONObject obj=new JSONObject();
				obj.element("code", uporderCode);
				obj.element("objId", za.getInt("id"));
				obj.element("userid", id);
				obj.element("title", za.getString("title"));
				obj.element("money", za.getDouble("payMoney"));
				/*if (za.getInt("type")==1) {
					obj.element("type", 13);//房卡
				}else if (za.getInt("type")==2){
					obj.element("type", 14);//金币
				}else if(za.getInt("type")==4){
					obj.element("type", 16);//VIP
				}*/
				/*if (za.getInt("type")==Dto.ZAMALL_TYPE_CARD) {
					obj.element("type",Dto.ZAMALLEXCHANGEREC_TYPE_BUYCARD);//房卡
				}else if (za.getInt("type")==Dto.ZAMALL_TYPE_CONIS){
					obj.element("type", Dto.ZAMALLEXCHANGEREC_TYPE_BUYCOINS);//金币
				}else if (za.getInt("type")==Dto.ZAMALL_TYPE_VIP){
					obj.element("type", Dto.ZAMALLEXCHANGEREC_TYPE_VIP);//VIP
				}else if (za.getInt("type")==Dto.ZAMALL_TYPE_DISCOUNT){
					obj.element("type", Dto.ZAMALLEXCHANGEREC_TYPE_DISCOUNT);//VIP
				}else if(za.getInt("type")==Dto.ZAMALL_TYPE_YUANBAO){
					obj.element("type", Dto.ZAMALLEXCHANGEREC_TYPE_YUANBAO);//元宝
				}*/
				obj.element("type", Dto.ZAMALLEXCHANGEREC_TYPE_APP);//type  用于记录是来自哪的订单
				obj.element("sum",za.getInt("denomination"));
				obj.element("memo",platform);
				obj.element("platform",platform);//新加后用于平台标识
				//obj.element("text", za.getString("des"));
				boolean is= fbiz.creataOrder(obj);
				String str="";
				if (is) {
					if(ver.getString("ThirdPay").equals("tellni")){
						if (paytype.equals("1")) {
							paytype="aliwap";//支付宝
						}else{
							paytype="wxhtml";//微信
						}
						
						 str=MD5.MD5Encode(uporderCode+String.valueOf(za.getInt("payMoney")*100)+paytype+String.valueOf(System.currentTimeMillis()/1000)+ver.getString("mch")+MD5.MD5Encode(ver.getString("key")));
						msg1="<html><head>" +
								"<script type='text/javascript'>" +
								"location.href ='"+ver.getString("thirdUrl")+"?mch="+ver.getString("mch")+"&pay_type="+paytype+"&order_id="+uporderCode+"&time="+String.valueOf(System.currentTimeMillis()/1000)+"&money="+String.valueOf(za.getInt("payMoney")*100)+"&sign="+str+"&extra="+za.getString("id")+"_"+id+"_"+platform+"&notify_url="+ver.getString("veUrl")+"/zagame/zaTellniBack.html&return_url="+ver.getString("veUrl")+"/zagame/zaTellni.html?order_id="+uporderCode+"';" +
								"</script></head><body>请进行付款，静心等待！</body></html>";
					}else if(ver.getString("ThirdPay").equals("openepay")){
						if (paytype.equals("1")) {
							paytype="38";//开联通 支付宝
						}else{
							paytype="48";//开联通 微信
						}
						String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
						str=MD5.MD5Encode("inputCharset=1&receiveUrl="+ver.getString("veUrl")+"/zagame/zaOpenepay.html&version=v1.0&language=1&signType=0&merchantId="+ver.getString("mch")+"&orderNo="+uporderCode+"&orderAmount="+String.valueOf(za.getInt("payMoney")*100)+"&orderCurrency=156&orderDatetime="+dateTime+"&productName="+za.getString("title")+"&ext1="+za.getString("id")+"&ext2="+id+"&payType="+paytype+"&key="+ver.getString("key")).toUpperCase();
						
						msg1="<html><head>" +
								"<script type='text/javascript'>" +
								"location.href ='"+ver.getString("thirdUrl")+"?inputCharset=1&receiveUrl="+ver.getString("veUrl")+"/zagame/zaOpenepay.html&version=v1.0&language=1&signType=0&merchantId="+ver.getString("mch")+"&orderNo="+uporderCode+"&orderAmount="+String.valueOf(za.getInt("payMoney")*100)+"&orderCurrency=156&orderDatetime="+dateTime+"&productName="+za.getString("title")+"&ext1="+za.getString("id")+"&ext2="+id+"&payType="+paytype+"&signMsg="+str+"'"+
								"</script></head><body>请进行付款，静心等待！</body></html>";
					}else if(ver.getString("ThirdPay").equals("jtpay")){
						if (paytype.equals("1")) {
							paytype="4";//骏付通 支付宝
						}else{
							paytype="3";//骏付通 微信
						}
						String userip=request.getRemoteAddr();
						userip=userip.replace(".", "_");
						String dateTime1 = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
						//str=MD5.MD5Encode("p1_usercode=1&p2_order=&p3_money=v1.0&p4_returnurl=1&p5_notifyurl="+ver.getString("veUrl")+"/zagame/zaJypayBack.html&p6_ordertime="+dateTime1+);
						String rawString = ver.getString("mch")+"&"+uporderCode+"&"+za.getInt("payMoney")+"&"+ver.getString("veUrl")+"/zagame/zaJtpay.html&"+ver.getString("veUrl")+"/zagame/zaJtpayBack.html&"+dateTime1+ver.getString("key");
						
						str=MD5.MD5Encode(rawString).toUpperCase();
						
						msg1="<html><head>" +
								"<script type='text/javascript'>" +
								"location.href ='"+ver.getString("thirdUrl")+"?p1_usercode="+ver.getString("mch")+"&p2_order="+uporderCode+"&p3_money="+za.getInt("payMoney")+"&p4_returnurl="+ver.getString("veUrl")+"/zagame/zaJtpay.html&p5_notifyurl="+ver.getString("veUrl")+"/zagame/zaJtpayBack.html&p6_ordertime="+dateTime1+"&p9_paymethod="+paytype+"&p14_customname="+user.getString("account")+"&p17_customip="+userip+"&p18_product="+za.getString("title")+"&p20_productnum=1&p24_remark="+za.getString("id")+"_"+id+"_"+platform+"&p25_terminal=3&p26_iswappay=3&p7_sign="+str+"'"+
								"</script></head><body>请进行付款，静心等待！</body></html>";
					
					}else if(ver.getString("ThirdPay").equals("huifutianxia")){
						//惠福天下支付
						if(za.getInt("payMoney")<=1){
							msg1="<html><head></head><body>购买金额需大于1元</body></html>";
						}else if(Dto.stringIsNULL(request.getParameter("account"))){
							msg1="<html><head></head><body>请输入支付宝账号</body></html>";
						}else{
							JSONObject param=new JSONObject();
							param.element("mch", ver.getString("mch"))
								 .element("veUrl", ver.getString("veUrl")+"/zagame/huifupayback.do")
								 .element("payType", paytype.equals("1")?"1":"2")
								 .element("money", za.getInt("payMoney") * 100)
								 .element("uporderCode", uporderCode)
								 .element("account", request.getParameter("account"));
							askHuifuPay(response, param);
							return;
							
						}
					}else if(ver.getString("ThirdPay").equals("shwwmj")){
						String redirect="";
						if (paytype.equals("1")) {
							paytype="ALIPAY";//支付宝
							redirect="1";
						}else{
							paytype="WEIXIN";//微信
							redirect="1";
						}
						 Random random = new Random();
						 int d= random.nextInt(10);
						 double zb =Double.valueOf(d)/100;
						 
						 double me=za.getDouble("payMoney")-zb;
						//String md="mchid="+ver.getString("mch")+"&money="+za.getInt("payMoney")+"&notifyurl="+ver.getString("veUrl")+"/zagame/zaShwwmjBack.html&payChannel="+paytype+"&redirect="+redirect+"&returnurl="+ver.getString("veUrl")+"/zagame/zaShwwmj.html&trade_sn="+uporderCode+"&key="+ver.getString("key");
						str=MD5.MD5Encode("mchid="+ver.getString("mch")+"&money="+me+"&notifyurl="+ver.getString("veUrl")+"/zagame/zaShwwmjBack.html&payChannel="+paytype+"&redirect="+redirect+"&returnurl="+ver.getString("veUrl")+"/zagame/zaShwwmj.html&trade_sn="+uporderCode+"&key="+ver.getString("key"));
							//"location.href ='http://m.shwwmj.com/api/v1.alipay/index?mchid="+ver.getString("mch")+"&money="+za.getInt("payMoney")+"&payChannel="+paytype+"&redirect="+redirect+"&trade_sn="+uporderCode+"&notifyurl="+ver.getString("veUrl")+"/zagame/zaShwwmjBack.html&returnurl="+ver.getString("veUrl")+"/zagame/zaShwwmj.html&sign="+str+"'"+
						/*if ("WEIXIN".equals(paytype)) {
							Map<String,String> map=new HashMap<String, String>();
							map.put("mchid", ver.getString("mch"));
							map.put("money", za.getString("payMoney"));
							map.put("payChannel", paytype);
							map.put("redirect", redirect);
							map.put("trade_sn", uporderCode);
							map.put("notifyurl", ver.getString("veUrl")+"/zagame/zaShwwmjBack.html");
							map.put("returnurl", ver.getString("veUrl")+"/zagame/zaShwwmj.html");
							map.put("sign", str);
							String msg= HttpReqUtil.doPost("http://m.shwwmj.com/api/v1.alipay/index", map, null, "utf-8");
							JSONObject backData=JSONObject.fromObject(msg);
							if(backData.containsKey("data") && backData.get("data")!=null && !("null").equals(backData.getString("data")))
							{
								msg1="";
							}
							msg1="<html><head></head><body>暂时未开通！</body></html>";
						}else{}*/
							//http://www.fjlmnet.com/api/v3.index/pingan 测试
							//http://m.shwwmj.com/api/v1.alipay/index  线上
						/*LogUtil.print("mchid="+ver.getString("mch")+"&money="+me+"&payChannel="+
							paytype+"&redirect="+redirect+"&trade_sn="+uporderCode+"&notifyurl="+
								ver.getString("veUrl")+"/zagame/zaShwwmjBack.html"+"&returnurl="
							+ver.getString("veUrl")+"/zagame/zaShwwmj.html"+"&sign="+str);*/
							msg1="<html><head>" +
									"<script type='text/javascript'>" +
									"window.onload=function(){  document.getElementById('myform').submit(); } " +
									"</script></head><body>请进行付款，静心等待！" +
									"<form action='"+ver.getString("thirdUrl")+"' id='myform' method='post'>" +
									"<input type='text' name='mchid' value='"+ver.getString("mch")+"'>" +
									"<input type='text' name='money' value='"+me+"'>" +
									"<input type='text' name='payChannel' value='"+paytype+"'>" +
									"<input type='text' name='redirect' value='"+redirect+"'>" +
									"<input type='text' name='trade_sn' value='"+uporderCode+"'>" +
									"<input type='text' name='notifyurl' value='"+ver.getString("veUrl")+"/zagame/zaShwwmjBack.html'>" +
									"<input type='text' name='returnurl' value='"+ver.getString("veUrl")+"/zagame/zaShwwmj.html'>" +
									"<input type='text' name='sign' value='"+str+"'>" +
									"</form> " +
									"</body></html>";
						
						
					}else if(ver.getString("ThirdPay").equals("wechat")){
						JSONObject wechatset=fbiz.getWechatSetting(1L);
						Configure.setAppID(wechatset.getString("appId"));
						Configure.setMchID(wechatset.getString("merchantCode"));
						Configure.setappKey(wechatset.getString("appSecret"));
						Configure.setOpenPlatID(wechatset.getString("appId"));
						Configure.setBASE_URL(ver.getString("veUrl"));
						Configure.setPAY_NOTIFY_URL(ver.getString("veUrl")+"/zagame/wxpay_notify.json");
						System.out.println(Configure.getAppid());
						System.out.println(Configure.getMchid());
						System.out.println(Configure.getappKey());
						System.out.println(Configure.getOpenPlatID());
						System.out.println(Configure.getPAY_NOTIFY_URL());
						System.err.println(ver.getString("veUrl"));
						JSONObject order=fbiz.getExchangeRec(uporderCode);
						msg1="<html><head>" +
								"<script type='text/javascript'>" +
								"location.href ='"+ver.getString("veUrl")+"/zagame/pay/wxpay.json?pay_no="+order.getLong("id")+"'"+
								"</script></head><body>请进行付款，静心等待！</body></html>";
						/*JSONObject object=new JSONObject();
						object.element("orderCode", uporderCode);
						object.element("body", za.getString("title"));
						object.element("money", za.getDouble("payMoney"));
						object.element("ip", request.getRemoteAddr());
						object.element("tradeType", Configure.PAY_BY_APP);
						object.element("openid", user.getString("openid"));
						String jsApiParameters=WxPayApi.wxpay(object);
						Map<String, Object> map = XMLParser.getMapFromXML(jsApiParameters);
						JSONObject res=JSONObject.fromObject(map);
						if (res.containsKey("return_code")&&res.getString("return_code").equals("SUCCESS")&&res.containsKey("result_code")&&res.getString("result_code").equals("SUCCESS")) {
							String prepay_id=res.getString("prepay_id");
							

						}else {
							msg1="<html><head></head><body>参数有误！！！</body></html>";
						}*/
					}else if(ver.getString("ThirdPay").equals("qianf8")){
						//乾富吧支付
						if(za.getInt("payMoney")<=0){
							msg1="<html><head></head><body>购买金额需大于1元</body></html>";
						}else{
							if (paytype.equals("1")) {
								paytype="aliwap";//乾富吧 支付宝
							}else if (paytype.equals("2")) {
								paytype="ylggp";//乾富吧 银联网关 暂时未开通
							}else{
								paytype="wxhtml";//乾富吧 微信
							}
							String userip=request.getRemoteAddr();
							//userip=userip.replace(".", "_");
							//String dateString = new Date().getTime();
							String dateTime1 =  DateUtils.getNowTimeStamp();
							dateTime1=dateTime1.substring(0, 10);
							System.out.println(za.getInt("payMoney"));
							//md5(order_sn+totle_amount+pay_type+this_date+mch_number+md5 (key) )
							String rawString = uporderCode+(za.getInt("payMoney")*100)+paytype+dateTime1+ver.getString("mch")+MD5.MD5Encode(ver.getString("key"));
							System.out.println(rawString);
							str=MD5.MD5Encode(rawString).toLowerCase();
							System.out.println("mch_number="+ver.getString("mch")
										+"&order_sn="+uporderCode+"&totle_amount="+(za.getInt("payMoney")*100)
										+"&jump_url="+ver.getString("veUrl")+"/zagame/zaQianf8.html&asyn_url="
										+ver.getString("veUrl")+"/zagame/zaQianf8Back.html&this_date="
										+dateTime1+"&pay_type="+paytype+"&ip_add="
										+userip+"&extra_note="+za.getString("id")+"_"+id+"_"+platform
										+"&sign_info="+str+"'");
							msg1="<html><head>" +
									"<script type='text/javascript'>" +
									"location.href ='"+ver.getString("thirdUrl")+"?mch_number="+ver.getString("mch")
										+"&order_sn="+uporderCode+"&totle_amount="+(za.getInt("payMoney")*100)
										+"&jump_url="+ver.getString("veUrl")+"/zagame/zaQianf8.html&asyn_url="
										+ver.getString("veUrl")+"/zagame/zaQianf8Back.html&this_date="
										+dateTime1+"&pay_type="+paytype+"&ip_add="
										+userip+"&extra_note="+za.getString("id")+"_"+id+"_"+platform
										+"&sign_info="+str+"'"+
									"</script></head><body>请进行付款，静心等待！</body></html>";
							
						}
					}else if (ver.getString("ThirdPay").equals("posqian")) {
						if(za.getInt("payMoney")<=0){
							msg1="<html><head></head><body>购买金额需大于1元</body></html>";
						}else{
							if (paytype.equals("1")) {
								// 支付宝
								paytype="aliwap";
							}else if (paytype.equals("2")) {
								// 微信
								paytype="wxhtml";
							}
							String userip=request.getRemoteAddr();
							// 时间戳
							String dateTime1 =  DateUtils.getNowTimeStamp();
							dateTime1=dateTime1.substring(0, 10);
							System.out.println(za.getInt("payMoney"));
							// 签名
							String rawString = uporderCode+(za.getInt("payMoney")*100)+paytype+dateTime1+ver.getString("mch")+MD5.MD5Encode(ver.getString("key"));
							System.out.println(rawString);
							str=MD5.MD5Encode(rawString).toLowerCase();
							System.out.println(str);
							msg1="<html><head>" +
									"<script type='text/javascript'>" +
									"location.href ='"+ver.getString("thirdUrl")+"?mch_number="+ver.getString("mch")
										+"&pay_type="+paytype+"&totle_amount="+(za.getInt("payMoney")*100)
										+ "&this_date="+dateTime1+"&order_sn="+uporderCode
										+"&jump_url="+ver.getString("veUrl")+"/zagame/posqian.html&asyn_url="
										+ver.getString("veUrl")+"/zagame/posqianBack.html"
										+"&ip_add="+userip+"&extra_note="+za.getString("id")+"_"+id+"_"+platform
										+"&sign_info="+str+"'"+
									"</script></head><body>请进行付款，静心等待！</body></html>";
							
						}
					}else if (ver.getString("ThirdPay").equals("jtpayYQW")) {
						if(za.getDouble("payMoney")<=0){
							msg1="<html><head></head><body>购买金额需大于1元</body></html>";
						}else{
							String productCode="";
							if (paytype.equals("1")) {
								productCode="ZFB";
							}else{
								productCode="WX";
							}
							DecimalFormat df = new DecimalFormat("##0.00");
							String money =  df.format(za.getDouble("payMoney"));
							String nowTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
							str=MD5.MD5Encode(ver.getString("mch")+"&"+uporderCode+"&"+money+"&"+nowTime+"&"+productCode+"&"+ver.getString("key"));
							System.out.println(str);
							msg1="<html><head>" +
									"<script type='text/javascript'>" +
									"window.onload=function(){  document.getElementById('myform').submit(); } " +
									"</script></head><body>请进行付款，静心等待！" +
									"<form action='" + ver.getString("thirdUrl") + "' id='myform' method='post'>" +
									"<input type='text' name='p1_yingyongnum' value='"+ver.getString("mch")+"'>" +
									"<input type='text' name='p2_ordernumber' value='"+uporderCode+"'>" +
									"<input type='text' name='p3_money' value='"+money+"'>" +
									"<input type='text' name='p6_ordertime' value='"+nowTime+"'>" +
									"<input type='text' name='p7_productcode' value='"+productCode+"'>" +
									"<input type='text' name='p8_sign' value='"+str+"'>" +
									"<input type='text' name='p14_customname' value='"+id+"'>" +
									"<input type='text' name='p16_customip' value='"+request.getRemoteAddr().replace(".", "_")+"'>" +
									"<input type='text' name='p20_pdesc' value='"+id+"'>" +
									"<input type='text' name='p24_remark' value='"+platform+"'>" +
									"<input type='text' name='p25_terminal' value='"+3+"'>" +
									"<input type='text' name='p26_ext1' value='"+1.1+"'>" +
									"</form> " +
									"</body></html>";
							System.out.println(msg1);
						}
					} else if (ver.getString("ThirdPay").equals("codepay")) {
						// 记得更改 http://codepay.fateqq.com 后台可获得
						String codepay_id = ver.getString("mch");
						// 表单提交的价格
						String price = za.getString("payMoney");
						// 支付类型  1：支付宝 2：QQ钱包 3：微信
						String type = "0".equals(paytype) ? "3" : paytype;
						if (paytype.equals("1")) {
							msg1="<html><head></head><body>请使用微信进行充值</body></html>";
						} else {
							// 支付人的唯一标识
							String pay_id = id;
							// MD5签名
							String origin = "id=" + codepay_id + "&param=" + uporderCode + "&pay_id=" + pay_id + 
									"&price=" + price + "&type=" + type + ver.getString("key");
							str = MD5.MD5Encode(origin);
							msg1="<html><head>" +
									"<script type='text/javascript'>" +
									"window.onload=function(){  document.getElementById('myform').submit(); } " +
									"</script></head><body>请进行付款，静心等待！" +
									"<form action='" + ver.getString("thirdUrl") + "' id='myform' method='post'>" +
									"<input type='text' name='id' value='" + codepay_id + "'>" +
									"<input type='text' name='pay_id' value='" + pay_id + "'>" +
									"<input type='text' name='param' value='" + uporderCode + "'>" +
									"<input type='text' name='price' value='" + price + "'>" +
									"<input type='text' name='type' value='" + type + "'>" +
									"<input type='text' name='sign' value='" + str + "'>" +
									"</form> " +
									"</body></html>";
						}
					}

				}else{
					msg1="<html><head></head><body>暂时没开通购买！</body></html>";
				}
			}else{
				msg1="<html><head></head><body>参数有误！！！</body></html>";
			}
		} catch (Exception e) {
			// TODO: handle exception
			/*msg1.element("code", "0")
			.element("msg", "网络错误，请稍后购买");*/
			e.printStackTrace();
			msg1="<html><head></head><body>请联系平台，静心等待！</body></html>";
		}
		
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.getWriter().write(msg1);
		//Dto.printMsg(response,msg.toString());
	}
	/**************************************************************Shwwmj  回调处理****************************************************************************/
	public static void main(String[] args) {
		String str=MD5.MD5Encode("pay_order_sn=TL1002278920171120200941943822&paytime=2017-11-20 20:10:10&paytype=alipay&totalmoney=2.0&trade_sn=201711202009947291050&key=LsGc095QPc3g35mR9jhxNwpuhop8OwIk");
		System.err.println(str);
	}
	
	/**
	 * Shwwmj
	 * 付款异步回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws FileUploadException 
	 */
	@RequestMapping("zaShwwmjBack")
	public void zaShwwmjBack(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, IOException, FileUploadException {
	
	        // 转型为MultipartHttpRequest  
	        //MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;  
	        
	       
	        String sign = request.getParameter("sign");
	        String time = request.getParameter("paytime");
	        String pay_order_sn = request.getParameter("pay_order_sn");
	        String orderNo = request.getParameter("trade_sn");
	        String money = request.getParameter("totalmoney");
	        String paytype = request.getParameter("paytype");
	        String channel = request.getParameter("channel");
	       //String mch = multipartRequest.getParameter("mch");
	       // String order_id = multipartRequest.getParameter("order_id");
	        //"mchid="+ver.getString("mch")+"&money="+za.getInt("payMoney")+"&notifyurl="+ver.getString("veUrl")+"/zagame/zaShwwmjBack.html&payChannel="+paytype+"&redirect="+redirect+"&returnurl="+ver.getString("veUrl")+"/zagame/zaShwwmj.html&trade_sn="+uporderCode+"&key="+ver.getString("key")
	 		
	        JSONObject za=fbiz.getExchangeRec(orderNo);
	        if (Dto.isObjNull(za)) {
				return;
			}
	        //取得平台信息
			JSONObject ver;
			if (za.containsKey("platform")&&!Dto.stringIsNULL(za.getString("platform"))) {
				ver=gsbiz.getVersion(za.getString("platform"));
			}else {
				ver = gsbiz.getVersion1(1);
			}
			String str=MD5.MD5Encode("channel="+channel+"&pay_order_sn="+pay_order_sn+"&paytime="+time+"&paytype="+paytype+"&totalmoney="+money+"&trade_sn="+orderNo+"&key="+ver.getString("key"));
			System.out.println("jm:"+str+"md5:"+sign);
			if(str.equals(sign))//验证签名
			{ 
			Dto.printMsg(response, "SUCCESS");
			//取得商品信息	
			JSONObject mall=gsbiz.getzamall(za.getString("objId"));
			//if(status.equals("1")){//成功}
				//查询订单
				//更新用户积分
				Map<String,String> map=new HashMap<String,String>();
				
				map.put("userId", za.getString("userId"));
				map.put("ordercode", orderNo);
				if (za.getInt("status")==0) {
					if(mall.getInt("type")==Dto.ZAMALL_TYPE_YUANBAO) {
						//用户添加元宝
						fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "yuanbao");
						//map.put("type", "用户购买元宝："+za.getInt("sum")+"个");
					}else if(mall.getInt("type")==Dto.ZAMALL_TYPE_CARD) {
						//用户添加房卡
						fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "roomcard");
						//map.put("type", "用户购买房卡："+za.getInt("sum")+"张");
					}else if (mall.getInt("type")==Dto.ZAMALL_TYPE_CONIS){
						//用户添加金币
						fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "coins");
						//map.put("type", "用户购买金币："+za.getInt("sum")+"个");
					}/*else if (mall.getInt("type")==Dto.ZAMALLEXCHANGEREC_TYPE_VIP){//用户购买VIP
						JSONObject mall= fbiz.getZaMallById(za.getLong("objId"));
						if (mall.getInt("type")==Dto.ZAMALLEXCHANGEREC_TYPE_VIP) {
							if (!Dto.stringIsNULL(mall.getString("coins"))&&mall.getInt("coins")!=0) {
								//用户添加金币
								fbiz.updZaUser(za.getString("userId"), mall.getInt("coins"), "coins");
							}
							if (!Dto.stringIsNULL(mall.getString("roomcard"))&&mall.getInt("roomcard")!=0) {
								//用户添加房卡
								fbiz.updZaUser(za.getString("userId"), mall.getInt("roomcard"), "roomcard");
							}
						}
						//更改玩家VIP状态
						fbiz.updateUserVip(za.getLong("userId"), za.getInt("objId"));
						map.put("type", "购买VIP："+za.getInt("sum")+"天");
					}*/
					//支付成功更改订单状态
					fbiz.updExchangeRec(za.getLong("id"));
					System.out.println("购买成功："+orderNo);
					
					//更新订单金额
					fbiz.updExchangePayc( Double.valueOf(money),za.getLong("id"));
					
					String backMsg=HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null, "utf-8");
					System.out.println("更新用户积分返回参数："+backMsg);
				}
			/*}else if(status.equals("0")){
				//失败
				System.out.println("购买失败1："+order_id);
			}*/
		}else{
			System.out.println("购买失败2："+orderNo);
			Dto.printMsg(response,"ERROR");
		}
	}
	
	/**
	 * 前端页面回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("zaShwwmj")
	public void zaShwwmjHtml(HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		String msg1="<html><head><script type='text/javascript'>window.close();</script></head><body></body></html>";
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.getWriter().write(msg1);
	}
	
	
	/**************************************************************Shwwmj  回调处理完毕****************************************************************************/
	
	
	/**************************************************************Tellni  回调处理****************************************************************************/
	/**
	 * Tellni
	 * 付款异步回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws FileUploadException 
	 */
	@RequestMapping("zaTellniBack")
	public void zaTellniBack(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, IOException, FileUploadException {
	
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
	        
	        System.out.println("进入zaTellniBack回调："+sign);
	        JSONObject receiveData=new JSONObject();
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
		
		//JSONObject receiveData=JSONObject.fromObject(result);
	        String platform=extra.split("\\_")[2];
	 		//取得平台信息
			JSONObject ver=gsbiz.getVersion(platform);
			String str=MD5.MD5Encode(order_id+orderNo+money+ver.getString("mch")+pay_type+time+MD5.MD5Encode(ver.getString("key")));
			System.out.println("jm:"+str+"md5:"+sign);
		if(str.equals(sign))//验证签名
		{ 
			Dto.printMsg(response, "SUCCESS");
			
			if(status.equals("1")){//成功
				String mallid=extra.split("\\_")[0];
				String userid=extra.split("\\_")[1];
				//查询订单
				JSONObject za=fbiz.getExchangeRec(order_id);
				//取得商品信息	
				JSONObject mall=gsbiz.getzamall(za.getString("objId"));
				//更新用户积分
				Map<String,String> map=new HashMap<String,String>();
				map.put("userId", za.getString("userId"));
				map.put("ordercode", order_id);
				
				if (za.getInt("status")==0) {
					if(mall.getInt("type")==Dto.ZAMALL_TYPE_YUANBAO) {
						//用户添加元宝
						fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "yuanbao");
						//map.put("type", "用户购买元宝："+za.getInt("sum")+"个");
					}else if(mall.getInt("type")==Dto.ZAMALL_TYPE_CARD) {
						//用户添加房卡
						fbiz.updZaUser(userid, za.getInt("sum"), "roomcard");
						//map.put("type", "用户购买房卡："+za.getInt("sum")+"张");
					}else if (mall.getInt("type")==Dto.ZAMALL_TYPE_CONIS){
						//用户添加金币
						fbiz.updZaUser(userid, za.getInt("sum"), "coins");
						//map.put("type", "用户购买金币："+za.getInt("sum")+"个");
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
					System.out.println("购买成功："+order_id);
					
					String backMsg=HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null, "utf-8");
					System.out.println("更新用户积分返回参数："+backMsg);
				}
			}else if(status.equals("0")){
				//失败
				System.out.println("购买失败1："+order_id);
			}
		}else{
			System.out.println("购买失败2："+order_id);
			Dto.printMsg(response,"ERROR");
		}
	}
	/**
	 * 签名验证
	 * @param obj
	 * @return
	 */
	private Boolean yzsign(JSONObject obj) {
		String str=MD5.MD5Encode(obj.getString("order_id")+obj.getString("orderNo")+obj.getString("money")+Dto.mch+obj.getString("pay_type")+obj.getString("time")+MD5.MD5Encode(Dto.key));
		
		boolean is=false;
		if (str.equals(obj.getString("sign"))) {
			is=true;
		}
		return is;
	}
	/**
	 * 前端页面回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("zaTellni")
	public void gmzamallBackHtml(HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		String msg1="<html><head><script type='text/javascript'>window.close();</script></head><body></body></html>";
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.getWriter().write(msg1);
	}
	
	
	/**************************************************************Tellni  回调处理完毕****************************************************************************/
	
	/**************************************************************openepay  回调处理****************************************************************************/
	
	/**
	 *  openepay
	 * 付款异步回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("zaOpenepay")
	public void zaOpenepayback(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String merchantId = request.getParameter("merchantId");
        String version = request.getParameter("version");
        String language = request.getParameter("language");
        String signType = request.getParameter("signType");
        String payType = request.getParameter("payType");
        String issuerId = request.getParameter("issuerId");
        String mchtOrderId = request.getParameter("mchtOrderId");
        String orderDatetime = request.getParameter("orderDatetime");
        String orderNo = request.getParameter("orderNo");
        String orderAmount = request.getParameter("orderAmount");
        String payDatetime = request.getParameter("payDatetime");
        String ext1 = request.getParameter("ext1");//商品ID
        String ext2 = request.getParameter("ext2");//用户id
        String signMsg = request.getParameter("signMsg");
        String payResult = request.getParameter("payResult");
        
		String str=MD5.MD5Encode("merchantId="+merchantId+"&version="+version+"&language="+language+"&signType="+signType+"&payType="+payType+"&mchtOrderId="+mchtOrderId+"&orderNo="+orderNo+"&orderDatetime="+orderDatetime+"&orderAmount="+orderAmount+"&payDatetime="+payDatetime+"&ext1="+ext1+"&ext2="+ext2+"&payResult="+payResult+"&key=d1o2n3g4d1o2n3g4").toUpperCase();
		/*System.out.println("merchantId="+merchantId+"&version="+version+"&language="+language+"&signType="+signType+"&payType="+payType+"&issuerId="+issuerId+"&mchtOrderId="+mchtOrderId+"&orderNo="+orderNo+"&orderDatetime="+orderDatetime+"&orderAmount="+orderAmount+"&payDatetime="+payDatetime+"&ext1="+ext1+"&ext2="+ext2+"&signMsg="+signMsg+"&payResult="+payResult);
		System.out.println("MD5:"+str);*/
        if(signMsg.equals(str))//验证签名
		{ 
        	Dto.printMsg(response, "success");
			if(payResult.equals("1")){//成功
				String mallid=ext1;
				String userid=ext2;
				//查询订单
				JSONObject za=fbiz.getExchangeRec(orderNo);
				//取得商品信息	
				JSONObject mall=gsbiz.getzamall(za.getString("objId"));
				//更新用户积分
				Map<String,String> map=new HashMap<String,String>();
				map.put("userId", za.getString("userId"));
				map.put("ordercode", orderNo);
				
				if (za.getInt("status")==0) {
					if(mall.getInt("type")==Dto.ZAMALL_TYPE_YUANBAO) {
						//用户添加元宝
						fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "yuanbao");
						//map.put("type", "用户购买元宝："+za.getInt("sum")+"个");
					}else if(mall.getInt("type")==Dto.ZAMALL_TYPE_CARD) {
						//用户添加房卡
						fbiz.updZaUser(userid, za.getInt("sum"), "roomcard");
						//map.put("type", "用户购买房卡："+za.getInt("sum")+"张");
					}else if (mall.getInt("type")==Dto.ZAMALL_TYPE_CONIS){
						//用户添加金币
						fbiz.updZaUser(userid, za.getInt("sum"), "coins");
						//map.put("type", "用户购买金币："+za.getInt("sum")+"个");
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
						fbiz.updateUserVip(Long.parseLong(userid), 1);
						map.put("type", "购买VIP："+za.getInt("sum")+"天");
					}*/
					//支付成功更改订单状态
					fbiz.updExchangeRec(za.getLong("id"));
					System.out.println("购买成功："+orderNo);
					
					String backMsg=HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null, "utf-8");
					System.out.println("更新用户积分返回参数："+backMsg);
				}
			}else {
				//失败
				System.out.println("购买失败："+orderNo);
			}
		}else{
			System.out.println("验签失败："+orderNo);
			
			Dto.printMsg(response,"error");
		}
		
	}
	
	
	/**************************************************************openepay  回调处理完毕****************************************************************************/
	
	
	/**************************************************************jtpay  回调处理****************************************************************************/
	/**
	 * 前端页面回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("zaJtpay")
	public void gmzamallBackHtml1(HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		String msg1="<html><head><script type='text/javascript'>window.close();</script></head><body></body></html>";
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html");
		response.getWriter().write(msg1);
	}
	/**
	 *  jtpay
	 * 付款异步回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("zaJtpayBack")
	public void zaJtpayback(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String p1_usercode= request.getParameter("p1_usercode");//商户id
		String p2_order=request.getParameter("p2_order");//订单
		String p3_money= request.getParameter("p3_money");//钱
		String p4_status= request.getParameter("p4_status");//状态
		String p5_jtpayorder= request.getParameter("p5_jtpayorder");//竣付通订单号
		String  p6_paymethod=request.getParameter("p6_paymethod");//商户支付方式
		
		String P8_charset=request.getParameter("p8_charset");//编码格式
		String P9_signtype=request.getParameter("p9_signtype");//签名验证方式
		String P10_sign=request.getParameter("p10_sign");//验证签名
		String P11_remark=request.getParameter("p11_remark");//原样返回
		
		String platform=P11_remark.split("\\_")[2];
		//取得平台信息
		JSONObject ver=gsbiz.getVersion(platform);
		
		String sign=MD5.MD5Encode(p1_usercode+"&"+p2_order+"&"+p3_money+"&"+p4_status+"&"+p5_jtpayorder+"&"+p6_paymethod+"&&"+P8_charset+"&"+P9_signtype+"&"+ver.getString("key")).toUpperCase();
		
		if (sign.equals(P10_sign)) {
			Dto.printMsg(response, "success");
			
			if(p4_status.equals("1")){//成功
				String mallid=P11_remark.split("\\_")[0];
				String userid=P11_remark.split("\\_")[1];
				//String platform=P11_remark.split("\\_")[2];
				//查询订单
				JSONObject za=fbiz.getExchangeRec(p2_order);
				//取得商品信息	
				JSONObject mall=gsbiz.getzamall(za.getString("objId"));
				//更新用户积分
				Map<String,String> map=new HashMap<String,String>();
				map.put("userId", za.getString("userId"));
				map.put("ordercode", p2_order);
				
				if (za.getInt("status")==0) {
					if(mall.getInt("type")==Dto.ZAMALL_TYPE_YUANBAO) {
						//用户添加元宝
						fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "yuanbao");
						//map.put("type", "用户购买元宝："+za.getInt("sum")+"个");
					}else if(mall.getInt("type")==Dto.ZAMALL_TYPE_CARD) {
						//用户添加房卡
						fbiz.updZaUser(userid, za.getInt("sum"), "roomcard");
						//map.put("type", "用户购买房卡："+za.getInt("sum")+"张");
					}else if (mall.getInt("type")==Dto.ZAMALL_TYPE_CONIS){
						//用户添加金币
						fbiz.updZaUser(userid, za.getInt("sum"), "coins");
						//map.put("type", "用户购买金币："+za.getInt("sum")+"个");
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
					System.out.println("购买成功："+p2_order);
					
					String backMsg=HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null, "utf-8");
					System.out.println("更新用户积分返回参数："+backMsg);
				}
			}else {
				//失败
				System.out.println("购买失败："+p2_order);
			}
		}
		
	}
	
	/**************************************************************jtpay  回调处理完毕****************************************************************************/
	
	
	/**************************************************************惠福天下 支付接口 ******************************************************************************/
	/**
	 * 发起支付
	 * @throws IOException 
	 **/
	public void askHuifuPay(HttpServletResponse response,JSONObject param) throws IOException{
		KFTSMPayMode model=new KFTSMPayMode();
		model.setAccount(param.getString("mch"));
		model.setNotifyUrl(param.getString("veUrl"));
		model.setPayType(param.getString("payType"));
		model.setPrice(param.getInt("money"));
		model.setTradeNo(param.getString("uporderCode"));
		model.setZfbAccount(param.getString("account"));
		
		Map<String,String> map=new HashMap<String, String>();
		map.put("messageid", "hf0000001");
		map.put("msgJson", JSONSerializer.toJSON(model).toString());
		String back=HttpReqUtil.doPost("http://hf.ebao35.com/api/CenterPay", map, null, "utf-8");
		System.out.println(back.replace("\\\\", "$").replace("\\", "").replace("$","\\"));
		String result_str=(back.replace("\\\\", "$").replace("\\", "").replace("$","\\"));
		
		if(!Dto.stringIsNULL(back)){
			JSONObject result=JSONObject.fromObject(result_str.substring(1, result_str.length() - 1));
			response.sendRedirect(result.getString("url"));
		}
	}
	
	
	/**
	 * 惠福天下回调地址
	 * @throws UnsupportedEncodingException 
	 * @throws IOException 
	 **/
	@RequestMapping("huifupayback")
	public void huifuPayBack(HttpServletRequest request,HttpServletResponse response) throws IOException{
		System.out.println("==============进入惠福天下通道通知流程================");
		System.out.println("接收参数如下");
		Enumeration<String> enu=request.getParameterNames();
		String recDataStr=Dto.receivePostMsg(request.getInputStream());
		while (enu.hasMoreElements()) {
			String string = enu.nextElement();
			System.out.println("=====键名："+string+"  键值："+request.getParameter(string)+"=====");
		}
		System.out.println("接收字符串流为："+recDataStr);
		Dto.printMsg(response, "接收异步消息成功！！！");
		
		if(request.getParameter("state").equals("success")){
			Dto.printMsg(response, "success");
			String orderNo=request.getParameter("tradeno");
			
			//查询订单
			JSONObject za=fbiz.getExchangeRec(orderNo);
			//取得商品信息	
			JSONObject mall=gsbiz.getzamall(za.getString("objId"));
			//更新用户积分
			Map<String,String> map=new HashMap<String,String>();
			map.put("userId", za.getString("userId"));
			map.put("ordercode", orderNo);
			
			if (za.getInt("status")==0) {
				if(mall.getInt("type")==Dto.ZAMALL_TYPE_YUANBAO) {
					//用户添加元宝
					fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "yuanbao");
					//map.put("type", "用户购买元宝："+za.getInt("sum")+"个");
				}else if(mall.getInt("type")==Dto.ZAMALL_TYPE_CARD) {
					//用户添加房卡
					fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "roomcard");
					//map.put("type", "用户购买房卡："+za.getInt("sum")+"张");
				}else if (mall.getInt("type")==Dto.ZAMALL_TYPE_CONIS){
					//用户添加金币
					fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "coins");
					//map.put("type", "用户购买金币："+za.getInt("sum")+"个");
				}/*else if (za.getInt("type")==Dto.ZAMALLEXCHANGEREC_TYPE_VIP){//用户购买VIP
					JSONObject mall= fbiz.getZaMallById(za.getLong("objId"));
					if (mall.getInt("type")==Dto.ZAMALLEXCHANGEREC_TYPE_VIP) {
						if (!Dto.stringIsNULL(mall.getString("coins"))&&mall.getInt("coins")!=0) {
							//用户添加金币
							fbiz.updZaUser(za.getString("userId"), mall.getInt("coins"), "coins");
						}
						if (!Dto.stringIsNULL(mall.getString("roomcard"))&&mall.getInt("roomcard")!=0) {
							//用户添加房卡
							fbiz.updZaUser(za.getString("userId"), mall.getInt("roomcard"), "roomcard");
						}
					}
					//更改玩家VIP状态
					fbiz.updateUserVip(Long.parseLong(za.getString("userId")), 1);
					map.put("type", "购买VIP："+za.getInt("sum")+"天");
				}*/
				//支付成功更改订单状态
				fbiz.updExchangeRec(za.getLong("id"));
				System.out.println("购买成功："+orderNo);
				
				String backMsg=HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null, "utf-8");
				System.out.println("更新用户积分返回参数："+backMsg);
			}
		}
	}
	/**************************************************************  回调处理完毕****************************************************************************/
	
	/**************************************************************APP微信原生支付接口 ******************************************************************************/
	/**
	 * 苹果上架支付
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("apleMall")
	public void wexinpayBack(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		String openid=request.getParameter("openid");//用户openid
		String sum=request.getParameter("sum");//商品数量
		String type=request.getParameter("type");//0 房卡 1金币  2元宝
		String platform=request.getParameter("platform");//平台标识
		
	
		if ("111111".equals(openid)||"222222".equals(openid)) {
				System.out.println("进入添加");
				JSONObject obj=new JSONObject();
				obj.element("sum", sum);
				obj.element("openid", openid);
				obj.element("platform", platform);
					if("2".equals(type)) {
						//用户添加元宝
						obj.element("zd", "yuanbao");
					}else if("0".equals(type)) {
						//用户添加房卡
						obj.element("zd", "roomcard");
					}else if ("1".equals(type)){
						obj.element("zd", "coins");
					}
					userService.apleMall(obj);
		}
	}
	
	/**************************************************************APP微信原生支付接口****************************************************************************/
	/**************************************************************乾富吧 支付接口 ******************************************************************************/
	/**
	 * 前端页面回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("zaQianf8")
	public void qianf8BackHtml(HttpServletRequest request,HttpServletResponse response) throws IOException {
		System.out.println("进入乾富吧通知页面");
		String msg1="success";
		//String msg1="<html><head></head><body>success</body></html>";
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		Dto.printMsg(response, msg1);
	}
	/**
	 *  Qianf8
	 * 付款异步回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("zaQianf8Back")
	public void zaQianf8back(HttpServletRequest request,HttpServletResponse response) throws IOException{
		System.out.println("进入乾富吧回调");
		//String p1_usercode= request.getParameter("p1_usercode");//商户id
		String order_sn=request.getParameter("order_sn");//商户订单号(不超过50位)
		String platform_sn= request.getParameter("platform_sn");//系统平台返回的订单
		String totle_amount= request.getParameter("totle_amount");//钱支付金额 单位为分
		String mch_number=request.getParameter("mch_number");//商户支付方式
		String  pay_type=request.getParameter("pay_type");//商户支付方式
		String extra_note=request.getParameter("extra_note");//原样返回
		String transaction_id=request.getParameter("transaction_id");//如微信的：400；支付宝的：100
		String status= request.getParameter("status");//状态1：成功；0：失败
		String sign_info=request.getParameter("sign_info");//签名验证方式
		String this_date=request.getParameter("this_date");//精确到秒，例如1480839643
		
		String platform=extra_note.split("\\_")[2];
		//取得平台信息
		JSONObject ver=gsbiz.getVersion(platform);
		
		//md5( 订单号码+系统订单+支付金额+商务号+支付类型+时间戳+md5(商户密钥) )
		String sign=MD5.MD5Encode(order_sn+platform_sn+totle_amount+mch_number+pay_type+this_date+MD5.MD5Encode(ver.getString("key")) ).toLowerCase();
		
		if (sign.equals(sign_info)) {
			Dto.printMsg(response, "success");
			
			if(status.equals("1")){//成功
				String mallid=extra_note.split("\\_")[0];
				String userid=extra_note.split("\\_")[1];
				
				//查询订单
				JSONObject za=fbiz.getExchangeRec(order_sn);
				//取得商品信息	
				JSONObject mall=gsbiz.getzamall(za.getString("objId"));
				//更新用户积分
				Map<String,String> map=new HashMap<String,String>();
				map.put("userId", za.getString("userId"));
				map.put("ordercode", order_sn);
				
				if (za.getInt("status")==0) {
					if(mall.getInt("type")==Dto.ZAMALL_TYPE_YUANBAO) {
						//用户添加元宝
						fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "yuanbao");
						//map.put("type", "用户购买元宝："+za.getInt("sum")+"个");
					}else if(mall.getInt("type")==Dto.ZAMALL_TYPE_CARD) {
						//用户添加房卡
						fbiz.updZaUser(userid, za.getInt("sum"), "roomcard");
						//map.put("type", "用户购买房卡："+za.getInt("sum")+"张");
					}else if (mall.getInt("type")==Dto.ZAMALL_TYPE_CONIS){
						//用户添加金币
						fbiz.updZaUser(userid, za.getInt("sum"), "coins");
						//map.put("type", "用户购买金币："+za.getInt("sum")+"个");
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
					System.out.println("购买成功："+order_sn);
					
					String backMsg=HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null, "utf-8");
					System.out.println("更新用户积分返回参数："+backMsg);
				}
			}else {
				//失败
				System.out.println("购买失败："+order_sn);
			}
		}
		
	}
	/**************************************************************  回调处理完毕****************************************************************************/

	/**************************************************************萌圈 支付接口 ******************************************************************************/
	/**
	 * 前端页面回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("posqian")
	public void posqianBackHtml(HttpServletRequest request,HttpServletResponse response) throws IOException {
		System.out.println("进入萌圈通知页面");
		String msg1="success";
		//String msg1="<html><head></head><body>success</body></html>";
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		Dto.printMsg(response, msg1);
	}
	/**
	 *  postQian
	 * 付款异步回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("posqianBack")
	public void posqianBack(HttpServletRequest request,HttpServletResponse response) throws IOException{
		System.out.println("进入萌圈回调");
		String order_sn = request.getParameter("order_sn");//商户订单号(不超过50位)
		String platform_sn = request.getParameter("platform_sn");//系统平台返回的订单
		String totle_amount = request.getParameter("totle_amount");//钱支付金额 单位为分
		String mch_number = request.getParameter("mch_number");//商户支付方式
		String  pay_type = request.getParameter("pay_type");//商户支付方式
		String extra_note = request.getParameter("extra_note");//原样返回
		String transaction_id = request.getParameter("transaction_id");//如微信的：400；支付宝的：100
		String status = request.getParameter("status");//状态1：成功；0：失败
		String sign_info = request.getParameter("sign_info");//签名验证方式
		String this_date = request.getParameter("this_date");//精确到秒，例如1480839643
		
		String platform=extra_note.split("\\_")[2];
		//取得平台信息
		JSONObject ver=gsbiz.getVersion(platform);
		//md5( 订单号码+系统订单+支付金额+商务号+支付类型+时间戳+md5(商户密钥) )
		String sign=MD5.MD5Encode(order_sn+platform_sn+totle_amount+mch_number+pay_type+this_date+MD5.MD5Encode(ver.getString("key")) ).toLowerCase();
		
		if (sign.equals(sign_info)) {
			Dto.printMsg(response, "success");
			
			if(status.equals("1")){//成功
				String userId = extra_note.split("\\_")[1];
				
				//查询订单
				JSONObject za = fbiz.getExchangeRec(order_sn);
				//取得商品信息	
				JSONObject mall=gsbiz.getzamall(za.getString("objId"));
				//更新用户积分
				Map<String,String> map=new HashMap<String,String>();
				map.put("userId", za.getString("userId"));
				map.put("ordercode", order_sn);
				
				if (za.getInt("status") == 0) {
					if(mall.getInt("type") == Dto.ZAMALL_TYPE_YUANBAO) {
						//用户添加元宝
						fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "yuanbao");
						//map.put("type", "用户购买元宝："+za.getInt("sum")+"个");
					}else if(mall.getInt("type") == Dto.ZAMALL_TYPE_CARD) {
						//用户添加房卡
						fbiz.updZaUser(userId, za.getInt("sum"), "roomcard");
						//map.put("type", "用户购买房卡："+za.getInt("sum")+"张");
					}else if (mall.getInt("type") == Dto.ZAMALL_TYPE_CONIS){
						//用户添加金币
						fbiz.updZaUser(userId, za.getInt("sum"), "coins");
						//map.put("type", "用户购买金币："+za.getInt("sum")+"个");
					}
					//支付成功更改订单状态
					fbiz.updExchangeRec(za.getLong("id"));
					System.out.println("购买成功："+order_sn);
					
					String backMsg=HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null, "utf-8");
					System.out.println("更新用户积分返回参数："+backMsg);
				}
			}else {
				//失败
				System.out.println("购买失败："+order_sn);
			}
		}
		
	}
	/**************************************************************  回调处理完毕****************************************************************************/
	
	/**************************************************************jtpay(一起玩)  回调处理****************************************************************************/
	/**
	 *  jtpay
	 * 付款异步回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("yqwJtpayBack")
	public void yqwJtpayBack(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		// 用户编号
		String p1_yingyongnum = request.getParameter("p1_yingyongnum");
		// 订单号
		String p2_ordernumber = request.getParameter("p2_ordernumber");
		// 金额
		String p3_money = request.getParameter("p3_money");
		// 支付结果
		String p4_zfstate = request.getParameter("p4_zfstate");
		// 竣付通订单号
		String p5_orderid = request.getParameter("p5_orderid");
		// 商户支付方式
		String  p6_productcode = request.getParameter("p6_productcode");
		// 编码格式
		String p8_charset = request.getParameter("p8_charset");
		// 签名验证方式
		String p9_signtype = request.getParameter("p9_signtype");
		// 验证签名
		String p10_sign = request.getParameter("p10_sign");
		// 原样返回
		String userid = request.getParameter("p11_pdesc");
		// 原样返回
		String platform = request.getParameter("p12_remark");
		// 实际支付金额
		String p13_zfmoney = request.getParameter("p13_zfmoney");
		
		//取得平台信息
		JSONObject ver=gsbiz.getVersion(platform);
		
		String sign=MD5.MD5Encode(p1_yingyongnum + "&" + p2_ordernumber + "&" + p3_money + "&" + p4_zfstate + "&"
				+ p5_orderid + "&" + p6_productcode + "&&" + p8_charset + "&" + p9_signtype + "&" + userid 
				+ "&" + p13_zfmoney + "&" + ver.getString("key")).toUpperCase();
		
		if (sign.equals(p10_sign)) {
			Dto.printMsg(response, "success");
			
			if(p4_zfstate.equals("1")){//成功
				//查询订单
				JSONObject za = fbiz.getExchangeRec(p2_ordernumber);
				if (!Dto.isNull(za)) {
					//取得商品信息	
					JSONObject mall = gsbiz.getzamall(za.getString("objId"));
					//更新用户积分
					Map<String,String> map = new HashMap<String,String>();
					map.put("userId", za.getString("userId"));
					map.put("ordercode", p2_ordernumber);
					
					if (za.getInt("status")==0) {
						if(mall.getInt("type") == Dto.ZAMALL_TYPE_YUANBAO) {
							//用户添加元宝
							fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "yuanbao");
						}else if(mall.getInt("type") == Dto.ZAMALL_TYPE_CARD) {
							//用户添加房卡
							fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "roomcard");
						}else if (mall.getInt("type") == Dto.ZAMALL_TYPE_CONIS){
							//用户添加金币
							fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "coins");
						}
						//支付成功更改订单状态
						fbiz.updExchangeRec(za.getLong("id"));
						System.out.println("购买成功："+p2_ordernumber);
						
						String backMsg=HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null, "utf-8");
						System.out.println("更新用户积分返回参数："+backMsg);
					}
				}
			}else {
				//失败
				System.out.println("购买失败："+p2_ordernumber);
			}
		}
		
	}
	/**************************************************************  回调处理完毕****************************************************************************/

	/**************************************************************码支付(山西天九)  回调处理****************************************************************************/
	/**
	 * 码支付(山西天九)付款异步回调
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("codepayBack")
	public void codepayBack(HttpServletRequest request,HttpServletResponse response) throws IOException{
		// 签名
		String sign = request.getParameter("sign");
		// 流水号
		String pay_no = request.getParameter("pay_no");
		// 自定义参数
		String orderCode = request.getParameter("param");
		if (!Dto.stringIsNULL(orderCode)) {
			if(!Dto.isNull(pay_no)){//成功
				//查询订单
				JSONObject za = fbiz.getExchangeRec(orderCode);
				if (!Dto.isNull(za)) {
					String platform = za.getString("platform");
					//取得平台信息
					JSONObject ver = gsbiz.getVersion(platform);
					//申明hashMap变量储存接收到的参数名用于排序
					Map<String,String> params = new HashMap<String,String>();
					//获取请求的全部参数
					Map requestParams = request.getParameterMap();
					//申明字符变量 保存接收到的变量
					String valueStr = "";
					for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
						String name = (String) iter.next();
						String[] values = (String[]) requestParams.get(name);
						valueStr = values[0];
						//乱码解决，这段代码在出现乱码时使用。如果sign不相等也可以使用这段代码转化
						params.put(name, valueStr);//增加到params保存
					}
					List<String> keys = new ArrayList<String>(params.keySet()); //转为数组
				  	Collections.sort(keys); //重新排序
					String prestr = "";
					//遍历拼接url 拼接成a=1&b=2 进行MD5签名
					for (int i = 0; i < keys.size(); i++) {
						String key_name = keys.get(i);
						String value = params.get(key_name);
						if(value== null || value.equals("") ||key_name.equals("sign")){ //跳过这些 不签名
							continue;
						}
						if (prestr.equals("")){
							prestr =  key_name + "=" + value;
						}else{
							prestr =  prestr +"&" + key_name + "=" + value;
						}
					}
					prestr = prestr + ver.getString("key");
					String str = MD5.MD5Encode(prestr);
					if (str.equals(sign)) {
						//取得商品信息	
						JSONObject mall = gsbiz.getzamall(za.getString("objId"));
						//更新用户积分
						Map<String,String> map = new HashMap<String,String>();
						map.put("userId", za.getString("userId"));
						map.put("ordercode", orderCode);

						if (za.getInt("status") == 0) {
							if(mall.getInt("type") == Dto.ZAMALL_TYPE_YUANBAO) {
								//用户添加元宝
								fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "yuanbao");
							}else if(mall.getInt("type") == Dto.ZAMALL_TYPE_CARD) {
								//用户添加房卡
								fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "roomcard");
							}else if (mall.getInt("type") == Dto.ZAMALL_TYPE_CONIS){
								//用户添加金币
								fbiz.updZaUser(za.getString("userId"), za.getInt("sum"), "coins");
							}
							//支付成功更改订单状态
							fbiz.updExchangeRec(za.getLong("id"));
							System.out.println("购买成功：" + orderCode);

							String backMsg=HttpReqUtil.doPost("http://127.0.0.1:80/gameproxy/appRecharge", map, null, "utf-8");
							System.out.println("更新用户积分返回参数：" + backMsg);

							Dto.printMsg(response, "success");
							return;
						}
					}
				}
			}
		}
		Dto.printMsg(response, "fail");
	}
	/**************************************************************  回调处理完毕****************************************************************************/


}
