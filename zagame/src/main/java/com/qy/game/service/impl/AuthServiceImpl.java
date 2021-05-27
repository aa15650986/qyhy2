package com.qy.game.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.qy.game.service.UserAuthService;
import com.qy.game.utils.Dto;
import com.qy.game.ssh.dao.SSHUtilDao;





@Component
@Service
@Transactional
public class AuthServiceImpl implements UserAuthService {

	@Resource
	SSHUtilDao sshUtilDao;
	@Override
	public JSONObject insertUserAuth(long userID,String name,String idCard,String platform) {
		
		JSONObject json = new JSONObject();
		
		String is = "select id,status from za_authentication where (userID = ? or idCard = ? )";
		
		//JSONObject isTrue = DBUtil.getObjectBySQL(is, new Object[]{userID,idCard});
		JSONObject isTrue = JSONObject.fromObject(sshUtilDao.getObjectBySQL(is, new Object[]{userID,idCard}));
		if(!Dto.isNull(isTrue)&&isTrue.getInt("status")!=-1){
			return json.element("status", -1);
		}else{
			String sql3="DELETE from za_authentication where userID=?";
			Object[] obj={userID};
			//DBUtil.executeUpdateBySQL(sql3, obj);
			sshUtilDao.delObjectBySQL(sql3, obj);
			String sql = "insert into za_authentication(userID,realName,idCard,status,createTime,platform) values (?,?,?,?,?,?)";
			
			Object[] param = new Object[]{userID,name,idCard,0,new Date(),platform};
			
			//int result = DBUtil.executeUpdateBySQL(sql, param);
			boolean result = sshUtilDao.updObjectBySQL(sql, param);
			if (result) {
				return json.element("status", 1);
			}else
				
				return json.element("status", 0);
		}
	}

	@Override
	public JSONObject insertUserJoin(long userID, String name, String tel) {
		
		JSONObject json = new JSONObject();
		
		String is = "DELETE  from sys_join_us where userID = ? ";
		
		//DBUtil.executeUpdateBySQL(is, new Object[]{userID});
		sshUtilDao.delObjectBySQL(is, new Object[]{userID});
		
		String sql = "insert into sys_join_us(userID,userName,userTel,status,createTime) values (?,?,?,?,?)";
			
		Object[] parm = new Object[]{userID,name,tel,0,new Date()};
			
		//int result = DBUtil.executeUpdateBySQL(sql, parm);
		sshUtilDao.updObjectBySQL(sql, parm);
			
		json.element("code", "1")
			.element("msg", "提交成功");
		return json;
	}

	@Override
	public JSONObject getUserAuth(long userID) {
		JSONObject json = new JSONObject();
		json.element("code", 0);
		json.element("msg", "");
		
		String sql = "select id,realName,idCard,status from za_authentication where userID = ? order by id desc ";
		
		//JSONArray array = DBUtil.getObjectListBySQL(sql, new Object[]{userID});
		JSONArray array = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{userID}, 1, 5));
		
		if(array.size()>0){
			JSONObject data = array.getJSONObject(0);
			if(data.getInt("status")==0){
				json.element("code", 1);
				json.element("msg", "审核中");
			}else if(data.getInt("status")==1){
				json.element("code", 1);
				json.element("msg", "已认证");
			}
		}
		
		
		return json;
	}

}
