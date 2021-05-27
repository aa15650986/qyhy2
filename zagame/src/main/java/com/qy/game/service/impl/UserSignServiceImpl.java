package com.qy.game.service.impl;

import java.text.ParseException;
import java.util.Date;

import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.qy.game.service.dating.SignServer;
import com.qy.game.utils.DateUtils;
import com.qy.game.utils.Dto;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.bean.PageUtil;
import com.qy.game.ssh.dao.SSHUtilDao;

@Transactional
@Component
@Service
public class UserSignServiceImpl implements SignServer{

	@Resource
	SSHUtilDao sshUtilDao;
	
	@Override
	public JSONObject jumpUserSign(long userID,String platform) {
		
		
		JSONObject data = new JSONObject();
		
		// 获取当前月份
		String time = DateUtils.date3SStr();
		
		PageUtil pageUtil=null;
		// 获取用户签到信息
		String sql = "select createtime,singnum from sys_sign where userID=? and createtime>=? ";
		
		Object[] queryParam = {userID,time};
		JSONArray array = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, queryParam,pageUtil));
		
		try {
			TimeUtil.transTimesArray(array, "createtime","d");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		int singnum = 0;
		if(array.size()>0){
			singnum = array.getJSONObject(0).getInt("singnum");
		}
		
		// 签到次数
		int sign = 0;
		// 获取当前月份的天数
		int days = TimeUtil.getNewMMDay();
		
		JSONArray array2 = new JSONArray();
		for(int i=0;i<array.size();i++){
			array2.add(array.getJSONObject(i));
		}
		
		JSONArray dateArray = new JSONArray();
		JSONObject qdcsjson=new JSONObject();
		// 获取连续签到次数
		for(int i=1;i<=days;i++){
			
			JSONObject dayDate = new JSONObject();
			dayDate.element("createtime", i+"");
			dayDate.element("status", 0+"");
			for(int k=0;k<array2.size();k++){
				if(array2.getJSONObject(k).getInt("createtime")==i){
					dayDate.element("status", 1+"");
					if(Dto.isObjNull(qdcsjson)){
							sign=1;
						}else{
							if(array2.getJSONObject(k).getInt("createtime")-qdcsjson.getInt("createtime")==1){
								sign +=1;
							}else{
								sign=1;
							}
						}
						qdcsjson=array2.getJSONObject(k);
						array2.remove(k);
						break;
				}
			}
			dateArray.add(dayDate);
		}
		
		if (isUserSign(userID)) {
			data.element("isSign", "1");
		}else {
			data.element("isSign", "0");
		}
		if("DFBY".equals(platform))
			days=7;
		// 领取节点
		JSONObject zar = getzasignreward();
		
		// 查询领奖次数
		JSONObject numlist = ifUserSign(Long.valueOf(userID));
		
		int jiangj = 0;			// 累积奖金
		int lv1 = 0;			// 累积vip奖金
		int canget = 0;         // 是否可以领取 ，控制按钮状态
		
		// 查询用户是否领取当前节点奖励，如果没有领取,则进行奖金累积并显示至页面 ----- start
		if ((sign == zar.getInt("firstnode") || (sign > zar.getInt("firstnode")&&sign < zar.getInt("secnode")) )
				&& numlist.getJSONObject("list").getInt("first") == 0) {
			
			jiangj = jiangj + zar.getInt("firstmoney");
			lv1 = lv1 + zar.getInt("lv1");
			canget = 1;					// 状态变为可领取
		}
		if ( (sign == zar.getInt("secnode") || (sign>zar.getInt("secnode")&&sign < zar.getInt("thnode")) )
				&& numlist.getJSONObject("list").getInt("secound") == 0) {
			
			jiangj = jiangj + zar.getInt("secmoney");
			lv1 = lv1 + zar.getInt("lv2");
			canget = 1;
		} 
		if ( (sign == zar.getInt("thnode") || (sign>zar.getInt("thnode")&&sign < days) )
				&& numlist.getJSONObject("list").getInt("third") == 0) {
			
			jiangj = jiangj + zar.getInt("thmoney");
			lv1 = lv1 + zar.getInt("lv3");
			canget = 1;
		} 
		if (sign == days && numlist.getJSONObject("list").getInt("month") == 0) {
			jiangj = jiangj + zar.getInt("monthmoney");
			lv1 = lv1 + zar.getInt("lv4");
			canget = 1;
		} 
		// ---------------------- end -------------------------
		
		data.element("totalv", lv1);
		data.element("jiangj", jiangj);
		data.element("canget", canget);
		data.element("array", dateArray);
		data.element("singnum", singnum);
		data.element("sign", sign);
		
		return data;
		
	}
	

	@Override
	public JSONObject saveUserSign(long userID,String platform) {
		
		JSONObject data = new JSONObject();
		
		if(isUserSign(userID)){
			
			String time = DateUtils.date3SStr();
			
			String sql = "select id,singnum from sys_sign where userID=? and createtime>=? order by id desc ";
			
			Object[] queryParam = {userID,time};
			
			JSONObject signData = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, queryParam));
			
			int singnum = 0;
			
			if(!Dto.stringIsNULL(signData.toString())){
				singnum = signData.getInt("singnum");
			}
			
			// 新增签到记录
			String sql2="INSERT INTO sys_sign (userID,detail,singnum,createTime) values (?,?,?,?)";
			Object[] objects={userID,"签到",0,new Date()};
			boolean result = sshUtilDao.updObjectBySQL(sql2, objects);
			
			
			if(result){
				String sql1="select id,vip,coins,roomcard from za_users where id=?";
				Object[] objects2={userID};
				JSONObject zu=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql1, objects2));
				
				String sql3="select meitfangk,meitmoney from za_sign_reward where id=? ";
				Object [] zid = {1};
				JSONObject zar = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql3, zid));
				Integer coinIn = zar.getInt("meitmoney");
				Integer Roomcard =  zar.getInt("meitfangk");
				
				if(zu.getInt("vip")>0){
					String sql4="select viproomcard,vipcoins from za_mall where id=?";
					Object[] objects4={zu.getString("vip")};
					JSONObject vipjson=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql4, objects4));
					coinIn=coinIn+vipjson.getInt("vipcoins");
					Roomcard=Roomcard+vipjson.getInt("viproomcard");
				}
				if("JSWX".equals(platform)){
					String sql5="update za_users set yuanbao=yuanbao+? ,roomcard=roomcard+? where id=?";
					Object[] objects3={coinIn,Roomcard,userID};
					sshUtilDao.updObjectBySQL(sql5, objects3);
				}else{
					String sql5="update za_users set coins=coins+? ,roomcard=roomcard+? where id=?";
					Object[] objects3={coinIn,Roomcard,userID};
					sshUtilDao.updObjectBySQL(sql5, objects3);
				}
				data.element("msg", "签到成功");
				data.element("status", "1");
				
				
			}else{
				data.element("msg", "签到失败");
				data.element("status", "0");
			}
		}else{
			data.element("msg", "今天已签到");
			data.element("status", "2");
		}
		
		return data;
	}

	
		@Override
		public JSONObject userTakeSign(long userID,String platform) {
			
			JSONObject json = new JSONObject();
			int tianshu=0;
			// 获取当前月份的天数
			
			// 获取连续签到天数  ---- start
			JSONObject data = jumpUserSign(Long.valueOf(userID),platform);
			
			int days = data.getInt("sign");
			// 获取连续签到天数 ---- end
		 if("DFBY".equals(platform)){
			tianshu = 7;
				if(days==7){
					String sql10="DELETE FROM sys_sign WHERE userID=?";//每7天领取时清掉
					Object[] objects10={userID}; 
					boolean result = sshUtilDao.updObjectBySQL(sql10, objects10);
				}		
			}else{
				tianshu = TimeUtil.getNewMMDay();
			}
			// 获取用户信息表
			String sql1="select id,vip,coins,roomcard from za_users where id=?";
			Object[] objects={userID};
			JSONObject zu=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql1, objects));
			
			// 获取后台奖励金额
			String sql2="select firstnode,firstfangka,firstnode,secnode,secmoney,secfangk,thnode,thmoney,thmofangk," +
					"monthmoney,monthfangk,meitmoney,meitfangk,lv1,lv2,lv3,lv3,lv4,type from za_sign_reward where id=? ";
			Object [] zid = {1};
			
			JSONObject zar = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, zid));
			
			// 查询用户领取次数
			JSONObject numlist = ifUserSign(Long.valueOf(userID));
			int coinIn=0;
			if("JSWX".equals(platform))
				coinIn=zu.getInt("yuanbao");
			else
				coinIn=zu.getInt("coins");
			int Roomcard = zu.getInt("roomcard");
			// 判断是否领奖
