package com.qy.game.service.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qy.game.service.HService;
import com.qy.game.utils.Dto;
import com.qy.game.utils.MathDelUtil;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.bean.PageUtil;
import com.qy.game.ssh.dao.SSHUtilDao;

@Service
@TransactionAttribute
@Transactional
public class HWuServiceImpl implements HService{
	
	@Resource
	SSHUtilDao sshUtilDao;

	@Override
	public JSONObject gosysybaserec() {
		// TODO Auto-generated method stub
		String sql="select Managprice from sys_base_set";
		Object[] objects={};
		//JSONObject data=DBUtil.getObjectBySQL(sql,objects);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		return data;
	}

	@Override
	public void upbaseismage(String id) {
		// TODO Auto-generated method stub
		JSONObject data=gosysybaserec();
		String sql="update za_users set isManag=1,roomcard=roomcard-? where id=?";
		Object[] objects={data.getString("Managprice"),id};
		//DBUtil.executeUpdateBySQL(sql, objects);
		sshUtilDao.updObjectBySQL(sql, objects);
	}
	
	@Override
	public void closemage(String id) {
		// TODO Auto-generated method stub
		String sql="update za_users set isManag=0 where id=?";
		Object[] objects={id};
		//DBUtil.executeUpdateBySQL(sql, objects);
		sshUtilDao.updObjectBySQL(sql, objects);
	}

	@Override
	public String addusersendroom(String id, String price) {
		// TODO Auto-generated method stub
		//扣用户
		String sql="update za_users set roomcard=roomcard-? where id=?";
		Object[] objects={price,id};
		//DBUtil.executeUpdateBySQL(sql, objects);
		sshUtilDao.updObjectBySQL(sql, objects);
		
		//增加数据
		String sql2="INSERT INTO user_send_roomcard (userID,price,sta,createTime) values(?,?,?,?)";
		Object[] objects2={id,price,0,new Date()};
		//DBUtil.executeUpdateBySQL(sql2, objects2);
		sshUtilDao.updObjectBySQL(sql2, objects2);
		
		//返回id
		String sql3="select id from user_send_roomcard where userID=? and price=? order by createTime DESC";
		Object[] objects3={id,price};
		//JSONObject data=DBUtil.getObjectBySQL(sql3, objects3);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql3, objects3));
		return data.getString("id");
	}

