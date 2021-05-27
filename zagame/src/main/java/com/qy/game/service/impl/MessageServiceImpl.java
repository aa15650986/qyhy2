package com.qy.game.service.impl;

import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qy.game.db.DBUtil;
import com.qy.game.service.dating.MessageService;
import com.qy.game.utils.Dto;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.bean.PageUtil;
import com.qy.game.ssh.dao.SSHUtilDao;

@Transactional
@Component
@Service
public class MessageServiceImpl implements MessageService {

	@Resource
	SSHUtilDao sshUtilDao;
	
	@Override
	public JSONArray getActivities(String platform) {
		String tj="id,title,con,logo,image,startTime,endTime,showType,createTime";
		String sql="SELECT "+tj+" FROM za_activity WHERE `status`=1 and platform=?  ORDER BY createTime DESC";
		Object[] params = {platform};
		JSONArray data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params,1,5));
		if(data.size()==0){
			sql="SELECT "+tj+" FROM za_activity WHERE `status`=1 and ISNULL(platform)  ORDER BY createTime DESC";
			data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{},1,5));
		}
		data=TimeUtil.transTimestamp(data, "createTime", "yyyy-MM-dd");
		return data;
	}

	@Override
	public JSONArray getNotice(String platform) {
		String tj="id,title,con,image,strcreateTime,endcreateTime,showType,createTime";
		String sql="SELECT "+tj+" FROM za_message WHERE `status`=1 AND type=1 and platform=? ORDER BY createTime DESC";
		Object[] params = {platform};
		JSONArray data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params,1,10));
		if(data.size()==0){
			sql="SELECT "+tj+" FROM za_message WHERE `status`=1 AND type=1 and  ISNULL(platform) ORDER BY createTime DESC";
			data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{},1,10));
		}
		data=TimeUtil.transTimestamp(data, "createTime", "yyyy-MM-dd");
		return data;
	}

	@Override
	public JSONObject getEjectMessage(String platform) {
		String tj="id,title,con,image,strcreateTime,endcreateTime,showType,createTime";
		String sql="SELECT "+tj+" FROM za_message WHERE `status`=1 AND type=4 and platform=?  ORDER BY createTime DESC LIMIT 1";
		Object[] params = {platform};
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
		if(Dto.isNull(data)){
			sql="SELECT "+tj+" FROM za_message WHERE `status`=1 AND type=4 and ISNULL(platform)  ORDER BY createTime DESC LIMIT 1";
			data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{}));
		}
	//	data=TimeUtil.transTimeStamp(data, "createTime", "yyyy-MM-dd");
		return data;
	}

	@Override
	public JSONArray getRollNotice(String platform) {
		String tj="id,title,con,image,strcreateTime,endcreateTime,showType,createTime";
		String sql="SELECT "+tj+" FROM za_message WHERE `status`=1 AND type=2 and platform=? ORDER BY createTime DESC";
		Object[] params = {platform};
		JSONArray data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params,1,5));
		if(data.size()==0){
			sql="SELECT "+tj+" FROM za_message WHERE `status`=1 AND type=2 and ISNULL(platform) ORDER BY createTime DESC";
			data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{},1,5));
		}
		if(data.size()>0)
			data=TimeUtil.transTimestamp(data, "createTime", "yyyy-MM-dd");
		return data;
	}

	@Override
	public JSONArray getRollImg(String platform) {
		String tj="id,title,con,image,strcreateTime,endcreateTime,showType,createTime";
		String sql="SELECT "+tj+" FROM za_message WHERE `status`=1 AND type=3 and platform=? ORDER BY createTime DESC";
		Object[] params = {platform};
		JSONArray data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params,1,20));
