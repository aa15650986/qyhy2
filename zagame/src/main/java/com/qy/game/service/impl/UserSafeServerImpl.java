package com.qy.game.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qy.game.service.dating.UserSafeServerBiz;
import com.qy.game.utils.Dto;
import com.qy.game.utils.HttpReqUtil;
import com.qy.game.ssh.dao.SSHUtilDao;
@Service
@TransactionAttribute
@Transactional
public class UserSafeServerImpl implements UserSafeServerBiz{

	@Resource
	SSHUtilDao sshUtilDao;
	@Override
	public JSONObject getbaseSafe(String id) {
		// TODO Auto-generated method stub
		String sql="select vip,password,safeprice from za_users where id=?";
		Object[] objects={id};
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		JSONObject obj=new JSONObject();
		obj.element("msg", "成功")
			.element("code", "1");
		if(data.getInt("vip")==0){
			obj.element("msg", "2")
			.element("code", "0");
		}
		else if(!data.containsKey("password")){
			obj.element("msg", "3")
			.element("code", "0");
		}
		return obj;
	}

	@Override
	public JSONObject upbaseSafe(String id, String pass,String tel) {
		// TODO Auto-generated method stub
		String sql="select id from za_users where tel=?";
		Object[] objects={tel};
		JSONObject obj=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		JSONObject msg=new JSONObject();
		if(Dto.isNull(obj)||id.equals(obj.getString("id"))){
			String sql2="update za_users set password=?,tel=? where id=?";
			Object[] objects2={pass,tel,id};
			sshUtilDao.updObjectBySQL(sql2, objects2);
			msg.element("code", "1")
				.element("msg", "设置成功");
		}else{
			msg.element("code", "0")
			.element("msg", "该手机已被注册");
		}
		return msg;
	}

	@Override
	public void upbasesafeprice(String id, String pirce) {
		// TODO Auto-generated method stub
		String sql="update za_users set coins=coins-?,safeprice=safeprice+? where id=?";
		Object[] objects={pirce,pirce,id};
		sshUtilDao.updObjectBySQL(sql, objects);
	}

	@Override
	public JSONObject upbasesafepricejin(JSONObject data) {
		// TODO Auto-generated method stub
		JSONObject msg=new JSONObject();
		JSONObject obj=new JSONObject();
		if(data.containsKey("platform")){
			if("JHG".equals(data.getString("platform"))){
				String sql2="select safe as password from za_users where id=?";
				Object[] objects2={data.getString("id")};
				 obj=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects2));
			}
				
		}else{
			String sql2="select password from za_users where id=?";
			Object[] objects2={data.getString("id")};
			 obj=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects2));
		}
		if(obj.containsKey("password")){
			if(obj.getString("password").equals(data.getString("safe"))){
				String sql="update za_users set coins=coins+?,safeprice=safeprice-? where id=?";
				Object[] objects={data.getString("price"),data.getString("price"),data.getString("id")};
				sshUtilDao.updObjectBySQL(sql, objects);
				msg.element("msg", "取出成功")
				.element("code", "1");
			}
			else{
				msg.element("msg", "密码错误")
				.element("code", "0");
			}
		}else{
			msg.element("msg", "未设置取款密码")
			.element("code", "0");
		}
		return msg;
	}

	@Override
	public JSONObject upbaseSafexg(JSONObject data) {
		// TODO Auto-generated method stub
		String type="password";
		if(data.containsKey("type")){
			type=data.getString("type");
		}
		String sql="select password,safe from za_users where id=?";
		Object[] objects={data.getString("id")};
		JSONObject obj=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		
		JSONObject msg=new JSONObject();
		if(obj.containsKey(type)&&!Dto.stringIsNULL(obj.getString(type))){
			if(obj.getString(type).equals(data.getString("safe1"))){
				String sql2="update za_users set "+type+"=? where id=?";
				Object[] objects2={data.getString("safe2"),data.getString("id")};
				sshUtilDao.updObjectBySQL(sql2, objects2);
				msg.element("msg", "修改成功")
					.element("code", "1");
			}else{
				msg.element("msg", "旧密码错误")
					.element("code", "0");
			}
		}else{
			msg.element("msg", "您未设置保险柜密码")
				.element("code", "0");
		}
		return msg;
	}

	@Override
	public JSONObject jcusertel(String id, String tel,String type) {
		// TODO Auto-generated method stub
		String lei="password";
		if(!Dto.stringIsNULL(type)){
			lei=type;
		}
		String sql="select tel,password,safe from za_users where id=?";
		Object[] objects={id};
		JSONObject obj=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		JSONObject con=new JSONObject();
		if(obj.getString("tel").equals(tel)){
			if(obj.containsKey(lei)&&!Dto.stringIsNULL(obj.getString(lei))){
				String msg=String.format("【恋装装饰】保险柜密码：%s。请勿泄露给他人。如非本人操作，请联系客服4008260381。",obj.getString(lei));
				try {
					String url = String.format("http://182.254.141.209:8888/Modules/Interface/http/Iservicesbsjy.aspx?flag=sendsms&loginname=lianzhuang&password=aI6pCJdfMy&p=%s&c=%s&d=2012-07-27",
									tel,URLEncoder.encode(msg,"utf-8") );
					HttpReqUtil.doGet(url, "", "utf-8");
					con.element("code", "1")
					.element("msg", "短信发送成功");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					con.element("code", "0")
						.element("msg", "短信发生错误");
				}
			}else{
				con.element("code", "0")
				.element("msg", "您未设置保险柜密码");
			}
			
		}else{
			con.element("code", "0")
				.element("msg", "绑定的手机号不正确");
		}
		return con;
	}

	
	

}
