package com.qy.game.filter;


import java.io.PrintWriter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.qy.game.utils.Configure;
import com.qy.game.utils.Doweixin;
import com.qy.game.utils.Dto;
import com.qy.game.utils.HttpTookit;
import net.sf.ehcache.constructs.web.filter.Filter;
import net.sf.json.JSONObject;


public class WeixinFilter extends Filter{
	@Override
	protected void doDestroy() {
		
	}

	@Override
	protected void doInit() throws Exception {

	}
	

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) throws Throwable {
		HttpServletRequest httpRequest=request;  	
		String url2= httpRequest.getServletPath();      //请求页面或其他地址  
		String query=request.getQueryString();
	    if(!Dto.stringIsNULL(query))
	        	   url2=url2+ "?" + (httpRequest.getQueryString()); //参数  
		String ua = request.getHeader("user-agent").toLowerCase();
		
		String wxOpenID = (String) request.getSession().getAttribute("openId");
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		if(ua.indexOf("micromessenger") > 0){ // 判断是否是微信端进来的请求
			
			
			//  排除过滤静态资源路径
			if(request.getServletPath().contains("/css")
					|| request.getServletPath().contains("/js")
					|| request.getServletPath().contains("/img")
					|| request.getServletPath().contains("/font")
					|| request.getServletPath().contains("/upload")){
				
				chain.doFilter(request, response);
				
			}else if(wxOpenID==null){  // 首次微信授权登录
				
				String code = request.getParameter("code");
				
				if(code!=null && !"".equals(code)){ // 微信回调请求
					JSONObject accessToken = Doweixin.getAccessToken(code);
					System.out.println("accessToken--:" +accessToken);
					request.getSession().setAttribute("openId", accessToken.getString("openid"));
					
					//获取用户信息
					JSONObject userInfo = Doweixin.getUserInfo(accessToken.getString("access_token"), accessToken.getString("openid"));
					
					request.getSession().setAttribute("userInfo", userInfo);
					
					
					if(Dto.isNull(user)){ // 微信端登录
					String url = request.getContextPath()+"/wxlogin.do?url="+url2;
						System.out.println("----------url-------:"+url);
						PrintWriter out = response.getWriter();
						
						out.println("<html><head><script type='text/javascript'>location.href = '" + url + "';</script></head><body></body></html>");
						
						out.flush();
						out.close();
					}else {
						
						chain.doFilter(request, response);
					}
				
				}else{ // 向微信发起第一次请求
					
					// 请求路径
					StringBuffer reqURL = request.getRequestURL();
					reqURL.append("?");
					reqURL.append(request.getQueryString());
					String url = Doweixin.getCodeURL(reqURL.toString());
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					
					out.println("<html><head><script type='text/javascript'>location.href = '" + url + "';</script></head><body></body></html>");
					
					out.flush();
					out.close();
					
				}
				
			}else{
				request.getSession().removeAttribute("openId");
				chain.doFilter(request, response);
				
			}
		}else{
			chain.doFilter(request, response);
		}
		
		
	}
	
	
	/**
	 * 获取AccessToken
	 * @return
	 */
	public String getAccessToken(){
		
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
		url = url.replace("APPID", Configure.getAppid())
				 .replace("APPSECRET", Configure.appKey);
		String result = HttpTookit.doGet(url, "UTF-8", false);
		JSONObject accessToken = JSONObject.fromObject(result);
		return accessToken.getString("access_token");
	}
	
	
	/**
	 * 获取JsapiTicket
	 * @param access_Token
	 * @return
	 */
	public String getJsapiTicket(String access_Token){
		
		String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
		url = url.replace("ACCESS_TOKEN", access_Token);
		String result = HttpTookit.doGet(url, "UTF-8", false);
		JSONObject jsapiTicket = JSONObject.fromObject(result);
		return jsapiTicket.getString("ticket");
	}

	
}
