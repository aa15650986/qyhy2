package com.qy.game.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;



import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author lhp
 *
 */
public class Dto {

    private static Logger log;
    public static String LOGIN_UID_ERROR_MSG = "用户不存在";
    public static String LOGIN_UUID_ERROR_MSG = "该帐号已在其他地方登录";
    public static String LOGIN_USER="usermsg";//登录用户sessionKey
    public static String APP_PLATFORM="YOUJ";
    public static String PLATFORM="";//平台号
    /**
     * 平台号集合
     * Map<id,platform>
     */
    public static Map<String,String> PLATFORM_MAP = new HashMap<String, String>();
    public static String USER="USER";//用户user

    public static String SECRET_KEY = "zhuoan";// 加密密钥

    //public static String H5userID="0";//邀请成员时的id

    public static int DOUNIU_GAME_ID = 1;
    public static int DOUDIZHU_GAME_ID = 2;
    public static int MAJIANG_GAME_ID = 3;


    // 签到奖励类型
    public static int ZA_SIGN_JINBI = 0; // 金币奖励
    public static int ZA_SIGN_ROOM = 1; // 房卡奖励

    // 是否领取签到奖励
    public static int ZA_IFSIGN_YES = 1; // 是
    public static int ZA_IFSIGN_NO = 0; // 否

    //用户资金流水-交易类型
    public static int  USERBALREC_TYPE_PROXYCUT=0;//分润
    public static int  USERBALREC_TYPE_RECHARGE=1;//用户充值

    public static String mch="473004694826";//T淘 791683957990    万金油商户号473004694826
    public static String key="8f15cbf996410afffd912618b091ed71";//T淘 a590e567c7f1ab6d11368cd4501973b9  万金油密钥8f15cbf996410afffd912618b091ed71

    public static int ZaUserdeduction_dotype_xipai=1;//用于游戏中洗牌
    public static int ZaUserdeduction_dotype_pump=2;//用于游戏中抽水
    public static int ZaUserdeduction_dotype_cashback=3;//用于提现返还，金皇冠
    public static int ZaUserdeduction_dotype_fuli=4;//福利获取
    public static int ZaUserdeduction_dotype_system=5;//系统操作

    public static int ZaUserdeduction_type_card=0;//房卡
    public static int ZaUserdeduction_type_conin=1;//金币
    public static int ZaUserdeduction_type_score=2;// 积分
    public static int ZaUserdeduction_type_yuanbao=3;//元宝

    //za_mall 游戏商城  type常量
    public static int  ZAMALL_TYPE_CARD=1;//房卡
    public static int  ZAMALL_TYPE_CONIS=2;//金币
    public static int  ZAMALL_TYPE_SCORE=3;//积分
    public static int  ZAMALL_TYPE_YUANBAO=4;//元宝
    public static int  ZAMALL_TYPE_FUDAI=5;//福袋
    public static int  ZAMALL_TYPE_VIP=6;//VIP

    //za_mall_exchange_rec 买兑换记录  type常量
		/*public static int  ZAMALLEXCHANGEREC_TYPE_EXCHANGE=1;//兑换
		public static int  ZAMALLEXCHANGEREC_TYPE_BUYCOINS=14;//购买金币
		public static int  ZAMALLEXCHANGEREC_TYPE_BUYCARD=13;//购买房卡
		public static int  ZAMALLEXCHANGEREC_TYPE_DISCOUNT=15;//特惠
		public static int  ZAMALLEXCHANGEREC_TYPE_VIP=16;//VIP
		public static int  ZAMALLEXCHANGEREC_TYPE_FUDAI=17;//福袋
		public static int  ZAMALLEXCHANGEREC_TYPE_YUANBAO=18;//元宝
*/
    public static int  ZAMALLEXCHANGEREC_TYPE_EXCHANGE=0;//兑换
    public static int  ZAMALLEXCHANGEREC_TYPE_APP=1;//游戏端

