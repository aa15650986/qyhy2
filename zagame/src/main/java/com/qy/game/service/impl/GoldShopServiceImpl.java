package com.qy.game.service.impl;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qy.game.service.dating.GoldShopBizServer;
import com.qy.game.utils.Dto;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.bean.PageUtil;
import com.qy.game.ssh.dao.SSHUtilDao;

@Service
@TransactionAttribute
@Transactional
public class GoldShopServiceImpl implements GoldShopBizServer{
	
	@Resource
	SSHUtilDao sshUtilDao;

	@Override
	public JSONArray getzamall(JSONObject con) {
		// TODO Auto-generated method stub
		String sql="select * from za_mall where status=1 ";
		if(con.containsKey("platform")){
			sql+=" and platform='"+con.getString("platform")+"'";
		}
		if(con.containsKey("type")&&!Dto.stringIsNULL(con.getString("type"))){
			sql+=" and type="+con.getString("type");
		}
		sql+=" ORDER BY payMoney ASC ";
		Object[] objects={};
		String sql2=sql.replace("*", "id,title,denomination,payMoney,logo");
		//JSONArray data=JSONArray.fromObject(DBUtil.getObjectListBySQL(sql2, objects));
		PageUtil pageUtil = null;
		JSONArray data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql2, objects, pageUtil));
		return data;
	}
	
	
	@Override
	public JSONArray getexchange(JSONObject con) {
		// TODO Auto-generated method stub
		String sql="select * from za_exchange where status=? and platform=?";
		Object[] objects={1,con.getString("platform")};
		String sql2=sql.replace("*", "id,title,denomination,logo,srcDenomination,type");
		//JSONArray data=JSONArray.fromObject(DBUtil.getObjectListBySQL(sql2, objects));
		PageUtil pageUtil = null;
		JSONArray data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql2, objects, pageUtil));
		return data;
	}


	@Override
	public void upsyabaldes(String id, String text) {
		// TODO Auto-generated method stub
		String sql="update user_bal_rec set des=? where id=?";
		Object[] objects={text,id};
		//DBUtil.executeUpdateBySQL(sql, objects);
		sshUtilDao.updObjectBySQL(sql, objects);
	}


	@Override
	public JSONArray getexchangerec(JSONObject con) {
		// TODO Auto-generated method stub
		String sql="select * from za_mall_exchange_rec where userId=? and type=?";
		Object[] objects={con.getString("userId"),Dto.ZAMALLEXCHANGEREC_TYPE_EXCHANGE};
		String sql2=sql.replace("*", "orderCode,titile,createTime,status");
		//JSONArray data=JSONArray.fromObject(DBUtil.getObjectListBySQL(sql2, objects));
		PageUtil pageUtil = null;
		JSONArray data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql2, objects, pageUtil));
		data=TimeUtil.transTimestamp(data, "createTime", "yyyy-MM-dd");
		for (int i = 0; i < data.size(); i++) {
			int sta=data.getJSONObject(i).getInt("status");
			if(sta==1)
				data.getJSONObject(i).element("status","已通过");
			else if(sta==0)
				data.getJSONObject(i).element("status","未审核");
			else if(sta==2)
				data.getJSONObject(i).element("status","未通过");
			else if(sta==3)
				data.getJSONObject(i).element("status","已到帐");
		}
		return data;
	}


	@Override
	public JSONObject getzamall(String id) {
		// TODO Auto-generated method stub
		String sql="select id,title,payMoney,type,denomination from za_mall where id=?";
		Object[] objects={id};
		//return DBUtil.getObjectBySQL(sql, objects);
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
	}


	@Override
	public JSONArray getsysvip() {
		// TODO Auto-generated method stub
		String sql="select id,name,cycle,price,roomcard,coins,text from sys_vip";
		Object[] objects={};
		//JSONArray list=DBUtil.getObjectListBySQL(sql, objects);
		PageUtil pageUtil = null;
		JSONArray list=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects, pageUtil));
		
		return list;
	}


	@Override
	public JSONObject getsysvip(String id) {
		// TODO Auto-generated method stub
		String sql="select id,name,cycle,price,roomcard,coins,text from sys_vip where id=?";
		Object[] objects={id};
		//return DBUtil.getObjectBySQL(sql, objects);
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
	}


	@Override
	public JSONObject getVersion(String platform) {
		String sql="select mch,`key`,ThirdPay,veUrl,thirdUrl from za_version where platform=? ORDER BY id DESC";
		Object[] objects={platform};
		
		//return DBUtil.getObjectBySQL(sql, objects);
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		
	}
	@Override
	public JSONObject getVersion1(int id) {
		String sql="select `key` from za_version where id=?";
		Object[] objects={id};
		
		//return DBUtil.getObjectBySQL(sql, objects);
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		
	}


	@Override
	public String getdomain(String url) {
		
		String sql="select platform from base_domain where domain=?";
		Object[] objects={url};
		//JSONObject data=DBUtil.getObjectBySQL(sql, objects);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		String platform=null;
		if (!Dto.isObjNull(data)) {
			platform=data.getString("platform");
		}else{
			platform="";
		}
		return platform;
	}


}
