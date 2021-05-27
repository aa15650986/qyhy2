package com.qy.game.server.dating;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.qy.game.service.GlobalService;
import com.qy.game.service.HService;
import com.qy.game.service.UserService;
import com.qy.game.utils.Dto;
import com.qy.game.utils.JsSdkUtils;

/**
 * H5
 * @author ASUS
 *
 */
@Controller
public class HWuService{

	@Resource
	private HService hwbiz;
	@Resource
	private UserService userService;
	@Resource
	private GlobalService globalService; 
	
	/**
	 * 进入主页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("zjh/Hlogin")
	public String Hlogin(HttpServletRequest request,HttpServletResponse response){
		String openID=null;
		HttpSession session=request.getSession();
		if(session.getAttribute(Dto.WEIXIN_USER_OPENID)!=null)
			openID=(String) session.getAttribute(Dto.WEIXIN_USER_OPENID);
		if(openID==null) openID="";
		JSONObject data=userService.getUserInfoByOpenID(openID,false,null);
//		JSONObject data = userService.getUserInfoByID(Long.valueOf(1));
		session.setAttribute(Dto.LOGIN_USER, data);
		JSONObject prentRec=hwbiz.getusersysroom(data.getString("id"));
		JSONArray coninsRank=hwbiz.getUserListForTopCoin(new JSONObject().element("start", 0).element("end", 5));
		request.getSession().setAttribute(Dto.LOGIN_USER,data);
		request.setAttribute("user", data);
		request.setAttribute("prentRec", prentRec);
		request.setAttribute("coninsRank", coninsRank);
		
		//组织各游戏的胜率统计
		JSONArray gameList=hwbiz.getCanUseGameList(1);
		JSONObject query=new JSONObject().element("id", data.getInt("id"));
		for(int i=0;i<gameList.size();i++){
			query.element("gid", gameList.getJSONObject(i).getInt("id"));
			JSONObject winData=hwbiz.getGameCountByGid(query);
			
			request.setAttribute("game_"+query.getInt("gid"), winData);
		}
//		return "work/mobile/index";
		return "work/H5/personal";
	}
	
	/**
	 * 获取基础数据
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("gosysbaseset")
	public void gosysbaseset(HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject data=hwbiz.gosysybaserec();
		Dto.printMsg(response, data.toString());
	}
	
	/**
	 * 开启管理栏目
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("upbaseismage")
	public void upbaseismage(HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		JSONObject msg=new JSONObject();
		if(user!=null){
			JSONObject lastUser=userService.getUserInfoByID(user.getLong("id"));
			JSONObject baseData=hwbiz.gosysybaserec();
			if(baseData.getDouble("Managprice")<=lastUser.getDouble("roomcard")){
				try {
					if(user.getInt("isManag")==0)
						hwbiz.upbaseismage(user.getString("id"));
					msg.element("code", 1)
					.element("msg", "操作成功");
				} catch (Exception e) {
					msg.element("code", 0)
					.element("msg", "操作失败");
				}
			}else
				msg.element("code", 0)
				   .element("msg", "房卡不足");
		}else
			msg.element("code", 0)
			   .element("msg", "身份认证已过期");
		Dto.printMsg(response, msg.toString());
	}
	/**
	 * 关闭管理栏目
	 */
	@RequestMapping("closemage")
	public void closemage(HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		JSONObject msg=new JSONObject();
		if(user!=null){
				hwbiz.closemage(user.getString("id"));
				msg.element("code", 1)
				.element("msg", "操作成功");
			} else{
				msg.element("code", 0)
				.element("msg", "操作失败");
			}
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 生成邀请好友
	 * @throws IOException 
	 */
	@RequestMapping("cswxyqhy_{id_str}")
	public String cswxyqhy(@PathVariable String id_str,HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject msg=new JSONObject();
		try {
			JSONObject user = userService.getUserInfoByID(Long.valueOf(id_str));
			msg.element("jssdkdata", hqwxfxsj(request));
			msg.element("name", user.getString("name"))
				.element("img", user.getString("headimg"))
				.element("id", id_str)
				.element("code", 1);
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", 0);
		}
		request.setAttribute("msg", msg);
		return "work/mobile/yaoqin";
	}
	
	/**
	 * 邀请好友的加入
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("addcswxyqhy")
	public void addcswxyqhy(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		JSONObject user;
		if(request.getSession().getAttribute(Dto.LOGIN_USER)==null){
			String openID=(String) request.getSession().getAttribute(Dto.WEIXIN_USER_OPENID);
			user=userService.getUserInfoByOpenID(openID, false, null);
		}else
			user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		JSONObject msg=new JSONObject();
		try {
			msg=hwbiz.addcswxyqhy(id,user.getString("id"));
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", 0)
			.element("msg", "程序错误");
		}
		Dto.printMsg(response, msg.toString());
		
	}
	
	/**
	 * 查看申请加入的
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("gozamembr")
	public String gozamembr(HttpServletRequest request,HttpServletResponse response){
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		JSONArray list=hwbiz.gozamembr(user.getString("id"));
		request.setAttribute("list", list);
		return "work/mobile/member";
	}
	
	/**
	 * 审核好友的加入
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("upzamembr")
	public void upzamembr(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		String sta=request.getParameter("sta");
		JSONObject msg=new JSONObject();
		try {
			hwbiz.upzamembr(id,sta);
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
	 * 进入制作房卡的
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("gozauserfank")
	public String gozauserfank(HttpServletRequest request,HttpServletResponse response){
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		request.setAttribute("room", user.getString("roomcard"));
		return "work/mobile/sendroom";
		
		
	}
	
	/**
	 * 增加发送房卡的
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("upzauserfank")
	public void upzauserfank(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String shu=request.getParameter("shu");
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		JSONObject msg=new JSONObject();
		if(user.getInt("roomcard")<Integer.valueOf(shu)){
			msg.element("code", 0)
				.element("msg", "房卡数不够");
		}else{
			String id=hwbiz.addusersendroom(user.getString("id"),shu);
			//更新用户数据
			JSONObject data = userService.getUserInfoByID(user.getLong("id"));
			request.getSession().setAttribute(Dto.LOGIN_USER,data);
			msg.element("code",1)
			   .element("id", id);
			
		}
	
		Dto.printMsg(response, msg.toString());
		
	}
	
	/**
	 * 进入领取别人发送的房卡
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("zjh/getusersendroom_{id}")
	public String addsyssengroom(@PathVariable String id,HttpServletRequest request,HttpServletResponse response){		
		JSONObject user;
		if(request.getSession().getAttribute(Dto.LOGIN_USER)!=null)
			user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		else{
			String openID=null;
			HttpSession session=request.getSession();
			if(session.getAttribute(Dto.WEIXIN_USER_OPENID)!=null)
				openID=(String) session.getAttribute(Dto.WEIXIN_USER_OPENID);
			if(openID==null) openID="";
			user=userService.getUserInfoByOpenID(openID,false,null);
			
			request.getSession().setAttribute(Dto.LOGIN_USER,user);
		}
		
		JSONObject data=hwbiz.getusersendroom(id);
		request.setAttribute("data", data);
		request.setAttribute("jssdkdata", hqwxfxsj(request));
		request.setAttribute("name", user.getString("name"));
		request.setAttribute("img", user.getString("headimg"));
		return "work/mobile/Receiveroom";
	}
	
	/**
	 * 领取别人发的房卡
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("addsyssendroom")
	public synchronized void addsyssendroom(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		JSONObject msg=new JSONObject();
		try {
			msg=hwbiz.addsyssendroom(id,user);
			
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", -1);
		}
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 查看领取和发送的房卡记录
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getusersysroom")
	public String getusersysroom(HttpServletRequest request,HttpServletResponse response){
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		JSONObject data=hwbiz.getusersysroom(user.getString("id"));
		request.setAttribute("data", data);
		request.setAttribute("user", request.getSession().getAttribute(Dto.LOGIN_USER));
//		return "work/mobile/userroom";
		return "work/H5/userroom";
	}
	
	
	/**
	 * 转移页面
	 * @param request
	 * @param response
	 */
	@RequestMapping("tozamembr")
	public String  tozamembr(HttpServletRequest request,HttpServletResponse response){
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		JSONArray list=hwbiz.gozamembr(user.getString("id"));
		request.setAttribute("name", user.getString("name"));
		request.setAttribute("headimg", user.getString("headimg"));
		request.setAttribute("roomcard", user.getString("roomcard"));
		request.setAttribute("list", list);
		return "work/mobile/Transferroom";
	}
	
	/**
	 * 转移时查看成员
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("ckzamembr")
	public void ckzamembr(HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		JSONArray list=hwbiz.tozamembr(user.getString("id"));
		Dto.printMsg(response, list.toString());
	}
	
	/**
	 * 进行房卡的转移
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("upzamemroom")
	public void upzamemroom(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		String shu=request.getParameter("shu");
		JSONObject msg=new JSONObject();
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		if(user.getInt("roomcard")<Integer.valueOf(shu)){
			msg.element("code", 0)
				.element("msg", "房卡 不足");
		}else{
			try {
				hwbiz.upzamemroom(id, user.getString("id"), shu);
				msg.element("code", 1)
				.element("msg", "操作成功");
			} catch (Exception e) {
				// TODO: handle exception
				msg.element("code", 0)
				.element("msg", "操作失败");
			}
		}
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 查询创建的房间
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("H5gamerooms")
	public String H5gamerooms(HttpServletRequest request,HttpServletResponse response){
		JSONObject user=(JSONObject)request.getSession().getAttribute(Dto.LOGIN_USER);
		JSONArray list=hwbiz.H5gamerooms(user.getString("id"));
		request.setAttribute("list", list);
		return "work/mobile/H5gamerooms";
	}
	/**
	 * 查询是否可加入房间
	 * @throws IOException 
	 */
	@RequestMapping("H5member")
	public void H5member(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		String room=request.getParameter("room");
		JSONObject msg=hwbiz.H5member(room, id);
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 房主同意加入房间
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("H5addmember")
	public void H5addmember(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String id=request.getParameter("id");
		String room=request.getParameter("room");
		JSONObject msg=new JSONObject();
		try {
			hwbiz.H5addmember(room, id);
			msg.element("code", "1");
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0");
		}
		Dto.printMsg(response, msg.toString());
	}
	
	/**
	 * 获取微信分享的数据
	 * @param request
	 * @return
	 */
	public JSONObject hqwxfxsj(HttpServletRequest request){
		HttpServletRequest httpRequest=request;  	
		String url ="http://"+request.getServerName() //服务器地址       
	            + httpRequest.getContextPath()      //项目名称  
	            +httpRequest.getServletPath();      //请求页面或其他地址  
		String query=request.getQueryString();
	    if(!Dto.stringIsNULL(query))
	        	   url=url+ "?" + (httpRequest.getQueryString()); //参数  
		JSONObject jssdkdata=JsSdkUtils.getReadyParameter(request, url);
		return jssdkdata;
	}
	
/************************************************战绩跳转************************************************************************************/	

	/**
	 * 跳转前往战绩汇总页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("userGameRecord")
	public ModelAndView getUserGameRecord(HttpServletRequest request,HttpServletResponse response){
		String gid_str=request.getParameter("gid");
		JSONObject userData=(JSONObject) request.getSession().getAttribute(Dto.LOGIN_USER);
		int gid=gid_str==null?6:Integer.valueOf(gid_str);
		
		//根据用户uuid获得用户战绩
		JSONArray gameLogs=globalService.getUserGameList(userData.getInt("id"), gid);
		
		//跳转页面
		ModelAndView mav=new ModelAndView("work/H5/zhanjiList");
		mav.addObject("gameLogs", gameLogs);
		mav.addObject("gid", gid);
		return mav;
	}
	
	/**
	 * 获得战绩详情
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getUserGameDetail")
	public ModelAndView jumpToRecordDetail(HttpServletRequest request,HttpServletResponse response){
		String room_id=request.getParameter("room_id");
		JSONArray list=globalService.getzausergamelogs(room_id);
		
		ModelAndView mav=new ModelAndView("work/H5/zhanjiDetail");
		mav.addObject("recordList", list);
		return mav;
	}
}
