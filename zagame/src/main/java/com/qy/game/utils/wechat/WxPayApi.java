package com.qy.game.utils.wechat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONObject;

import org.xml.sax.SAXException;

import com.qy.game.utils.HttpTookit;



public class WxPayApi {

	
	/**
	 * 调用统一下单接口，获取微信支付的参数
	 * @param order
	 * @return
	 */
	public static String wxpay(JSONObject order) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("out_trade_no", order.getString("orderCode"));
		map.put("body", order.getString("titile"));
		map.put("total_fee",  order.getDouble("money")+"");
		map.put("spbill_create_ip", order.getString("ip"));
		map.put("trade_type", order.getString("tradeType"));
		map.put("openid", order.getString("openid"));
		
		//统一下单
		String prepay_id = unifiedOrder(map);
		
		//获取支付参数（JSON格式）
		String jsApiParameters = getJsApiParameters(prepay_id);
		
		return jsApiParameters;
	}
	
	
	/**
	 * 统一下单
	 * @param map
	 * @return
	 */
	public static String unifiedOrder(Map<String, String> map) {
		
		map.put("appid", Configure.getAppid());
		map.put("mch_id", Configure.getMchid());
		map.put("notify_url", Configure.PAY_NOTIFY_URL);
		map.put("nonce_str", RandomStringGenerator.getRandomStringByLength(16));
		map.put("total_fee", getMoney(map.get("total_fee")));
		map.put("sign", Signature.getSign(map));
		
		String xmlStr = XMLParser.getXMLFromMap(map);
		
		System.out.println(xmlStr);
		
		String resultXML = HttpTookit.doPost(Configure.UNIFIEDORDER_API_URL, xmlStr);
		
		System.out.println("返回结果："+resultXML);
		
		String prepayId = "";
		
		try {
			if(resultXML.indexOf("FAIL") == -1){
				Map<String,Object> mapXML = XMLParser.getMapFromXML(resultXML);
				prepayId = (String) mapXML.get("prepay_id");
		    }
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		return prepayId;
	}
	
	
	/**
	 * 获取支付参数
	 * @param prepay_id
	 * @return
	 */
	public static String getJsApiParameters(String prepay_id) {
		
		String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
		String nonceStr = RandomStringGenerator.getRandomStringByLength(32);
		Map<String, String> map = new HashMap<String, String>();
		String packages = "prepay_id="+prepay_id;
		map.put("appId", Configure.getAppid());  
		map.put("timeStamp", timestamp);  
		map.put("nonceStr", nonceStr);  
		map.put("package", packages);  
		map.put("signType", "MD5");
		
		String sign = Signature.getSign(map);
		
		String jsonStr = "{\"appId\":\"" + Configure.getAppid() + "\",\"timeStamp\":\"" + timestamp
				+ "\",\"nonceStr\":\"" + nonceStr + "\",\"package\":\""
				+ packages + "\",\"signType\":\"MD5" + "\",\"paySign\":\""
				+ sign + "\"}";
		
		return jsonStr;
	}
	
	
	/**
	 * 元转换成分
	 * @param money
	 * @return
	 */
	public static String getMoney(String amount) {
		if(amount==null){
			return "";
		}
		// 金额转化为分为单位
		String currency =  amount.replaceAll("\\$|\\￥|\\,", "");  //处理包含, ￥ 或者$的金额  
        int index = currency.indexOf(".");  
        int length = currency.length();  
        Long amLong = 0l;  
        if(index == -1){  
            amLong = Long.valueOf(currency+"00");  
        }else if(length - index >= 3){  
            amLong = Long.valueOf((currency.substring(0, index+3)).replace(".", ""));  
        }else if(length - index == 2){  
            amLong = Long.valueOf((currency.substring(0, index+2)).replace(".", "")+0);  
        }else{  
            amLong = Long.valueOf((currency.substring(0, index+1)).replace(".", "")+"00");  
        }  
        return amLong.toString(); 
	}

}