//			if (numlist.getJSONObject("list").getInt("first")==0) {
				// 第一次奖励//|| days > zar.getDouble("firstnode") && days < zar.getDouble("secnode")
				if (days == zar.getInt("firstnode") ) {
					// 金币
					coinIn = coinIn+zar.getInt("firstmoney");
					Roomcard = Roomcard + zar.getInt("firstfangka");
					boolean is = saveZaUserlist(zu.getString("id"),coinIn,Roomcard,platform);
					if (is) {
						// 保存用户领奖信息
						boolean res = saveZaUserSignlist("first", Dto.ZA_IFSIGN_YES, userID);
						if (res) {
//							if (zu.getInt("lv") > 0) {
//								json.element("price", zar.getInt("firstmoney")+zar.getInt("lv1"));
//							}else {
//								json.element("price", zar.getInt("firstmoney"));
//							}
							json.element("data", "1");
						}else {
							json.element("data", "-1");
						}
					}}
//				else {
//					json.element("data", "2");
//				}
//			}else {
//				json.element("data", "-2");
//			}
			
//			if (numlist.getJSONObject("list").getInt("secound")==0) {
				// 第二次奖励//|| days > zar.getInt("secnode") && days < zar.getInt("thnode")
				else if (days == zar.getInt("secnode") ) {
					
//					if (zar.getInt("type")==Dto.ZA_SIGN_JINBI) {
//						if (zu.getInt("lv") > 0) {
//							coinIn = coinIn + zar.getInt("secmoney") + zar.getInt("lv2");
//						}else 
							coinIn = coinIn + zar.getInt("secmoney");
//					}else if (zar.getInt("type")==Dto.ZA_SIGN_ROOM) {
//						if (zu.getInt("lv") > 0) {
//							Roomcard = Roomcard + zar.getInt("secmoney") + zar.getInt("lv2");
//						}else 
							Roomcard = Roomcard + zar.getInt("secfangk");
//					}
					boolean is = saveZaUserlist(zu.getString("id"),coinIn,Roomcard,platform);
					if (is) {
						// 保存领奖信息
						boolean res = saveZaUserSignlist("secound", Dto.ZA_IFSIGN_YES, userID);
						if (res) {
//							if (zu.getInt("lv") > 0) {
//								json.element("price", zar.getInt("secmoney")+zar.getInt("lv2"));
//							}else {
//								json.element("price", zar.getInt("secmoney"));
//							}
							json.element("data", "1");
						}else {
							json.element("data", "-1");
						}
					}
				}
