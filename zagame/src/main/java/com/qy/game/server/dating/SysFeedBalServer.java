package com.qy.game.server.dating;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qy.game.service.dating.FeedBalServer;
import com.qy.game.utils.Dto;


/**
 * 反馈信息
 * @author ASUS
 *
 */
@Controller
public class SysFeedBalServer {
	@Resource
	private LoginServer ls;
	
	@Resource
	private FeedBalServer fbs;
	
	/**
	 * 添加反馈信息
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("addsysfeedback")
	public void addsysfeedback(HttpServletRequest request,HttpServletResponse response) throws IOException{
		/*request.setCharacterEncoding("utf-8");
		((ServletResponse) request).setContentType("text/html;charset=utf-8");
		 PrintWriter pw =response.getWriter();
		 pw.print(text);
		*/
		String userid=request.getParameter("id");
		String text=request.getParameter("text");
		if(!Dto.stringIsNULL(text)){
			text = new String(text.getBytes("iso8859-1"),"utf-8");
		}
		String platform=request.getParameter("platform");
		JSONObject con=new JSONObject();
		con.element("userid", userid)
			.element("text", text)
			.element("platform", platform);
		JSONObject data=new JSONObject();
		try {
			fbs.addsysfeedback(con);
			data.element("msg", "提交成功")
				.element("code", "1");
		} catch (Exception e) {
			// TODO: handle exception
			data.element("msg", "网路错误,请稍后重试")
				.element("code", "0");
		}
		Dto.printMsg(response, data.toString());
			
	}
	/**
	 * 查询反馈信息
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("getfeedback.json")
	public void getfeedback(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		JSONArray list=fbs.getfeedback(id);
		Dto.printMsg(response, list.toString());
	}
	/**
	 * 赠送房卡
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("upbaseroomcard")
	public void upbaseroomcard(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		JSONObject data=JSONObject.fromObject(request.getParameter("data"));
		JSONObject msg=new JSONObject();
		try {
			String dd=fbs.upbaseroomcard(data);
			if(dd=="1"){
				msg.element("code", "1")
					.element("msg", "赠送成功");
			}else if (dd=="2") {
				msg.element("code", "0")
				.element("msg", "该用户不存在");
			}else{
				msg.element("code", "0")
				.element("msg", "您所赠送的不足");
			}
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0")
			.element("msg", "网络错误，请稍后赠送");
		}
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 绑定电话
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("upuserBindphone.json")
	public void upuserBindphone(HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject msg=new JSONObject();
		try {
			JSONObject dd=ls.codeMap;
			String codeKey=request.getParameter("codeKey");
			String code=request.getParameter("code");
			JSONObject data=new JSONObject();
			data.element("id", request.getParameter("id"))
				.element("tel",request.getParameter("tel"))
				.element("pass", request.getParameter("safe"));
			if(Dto.stringIsNULL(request.getParameter("id"))){//微信端
				JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
				data.element("id", user.getString("id"))
					.element("pass", user.containsKey("password")?user.getString("password"):"");
			}
			
			if(Dto.isNull(data)){
				msg.element("code", "0")
				.element("msg", "请填写数据");
			}else{
				if(!Dto.isNull(dd)&&dd.containsKey(codeKey)){
					String yzm=dd.getString(codeKey);
					if(yzm.equals(code)){
						msg=fbs.upuserBindphone(data);
						ls.codeMap.remove(codeKey);
					}else{
						msg.element("code", "0")
						.element("msg", "验证码错误");
					}
				}else{
					msg.element("code", "0")
					.element("msg", "请重新获取验证码");
				}
					
			}
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0")
			.element("msg", "网络错误，请稍后绑定");
		}
			Dto.printMsg(response, msg.toString());	
	}
	
	/**
	 * 判断是否绑定过
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("cxsfbdg.json")
	public void cxsfbdg(HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject msg=new JSONObject();
		try {
			String id=request.getParameter("id");
			String type=request.getParameter("type");
			JSONObject con=new JSONObject();
			con.element("id", id)
				.element("type", type);
			msg=fbs.cxsfbdg(con);
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0")
			  .element("msg", "网络错误");
		}
		Dto.printMsg(response, msg.toString());
	}
	/**
	 * 测试
	 */
	@RequestMapping("cs.html")
	public String cs(HttpServletRequest request,HttpServletResponse response){
		return "work/JHG/MyJsp";
		
	}
}