	@Override
	public JSONObject getusersendroom(String id) {
		// TODO Auto-generated method stub
		String sql="SELECT us.id,zu.headimg,zu.`name`,us.price,us.sta FROM user_send_roomcard us " +
				"	LEFT JOIN za_users zu on zu.id=us.userID " +
				" where us.id=?";
		Object[] objects={id};
		//JSONObject data=DBUtil.getObjectBySQL(sql, objects);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		
		String sql4="select  zu.headimg,zu.`name`,ss.createTime from sys_send_roomcard ss   " +
				" LEFT JOIN za_users zu on zu.id=ss.senduserid  " +
				" where ss.roomid=?";
		Object[] objects4={id};
		//JSONObject d=DBUtil.getObjectBySQL(sql4, objects4);
		JSONObject d = TimeUtil.objectToJSONObject(JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql4, objects4)));
		if(!Dto.isNull(d)){
			data.element("data", d);
		}
		return data;
	}

	@Override
	public JSONObject addsyssendroom(String id, JSONObject user) {
		// TODO Auto-generated method stub
		JSONObject data=getusersendroom(id);
		JSONObject msg=new JSONObject();
		String sql4="select usr.sta,zu.`name`,zu.headimg,ssr.createTime from user_send_roomcard usr " +
				" LEFT JOIN sys_send_roomcard ssr on ssr.roomid=usr.id " +
				" LEFT JOIN za_users zu on zu.id=ssr.senduserid"+
				" where usr.id=?";
		Object[] objects2={id};
		//JSONObject data2=DBUtil.getObjectBySQL(sql4, objects2);
		JSONObject data2 = TimeUtil.objectToJSONObject(JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql4, objects2)));
		if(data2.getInt("sta")==0){
			//加用户的房卡
			String sql="update za_users set roomcard=roomcard+? where id=?";
			Object[] objects={data.getString("price"),user.getString("id")};
			//DBUtil.executeUpdateBySQL(sql, objects);
			sshUtilDao.updObjectBySQL(sql, objects);
			
			//修改发送房卡数据的状态
			String sql2="update user_send_roomcard set sta=1 where id=?";
			//DBUtil.executeUpdateBySQL(sql2, objects2);
			sshUtilDao.updObjectBySQL(sql2, objects2);
			
			//添加领取的记录
			String sql3="INSERT INTO sys_send_roomcard (roomid,senduserid,createTime) values (?,?,?)";
			Object[] objects3={id,user.getString("id"),new Date()};
			//DBUtil.executeUpdateBySQL(sql3, objects3);
			sshUtilDao.updObjectBySQL(sql3, objects3);
			msg.element("code", "1")
			.element("msg", "领取成功")
			.element("name", user.getString("name"))
			.element("img", user.getString("headimg"))
			.element("time",String.valueOf(TimeUtil.getNowDate("yyyy-MM-dd HH:mm:ss")));
		}else{
			TimeUtil.transTimeStamp(data2, "yyyy-MM-dd HH:mm:ss", "createTime");
			msg.element("code", "0")
				.element("msg", "该房卡包已被领取")
				.element("name", data2.getString("name"))
				.element("img", data2.getString("headimg"))
				.element("time",data2.getString("createTime"));
		}
		return msg;
	}

	@Override
	public JSONObject getusersysroom(String id) {
		// TODO Auto-generated method stub
		JSONObject data=new JSONObject();

		//查看发送房卡的记录
		String sql="select createTime,sta,id,price from user_send_roomcard where userID=? order by createTime desc";
		Object[] objects={id};
		//JSONArray data1=DBUtil.getObjectListBySQL(sql, objects);
		PageUtil pageUtil = null;
		JSONArray data1 = TimeUtil.listToJSONArray(JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects, pageUtil)));
		
		//查看领取的记录
		String sql2="select ss.createTime,zu.`name`,us.price,us.sta from sys_send_roomcard ss  " +
				" LEFT JOIN za_users zu on zu.id=ss.senduserid " +
				" LEFT JOIN user_send_roomcard us ON us.id=ss.roomid " +
				"where ss.senduserid=? order by ss.createTime desc";
		Object[] objects2={id};
		//JSONArray data12=DBUtil.getObjectListBySQL(sql2, objects2);
		JSONArray data12 = TimeUtil.listToJSONArray(JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql2, objects2, pageUtil)));
		TimeUtil.transTimestamp(data1,"createTime", "yyyy/MM/dd");
		TimeUtil.transTimestamp(data12,"createTime", "yyyy/MM/dd");
		data.element("userroom", data1)
			.element("sysroom", data12);
		return data;
	}

	@Override
	public JSONObject addcswxyqhy(String id, String userID) {
		// TODO Auto-generated method stub
		JSONObject msg=new JSONObject();
		String sql2="select id from za_member where userID=? and jsuserid=?";
		Object[] objects2={id,userID};
		//JSONObject data=DBUtil.getObjectBySQL(sql2, objects2);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects2));
		if(Dto.isNull(data)){
		//添加加入记录	
			String sql="INSERT INTO za_member (userID,jsuserid,sta,createTime) values (?,?,?,?)";
			Object[] objects={id,userID,0,new Date()};
			//DBUtil.executeUpdateBySQL(sql, objects);
			sshUtilDao.updObjectBySQL(sql, objects);
			msg.element("code", 1)
				.element("msg", "申请审核中");
		}else{
			msg.element("code", 0)
				.element("msg", "请勿重复申请");
		}
	return msg;
	}

	@Override
	public JSONArray gozamembr(String id) {
		// TODO Auto-generated method stub
		String sql="SELECT za.id,zu.`name`,zu.headimg,za.sta FROM za_member za " +
				" LEFT JOIN za_users zu on za.jsuserid=zu.id " +
				" where za.userID=? ORDER BY za.sta DESC";
		Object[] objects={id};
		//JSONArray list=DBUtil.getObjectListBySQL(sql, objects);
		PageUtil pageUtil = null;
		JSONArray list = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects, pageUtil));
		return list;
	}

	@Override
	public void upzamembr(String id, String sta) {
		// TODO Auto-generated method stub
		Object[] objects={id};
		if("1".equals(sta)){
			String sql="update za_member set sta=1 where id=?";
			//DBUtil.executeUpdateBySQL(sql, objects);
			sshUtilDao.updObjectBySQL(sql, objects);
		}else{
			String sql="DELETE FROM za_member where id=?";
			//DBUtil.executeUpdateBySQL(sql, objects);
			sshUtilDao.updObjectBySQL(sql, objects);
		}
		
	}

	@Override
	public JSONArray tozamembr(String id) {
		// TODO Auto-generated method stub
		String sql="SELECT za.id,zu.`name`,zu.headimg,zu.roomcard FROM za_member za " +
				" LEFT JOIN za_users zu on za.jsuserid=zu.id " +
				" where za.userID=? and sta=1";
		Object[] objects={id};
		//JSONArray list=DBUtil.getObjectListBySQL(sql, objects);
		PageUtil pageUtil = null;
		JSONArray list = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects, pageUtil));
		return list;
	}

	@Override
	public void upzamemroom(String id, String userid, String shu) {
		// TODO Auto-generated method stub
		//更新被转移的
		String sql="update za_users set roomcard=roomcard+? where id=?";
		Object[] objects={shu,id};
		//DBUtil.executeUpdateBySQL(sql, objects);
		sshUtilDao.updObjectBySQL(sql, objects);
		
		//更新转移的
		String sql2="update za_users set roomcard=roomcard-? where id=?";
		Object[] objects2={shu,userid};
		//DBUtil.executeUpdateBySQL(sql2, objects2);
		sshUtilDao.updObjectBySQL(sql2, objects2);
	}

	@Override
	public JSONArray H5gamerooms(String id) {
		// TODO Auto-generated method stub
		String sql="select room_no,createTime from za_gamerooms where user_id0 =? ";
		Object[] objects={id};
		//JSONArray data=DBUtil.getObjectListBySQL(sql, objects);
		PageUtil pageUtil = null;
		JSONArray data = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects, pageUtil));
		TimeUtil.transTimestamp(data, "createTime", "yyyy-MM-dd HH:mm:ss");
		return data;
	}

	@Override
	public JSONObject H5member(String roon, String id) {
		// TODO Auto-generated method stub
		JSONObject msg=new JSONObject();
		String sql="select user_id0 from za_gamerooms where room_no=?";
		Object[] objects={roon};
		//JSONObject data=DBUtil.getObjectBySQL(sql, objects);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		
		String sql2="select id from za_member where userID=? and jsuserid=?";
		Object[] objects2={data.getString("user_id0"),id};
		//JSONObject data2=DBUtil.getObjectBySQL(sql2, objects2);
		JSONObject data2 = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects2));
		
		if(Dto.isNull(data2)){
			msg.element("code", 0);
		}else
			msg.element("code", 1);
		return msg;
	}

	@Override
	public void H5addmember(String roon, String id) {
		// TODO Auto-generated method stub
		String sql="select user_id0 from za_gamerooms where room_no=?";
		Object[] objects={roon};
		//JSONObject data=DBUtil.getObjectBySQL(sql, objects);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		
		String sql2="INSERT INTO za_member (userID,jsuserid,sta,createTime) values (?,?,?,?)";
		Object[] objects2={data.getString("user_id0"),id,1,new Date()};
		//DBUtil.executeUpdateBySQL(sql2, objects2);
		sshUtilDao.updObjectBySQL(sql2, objects2);
	}

	@Override
	public JSONArray getUserListForTopCoin(JSONObject param) {
		String sql="select account,`name`,coins,headimg from za_users order by coins desc limit ?,?";
		Object[] queryparam={param.getInt("start"),param.getInt("end")};
		//JSONArray array=DBUtil.getObjectListBySQL(sql, queryparam);
		PageUtil pageUtil = null;
		JSONArray array = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, queryparam, pageUtil));
		return array;
	}

	@Override
	public JSONObject getGameCountByGid(JSONObject param) {
		String start=TimeUtil.getMonOrSat(true)+" 00:00:00";
		String end=TimeUtil.getMonOrSat(false)+" 23:59:59";
		
		//游戏总局数
		String sql=" select count(id) as count from za_usergamelogs where user_id=? and createtime>=? and createtime<=? and gid=?";
		Object[] queryparam={param.getInt("id"),start,end,param.getInt("gid")};
		//JSONObject total=DBUtil.getObjectBySQL(sql, queryparam);
		JSONObject total = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, queryparam));
		
		//胜利的游戏局数
		int win=0;
		sql=" select zul.result,zgr.user_id0,zgr.user_name0,zgr.user_id1,zgr.user_name1,zgr.user_id2,zgr.user_name2,zgr.user_id3,zgr.user_name3,zgr.user_id4,zgr.user_name4," +
			" zgr.user_id5,zgr.user_name5,zgr.user_id6,zgr.user_name6,zgr.user_id7,zgr.user_name7,zgr.user_id8,zgr.user_name8,zgr.user_id9,zgr.user_name9 " +
			" from za_usergamelogs zul left join za_gamerooms zgr on zgr.room_no=zul.room_no" +
			" where zul.user_id=? and zul.createtime>=? and zul.createtime<=? and zul.gid=?";
		//JSONArray gameList=DBUtil.getObjectListBySQL(sql, queryparam);
		PageUtil pageUtil = null;
		JSONArray gameList = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, queryparam, pageUtil));
		for(int i=0;i<gameList.size();i++){
			JSONObject tmp=gameList.getJSONObject(i);
			String indexName="";
			int index=-1;
			for(int j=0;j<10;j++){
				if(tmp.containsKey("user_id"+j) && tmp.getInt("user_id"+j)==param.getInt("id"))
				{
					indexName=tmp.getString("user_name"+j);
					index=tmp.getInt("user_id"+j);
					break;
				}
			}
			if(indexName.equals("") || index==-1) continue;
			JSONArray result=tmp.getJSONArray("result");
			for(int k=0;k<result.size();k++){
				
				if((result.getJSONObject(k).containsKey("index") && result.getJSONObject(k).getInt("index")==index) ||
				   (result.getJSONObject(k).containsKey("player") && result.getJSONObject(k).getString("player").equals(indexName))){
					int isWin=0;
					try {
						isWin=result.getJSONObject(k).containsKey("win")?
							  result.getJSONObject(k).getInt("win"):
							  result.getJSONObject(k).getInt("isWinner");
					} catch (Exception e) {
						isWin=0;
					}
					win=win+(isWin==-1?0:isWin);
					break;
				}
			}
		}
		if(total.getInt("count")==0)
			return new JSONObject().element("total", total).element("win", win).element("persent", 0);
		else
			return new JSONObject().element("total", total).element("win", win).element("persent", MathDelUtil.halfUp(Double.valueOf(win)/total.getDouble("count"), 2) * 100);
	}

	@Override
	public JSONArray getCanUseGameList(int sta) {
		String sql="select id,logo from za_games where status=?";
		Object[] param={sta};
		//JSONArray array=DBUtil.getObjectListBySQL(sql, param);
		PageUtil pageUtil = null;
		JSONArray array = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, param, pageUtil));
		return array;
	}

	@Override
	public boolean isVisited(Long ownerID, Long userID) {
		String sql=" select count(id) as count from za_member where userID=? and jsuserid=? and sta=?";
		Object[] param={ownerID,userID,1};
		//JSONObject obj=DBUtil.getObjectBySQL(sql, param);
		JSONObject obj = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, param));
		if(obj.getInt("count")>0) return true;
		else return false;
	}

	

}
