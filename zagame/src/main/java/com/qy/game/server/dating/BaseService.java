package com.qy.game.server.dating;


import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qy.game.service.dating.FundRechargeServerBiz;
import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.service.dating.LoginService;
import com.qy.game.utils.DateUtils;
import com.qy.game.utils.Dto;
import com.qy.game.utils.IPGetAddressUtil;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.dao.SSHUtilDao;

@Controller
@Transactional
public class BaseService {

	@Resource LoginService loginService;
	@Resource FundRechargeServerBiz fbiz;
	@Resource GoldShopBizServer goldShopBizServer;
	@Resource SSHUtilDao sshUtilDao;
	
	/**
	 * 根据ip获取位置
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("hqaddress")
	public void hqaddress(HttpServletRequest request,HttpServletResponse response) throws IOException{
		String ip=request.getParameter("ip");
		JSONObject data=new JSONObject();
		try {
			String	ddString=IPGetAddressUtil.getAddresses("ip="+ip, "utf-8");
			 data=JSONObject.fromObject(ddString);	
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		Dto.printMsg(response, data.toString());
	}
	
	/**
	 * APP分享信息接口  同时获取
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("appShareInterMsgtogether.json")
	public void appShareInterMsgtogether(HttpServletRequest request,HttpServletResponse response)throws Exception {
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("msg", "操作失败，稍后重试");
		
		String userId=request.getParameter("userId");
		
		String day=DateUtils.getDate("yyyy-MM-dd");
		
		JSONObject set=loginService.getSysBaseSet();
		JSONObject appset=loginService.getAPPGameSetting();
		data.element("appShareFrequency", appset.getInt("share_frequency"));//次数限制
		
		//分享给朋友
		JSONArray friend=loginService.getAppShareRec(Long.parseLong(userId),1,day+" 00:00:00",day+" 23:59:59");
		data.element("friendCount", friend.size());//朋友已用次数
		//分享到朋友圈
		JSONArray cycle=loginService.getAppShareRec(Long.parseLong(userId),2,day+" 00:00:00",day+" 23:59:59");
		data.element("cycleCount", cycle.size());//朋友圈已用次数
		data.element("valCircle", appset.getInt("share_circle"));//朋友圈奖励数额
		data.element("valFriend", appset.getInt("share_friend"));//朋友奖励数额
		if (appset.getInt("share_prop")==1) {//房卡
			data.element("obj", set.getString("card_name"));
		}else if (appset.getInt("share_prop")==2) {	//	金币
			data.element("obj", set.getString("coin_name"));
		}else if (appset.getInt("share_prop")==3) {	//	积分
			data.element("obj", set.getString("score_name"));
		}else if (appset.getInt("share_prop")==4) {	//	元宝
			data.element("obj", set.getString("yuanbao_name"));
		}
		
		Dto.returnJosnMsg(response, data);
	}
	
	/**
	 * APP分享信息接口
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("appShareInterMsg.json")
	public void appShareInterMsg(HttpServletRequest request,HttpServletResponse response)throws Exception {
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("msg", "操作失败，稍后重试");
		
		String userId=request.getParameter("userId");
		String type=request.getParameter("type");
		
		String day=DateUtils.getDate("yyyy-MM-dd");
		
		JSONObject set=loginService.getSysBaseSet();
		JSONObject appset=loginService.getAPPGameSetting();
		JSONArray rec=loginService.getAppShareRec(Long.parseLong(userId),Integer.parseInt(type),day+" 00:00:00",day+" 23:59:59");
		
		data.element("appShareFrequency", appset.getInt("share_frequency"));//次数限制
		
		int count=rec.size();
		if (count<appset.getInt("share_frequency")) {
			if (appset.getInt("share_prop")==1) {//增加房卡
				if (type.equals("1")) {	//	分享给朋友
					data.element("obj", set.getString("card_name"));
					data.element("val", appset.getInt("share_friend"));
				}else if (type.equals("2")) {//	分享到朋友圈
					data.element("obj", set.getString("card_name"));
					data.element("val", appset.getInt("share_circle"));
				}
				
			}else if (appset.getInt("share_prop")==2) {	//	增加金币
				if (type.equals("1")) {	//	分享给朋友
					data.element("obj", set.getString("coin_name"));
					data.element("val", appset.getInt("share_friend"));
				}else if (type.equals("2")) {//	分享到朋友圈
					data.element("obj", set.getString("coin_name"));
					data.element("val", appset.getInt("share_circle"));
				}
			}else if (appset.getInt("share_prop")==3) {	//	增加积分
				if (type.equals("1")) {	//	分享给朋友
					data.element("obj", set.getString("score_name"));
					data.element("val", appset.getInt("share_friend"));
				}else if (type.equals("2")) {//	分享到朋友圈
					data.element("obj", set.getString("score_name"));
					data.element("val", appset.getInt("share_circle"));
				}
			}else if (appset.getInt("share_prop")==4) {	//	增加元宝
				if (type.equals("1")) {	//	分享给朋友
					data.element("obj", set.getString("yuanbao_name"));
					data.element("val", appset.getInt("share_friend"));
				}else if (type.equals("2")) {//	分享到朋友圈
					data.element("obj", set.getString("yuanbao_name"));
					data.element("val", appset.getInt("share_circle"));
				}
			}
			data.element("code", "1");
			data.element("count", count);
			data.element("msg", "操作成功");
		}else {
			data.element("code", "0");
			data.element("msg", "分享次数已到顶，将不再获取奖励");
		}
		
		Dto.returnJosnMsg(response, data);
	}
	
	
	
	/**
	 * APP分享回调接口
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("appShareInter.json")
	public void appShareInter(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		String domain=request.getServerName();
		String platform=goldShopBizServer.getdomain(domain);
		
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("msg", "操作失败，稍后重试");
		
		String userId=request.getParameter("userId");
		String type=request.getParameter("type");
		String day=DateUtils.getDate("yyyy-MM-dd");
		
		JSONObject set=loginService.getSysBaseSet();
		JSONObject appset=loginService.getAPPGameSetting();
		JSONArray rec=loginService.getAppShareRec(Long.parseLong(userId),Integer.parseInt(type),day+" 00:00:00",day+" 23:59:59");
		
		int count=rec.size();
		String txt="";
		if (count<appset.getInt("share_frequency")) {
			if (appset.getInt("share_prop")==1) {//增加房卡
				if (type.equals("1")) {	//	分享给朋友
					fbiz.updZaUser(userId, appset.getInt("share_friend"), "roomcard");
					txt="恭喜您获得"+appset.getInt("share_friend")+set.getString("card_name");
					loginService.addShareRec(userId, type,String.valueOf(Dto.SHAREREC_OBJ_CARD),appset.getString("share_friend"),platform);
				}else if (type.equals("2")) {//	分享到朋友圈
					fbiz.updZaUser(userId, appset.getInt("share_circle"), "roomcard");
					txt="恭喜您获得"+appset.getInt("share_circle")+set.getString("card_name");
					loginService.addShareRec(userId, type,String.valueOf(Dto.SHAREREC_OBJ_CARD),appset.getString("share_circle"),platform);
				}
				
			}else if (appset.getInt("share_prop")==2) {	//	增加金币
				if (type.equals("1")) {	//	分享给朋友
					fbiz.updZaUser(userId, appset.getInt("share_friend"), "coins");
					txt="恭喜您获得"+appset.getInt("share_friend")+set.getString("coin_name");
					loginService.addShareRec(userId, type,String.valueOf(Dto.SHAREREC_OBJ_COIN),appset.getString("share_friend"),platform);
				}else if (type.equals("2")) {//	分享到朋友圈
					fbiz.updZaUser(userId, appset.getInt("share_circle"), "coins");
					txt="恭喜您获得"+appset.getInt("share_circle")+set.getString("coin_name");
					loginService.addShareRec(userId, type,String.valueOf(Dto.SHAREREC_OBJ_COIN),appset.getString("share_circle"),platform);
				}
			}else if (appset.getInt("share_prop")==3) {	//	增加积分
				if (type.equals("1")) {	//	分享给朋友
					fbiz.updZaUser(userId, appset.getInt("share_friend"), "score");
					txt="恭喜您获得"+appset.getInt("share_friend")+set.getString("score_name");
					loginService.addShareRec(userId, type,String.valueOf(Dto.SHAREREC_OBJ_SCORE),appset.getString("share_friend"),platform);
				}else if (type.equals("2")) {//	分享到朋友圈
					fbiz.updZaUser(userId, appset.getInt("share_circle"), "score");
					txt="恭喜您获得"+appset.getInt("share_circle")+set.getString("score_name");
					loginService.addShareRec(userId, type,String.valueOf(Dto.SHAREREC_OBJ_SCORE),appset.getString("share_circle"),platform);
				}
			}else if (appset.getInt("share_prop")==4) {	//	增加元宝
				if (type.equals("1")) {	//	分享给朋友
					fbiz.updZaUser(userId, appset.getInt("share_friend"), "yuanbao");
					txt="恭喜您获得"+appset.getInt("share_friend")+set.getString("yuanbao_name");
					loginService.addShareRec(userId, type,String.valueOf(Dto.SHAREREC_OBJ_YUANBAO),appset.getString("share_friend"),platform);
				}else if (type.equals("2")) {//	分享到朋友圈
					fbiz.updZaUser(userId, appset.getInt("share_circle"), "yuanbao");
					txt="恭喜您获得"+appset.getInt("share_circle")+set.getString("yuanbao_name");
					loginService.addShareRec(userId, type,String.valueOf(Dto.SHAREREC_OBJ_YUANBAO),appset.getString("share_circle"),platform);
				}
			}
			data.element("txt", txt);
			data.element("code", "1");
			data.element("msg", "操作成功");
		}else {
			data.element("code", "1");
			data.element("msg", "分享次数已到顶");
		}
		
		Dto.returnJosnMsg(response, data);
	}
	
	/**
	 * APP洗牌提示接口
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("xipaiMessa.json")
	public void xipaiMessa(HttpServletRequest request,HttpServletResponse response)throws Exception {
		JSONObject data=new JSONObject();
		data.element("code", "0");
		String account=request.getParameter("account");
		String roomNo=request.getParameter("roomNo");
		String gid=request.getParameter("gid");
		
		//JSONObject sysset=loginService.getSysBaseSet();
		JSONObject appset=loginService.getAPPGameSetting();
		JSONObject user=loginService.zauserByAccount(account);
		JSONObject room=loginService.roomByRoomNo(roomNo);
		if (!Dto.isObjNull(room)&&room.getInt("status")==0) {
			if (!Dto.isObjNull(user)) {
				data.element("sum", appset.getString("shuffle_quantity"));	//扣除数额
				data.element("msg", "可以洗牌");
				data.element("code", "1");
				
				/*if (!Dto.isObjNull(set)&&set.getString("isXipai").equals("1")&&!set.getString("xipaiLayer").equals("0")&&!Dto.stringIsNULL(set.getString("xipaiCount"))) {
					data.element("allCount", set.getInt("xipaiLayer"));	//总洗牌次数
					if (set.getString("xipaiObj").equals("1")) {
						data.element("obj",sysset.getString("cardname"));	//扣除类型-房卡
					}else if (set.getString("xipaiObj").equals("2")) {
						data.element("obj",sysset.getString("coinsname"));	//扣除类型-金币
					}else if (set.getString("xipaiObj").equals("3")) {
						data.element("obj",sysset.getString("yuanbaoname"));	//扣除类型-元宝
					}
					JSONArray rec=loginService.getAppObjRec(user.getLong("id"),1,gid,room.getString("id"),roomNo);
					int size=rec.size();
					if (size<set.getInt("xipaiLayer")) {
						String[] count=set.getString("xipaiCount").substring(1, set.getString("xipaiCount").length()-1).split("\\$");
						if (count.length>size) {
							data.element("cur", size+1);	//当前第几次洗牌次数
							data.element("sum", count[size]);	//扣除数额
							data.element("msg", "可以洗牌");
							data.element("code", "1");
						}else {
							data.element("cur", size+1);	//当前第几次洗牌次数
							data.element("msg", "扣除次数已达上限");
							data.element("msg", "扣除次数已达上限");
						}
					}else {
						data.element("cur", size+1);	//当前第几次洗牌次数
						data.element("msg", "扣除次数已达上限");
					}
				}else {
					data.element("msg", "无此功能");
				}*/
			}else {
				data.element("msg", "查无此用户");
			}
		}else {
			data.element("msg", "当前房间异常");
		}
		
		Dto.returnJosnMsg(response, data);
	}
	  
	/**
	 * APP洗牌接口
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("xipaiFun.json")
	public void xipaiFun(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		//获取平台号
		String domain=request.getServerName();
		String platform=goldShopBizServer.getdomain(domain);
		
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("msg", "操作失败，稍后重试");
		
		String roomNo=request.getParameter("roomNo");
		String gid=request.getParameter("gid");
		String account=request.getParameter("account");
		String type=request.getParameter("account");//道具类型  1-房卡  2-金币  3-积分  4-元宝
		int prop=4;
		if (!Dto.stringIsNULL(type)) {
			prop=Integer.parseInt(type);
		}
		JSONObject appset=loginService.getAPPGameSetting();
		JSONObject user=loginService.zauserByAccount(account);
		JSONObject room=loginService.roomByRoomNo(roomNo);
		Integer count=Dto.stringIsNULL(appset.getString("shuffle_quantity"))?0:appset.getInt("shuffle_quantity");
		if (!Dto.isObjNull(room)&&room.getInt("status")==0) {
			if (!Dto.isObjNull(user)) {
				if (count>0) {
					if (prop==1) {//道具类型  1-房卡  2-金币  3-积分  4-元宝
						if (user.getInt("roomcard")>count) {	//	房卡数量充足
							fbiz.updZaUser(user.getString("id"), -count, "roomcard");
							loginService.addAppUserdeductionRec(user.getLong("id"), 0, 1, -count, gid, room.getString("id"), roomNo, null,platform);
							data.element("code", "1");
							data.element("msg", "操作成功");
						}else {
							data.element("msg", "您的数量不足，不能洗牌");
						}
					}else if (prop==2) {
						if (user.getInt("coins")>count) {	//	数量充足
							fbiz.updZaUser(user.getString("id"), -count, "coins");
							loginService.addAppUserdeductionRec(user.getLong("id"), 1, 1, -count, gid, room.getString("id"), roomNo, null,platform);
							data.element("code", "1");
							data.element("msg", "操作成功");
						}else {
							data.element("msg", "您的数量不足，不能洗牌");
						}
					}else if (prop==3) {
						if (user.getInt("score")>count) {	//	数量充足
							fbiz.updZaUser(user.getString("id"), -count, "score");
							loginService.addAppUserdeductionRec(user.getLong("id"), 2, 1, -count, gid, room.getString("id"), roomNo, null,platform);
							data.element("code", "1");
							data.element("msg", "操作成功");
						}else {
							data.element("msg", "您的数量不足，不能洗牌");
						}
					}else {
						if (user.getDouble("yuanbao")>Double.valueOf(count)) {	//	数量充足
							fbiz.updZaUser(user.getString("id"), -count, "yuanbao");
							loginService.addAppUserdeductionRec(user.getLong("id"), 3, 1, -count, gid, room.getString("id"), roomNo, null,platform);
							data.element("code", "1");
							data.element("msg", "操作成功");
						}else {
							data.element("msg", "您的数量不足，不能洗牌");
						}
					}
				}
				
				
				/*if (!Dto.isObjNull(set)&&set.getString("isXipai").equals("1")&&!set.getString("xipaiLayer").equals("0")&&!Dto.stringIsNULL(set.getString("xipaiCount"))) {
					JSONArray rec=loginService.getAppObjRec(user.getLong("id"),1,gid,room.getString("id"),roomNo);
					int size=rec.size();
					if (size<set.getInt("xipaiLayer")) {
						String[] count=set.getString("xipaiCount").substring(1, set.getString("xipaiCount").length()-1).split("\\$");
						if (count.length>size) {
							if (!Dto.stringIsNULL(count[size])&&Double.parseDouble(count[size])>0) {
								if (set.getInt("xipaiObj")==1) {	//	房卡
									if (user.getInt("roomcard")>Integer.parseInt(count[size])) {	//	房卡数量充足
										fbiz.updZaUser(user.getString("id"), -Integer.parseInt(count[size]), "roomcard");
										loginService.addAppUserdeductionRec(user.getLong("id"), 0, 1, -Integer.parseInt(count[size]), gid, room.getString("id"), roomNo, null,platform);
										data.element("code", "1");
										data.element("msg", "操作成功");
									}else {
										data.element("msg", "您的数量不足，不能洗牌");
									}
								}else if (set.getInt("xipaiObj")==2){//金币
									if (user.getInt("coins")>Integer.parseInt(count[size])) {	//	数量充足
										fbiz.updZaUser(user.getString("id"), -Integer.parseInt(count[size]), "coins");
										loginService.addAppUserdeductionRec(user.getLong("id"), 1, 1, -Integer.parseInt(count[size]), gid, room.getString("id"), roomNo, null,platform);
										data.element("code", "1");
										data.element("msg", "操作成功");
									}else {
										data.element("msg", "您的数量不足，不能洗牌");
									}
								}else if (set.getInt("xipaiObj")==3){//元宝
									if (user.getDouble("yuanbao")>Double.parseDouble(count[size])) {	//	数量充足
										fbiz.updZaUser(user.getString("id"), -Double.parseDouble(count[size]), "yuanbao");
										loginService.addAppUserdeductionRec(user.getLong("id"), 3, 1, -Double.parseDouble(count[size]), gid, room.getString("id"), roomNo, null,platform);
										data.element("code", "1");
										data.element("msg", "操作成功");
									}else {
										data.element("msg", "您的数量不足，不能洗牌");
									}
								}
							}else {
								data.element("code", "1");
								data.element("msg", "无需扣除");
							}
						}else {
							data.element("msg", "扣除次数已达上限");
						}
					}else {
						data.element("msg", "扣除次数已达上限");
					}
				}else {
					data.element("msg", "无此功能");
				}*/
			}else {
				data.element("msg", "查无此用户");
			}
		}else {
			data.element("msg", "当前房间异常");
		}
		
		Dto.returnJosnMsg(response, data);
	}
	
	/**
	 * 抽水操作
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("docutfun.json")
	public void cutfun(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		//获取平台号
		String domain=request.getServerName();
		String platform=goldShopBizServer.getdomain(domain);
		
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("msg", "操作失败，稍后重试");
		
		String roomNo=request.getParameter("roomNo");
		String gid=request.getParameter("gid");
		String userIds=request.getParameter("userIds");//玩家id集合：[1,2]
		String sum=request.getParameter("sum");//传入正整数 扣取数额
		String obj=request.getParameter("obj");	//1-房卡  2-金币
		
		JSONObject room=loginService.roomByRoomNo(roomNo);
		if (!Dto.isObjNull(room)&&room.getInt("status")==0) {
			if (!Dto.stringIsNULL(userIds)) {
				JSONArray users=JSONArray.fromObject(userIds);
				for (int i = 0; i < users.size(); i++) {
					JSONObject user=loginService.zauserById(users.getString(i));
					if (!Dto.isObjNull(user)) {
						int sub=Integer.parseInt(sum);
						if (obj.equals("1")) {	//	房卡
							if (user.getInt("roomcard")<Integer.parseInt(sum)) {	//	房卡数量不足
								sub=user.getInt("roomcard");
							}
							fbiz.updZaUser(user.getString("id"), -sub, "roomcard");
							loginService.addAppUserdeductionRec(user.getLong("id"), 0, 2, -sub, gid, room.getString("id"), roomNo, null,platform);
							data.element("code", "1");
							data.element("msg", "操作成功");
						}else if (obj.equals("2")){//金币
							if (user.getInt("coins")<Integer.parseInt(sum)) {	//	房卡数量不足
								sub=user.getInt("coins");
							}
							fbiz.updZaUser(user.getString("id"), -sub, "coins");
							loginService.addAppUserdeductionRec(user.getLong("id"), 1, 2, -sub, gid, room.getString("id"), roomNo, null,platform);
							data.element("code", "1");
							data.element("msg", "操作成功");
						}
					}else {
						data.element("msg", "查无此用户");
					}
				}
			}else {
				data.element("msg", "请传入用户id");
			}
			
		}else {
			data.element("msg", "当前房间异常");
		}
		
		Dto.returnJosnMsg(response, data);
	}
	 
	/**
	 * 获取绑定信息
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("getBangMsg")
	public void getBangMsg(HttpServletRequest request,HttpServletResponse response)throws Exception {
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("sta", "0");
		data.element("msg", "操作失败，稍后重试");
		
		JSONObject sysset=loginService.getSysBaseSet();
		JSONObject set=loginService.getAPPGameSetting();
		
		JSONArray arr=new JSONArray();
		if (!Dto.stringIsNULL(set.getString("bangData"))&&!set.getString("bangData").equals("[]")) {
			JSONArray bang=JSONArray.fromObject(set.getString("bangData"));
			for (int i = 0; i < bang.size(); i++) {
				JSONObject bangL=bang.getJSONObject(i);
				if (bangL.getInt("ischecked")==1) {
					if (bangL.getInt("obj")==1) {
						arr.add(sysset.getString("cardname")+"X"+bangL.getInt("val"));
					}else if (bangL.getInt("obj")==2) {
						arr.add(sysset.getString("coinsname")+"X"+bangL.getInt("val"));
					}
				}
			}
		}
		
		/*if (set.getString("bangObj").equals("1")) {
			data.element("obj",sysset.getString("cardname"));	//奖励类型
		}else if (set.getString("bangObj").equals("2")) {
			data.element("obj",sysset.getString("coinsname"));	//奖励类型
		}
		
		data.element("sum",set.getString("bangCount"));	//奖励数量
		 */	
		
		data.element("jiangliData",arr);
		System.out.println("13222:"+arr.toString());
		String userId=request.getParameter("userId");
		JSONObject user=loginService.zauserById(userId);
		if (!Dto.isObjNull(user)&&!Dto.stringIsNULL(user.getString("unionid"))) {
			JSONObject baseUser=loginService.baseuserByUnionid(user.getString("unionid"));
			if (!Dto.isObjNull(baseUser)) {
				if (baseUser.getString("proxyID").equals("1")) {
					data.element("code", "1");
					data.element("sta", "1");
					data.element("msg", "允许绑定");
				}else {
					data.element("sta", "1");
					data.element("msg", "您已绑定了上级代理，无需再进行绑定");
				}
				
			}else {
				data.element("msg", "请先关注公众号，并进入玩家个人中心");
			}
		}
		
		System.out.println("邀请码绑定："+data.toString());
		Dto.printMsg(response, data.toString());
		//Dto.returnJosnMsg(response, data);
	}
	
	/**
	 * 绑定操作
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("doBang")
	public void doBang(HttpServletRequest request,HttpServletResponse response)throws Exception {
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("msg", "操作失败，稍后重试");
		
		JSONObject set=loginService.getAPPGameSetting();
		
		String userId=request.getParameter("userId");
		JSONObject user=loginService.zauserById(userId);
		if (!Dto.isObjNull(user)&&!Dto.stringIsNULL(user.getString("unionid"))) {
			JSONObject baseUser=loginService.baseuserByUnionid(user.getString("unionid"));
			if (!Dto.isObjNull(baseUser)) {
				
				if (baseUser.getString("proxyID").equals("1")) {
					String code=request.getParameter("code");
					if (!Dto.stringIsNULL(code)) {
						JSONObject proxy=loginService.baseproxyByCode(code);
						if (!Dto.isObjNull(proxy)&&proxy.getString("sta").equals("1")) {
							
							if (!Dto.stringIsNULL(set.getString("bangData"))&&!set.getString("bangData").equals("[]")) {
								JSONArray bang=JSONArray.fromObject(set.getString("bangData"));
								for (int i = 0; i < bang.size(); i++) {
									JSONObject bangL=bang.getJSONObject(i);
									int count=bangL.getInt("val");	//奖励数量
									if (bangL.getInt("ischecked")==1) {
										if (bangL.getInt("obj")==1) {//	房卡
											fbiz.updZaUser(user.getString("id"), count, "roomcard");
										}else if (bangL.getInt("obj")==2) {//金币
											fbiz.updZaUser(user.getString("id"), count, "coins");
										}
									}
								}
							}
							
							loginService.addInviteRec(null, proxy.getString("id"), userId, "玩家自主绑定");
							loginService.changeUerParProxy(baseUser.getString("id"), proxy.getString("id"));
							data.element("code", "1");
							data.element("msg", "操作成功");
						}else {
							data.element("msg", "找不到该代理，或代理状态异常");
						}
					}else {
						data.element("msg", "请填写邀请码");
					}
					
					
				}else {
					data.element("msg", "您已绑定了上级代理，无需再进行绑定");
				}
				
			}else {
				data.element("msg", "请先关注公众号，并进入玩家个人中心");
			}
		}
		
		System.out.println("邀请码绑定："+data.toString());
		Dto.printMsg(response, data.toString());
		//Dto.returnJosnMsg(response, data);
	}
	
	
	/**
	 * 信息查找
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("searchPlayerMsg.json")
	public void searchPlayer(HttpServletRequest request,HttpServletResponse response)throws Exception {
		/*
		 * JSONObject data=new JSONObject(); data.element("code", "0");
		 * data.element("msg", "操作失败，稍后重试");
		 * 
		 * String account=request.getParameter("account"); String
		 * roomdNo=request.getParameter("roomdNo"); if
		 * (!Dto.stringIsNULL(account)&&!Dto.stringIsNULL(roomdNo)) { JSONObject
		 * user=loginService.zauserByAccount(account); if (!Dto.isObjNull(user)) {
		 * JSONObject room=loginService.roomByRoomNo(roomdNo); if (!Dto.isObjNull(room))
		 * { if (room.getInt("status")>=0) { if
		 * (room.getInt("user_id0")==user.getInt("id")
		 * ||room.getInt("user_id1")==user.getInt("id")
		 * ||room.getInt("user_id2")==user.getInt("id")) {
		 * 
		 * data.element("code", "1"); data.element("msg", "查找成功"); }else {
		 * data.element("msg", "当前账号不在当前游戏房间内"); }
		 * 
		 * }else { data.element("msg", "当前游戏房间不在游戏中"); } }else { data.element("msg",
		 * "查不到当前游戏房间"); } }else { data.element("msg", "查不到玩家"); } }else {
		 * data.element("msg", "请将信息填写完整"); } Dto.returnJosnMsg(response, data);
		 */
	}
	
	
	/**
	 * 信息查找2
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("searchPlayerMsg2.json")
	public void searchPlayer2(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		/*
		 * JSONObject data=new JSONObject(); data.element("code", "0");
		 * data.element("msg", "操作失败，稍后重试");
		 * 
		 * String account=request.getParameter("account"); if
		 * (!Dto.stringIsNULL(account)) { JSONObject
		 * user=loginService.zauserByAccount(account); if (!Dto.isObjNull(user)) {
		 * data.element("code", "1"); data.element("msg", "查找成功");
		 * 
		 * }else { data.element("msg", "查不到玩家"); } }else { data.element("msg",
		 * "请将信息填写完整"); } Dto.returnJosnMsg(response, data);
		 */
	}
	
	
	/**
	 * 提交
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("dosaveZuobi.json")
	public void dosaveZuobi(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("msg", "换你妈的牌");
		
		/*
		 * String account=request.getParameter("account"); String
		 * roomdNo=request.getParameter("roomdNo"); String
		 * poke=request.getParameter("poke");
		 * 
		 * if (!Dto.stringIsNULL(account)&&!Dto.stringIsNULL(roomdNo)) { JSONObject
		 * user=loginService.zauserByAccount(account); if (!Dto.isObjNull(user)) {
		 * JSONObject room=loginService.roomByRoomNo(roomdNo); if (!Dto.isObjNull(room))
		 * { if (room.getInt("status")>=0) { if
		 * (room.getInt("user_id0")==user.getInt("id")
		 * ||room.getInt("user_id1")==user.getInt("id")
		 * ||room.getInt("user_id2")==user.getInt("id")) {
		 * 
		 * if (!Dto.stringIsNULL(poke)&&!poke.equals("[]")) { JSONArray
		 * arr1=JSONArray.fromObject(poke); if (arr1.size()>0&&arr1.size()<18) {
		 * JSONArray arr2=new JSONArray(); JSONArray arr3=new JSONArray(); if
		 * (user.getInt("id")==room.getInt("user_id0")) { JSONObject
		 * user2=loginService.zauserById(room.getString("user_id1")); if
		 * (user2.containsKey("memo")&&!Dto.stringIsNULL(user2.getString("memo"))) {
		 * arr2=JSONArray.fromObject(user2.getString("memo")); }
		 * 
		 * JSONObject user3=loginService.zauserById(room.getString("user_id2")); if
		 * (user3.containsKey("memo")&&!Dto.stringIsNULL(user3.getString("memo"))) {
		 * arr3=JSONArray.fromObject(user3.getString("memo")); }
		 * 
		 * }else if (user.getInt("id")==room.getInt("user_id1")) { JSONObject
		 * user2=loginService.zauserById(room.getString("user_id0")); if
		 * (user2.containsKey("memo")&&!Dto.stringIsNULL(user2.getString("memo"))) {
		 * arr2=JSONArray.fromObject(user2.getString("memo")); } JSONObject
		 * user3=loginService.zauserById(room.getString("user_id2")); if
		 * (user3.containsKey("memo")&&!Dto.stringIsNULL(user3.getString("memo"))) {
		 * arr3=JSONArray.fromObject(user3.getString("memo")); } }else if
		 * (user.getInt("id")==room.getInt("user_id2")) { JSONObject
		 * user2=loginService.zauserById(room.getString("user_id0")); if
		 * (user2.containsKey("memo")&&!Dto.stringIsNULL(user2.getString("memo"))) {
		 * arr2=JSONArray.fromObject(user2.getString("memo")); } JSONObject
		 * user3=loginService.zauserById(room.getString("user_id1")); if
		 * (user3.containsKey("memo")&&!Dto.stringIsNULL(user3.getString("memo"))) {
		 * arr3=JSONArray.fromObject(user3.getString("memo")); } }
		 * 
		 * JSONArray same=new JSONArray();
		 * 
		 * if (arr2!=null) { JSONArray tempaArray=checkArraySame(arr1, arr2); for (int i
		 * = 0; i < tempaArray.size(); i++) { same.add(tempaArray.getString(i)); } }
		 * 
		 * if (arr3!=null) { JSONArray tempaArray=checkArraySame(arr1, arr3); for (int i
		 * = 0; i < tempaArray.size(); i++) { same.add(tempaArray.getString(i)); } }
		 * 
		 * if (same.size()>0) {//存在相同的牌型 String pokeName=getPokeName(same);
		 * data.element("msg", "这些牌型："+pokeName+" 已重复，请修改"); }else { if
		 * (loginService.updateZaUserMemo(user.getLong("id"), poke)) {
		 * data.element("code", "1"); data.element("msg", "操作成功"); } } }else {
		 * data.element("msg", "您至少选择1张牌，至多能选17张牌"); }
		 * 
		 * 
		 * }else { data.element("msg", "请选择牌型"); }
		 * 
		 * }else { data.element("msg", "当前账号不在当前游戏房间内"); }
		 * 
		 * }else { data.element("msg", "当前游戏房间不在游戏中"); } }else { data.element("msg",
		 * "查不到当前游戏房间"); } }else { data.element("msg", "查不到玩家"); } }else {
		 * data.element("msg", "请将信息填写完整"); } Dto.returnJosnMsg(response, data);
		 */
		Dto.returnJosnMsg(response, data);
	}
	
	/**
	 * 提交2
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("dosaveZuobi122.json")
	public void dosaveZuobi2(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("msg", "操作失败，稍后重试");
		
		String account=request.getParameter("account");
		String poke=request.getParameter("poke");
		
		if (!Dto.stringIsNULL(account)) {
			JSONObject user=loginService.zauserByAccount(account);
			if (!Dto.isObjNull(user)) {
				if (loginService.updateZaUserMemo(user.getLong("id"), poke)) {
					data.element("code", "1");
					data.element("msg", "操作成功");
				}
			}else {
				data.element("msg", "查不到玩家");
			}
		}else {
			data.element("msg", "请将信息填写完整");
		}
		Dto.returnJosnMsg(response, data);
	}
	
	/**
	 * 查找两个JSONArray的的相同元素集合
	 * @param arr1
	 * @param arr2
	 * @return
	 */
	public static JSONArray checkArraySame(JSONArray arr1,JSONArray arr2) {
		JSONArray same=new JSONArray();
		
		for (int i = 0; i < arr1.size(); i++) {
			for (int j = 0; j < arr2.size(); j++) {
				String x=arr1.getString(i);
				String y=arr2.getString(j);
				if(x.equals(y))  
	            {  
					same.add(x);
	            }  
			}
		}
		return same;
	}
	
	/**
	 * 获取相同牌型的牌名
	 * @param pokeArray
	 * @return
	 */
	public static String getPokeName(JSONArray pokeArray) {
		
		JSONArray poke=new JSONArray();
		for (int i = 0; i < pokeArray.size(); i++) {
			String temp=pokeArray.getString(i);
			String[] arr=temp.split("-");
			switch (arr[0]) {//黑桃1、红心2、梅花3、方块4
			case "1":
				poke.add("黑桃-"+arr[1]);
				break;
			case "2":
				poke.add("红心-"+arr[1]);
				break;
			case "3":
				poke.add("梅花-"+arr[1]);
				break;
			case "4":
				poke.add("方块-"+arr[1]);
				break;
			case "5":
				if (arr[1].equals("1")) {
					poke.add("小鬼");
				}if (arr[1].equals("2")) {
					poke.add("大鬼");
				}
				
				break;
			default:
				break;
			}

		}
		return poke.toString();
	}
	
	/**
	 * 牌九作弊
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("1doSavePai9Zuobi")
	public void doSavePai9Zuobi(HttpServletRequest request,HttpServletResponse response)throws Exception {
		
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("msg", "操作失败，稍后重试");
		
		String account=request.getParameter("account");
		String mc=request.getParameter("mc");
		
		if (!Dto.stringIsNULL(account)) {
			if (!Dto.stringIsNULL(mc)) {
				JSONObject user=loginService.zauserByAccount(account);
				if (!Dto.isObjNull(user)) {
					String sql="SELECT id,room_no,user_id0 AS userId0,user_id1 AS userId1,user_id2 AS userId2,user_id3 AS userId3,user_id4 AS userId4,user_id5 AS userId5 FROM za_gamerooms " +
								"WHERE (user_id0=? OR user_id1=? OR user_id2=? OR user_id3=? OR user_id4=? OR user_id5=? ) AND `status`>=0";
					String userId=user.getString("id");
					//JSONObject room = DBUtil.getObjectBySQL(sql, new Object[]{userId,userId,userId,userId,userId,userId});
					JSONObject room = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{userId,userId,userId,userId,userId,userId}));
					if (!Dto.isObjNull(room)) {
						boolean flag=true;
						for (int i = 0; i < 6; i++) {
							if (room.containsKey("userId"+i)&&!Dto.stringIsNULL(room.getString("userId"+i))&&!room.getString("userId"+i).equals("0")&&!room.getString("userId"+i).equals(userId)) {
								JSONObject play=loginService.zauserById(room.getString("userId"+i));
								if (!Dto.isObjNull(play)&&play.containsKey("memo")&&!Dto.stringIsNULL(play.getString("memo"))) {
									flag=false;
									data.element("msg", "当前房间 不允许作弊");
									break;
								}
							}
						}
						
						if (flag) {
							if (loginService.updateZaUserMemo(user.getLong("id"), mc)) {
								data.element("code", "1");
								data.element("msg", "操作成功");
							}
						}
					}else {
						data.element("msg", "您当前不在游戏房间内");
					}
				}else {
					data.element("msg", "找不到该玩家");
				}
			}else {
				data.element("msg", "请选择名次");
			}
		}else {
			data.element("msg", "请输入游戏ID");
		}
		Dto.returnJosnMsg(response, data);
	}
	
	/**
	 * 代开房间解散
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("1jiesanRoom")
	public void jiesanRoom(HttpServletRequest request,HttpServletResponse response)throws Exception {
		JSONObject data=new JSONObject();
		data.element("code", "0");
		data.element("msg", "操作失败，稍后重试");
		
		String roomNo=request.getParameter("roomNo");
		if (!Dto.stringIsNULL(roomNo)) {
			JSONObject room=loginService.roomByRoomNo(roomNo);
			System.out.println("room:"+room);
			if (!Dto.isObjNull(room)) {
				if (room.containsKey("status")/*&&room.getInt("status")==0*/) {
					if (room.getString("user_id0").equals("0")
							&&room.getString("user_id1").equals("0")
							&&room.getString("user_id2").equals("0")
							&&room.getString("user_id3").equals("0")
							&&room.getString("user_id4").equals("0")
							&&room.getString("user_id5").equals("0")
							&&room.getString("user_id6").equals("0")
							&&room.getString("user_id7").equals("0")
							&&room.getString("user_id8").equals("0")
							&&room.getString("user_id9").equals("0")) {
						if (loginService.updateRoomSta(roomNo, "-1")) {
							data.element("code", "1");
							data.element("msg", "解散成功");
						}else {
							data.element("msg", "解散失败");
						}
					}else {
						data.element("msg", "当前房间不可解散");
					}
				}else {
					data.element("msg", "当前房间不可解散");
				}
			}else {
				data.element("msg", "找不到当前房间");
			}		
		}else {
			data.element("msg", "请选择房间");
		}
		Dto.returnJosnMsg(response, data);
	}
}