//			}else {
//				json.element("data", "-2");
//			}
			
//			if (numlist.getJSONObject("list").getInt("third")==0) {
				// 第三次奖励// || days > zar.getInt("thnode") && days < tianshu
				else if (days == zar.getInt("thnode")) {
//					
//					if (zar.getInt("type")==Dto.ZA_SIGN_JINBI) {
//						if (zu.getInt("lv") > 0) {
//							coinIn = coinIn + zar.getInt("thmoney") + zar.getInt("lv3");
//						}else 
							coinIn = coinIn + zar.getInt("thmoney");
//					}else if (zar.getInt("type")==Dto.ZA_SIGN_ROOM) {
//						if (zu.getInt("lv") > 0) {
//							Roomcard = Roomcard + zar.getInt("thmoney") + zar.getInt("lv3");
//						}else 
							Roomcard = Roomcard + zar.getInt("thmofangk");
//					}
					boolean is = saveZaUserlist(zu.getString("id"),coinIn,Roomcard,platform);
					if (is) {
						boolean res = saveZaUserSignlist("third", Dto.ZA_IFSIGN_YES, userID);
						if (res) {
//							if (zu.getInt("lv") > 0) {
//								json.element("price", zar.getInt("thmoney")+zar.getInt("lv3"));
//							}else {
//								json.element("price", zar.getInt("thmoney"));
//							}
							json.element("data", "1");
						}else {
							json.element("data", "-1");
						}
					}
				}
//			}else {
//				json.element("data", "-2");
//			}
			
			
//			if (numlist.getJSONObject("list").getInt("month")==0) {
				else if (days == tianshu) {
					
//					if (zar.getInt("type")==Dto.ZA_SIGN_JINBI) {
//						if (zu.getInt("lv") > 0) {
//							coinIn = coinIn + zar.getInt("monhtmoney") + zar.getInt("lv4");
//						}else 
							coinIn = coinIn + zar.getInt("monhtmoney");
//					}else if (zar.getInt("type")==Dto.ZA_SIGN_ROOM) {
//						if (zu.getInt("lv") > 0) {
//							Roomcard = Roomcard + zar.getInt("monhtmoney") + zar.getInt("lv4");
//						}else 
							Roomcard = Roomcard + zar.getInt("monthfangk");
//					}
					boolean is = saveZaUserlist(zu.getString("id"),coinIn,Roomcard,platform);
					if (is) {
						boolean res = saveZaUserSignlist("month", Dto.ZA_IFSIGN_YES, userID);
						
						/*if (zu.getInt("lv") > 0) {
							json.element("price", zar.getInt("monhtmoney")+zar.getInt("lv4"));
						}else {
							json.element("price", zar.getInt("monhtmoney"));
						}*/
						json.element("data", "1");
					}else {
						json.element("data", "-1");
					}
				}
				else{
					json.element("data", "-2");
				}