//		if(data.size()==0){
//			 sql="SELECT * FROM za_message WHERE `status`=1 AND type=3 and ISNULL(platform) ORDER BY createTime DESC";
//			 data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params));
//		}
		data=TimeUtil.transTimestamp(data, "createTime", "yyyy-MM-dd");
		return data;
	}

	@Override
	public JSONObject getNoticeInfo(Long id) {
		String tj="id,title,con,image,strcreateTime,endcreateTime,showType,createTime";
		String sql="SELECT "+tj+" FROM za_message WHERE id=? ";
		Object[] params = {id};
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql,params));
		if (Dto.isNull(data)) {
			 sql="SELECT "+tj+" FROM za_message WHERE id=? and ISNULL(platform)";
			 Object[] params2 = {id};
			 data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql,params2));
		}
		/*data=TimeUtil.transTimeStamp(data, "createTime", "yyyy-MM-dd");*/
		return data;
	}

	@Override
	public JSONObject getActivityInfo(Long id) {
		String tj="id,title,con,logo,image,startTime,endTime,showType,createTime";
		String sql="SELECT "+tj+" FROM za_activity WHERE id=? ";
		Object[] params = {id};
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
		if(Dto.isNull(data)){
			 sql="SELECT "+tj+" FROM za_activity WHERE id=? and ISNULL(platform)";
			Object[] params2 = {id,Dto.PLATFORM};
			 data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params2));
		}
		//data=TimeUtil.transTimeStamp(data, "createTime", "yyyy-MM-dd");
		return data;
	}
	
	@Override
	public JSONArray getUserMsg(Long id,String platform) {
		JSONArray data=new JSONArray();
		PageUtil pageUtil=null;
		if(Dto.stringIsNULL(platform))
			data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL("SELECT id,fromUser,toUser,objId,title,con,status,createtime,platform,memo FROM za_feedback "
					+ "WHERE `status`=0 AND toUser=?  ORDER BY createTime DESC", new Object[]{id},pageUtil));
		else
			data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL("SELECT id,fromUser,toUser,objId,title,con,status,createtime,platform,memo FROM za_feedback "
					+ "WHERE (`status`=0 or `status`=1) AND toUser=?  ORDER BY createTime DESC", new Object[]{id},pageUtil));
		data=TimeUtil.transTimestamp(data, "createTime", "yyyy-MM-dd");
		return data;
	}

	@Override
	public JSONObject getFeedBackInfo(Long id) {
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL("SELECT id,fromUser,toUser,objId,title,con,status,createtime,platform,memo FROM za_feedback WHERE id=?", new Object[]{id}));
		data=TimeUtil.transTimeStamp(data, "yyyy-MM-dd", "createTime");
		return data;
	}
	
	@Override
	public boolean setFeedBackStatus(Long id,int sta) {
		
		boolean result=sshUtilDao.updObjectBySQL("UPDATE za_feedback SET `status`=? WHERE id=?", new Object[]{sta,id});
		return result;
		
	}

	@Override
	public JSONArray getZaCoinsPaihang(String type) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select b.id,b.name,b.headimg,a.coins from (select user_id,SUM(coins) coins  from za_coins_rec where " );
		
		if("1".equals(type)){//本周
			String startime = TimeUtil.getMonOrSat(true)+" 00:00:00";
			String endtime = TimeUtil.getMonOrSat(false)+" 23:59:59";
			sql.append(" createTime >'"+startime+"' and createTime <'"+endtime+"'");
		}else{//本月
			
			sql.append(" createTime >'"+TimeUtil.getNowDate("yyyy-MM")+"-01 00:00:00'");
		}
		
		sql.append(" GROUP BY user_id) a ");
		sql.append(" LEFT JOIN za_users b ");
		sql.append(" on a.user_id = b.id  ORDER BY a.coins desc  limit 0,20");
		
		JSONArray array=JSONArray.fromObject(
				sshUtilDao.getObjectListBySQL(sql.toString(), new Object[]{},1,50));
		
		System.out.println(array);
		return array;
		
	}
	
	@Override
	public JSONArray getYuepaihangbang(String type) {
		String sql1="SELECT yuestartTime,yueendTime FROM za_arena where id=1";
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql1, new Object[]{}));
		
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.id,b.name,b.account,b.headimg,a.coins from (select user_id,SUM(coins) coins  from za_coins_rec where " );

		if("1".equals(type)){//本周
			String startime = TimeUtil.getMonOrSat(true)+" 00:00:00";
			String endtime = TimeUtil.getMonOrSat(false)+" 23:59:59";
			sql.append(" createTime >'"+startime+"' and createTime <'"+endtime+"'");
		}else {//本月
			if(!Dto.isNull(data)){
				data=TimeUtil.transTimeStamp(data, "yyyy-MM-dd HH:mm:ss", "yuestartTime");
				data=TimeUtil.transTimeStamp(data, "yyyy-MM-dd HH:mm:ss", "yueendTime");
				sql.append(" createTime >'"+data.getString("yuestartTime")+"' and createTime <'"+data.getString("yueendTime")+"'");
			}
			
		}
		
		sql.append(" GROUP BY user_id) a ");
		sql.append(" LEFT JOIN za_users b ");
		sql.append(" on a.user_id = b.id  ORDER BY a.coins desc ");
		
		JSONArray array=JSONArray.fromObject(
				sshUtilDao.getObjectListBySQL(sql.toString(), new Object[]{},1,20));
		
		System.out.println(array);
		return array;
		
	}
	
	@Override
	public JSONArray getyueCoinsPaihang(String userid) {
		String sql1="select  b.id,b.account,b.headimg,b.`name`,SUM(a.coins) coins from za_coins_rec a LEFT JOIN za_users b on a.user_id = b.id WHERE a.user_id=?";
		Object[] params = {userid};
		JSONArray data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql1, params,1,10));		
		return data;
		
	}

	@Override
	public JSONArray getZaCoinsPaihang2(String platform) {
		// TODO Auto-generated method stub
		String sql="SELECT headimg,id,name,yuanbao,sign FROM za_users where platform=? ORDER BY yuanbao  DESC ";
		Object[] objects={platform};
		JSONArray list=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects,1,3));
		if(list.size()==0){
			 sql="SELECT headimg,id,name,yuanbao,sign FROM za_users where ISNULL(platform) ORDER BY yuanbao  DESC ";
			 list=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{},1,3));
		}
		list=Dto.string_JSONArray(list);
		
		
		return list;
	}
	
	@Override
	public JSONArray getZaCoinsPaihang3(int end) {
		String sql="SELECT headimg,id,name,yuanbao,sign,platform FROM za_users ORDER BY yuanbao  DESC ";
		JSONArray list = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{},1,end+50));
		list=Dto.string_JSONArray(list);
		return list;
	}
	
	@Override
	public JSONArray getZaCoinsPaihang5(String platform) {
		// TODO Auto-generated method stub
		String sql="SELECT headimg,id,name,yuanbao,sign FROM za_users where platform=? ORDER BY yuanbao  DESC ";
		Object[] objects={platform};
		JSONArray list=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects,1,50));
