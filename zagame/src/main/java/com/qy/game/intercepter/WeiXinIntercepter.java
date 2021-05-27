package com.qy.game.intercepter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.qy.game.model.User;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.qy.game.server.LoginOauth;
import com.qy.game.service.GlobalService;
import com.qy.game.service.UserService;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.service.impl.UserServiceImpl;
import com.qy.game.utils.Doweixin;
import com.qy.game.utils.Dto;


@Component("weixinIntercepter")
public class WeiXinIntercepter extends LoginOauth implements HandlerInterceptor {
	
	@Resource
	private UserService userService;

	@Resource
	private GlobalService globalService;
	
	@Resource
	private GoldShopBizServer gsbiz;
	
	private static String[] uuid = {"111111","222222","333333","444444","u1"};
	private int indexNum=0;

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}
	
	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		HttpSession session=request.getSession();
			
		//0.判断是否为微信浏览器
		try{
//			if(indexNum<uuid.length){
//				session.setAttribute(Dto.WEIXIN_USER_OPENID, uuid[indexNum]);
//				indexNum++;
//			}else{
//				session.setAttribute(Dto.WEIXIN_USER_OPENID, uuid[0]);
//				indexNum=0;
//			}
//			return true;
			System.out.println("进入微信拦截器");
//			String ua = request.getHeader("user-agent").toLowerCase();
			String u="Mozilla/5.0 (iPhone; CPU iPhone OS 6_1_3 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Mobile/10B329 MicroMessenger/5.0.1";
			String ua=u.toLowerCase();
			if(ua!=null && ua.indexOf("micromessenger") > 0){
				//1.当为微信浏览器时，检查session和request中的参数
				String openId=session.getAttribute(Dto.WEIXIN_USER_OPENID).toString();//用户openID
				String code=request.getParameter("code");//回调时微信以get方式回传code
				if(openId==null)
				{
					boolean isSuccess=weixinOauth(code, response, request);
					if(isSuccess){
						JSONObject userInfo=(JSONObject) request.getSession().getAttribute(Dto.USER_WEIXIN_INFO);
						JSONObject postData=new JSONObject().element("data", new JSONObject().element("user_info", userInfo)).element("status", "ok");
						super.wxLogin(request, postData.toString(), globalService,gsbiz,userService);
					}
					return isSuccess;
				}else{

					UserService userService = new UserServiceImpl();
					JSONObject operatorMark=userService.getOperatorMarkByOpenId(openId);
					if(operatorMark.isEmpty()){
						//获取完整url
						String uri=request.getRequestURL().toString();
						//通过完整url得到domain 
						URL url=new URL(uri);
						String domain=url.getHost();
						//通过domain得到运营商的markd
						String mark=userService.getMarkByDoMain(domain).toString();
						//更新数据
						userService.updateOperatorMark(mark, openId);
					}
					return true;
				}
			}else{
				if(indexNum<uuid.length){
					session.setAttribute(Dto.WEIXIN_USER_OPENID, uuid[indexNum]);
					indexNum++;
				}else{
					session.setAttribute(Dto.WEIXIN_USER_OPENID, uuid[0]);
					indexNum=0;
				}
				return true;
			}
		}catch(Exception e){
//				String name=request.getRequestURI();
//				Dto.writeLog("post apply occur no user-agent error,post add is"+name);
			e.printStackTrace();
			return true;
		}
	}
	
	/**
	 * 微信授权操作
	 * @param code
	 * @throws IOException 
	 */
	private boolean weixinOauth(String code,HttpServletResponse response,HttpServletRequest request) throws IOException
	{
		System.out.println("weinxinCode:"+code+",visit url is:"+request.getRequestURL().toString());
		HttpSession session=request.getSession();
		if(code==null)
		{
			String url=Doweixin.getCodeURL(request.getRequestURL().toString());
			if(url.indexOf(".html")==-1)
				url+="home.html";
			System.out.println("jump oauth url is:"+url);
//			String url=Doweixin.getCodeURL("http://www.zsbaomu.com:8080/nanny/home.html");
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("<html><head>" +
					"</script><script type='text/javascript'>" +
					"(function(){window.location.href ='"+ url +"'}());" +
					"</script></head><body></body></html>");
			out.flush();
			out.close();
			return false;
		}else{
			System.out.println("....into method to get the token and openID....\n");
			JSONObject weixinData=Doweixin.getAccessToken(code);//获得access_token、openID
			System.out.println("weixinData:"+weixinData+"\n");
			JSONObject userInfo=Doweixin.getUserInfo(weixinData.getString("access_token"), weixinData.getString("openid"));//获得微信下的用户信息
			session.setAttribute(Dto.WEIXIN_USER_OPENID, weixinData.getString("openid"));
			session.setAttribute(Dto.USER_WEIXIN_INFO, userInfo);
			System.out.println("....end the method.......\n");
			System.out.println("userInfo:"+userInfo+"\n");
			return true;
		}
	}

	
}

	
