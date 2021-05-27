package com.qy.game.service.impl;

import java.sql.Timestamp;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qy.game.service.dating.FeedBalServer;
import com.qy.game.utils.Dto;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.dao.SSHUtilDao;
@Service
@TransactionAttribute
@Transactional
public class FeedBalServiceImpl implements FeedBalServer{

	@Resource
	SSHUtilDao sshUtilDao;
	
	@Override
	public void addsysfeedback(JSONObject con) {
		// TODO Auto-generated method stub
		String sql="INSERT INTO za_feedback (fromUser,toUser,title,con,status,createTime) values (?,?,?,?,?,?)";
		Object[] objects={con.getString("userid"),"0","反馈",con.getString("text"),0,Timestamp.valueOf(TimeUtil.getNowDate())};
		//DBUtil.executeUpdateBySQL(sql, objects);
		sshUtilDao.updObjectBySQL(sql, objects);
	}

	@Override
	public String upbaseroomcard(JSONObject con) {
		// TODO Auto-generated method stub
		String type=con.getString("type");
		String sql3="select "+type+" from za_users where id=?";
		Object[] objects3={con.getString("userid")};
		//JSONObject data=DBUtil.getObjectBySQL(sql3,objects3);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql3, objects3));
		
		String sql4="select id from za_users where account=?";
		Object[] objects4={con.getString("jsuserid")};
		//JSONObject data4=DBUtil.getObjectBySQL(sql4,objects4);
		JSONObject data4 = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql4, objects4));
		if(Dto.isNull(data4)){
			type="2";
			return type;
		}else{
			if(data.getDouble(type)-con.getDouble("price")>=0){
			
			//加被赠送的
				String sql="update za_users set "+type+"="+type+"+? where account=?";
				Object[] objects={con.getString("price"),con.getString("jsuserid")};
				//DBUtil.executeUpdateBySQL(sql, objects);
				sshUtilDao.updObjectBySQL(sql, objects);
				
				//扣赠送的
				String sql2="update za_users set "+type+"="+type+"-? where id=?";
				Object[] objects2={con.getString("price"),con.getString("userid")};
				//DBUtil.executeUpdateBySQL(sql2, objects2);
				sshUtilDao.updObjectBySQL(sql2, objects2);
				return "1";
			}else
				return "0";
			}
	}

	@Override
	public JSONObject upuserBindphone(JSONObject con) {
		// TODO Auto-generated method stub
		JSONObject msg=new JSONObject();
		
		String sql2="select id from za_users where tel=?";
		Object[] objects2={con.getString("tel")};
		//JSONObject obj=DBUtil.getObjectBySQL(sql2, objects2);
		JSONObject obj = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects2));
		if(Dto.isNull(obj)||con.getString("id").equals(obj.getString("id"))){
			String sql="update za_users set tel=?,password=? where id=?";
			Object[] objects={con.getString("tel"),con.getString("pass"),con.getString("id")};
			//DBUtil.executeUpdateBySQL(sql, objects);
			sshUtilDao.updObjectBySQL(sql, objects);
			msg.element("code", "1")
			.element("msg", "绑定成功");
		}else{
			msg.element("code", "0")
			.element("msg", "该手机号已绑定过");
		}
		return msg;
	}

	@Override
	public JSONObject cxsfbdg(JSONObject data) {
		// TODO Auto-generated method stub
		JSONObject msg=new JSONObject();
		msg.element("code", "0");
			
		if("tel".equals(data.getString("type"))){//判断是否绑定过电话
			String sql="select id from za_users where id=?";
			Object[] objects={data.getString("id")};
			//JSONObject obj=DBUtil.getObjectBySQL(sql, objects);
			JSONObject obj = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
			if(!Dto.isNull(obj)){
				msg.element("code", "1")
					.element("msg", "您已绑定过");
			}
		}
		else if("sID".equals(data.getString("type"))){//身份证
			String sql="select id,status from za_authentication where userId=?";
			Object[] objects={data.getString("id")};
			//JSONObject obj=DBUtil.getObjectBySQL(sql, objects);
			JSONObject obj = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
			if(!Dto.isNull(obj)){
				msg.element("code", "1")
					.element("msg", "您已实名过");
				if(obj.getInt("status")==-1){
					msg.element("code", "-1")
					.element("msg", "未通过");
				}
			}
		} else if("join".equals(data.getString("type"))){//加盟
			String sql="select id,status from sys_join_us where userId=?";
			Object[] objects={data.getString("id")};
			//JSONObject obj=DBUtil.getObjectBySQL(sql, objects);
			JSONObject obj = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
			if(!Dto.isNull(obj)){
				if(obj.getInt("status")==-1){
					msg.element("code", "-1");
				}else if(obj.getInt("status")==1)
					msg.element("code", "1");
				else{
					msg.element("code", "2");
				}
			}
		}
		return msg;
	}

	@Override
	public JSONArray getfeedback(String id) {
		// TODO Auto-generated method stub
		String sql="select id,title,con,status from za_feedback where fromUser=? or toUser=?";
		Object[] objects={id,id};
		//return DBUtil.getObjectListBySQL(sql, objects);
		return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects, 1, 10));
	}

	
	
}