//		if(list.size()==0){
//			 sql="SELECT headimg,id,name,yuanbao,sign FROM za_users where ISNULL(platform) ORDER BY yuanbao  DESC ";
//			 list=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{},1,3));
//		}
		list=Dto.string_JSONArray(list);
		
		
		return list;
	}

	@Override
	public JSONObject getUserPai(Long id) {
		// TODO Auto-generated method stub
		String sql="SELECT id,headimg,account,`name`,yuanbao,sex,sign  FROM za_users where id = ? ";
		Object[] parm = {id};
		JSONObject data =JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, parm));
		JSONObject user =new JSONObject();
		if(data.containsKey("isown") && !Dto.stringIsNULL(data.getString("isown")) ){
			user.element("isown", data.getInt("isown"));
		}else{
			user.element("isown", 0);
		}
		if(data.containsKey("id") && !Dto.stringIsNULL(data.getString("id"))){
			
			user.element("id", data.getLong("id"));
		}
		
		if(data.containsKey("headimg") && !Dto.stringIsNULL(data.getString("headimg"))){
			
			user.element("headimg", data.getString("headimg"));
		}
		
		if(data.containsKey("account") && !Dto.stringIsNULL(data.getString("account"))){
			
			user.element("account", data.getString("account"));
		}
		
		if(data.containsKey("name") && !Dto.stringIsNULL(data.getString("name"))){
			
			user.element("name", data.getString("name"));
		}
		
		if(data.containsKey("yuanbao") && !Dto.stringIsNULL(data.getString("yuanbao"))){
			
			user.element("yuanbao", data.getString("yuanbao"));
		}
		
		if(data.containsKey("sex") && !Dto.stringIsNULL(data.getString("sex"))){
			
			user.element("sex", data.getString("sex"));
		}
		
		if(data.containsKey("sign") && !Dto.stringIsNULL(data.getString("sign"))){
			
			user.element("sign", data.getString("sign"));
		}
		
		String sql2="SELECT count(account) as sort FROM za_users where yuanbao >= ? ";
		Object[] parms = {user.getDouble("yuanbao")};
		JSONObject obj =JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, parms));
		user.element("sort", obj.getInt("sort"));
		return user;
	}

	@Override
	public void upZafeedbackCon(String id) {
		// TODO Auto-generated method stub
		String sql="update za_feedback set status=2 where id=?";
		Object[] objects={id};
		sshUtilDao.updObjectBySQL(sql, objects);
	}
	@Override
	public JSONArray getzhoupaihang(String type) {
		StringBuffer sql = new StringBuffer();
		sql.append("select id,name,account,headimg,score from  za_users where" );
		if("1".equals(type)){//本周
			String startime = TimeUtil.getMonOrSat(true)+" 00:00:00";
			String endtime = TimeUtil.getMonOrSat(false)+" 23:59:59";
			sql.append(" createTime >'"+startime+"' and createTime <'"+endtime+"'");
		}
		sql.append("ORDER BY score desc limit 0,20 " );
		JSONArray array=JSONArray.fromObject(
		sshUtilDao.getObjectListBySQL(sql.toString(), new Object[]{},1,20));
		
		return array;
		
	}
	
	@Override
	public JSONArray getZhouCoinsPaihang(String userid) {
		String sql="select id,name,account,headimg,score from  za_users where id=?";
		Object[] params = {userid};
		JSONArray data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params,1,5));			
		return data;
		
	}

	@Override
	public JSONArray getzaarena() {
		// TODO Auto-generated method stub
		String sql="select endTime,startTime,day,hour,isOpen,description,explanation,status,name,id,gid from za_arena";
		String time=TimeUtil.getNowDate("yyyy-MM-dd HH:mm:ss");
		JSONArray list=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{},1,5));
		Integer type=0;//当前时间是否开启
		for (int i = 0; i < list.size(); i++) {
			JSONObject data=list.getJSONObject(i);	
			if(data.containsKey("endTime")){
				TimeUtil.transTimeStamp(data, "yyyy-MM-dd HH:mm:ss", "endTime");
				if(TimeUtil.isLatter(data.getString("endTime"), time)){
					type=1;
				}else{
					type=0;
				}
			}
			if(data.containsKey("startTime")){
				TimeUtil.transTimeStamp(data, "yyyy-MM-dd HH:mm:ss", "startTime");
				if(!TimeUtil.isLatter(data.getString("startTime"), time)){
					type=1;
				}else{
					type=0;
				}
				
			}
			if(data.containsKey("startTime")&&data.containsKey("endTime")){
				Boolean str=TimeUtil.isLatter(data.getString("startTime"), time);
				Boolean end=TimeUtil.isLatter(data.getString("endTime"), time);
				if(!str&&end){
					type=1;
				}else{
					type=0;
				}
			}
			
			data.element("type", type);
		}
		return list;
	}

	@Override
	public JSONArray getZaCoinsPaihang4() {
		// TODO Auto-generated method stub
		String sql = "select `key` from base_domain";
		JSONArray keyList = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{}, new PageUtil()));
		JSONArray userList = new JSONArray();
		for (int i = 0; i < keyList.size(); i++) {
			sql="SELECT headimg,id,name,yuanbao,sign,platform FROM za_users where platform=? ORDER BY yuanbao  DESC ";
			JSONArray list=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{keyList.getJSONObject(i).getString("key")},1,3));
			userList.addAll(list);
		}
		return userList;
	}
}
