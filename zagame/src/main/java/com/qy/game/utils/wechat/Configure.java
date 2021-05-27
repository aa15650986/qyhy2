package com.qy.game.utils.wechat;


/**
 * 配置数据
 */
public class Configure {

	public static String getWechatCode() {
		return wechatCode;
	}

	public static void setWechatCode(String wechatCode) {
		Configure.wechatCode = wechatCode;
	}

	/**
	 * 微信号
	 */
	private static String wechatCode = "wechatCode";
	
	public static String BASE_URL="baseurl";
	
	public static String getBASE_URL() {
		return BASE_URL;
	}

	public static void setBASE_URL(String bASE_URL) {
		BASE_URL = bASE_URL;
	}

	// 每次Post数据给API的时候都要用这个key来对所有字段进行签名，生成的签名会放在Sign这个字段，API收到Post数据的时候也会用同样的签名算法对Post过来的数据进行签名和验证
	// 收到API的返回的时候也要用这个key来对返回的数据算下签名，跟API的Sign数据进行比较，如果值不一致，有可能数据被第三方给篡改
	private static String key = "Zhuoan10086ChessZhuoan10086Chess";
	//private static String key = "Zhuoan10086wjyqpZhuoan10086wjyqp";	//	万金游
	
	//微信分配的公众号ID（开通公众号之后可以获取到）
	//private static String appID = "wxfccc8468f8d054e1";//wxfccc8468f8d054e1
	private static String appID = "appid";
	
	//微信开放平台ID 用于appkaif
	//private static String openPlatID="wxe4ed63a0f15ae55f";
	private static String openPlatID="openplatid";
	//微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
	//private static String mchID = "1401593702";
	private static String mchID = "mchid";
	
	//微信授权 start
	//public static String appKey="6a01fd79d82dbb115df0b47fea0483d3";//7c986dc3622502d514393b4a35b2e221
	public static String appKey="appkey";
		
	public static final String SCOPE = "snsapi_userinfo";
		
	public static String PAY_BY_QRCODE="NATIVE";//支付场景：扫码支付
	public static String PAY_BY_JSAPI="JSAPI";//支付场景：jsapi支付
	public static String PAY_BY_APP="APP";//支付场景：APP支付
	
	//微信授权 end
		
	//腾讯地图授权 key
	public static String TENCENT_MAP_KEY="QTGBZ-KSVK4-SM2UF-XXUPO-J5E7F-42FCO";
	//腾讯地图授权 key end
	
	//API的路径：
	//1）统一下单API
	public static String UNIFIEDORDER_API_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	//通知路径
	public static String PAY_NOTIFY_URL = getBASE_URL()+"/wxpay_notify.json";
	//public static String PAY_NOTIFY_URL = "pay_notify_url";

	//查询路径
	public static String ORDERQUERY_API_URL="https://api.mch.weixin.qq.com/pay/orderquery";
	
	//红包部分
	public static String API_URL_OF_NORMAL="https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";//普通红包api
	
	public static String API_URL_OF_GROUP="https://api.mch.weixin.qq.com/mmpaymkttransfers/sendgroupredpack";//裂变红包
	
	//短信通知管理员账户
	public static String ANNOUNCE_ACCOUNT="18659576697";
	
	public static String SEND_SHOP_NAME="游戏代理平台";

	public static void setappKey(String key) {
		Configure.appKey = key;
	}

	public static String getappKey(){
		return appKey;
	}
	
	public static void setKey(String key) {
		Configure.key = key;
	}

	public static String getKey(){
		return key;
	}
	
	public static void setAppID(String appID) {
		Configure.appID = appID;
	}

	public static String getAppid(){
		return appID;
	}
	
	public static void setMchID(String mchID) {
		Configure.mchID = mchID;
	}

	public static String getMchid(){
		return mchID;
	}

	public static String getOpenPlatID() {
		return openPlatID;
	}

	public static void setOpenPlatID(String openPlatID) {
		Configure.openPlatID = openPlatID;
	}

	public static String getPAY_NOTIFY_URL() {
		return PAY_NOTIFY_URL;
	}

	public static void setPAY_NOTIFY_URL(String pAY_NOTIFY_URL) {
		Configure.PAY_NOTIFY_URL = pAY_NOTIFY_URL;
	}
}