    //za_mall_exchange_rec 买兑换记录  status常量
    public static int  ZAMALLEXCHANGEREC_STATUS_0=0;//兑换-未审核；购买-下单（未支付）
    public static int  ZAMALLEXCHANGEREC_STATUS_1=1;//兑换-通过；购买-已支付
    public static int  ZAMALLEXCHANGEREC_STATUS_2=2;//兑换-不通过
    public static int  ZAMALLEXCHANGEREC_STATUS_3=3;//兑换-已到账


    //分享奖励类型
    public final static int SHAREREC_OBJ_CARD=1;//房卡
    public final static int SHAREREC_OBJ_COIN=2;//金币
    public final static int SHAREREC_OBJ_SCORE=3;//积分
    public final static int SHAREREC_OBJ_YUANBAO=4;//元宝


    //微信登录 start
    public static String WEIXIN_USER_OPENID="user_openid";//存储当前用户的微信openid
    public static String USER_WEIXIN_INFO="user_weixin_info";//用户微信信息
    public static String WEIXIN_ACCESS="access";//存放微信的ACCESS

    public static String WECHATACCESSTOKENTIME="weixACCE";
    public static String WECHATACCESSTOKEN="token";
    //微信登录 end
    /**
     * 生成订单编号
     * @return orderCode
     */
    public static String getorderCode(){
        String Time=TimeUtil.getNowDate("yyyyMMddHHmmSS");
        String str = "";
        str += (int)(Math.random()*9+1);
        for(int i = 0; i < 5; i++){
            str += (int)(Math.random()*10);
        }
        String uporderCode=Time+str;
        return uporderCode;
    }
    /**
     * 返回json消息到客户端
     * @param response
     * @param obj
     */
    public static void returnJosnMsg(HttpServletResponse response, JSONObject obj) {

        try {

            response.setHeader("Access-Control-Allow-Origin","*");
            response.setContentType("text/plain; charset=utf-8");
            response.getWriter().print(obj.toString());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 将图片保存到本地
     * @param url
     * @param filePath
     * @param cookies （可选）
     * @return
     */
    public static boolean saveImage(String url, String filePath, Map<String, String> cookies){

        File file = new File(filePath);

        try {
            StringBuffer cookieStr = new StringBuffer();
            URLConnection connection = new URL(url).openConnection();
            // 是否带cookie
            if(cookies!=null){
                for (String key : cookies.keySet()) {
                    cookieStr.append(key);
                    cookieStr.append("=");
                    cookieStr.append(cookies.get(key));
                    cookieStr.append("; ");
                }
                connection.setRequestProperty("Cookie", cookieStr.toString());
            }
            InputStream is = connection.getInputStream();
            BufferedImage image = ImageIO.read(is);
            return ImageIO.write(image, "JPG", file);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 接收远程传递的post消息
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String receivePostMsg(ServletInputStream inputStream) throws IOException{
        BufferedReader input=new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line=null;
        String result = "";
        try {
            while((line=input.readLine())!=null)
                result+=line+"\r\n";
        }catch(Exception e){
            e.printStackTrace();
        }finally{input.close();}

        return result;
    }

    /**
     * 判断String是否为空
     * @param msg
     * @return true or false (等于空返回true   不等于看返回false)
     */
    public static boolean stringIsNULL(String values){

        if(values==null || "".equals(values) || "null".equals(values) || "undefined".equals(values)){
            return true;
        }
        return false;

    }

    /**
     * ajax 返回
     * @param response
     * @param msg
     * @throws IOException
     */
    public static void printMsg(HttpServletResponse response, String msg) throws IOException{
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/plain");
        response.getWriter().write(msg);
    }

    /**
     * 返回指定数据格式的页面
     * @param response
     * @param msg
     * @param type
     * @throws IOException
     */
    public static void printMsgWithType(HttpServletResponse response, String msg,String type) throws IOException{
        response.setCharacterEncoding("utf-8");
        response.setContentType(type);
        response.getWriter().write(msg);
    }

    /**
     * 根据长度随机生成
     * 26英文字和0~9随机生成
     * @param length
     * @return entNum
     */
    public static String getEntNumCode(int length){
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = 65;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * 1.判断是否为空
     */
    public static boolean isNull(Object obj){
        if(obj!=null&&!String.valueOf(obj).equals("null")&&!String.valueOf(obj).equals("")){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 1.判断JSONObject是否为空
     */
    public static boolean isObjNull(JSONObject obj){
        if(obj!=null&&!obj.isEmpty()&&!String.valueOf(obj).equals("null")&&!String.valueOf(obj).equals("")){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 1.判断是否为空
     */
    public static boolean isNull(JSONObject obj,String keyname){
        if(obj.containsKey(keyname)&&obj.get(keyname)!=null&&!obj.get(keyname).equals("")&&!obj.get(keyname).equals("null")){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 编码转化
     * @param String
     * @return String
     * @throws UnsupportedEncodingException
     */
    public static String string_UTF_8(String values) throws UnsupportedEncodingException{
        if (!Dto.stringIsNULL(values)){
            values = new String(values.getBytes("iso8859-1"),"utf-8");
        }
        return values;

    }

    /**
     * JSONObject空key转为“”
     * @param String
     * @return String
     * @throws UnsupportedEncodingException
     */
    public static JSONObject string_JSONObject(JSONObject obj){

        if(!isNull(obj)){
            Iterator keys = obj.keys();
            while(keys.hasNext()){
                String key = keys.next().toString();
                String value = obj.optString(key);
                if(Dto.stringIsNULL(value)){
                    obj.element(key, "");
                }

            }
        }
        return obj;

    }

    /**
     * JSONArray空key转为“”
     * @param String
     * @return String
     * @throws UnsupportedEncodingException
     */
    public static JSONArray string_JSONArray(JSONArray array){

        for(int i=0;i<array.size();i++){
            JSONObject obj = array.getJSONObject(i);
            if(!isNull(obj)){
                Iterator keys = obj.keys();
                while(keys.hasNext()){
                    String key = keys.next().toString();
                    String value = obj.optString(key);
                    if(Dto.stringIsNULL(value)){
                        obj.element(key, "");
                    }

                }
            }
        }
        return array;

    }

    /**
     * JSONArray空key转为“”
     * @param String
     * @return String
     * @throws UnsupportedEncodingException
     */
    public static JSONArray string_JSONArray2(JSONArray array,Object objs){

        for(int i=0;i<array.size();i++){
            JSONObject obj = array.getJSONObject(i);
            if(!isNull(obj)){
                Iterator keys = obj.keys();
                while(keys.hasNext()){
                    String key = keys.next().toString();
                    String value = obj.optString(key);
                    if(Dto.stringIsNULL(value)){
                        obj.element(key, objs);
                    }

                }
            }
        }
        return array;

    }

    //加
    public static double add(double a1, double b1) {
        BigDecimal a2 = new BigDecimal(Double.toString(a1));
        BigDecimal b2 = new BigDecimal(Double.toString(b1));
        return a2.add(b2).doubleValue();
    }

    //减
    public static double sub(double a1, double b1) {
        BigDecimal a2 = new BigDecimal(Double.toString(a1));
        BigDecimal b2 = new BigDecimal(Double.toString(b1));
        return a2.subtract(b2).doubleValue();
    }

    //乘
    public static double mul(double a1, double b1) {
        BigDecimal a2 = new BigDecimal(Double.toString(a1));
        BigDecimal b2 = new BigDecimal(Double.toString(b1));
        return a2.multiply(b2).doubleValue();
    }

    //除
    public static double div(double a1, double b1, int scale) {

        if (scale < 0) {
            throw new IllegalArgumentException("error");
        }
        BigDecimal a2 = new BigDecimal(Double.toString(a1));
        BigDecimal b2 = new BigDecimal(Double.toString(b1));
        return a2.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    //除
    public static int getRandom(int min,int max) {
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return s;
    }

    public static void writeLog(String msg){
        log=Logger.getLogger(Dto.class);
        log.info(msg);
    }

    public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";

    public static final int HIT_THRESHOLD = 10;
}