//			}else {
//				json.element("data", "-2");
//			}
			
			return json;
		}
		
	@Override
	public boolean saveZaUserlist(String id,int coinIn,int Roomcard,String platform) {
		boolean result =false;
		if("JSWX".equals(platform)){
			String sql="update za_users set yuanbao=?,roomcard=? where id=?";
			Object[] objects={coinIn,Roomcard,id};
			  result = sshUtilDao.updObjectBySQL(sql, objects);
		}else{
			String sql="update za_users set coins=?,roomcard=? where id=?";
			Object[] objects={coinIn,Roomcard,id};
			result=sshUtilDao.updObjectBySQL(sql, objects);
		}
		
			return result;
	}


		@Override
		public JSONObject ifUserSign(long userID) {
			
			
			JSONObject data = new JSONObject();
			
			// 获取当前月份
			String time = DateUtils.date3SStr();
			
			// 获取用户签到信息
			PageUtil pageUtil=null;
			String sql = "select id,first,secound,third,month from za_user_sign where userID=? and createtime>=? order by id desc ";
			
			Object[] queryParam = {userID,time};
			JSONArray array = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, queryParam,pageUtil));
			
			if (array.size() > 0) {
				String sql2="select first,secound,third,month from za_user_sign where id=?";
				Object[] objects={array.getJSONObject(0).getLong("id")};
				JSONObject zus = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects));
				data.element("list", zus);
				
			}else{
				String sql2="INSERT INTO za_user_sign (userID,first,secound,third,month,createtime) values (?,?,?,?,?,?)";
				Object[] objects={userID,0,0,0,0,new Date()};
				boolean result = sshUtilDao.updObjectBySQL(sql2, objects);
				
				String sql3="select first,secound,third,month from za_user_sign where userID=?";
				Object[] object3={userID};
				JSONObject zus = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql3, object3));
			
				data.element("list", zus);
			}
			
			return data;
		}
	@Override
	public JSONObject getzasignreward() {
		
		String sql="select firstnode,firstfangka,firstnode,secnode,secmoney,secfangk,thnode,thmoney,thmofangk," +
					"monthmoney,monthfangk,meitmoney,meitfangk,lv1,lv2,lv3,lv3,lv4,type from za_sign_reward where id=? ";
		Object pram [] = {1};
		
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, pram));
	}
	@Override
	public boolean isUserSign(long userID) {
		
		boolean result = true;
		String time = DateUtils.formatDate();
		
		String sql = "select id from sys_sign where userId=? and createtime>=? order by id desc ";
		Object[] queryParam = {userID,time+" 00:00:00"};
		PageUtil pageUtil=null;
		JSONArray array = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, queryParam,pageUtil));
		
		if(array.size()>0){
			result = false;
		}
		
		return result;
	}
	
	
	@Override
	public boolean saveZaUserSignlist(String data, int is, long userID) {
		
		
		String sql = "update za_user_sign set "+data+"=? where userID = ? ";
		
		Object [] parm = {is,userID};
		
		
		
		return sshUtilDao.updObjectBySQL(sql, parm);
	}


	@Override
	public String getsignrew(String id,String platform) {
		// TODO Auto-generated method stub
		JSONObject data=jumpUserSign(Long.valueOf(id),platform);
//		int days = data.getInt("sign");
//		int tianshu=0;
//		if(Dto.stringIsNULL(platform))
//			tianshu= TimeUtil.getNewMMDay();
//		else if("DFBY".equals(platform))
//			tianshu=7;
//		String msg="0";
//		// 获取后台奖励金额
//		String sql2="select * from za_sign_reward where id=? ";
//		Object [] zid = {1};
//		JSONObject zar = DBUtil.getObjectBySQL(sql2, zid);
//		if(days>=zar.getInt("firstnode")&&days<zar.getInt("secnode"))
//			msg="1";
//		else if(!Dto.stringIsNULL(zar.getString("secnode"))&&days==zar.getInt("secnode"))
//			msg="1";
//		else if(!Dto.stringIsNULL(zar.getString("thnode"))&&days==zar.getInt("thnode"))
//			msg="1";
//		else if(zar.getInt("thnode")==tianshu)
//			msg="1";
		return data.getString("canget");
	}


	@Override
	public JSONObject getvip(String id) {
		// TODO Auto-generated method stub
		return null;
	}



/*	@Override
	public boolean upsyssig(JSONObject data) {
		
		String sql = "update za_sign_reward set firstnode=?,firstmoney=?,secnode=?,secmoney=?,thnode=?,thmoney=?,monthmoney=?,type=?,edittime=?";
		Object[] queryParam = {data.getString("firstnode"),data.getString("firstmoney"),data.getString("secnode"),
				data.getString("secmoney"),data.getString("thnode"),data.getString("thmoney"),data.getString("monthmoney"),data.getString("type"),DateUtils.gettimestamp()};
		int s=boolean result = sshUtilDao.updObjectBySQL(sql, queryParam);
		if(s==0)
			return false;
		else
			return true;
	}*/

}
