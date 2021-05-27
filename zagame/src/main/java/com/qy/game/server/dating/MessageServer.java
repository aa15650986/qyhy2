package com.qy.game.server.dating;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.qy.game.service.UserService;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.service.dating.MessageService;
import com.qy.game.utils.Dto;

@Controller
public class MessageServer {

	@Resource MessageService messageService;
	@Resource
	private UserService userService;
	@Resource
	private GoldShopBizServer gsbiz;
	/**
	 * 公告
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getZaNotice")
	public void getZaNotice(HttpServletRequest request,HttpServletResponse response)throws Exception {
		String domainstr=request.getServerName();
		String url=gsbiz.getdomain(domainstr);
		JSONArray data=messageService.getNotice(url);
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("msg", "");
		jsonObject.put("data", data);
		jsonObject.put("code",1);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	/**
	 * 弹出公告
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getZaEjectNotice")
	public void getZaEjectNotice(HttpServletRequest request,HttpServletResponse response)throws Exception {
		String domainstr=request.getServerName();
		String url=gsbiz.getdomain(domainstr);
		JSONObject data=messageService.getEjectMessage(url);
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/operator";
		if(data.containsKey("image")&&!Dto.stringIsNULL(data.getString("image"))){
			data.element("image", basePath+data.getString("image"));
		}
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("msg", "");
		jsonObject.put("data", data);
		jsonObject.put("code",1);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	/**
	 * 活动
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getZaActivity")
	public void getZaActivity(HttpServletRequest request,HttpServletResponse response)throws Exception {
		String domainstr=request.getServerName();
		String url=gsbiz.getdomain(domainstr);
		JSONArray data=messageService.getActivities(url);
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/operator"+"/";
		for (int i = 0; i < data.size(); i++) {
			JSONObject dat = data.getJSONObject(i);
			if(dat.containsKey("logo")&&!Dto.stringIsNULL(dat.getString("logo"))){
				dat.element("logo", basePath+dat.getString("logo"));
			}
			if(dat.containsKey("image")&&!Dto.stringIsNULL(dat.getString("image"))){
				dat.element("image", basePath+dat.getString("image"));
			}
		}
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("msg", "");
		jsonObject.put("data", data);
		jsonObject.put("code",1);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	/**
	 * 获取用户个人信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("1getuserInfomation")
	public void getuserInfomation(HttpServletRequest request,HttpServletResponse response)throws Exception {
		String id=request.getParameter("id");
		JSONObject user = userService.getUserInfoByID(Long.valueOf(id));
		Dto.printMsg(response, user.toString());
	}
	
	/**
	 * 滚动公告
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getRollNotice")
	public void getRollNotice(HttpServletRequest request,HttpServletResponse response)throws Exception {
		String domainstr=request.getServerName();
		String url=gsbiz.getdomain(domainstr);
		JSONArray data=messageService.getRollNotice(url);
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/operator";
		for (int i = 0; i < data.size(); i++) {
			JSONObject dat = data.getJSONObject(i);
			if(dat.containsKey("image")&&!Dto.stringIsNULL(dat.getString("image"))){
				dat.element("image", basePath+dat.getString("image"));
			}
		}
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("msg", "");
		jsonObject.put("data", data);
		jsonObject.put("code",1);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	/**
	 * 轮播图
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getRollImg")
	public void getRollImg(HttpServletRequest request,HttpServletResponse response)throws Exception {
		String domainstr=request.getServerName();
		String platform=gsbiz.getdomain(domainstr);
		JSONArray data=messageService.getRollImg(platform);
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/operator";
		for (int i = 0; i < data.size(); i++) {
			JSONObject dat = data.getJSONObject(i);
			if(dat.containsKey("image")&&!Dto.stringIsNULL(dat.getString("image"))){
				dat.element("image", basePath+dat.getString("image"));
			}
		}
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("msg", "");
		jsonObject.put("data", data);
		jsonObject.put("code",1);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	/**
	 * 公告详情
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getNoticeInfo")
	public void getNoticeInfo(HttpServletRequest request,HttpServletResponse response)throws Exception {
		String id=request.getParameter("id");
		JSONObject data=messageService.getNoticeInfo(Long.parseLong(id));
		String basePath = userService.getSokcetInfo().get("headimgUrl");
		if(data.containsKey("image")&&!Dto.stringIsNULL(data.getString("image"))){
			data.element("image", basePath+data.getString("image"));
		}
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("msg", "");
		jsonObject.put("data", data);
		jsonObject.put("code",1);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	/**
	 * 活动详情
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getActivityInfo")
	public void getActivityInfo(HttpServletRequest request,HttpServletResponse response)throws Exception {
		String id=request.getParameter("id");
		JSONObject data=messageService.getActivityInfo(Long.parseLong(id));
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/operator";
		if(data.containsKey("image")&&!Dto.stringIsNULL(data.getString("image"))){
			data.element("image", basePath+data.getString("image"));
		}
		if(data.containsKey("logo")&&!Dto.stringIsNULL(data.getString("logo"))){
			data.element("logo", basePath+data.getString("logo"));
		}
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("msg", "");
		jsonObject.put("data", data);
		jsonObject.put("code",1);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	/**
	 * 获取用户未读消息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getZaUserMessage")
	public void getZaUserMessage(HttpServletRequest request,HttpServletResponse response)throws Exception {
		String id=request.getParameter("userId");
		String platform=request.getParameter("platform");
		JSONArray data=messageService.getUserMsg(Long.parseLong(id),platform);
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("msg", "");
		jsonObject.put("data", data);
		jsonObject.put("code",1);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	/**
	 * 获取用户消息详情
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getZafeedbackConById")
	public void getZafeedbackConById(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		String id=request.getParameter("id");
		boolean is=messageService.setFeedBackStatus(Long.parseLong(id), 1);//标为已读
		
		JSONObject data=messageService.getFeedBackInfo(Long.parseLong(id));
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("msg", "");
		jsonObject.put("data", data);
		jsonObject.put("code",1);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	/**
	 * 游戏中个用户删除消息
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("upZafeedbackCon.json")
	public void upZafeedbackCon(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		JSONObject msg=new JSONObject();
		try {
			messageService.upZafeedbackCon(id);
			msg.element("code", 1)
				.element("msg", "操作成功");
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", 0)
			.element("msg", "操作失败");
		}
		Dto.printMsg(response, msg.toString());
	}

	/**
	 * 获取用户排行信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getZaCoinsPaihang")
	public void getZaCoinsPaihang(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		String type=request.getParameter("type");//type=1 本週 、 type=2本月
		JSONArray array = messageService.getZaCoinsPaihang(type);
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
		for (int i = 0; i < array.size(); i++) {
			JSONObject data = array.getJSONObject(i);
			if(data.containsKey("headimg")){
				data.element("headimg", basePath+data.getString("headimg"));
			}
		}
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("array", array);
		jsonObject.put("code", 1);
		System.out.println("jsonObject:"+jsonObject);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	
	/**
	 * 获取用户排行信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getYuepaihangbang")
	public void getYuepaihangbang(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		String type=request.getParameter("type");//type=1本周type=2本月
		String userid=request.getParameter("userid");
		String ti="";
		JSONArray array = messageService.getYuepaihangbang(type);
		String basePath = userService.getSokcetInfo().get("headimgUrl");
		for (int i = 0; i < array.size(); i++) {
			JSONObject data = array.getJSONObject(i);
			if(data.containsKey("headimg")){
				data.element("headimg", basePath+data.getString("headimg"));
			}
		}
		JSONArray self = messageService.getyueCoinsPaihang(userid);
		for (int i = 0; i < self.size(); i++) {
			JSONObject data = self.getJSONObject(i);
			if(data.containsKey("headimg")){
				data.element("headimg", basePath+data.getString("headimg"));
			}
		}
		JSONArray d=messageService.getzaarena();
		if(d.size()>0){
			ti="每"+d.getJSONObject(0).getString("day")+d.getJSONObject(0).getString("hour")+"点更新奖励";
		}
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("array", array);
		jsonObject.put("self", self);
		jsonObject.put("tishi", ti);
		jsonObject.put("code", 1);
		System.out.println("jsonObject:"+jsonObject);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	
	/**
	 * 元宝-三弹头
	 * @throws IOException 
	 */
	@RequestMapping("getZaCoinsPaihang2")
	public void getZaCoinsPaihang2(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String domainstr=request.getServerName();
		String url=gsbiz.getdomain(domainstr);
		JSONObject data=new JSONObject();
		JSONObject user = new JSONObject();
		String id = request.getParameter("id");
		if(!Dto.stringIsNULL(id)){
			user = messageService.getUserPai(Long.valueOf(id));
		}
//		JSONArray list = new JSONArray();
//		JSONArray list1 = messageService.getZaCoinsPaihang4();
//		JSONArray list2 = messageService.getZaCoinsPaihang3(list1.size());
//		for (int i = 0; i < list1.size(); i++) {
//			if (list1.getJSONObject(i).getString("platform").equals(url)) {
//				list.add(list1.getJSONObject(i));
//				if (list.size()==3) {
//					break;
//				}
//			}
//		}
		
//		for (int i = 0; i < list2.size(); i++) {
//			if (!list1.contains(list2.getJSONObject(i))) {
//				list.add(list2.getJSONObject(i));
//				if (list.size()==50) {
//					break;
//				}
//			}
//		}
		
		JSONArray list = messageService.getZaCoinsPaihang5(url);

		String basePath = userService.getSokcetInfo().get("headimgUrl");		for (int i = 0; i < list.size(); i++) {
			JSONObject data2 = list.getJSONObject(i);
			if(data2.containsKey("headimg")){
				data2.element("headimg", basePath+data2.getString("headimg"));
			}
		}
		
		data.element("code", "1")
		.element("user", user)
		.element("array", list);
		System.out.println(data);

		Dto.printMsg(response, data.toString());
	}
	
