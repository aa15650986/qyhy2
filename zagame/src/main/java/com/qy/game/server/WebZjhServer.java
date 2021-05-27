package com.qy.game.server;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.qy.game.server.dating.HWuService;
import com.qy.game.service.GameService;
import com.qy.game.service.GlobalService;
import com.qy.game.service.HService;
import com.qy.game.service.UserService;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.utils.Configure;
import com.qy.game.utils.Dto;
import com.qy.game.utils.ShortMessageUtil;
import com.qy.game.utils.TimeUtil;

/**
 * 炸金花控制台
 * @author nanoha
 *
 */
@Controller
public class WebZjhServer {
	
	@Resource
	private HService hwbiz;
	@Resource
	private UserService userService;

	@Resource
	private GlobalService globalService;
	
	@Resource
	private GameService gameService;
	
	@Resource
	private GoldShopBizServer gsbiz;
	//
	/**
	 * 测试用户数据,
	 */
	private static String[] uuid = {"111111","222222","333333","444444","u1"};
	/*private static String[] uuid = {"123456",};*/
	/**
	 * 测试用户数组下标
	 */
	private static int uuidIndex = 0;
	
	/**
	 * 根据用户uuid（通过微信授权登录获取）获取用户信息
	 * 
	 * @param openID
	 */
	@RequestMapping(value="zjh/gameHall")
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response,@RequestParam(required = false) String openID) throws IOException{
		HttpSession session=request.getSession();
		if(session.getAttribute(Dto.WEIXIN_USER_OPENID)!=null)
			openID=(String) session.getAttribute(Dto.WEIXIN_USER_OPENID);
		if(openID==null) openID="";
		
		
		//用户信息
		JSONObject userData=userLogin(openID,request).getJSONObject("data").getJSONObject("userinfo");
		//游戏选项
		JSONArray gameList=hwbiz.getCanUseGameList(1);
//		JSONArray settingInfos = gameService.getGameSetting(6, "YOUJ");

		request.getSession().setAttribute(Dto.LOGIN_USER, userData);
		
		ModelAndView mav=new ModelAndView("work/H5/home");
		mav.addObject("userData", userData);
		mav.addObject("gameList", gameList);
		mav.addObject("title", "乐淘游戏");
		return mav;
	}
	
	/**
	 * 跳转前往战斗场景加载页面
	 * @param roomNo
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("zjh/gameJoin_{roomNo}_{gid_str}")
	public ModelAndView jumToJoinPage(@PathVariable String roomNo,@PathVariable String gid_str,HttpServletRequest request, HttpServletResponse response){
		JSONObject userData;
		if(request.getSession().getAttribute(Dto.LOGIN_USER)==null){
			String openID=(String) request.getSession().getAttribute(Dto.WEIXIN_USER_OPENID);
			userData=userLogin(openID,request).getJSONObject("data").getJSONObject("userinfo");
		}else
			userData=(JSONObject) request.getSession().getAttribute(Dto.LOGIN_USER);
		
		
		//0.根据传入参数获得获得房间信息
		int gid=Integer.valueOf(gid_str);
		JSONObject roomInfo=gameService.getRoomInfo(gid, roomNo);
		
		//1.根据获得的房间信息组织游戏参数展示
		JSONArray settingInfos = gameService.getGameSetting(gid, "YOUJ");
		
		JSONArray roomSetArray=new JSONArray();
		for(int i=0;i<settingInfos.size();i++ ){
			JSONObject roomSetting=new JSONObject();
			JSONObject tmp=settingInfos.getJSONObject(i);
			
			roomSetting.element("keyName", tmp.getString("opt_name"));
			
			String select_val=roomInfo.getJSONObject("base_info").getString(tmp.getString("opt_key"));
			for(int j=0;j<tmp.getJSONArray("opt_val").size();j++){
				JSONObject tmp2=tmp.getJSONArray("opt_val").getJSONObject(j);
				if(tmp2.get("val").toString().equals(select_val))
				{
					roomSetting.element("keyVal", tmp2.getString("name"));
					break;
				}
			}
			roomSetArray.element(roomSetting);
		}
		
		//2.组织游戏参与者信息
		JSONArray joinList=new JSONArray();
		for(int i=0;i<10;i++){
			JSONObject tmp=new JSONObject();
			if(roomInfo.containsKey("user_id"+i) && roomInfo.getInt("user_id"+i)!=0){
				tmp.element("user_id", roomInfo.getInt("user_id"+i))
				   .element("user_head", roomInfo.getString("user_icon"+i))
				   .element("user_name", roomInfo.getString("user_name"+i))
				   .element("score", roomInfo.getInt("user_score"+i));
				joinList.element(tmp);
			}else
				break;
		}
		
		//2.返回页面
		HWuService service=new HWuService();
		ModelAndView mav=new ModelAndView("work/H5/xiangqing");
		mav.addObject("roomInfo", TimeUtil.transTimeStamp(roomInfo, "yyyy-MM-dd HH:mm", "createtime"));
		mav.addObject("roomSetArray", roomSetArray);
		mav.addObject("joinList", joinList);
		mav.addObject("userData", userData);
		mav.addObject("jssdkdata", service.hqwxfxsj(request));
		mav.addObject("appID", Configure.getAppid());
		return mav;
	}
	
	/**
	 * 跳转前往游戏战斗场景
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping("web-mobile/intogame_{roomNo}")
	public ModelAndView jumpToGame(@PathVariable String roomNo,HttpServletRequest request, HttpServletResponse response) throws IOException{
		//0.获得前台传入的房间信息
		String roomInfo_str=request.getParameter("roomInfo");
		String isVist=request.getParameter("isFromVist");
		if(isVist==null){
			JSONObject roomInfo = gameService.getRoomSettingByRoomId(roomNo);
			response.sendRedirect("/zagame/zjh/gameJoin_"+roomNo+"_"+roomInfo.getString("game_id")+".html");
			return null;
		}
		Dto.writeLog("接收到前台传递的房间信息："+roomInfo_str);
		
		//1.组织微信配置参数
		HWuService service=new HWuService();
		
		String openID=(String) request.getSession().getAttribute(Dto.WEIXIN_USER_OPENID);
		JSONObject userData=userLogin(openID,request).getJSONObject("data");
		
		ModelAndView mav=new ModelAndView("web-mobile/MyJsp");
		mav.addObject("userData", userData);
		mav.addObject("jssdkdata", service.hqwxfxsj(request));
		mav.addObject("appID", Configure.getAppid());
		mav.addObject("roomNo", roomNo);
		
		//2.不为空时走创建房间流程
		if(roomInfo_str!=null){
			JSONObject room=JSONObject.fromObject(roomInfo_str);
			mav.addObject("roominfo", room);
		}else
			mav.addObject("roominfo", "null");
		
		return mav;
	}
	
	/**
	 * 用户登录操作
	 * @param openID
	 * @param request
	 * @return
	 */
	private JSONObject userLogin(String openID,HttpServletRequest request){
		String domainstr=request.getServerName();
		String url=gsbiz.getdomain(domainstr);
		if(openID.equals("")){
			if(uuidIndex>=uuid.length){
				uuidIndex=0;
			}
			openID = uuid[uuidIndex];
			uuidIndex++;
		}
		
		JSONObject user = null;
		
		if(Arrays.asList(uuid).contains(openID)){
			user = userService.getUserInfoByOpenID(openID, false, null);
		}else{
			String ip ="";
			if (request.getHeader("x-forwarded-for") == null) { 
				ip= request.getRemoteAddr();  
			}else{
				ip= request.getHeader("x-forwarded-for");  
			}
			user = userService.getUserInfoByOpenID(openID, true, ip);
		}
		
		JSONObject jsonObject=new JSONObject();
		if(user == null) {
			jsonObject.put("msg", "用户登录失败");
			jsonObject.put("data", "");
			jsonObject.put("code", 0);
		} else {
			jsonObject.put("msg", "用户登录成功");
			String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
			user.put("headimg", basePath+user.getString("headimg"));
			JSONObject data = new JSONObject();
			data.put("userinfo", user);
			JSONObject setting=userService.getUserSetting(user.getInt("id"));
			if(setting!=null){
				data.put("setting", setting);
			}
			// 获取配置信息
			JSONObject globalinfo = globalService.getGlobalInfo(url);
			if(globalinfo!=null){
				
				data.put("version", globalinfo.get("version"));
			}else{
				data.put("version", 1);
			}
			jsonObject.put("data", data);
			jsonObject.put("code",1);
		}
		return jsonObject;
	}
	/****************************************************** 手机绑定 *****************************************************************/

	/**
	 * 实现输入手机号发送验证码操作，将验证码存储到session当中
	 * 
	 */
	@RequestMapping(value = "zjh/tel", method = RequestMethod.POST)
	public void sendMessage(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String tel = request.getParameter("tel");
		String messageBack = ShortMessageUtil.getCode(tel, request);// 调用工具类将request中的tel参数传入
		HttpSession session = request.getSession();
		session.setAttribute("messageBack", messageBack);
		JSONObject msg = new JSONObject();
		msg.element("msg", "验证码已发送").element("code", 1)
				.element("status", "success");
		Dto.returnJosnMsg(response, msg);
		
	}
	/**
	 * 实现得到手机验证码，填入验证码进行验证
	 * 
	 * @param tel
	 * @param request
	 * @throws Exception
	 */
	@RequestMapping(value = "zjh/telVerify", method = RequestMethod.POST)
	
	public void telVerify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		// 用户输出的手机号和短信验证码
		String tel = request.getParameter("tel");
		String messageBack = request.getParameter("messageBack");
		HttpSession session = request.getSession();
		// 从session当中取到的短信验证码
		String messageBack2 = (String) session.getAttribute("messageBack");
		JSONObject huizhi = new JSONObject();
		// 进行对比
		if (!messageBack.equals(messageBack2)) {
			huizhi.element("msg", "验证码输出错误！").element("code", 0)
					.element("status", "error");
		} else {
			huizhi.element("msg", "验证码输出正确！").element("code", 1)
					.element("status", "success");
		}
		// 从session中得到user的id
		JSONObject userinfo = (JSONObject) request.getSession().getAttribute(Dto.LOGIN_USER);
		long id = userinfo.getLong("id");
		
		userService.updateUserTel(tel, id);
		Dto.returnJosnMsg(response, huizhi);
		}
			
		
	

	/**
	 * 根据用户id解除绑定手机
	 */
	@RequestMapping(value = "zjh/clearTel")
	public boolean updateTel(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// 从session中得到user的id
		JSONObject userinfo = (JSONObject) request.getSession().getAttribute(Dto.LOGIN_USER);
		long id = userinfo.getLong("id");
		boolean result=userService.clearPhoneNumByUserId(id);
		return result;
	}
	/***************************************************************************/

	/**
	 * 根据游戏Id展示游戏的具体信息  ddzMatch
	 */
	@RequestMapping(value = "zjh/showGameList")
	public ModelAndView showgameSetting(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		JSONObject userinfo=(JSONObject) request.getSession().getAttribute(Dto.LOGIN_USER);
		String gameId_str = request.getParameter("gameId");
		long gameId2_int = Long.parseLong(gameId_str);
		JSONArray settingInfo = gameService.getGameSetting(gameId2_int,
				Dto.APP_PLATFORM);
		ModelAndView model = new ModelAndView("work/H5/ddzMatch");
		long id = userinfo.getLong("id");
		JSONObject roomDesc = gameService.checkIsExistRoom(gameId2_int, id);
		if(roomDesc!=null){
			roomDesc=TimeUtil.transTimeStamp(roomDesc, "yyyy-MM-dd HH:mm:ss", "createtime");
			Dto.returnJosnMsg(response, roomDesc);
		}
		model.addObject("roomDesc", roomDesc);
		model.addObject("settingInfo", settingInfo);
		model.addObject("userData", userinfo);
		model.addObject("gameId_str", gameId_str);
		request.getSession().setAttribute("settingInfo", settingInfo);
		return model;
	}

	/**
	 * 通过用户id和房间类型判断用户是否在房间内   ddzMatch
	 *//*
	@RequestMapping(value = "zjh/isExistMyself",method=RequestMethod.GET)
	public JSONObject isExistMyself(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// 从网页上得到gameid(游戏类型)
		String gameId = request.getParameter("gameId");
		long gameid = Long.valueOf(gameId);
		// 从session中得到user的id
		JSONObject userData = (JSONObject) request.getSession().getAttribute(
				Dto.LOGIN_USER);
		JSONObject userinfo = userData.getJSONObject("userinfo");
		long id = userinfo.getLong("id");
		// 将数据传入到Model中
		ModelAndView model = new ModelAndView("work/mobile/hall");// model位置待定
		// 通过gameid得到房间具体信息
		JSONArray roomInfo = gameService.getRoomDescByGameId(gameid);
		model.addObject("roomInfo", roomInfo);
		// 根据gameID和userID判断是否存在房间,是则返回房间信息,否则返回null
		JSONObject roomDesc = gameService.checkIsExistRoom(gameid, id);
		Dto.returnJosnMsg(response, roomDesc);
		if(roomDesc!=null){
			return roomDesc;
		}
		return null;
		
	}*/

	/**
	 * 根据游戏类型和房间信息得到详细信息  lijizuju
	 */
	@RequestMapping(value = "zjh/playGame_{roomNo}")
	public ModelAndView palyGame(@PathVariable String roomNo,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String gameId = request.getParameter("gameId");
		long gameID = Long.parseLong(gameId);
		JSONArray settingInfo = gameService.getGameSetting(gameID,
				Dto.APP_PLATFORM);
		// 根据房间id得到房间所有信息
		JSONObject roomInfo = gameService.getRoomSettingByRoomId(roomNo);
		// 获得当前房间的配置
		JSONObject roomSetting = roomInfo.getJSONObject("base_info");
		// 返回
		JSONArray info = new JSONArray();
		for (int i = 0; i < settingInfo.size(); i++) {
			JSONObject info_desc = new JSONObject();
			JSONObject tmpObj = settingInfo.getJSONObject(i);
			String opt_key = tmpObj.getString("opt_key");// 选项键名
			JSONArray opt_val = tmpObj.getJSONArray("opt_val");// 选项内容
			// 根据键名获得房间选项中对应的值
			String type = roomSetting.get(opt_key).toString();
			for (int j = 0; j < opt_val.size(); j++) {
				String opt_va = opt_val.getJSONObject(j).get("val").toString();
				if (type.equals(opt_va)) {
					info_desc.element("opt_key", opt_key)
							.element("opt_name", tmpObj.getString("opt_name"))
							.element("opt_va", opt_va)
							.element("opt_va_name",
									opt_val.getJSONObject(j).getString("name"));
					info.element(info_desc);
					
					break;
				}
			}
		}
		ModelAndView model = new ModelAndView("work/H5/lijizuju");
		model.addObject("info", info);
		String roomOwnerId=roomInfo.get("user_id0").toString();
		long roomOwnerID = Integer.parseInt(roomOwnerId);
		JSONObject userInfo=userService.getUserInfoByID(roomOwnerID);
		model.addObject("userInfo", userInfo);
		return model;
	}
	/**
	 * 所有牌局   zhanji
	 */
	@RequestMapping(value = "zjh/zhanji")
	public ModelAndView MyGameList(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		JSONObject userDate=(JSONObject) request.getSession().getAttribute(Dto.LOGIN_USER);
		ModelAndView model = new ModelAndView("work/H5/zhanji");
		String userId=userDate.containsKey("userinfo")?userDate.getJSONObject("userinfo").get("id").toString():userDate.get("id").toString();
		long gameId_int=Long.parseLong(userId);
		JSONArray settingInfo = gameService.getGameSetting(gameId_int,
				Dto.APP_PLATFORM);
		model.addObject("settingInfo",settingInfo);
		
		JSONObject userinfo=userDate.containsKey("userinfo")?userDate.getJSONObject("userinfo"):userDate;
		model.addObject("userinfo", userinfo);
		//页面传入参数choose参数，选择类型
		//玩过的牌局
		String choose=request.getParameter("choose")==null?"1":request.getParameter("choose");
		model.addObject("choose", choose);
		// 通过gameid得到房间具体信息
		JSONArray roomInfo = gameService.getRoomDescByGameId(gameId_int);
		model.addObject("roomInfo", roomInfo);
		
		if(choose.equals("1")){
			JSONArray palied=gameService.getUserAllGameList(userId);
			model.addObject("list", palied);
		}
		//正在进行的牌局
		if(choose.equals("2")){
			JSONArray rightNow=gameService.getRightNowPlay(userId);
			model.addObject("list", rightNow);
		}
		//我组建的牌局
		if(choose.equals("3")){
			JSONArray byMe=gameService.getPlayByMe(userId);
			model.addObject("list", byMe);
		}
		//结束的牌局
		if(choose.equals("4")){
			JSONArray end=gameService.getEndPlay(userId);
			model.addObject("list", end);
		}
			return model;
	}
	
	
	/**
	 * 商城展示
	 */
	@RequestMapping(value = "zjh/mall")
	public ModelAndView showMall(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		ModelAndView model = new ModelAndView("work/H5/mall");
		JSONObject userData = (JSONObject) request.getSession().getAttribute(
				Dto.LOGIN_USER);
		JSONObject userinfo = userData.getJSONObject("userinfo");
		long id = userinfo.getLong("id");
		JSONArray mall=gameService.getMallList();
		JSONArray mallPay=userService.getMallPayByUserId(id);
		if(mallPay!=null){
			mallPay=TimeUtil.transTimestamp(mallPay, "createTime", "yyyy-MM-dd HH:mm:ss");
			model.addObject("mallPay",mallPay);
		}
		if(mall!=null){
		model.addObject("mall", mall);
		}
		return model;
	}
	
	/**
	 *  检查是否可以加入牌局
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("zjh/iscanjoin")
	public void checkCanJoin(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String ownerID_str=request.getParameter("ownerID");
				
		JSONObject userData;
		if(request.getSession().getAttribute(Dto.LOGIN_USER)==null){
			String openID=(String) request.getSession().getAttribute(Dto.WEIXIN_USER_OPENID);
			userData=userLogin(openID,request).getJSONObject("data").getJSONObject("userinfo");
		}else
			userData=(JSONObject) request.getSession().getAttribute(Dto.LOGIN_USER);
		
		//0.检查房主的邀请成员中是否有自己
		boolean isVisted=true;
		if(Long.valueOf(ownerID_str)!=userData.getLong("id"))
			isVisted=hwbiz.isVisited(Long.valueOf(ownerID_str), userData.getLong("id"));
		//1.返回结果
		if(isVisted)
			Dto.printMsg(response, new JSONObject().element("code", 1).toString());
		else
			Dto.printMsg(response, new JSONObject().element("code", 0).toString());
	}
}