	/**
	 * 获取用户每周排行信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getzhoupaihang")
	public void getzhoupaihang(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		String userid=request.getParameter("userid");
		String type=request.getParameter("type");
		JSONArray array = messageService.getzhoupaihang(type);
		String ti="";
		String basePath = userService.getSokcetInfo().get("headimgUrl");		for (int i = 0; i < array.size(); i++) {
			JSONObject data = array.getJSONObject(i);
			if(data.containsKey("headimg")){
				data.element("headimg", basePath+data.getString("headimg"));
			}	
		}
		
		JSONArray self = messageService.getZhouCoinsPaihang(userid);
		for (int i = 0; i < self.size(); i++) {
			JSONObject data = self.getJSONObject(i);
			if(data.containsKey("headimg")){
				data.element("headimg", basePath+data.getString("headimg"));
			}
		}
		JSONArray d=messageService.getzaarena();
		if(d.size()>0){
			ti="每"+d.getJSONObject(0).getString("day")+d.getJSONObject(0).getString("hour")+"点更新奖励";
		}
		JSONObject jsonObject= new JSONObject();
		jsonObject.put("array", array);
		jsonObject.put("self", self);
		jsonObject.put("code", 1);
		jsonObject.put("tishi", ti);
		System.out.println("jsonObject:"+jsonObject);
		Dto.printMsg(response, jsonObject.toString());
	}
	
	/**
	 * 竞技场获取数据
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("getzaaena.json")
	public void getzaaena(HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONArray d=messageService.getzaarena();
		Dto.printMsg(response, d.toString());
	}
}
