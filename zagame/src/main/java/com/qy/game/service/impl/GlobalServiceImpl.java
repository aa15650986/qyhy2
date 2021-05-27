package com.qy.game.service.impl;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qy.game.service.GlobalService;
import com.qy.game.utils.Dto;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.bean.PageUtil;
import com.qy.game.ssh.dao.SSHUtilDao;

@Component
@Service
@Transactional
public class GlobalServiceImpl implements GlobalService {

	@Resource
	SSHUtilDao sshUtilDao;
	
	@Override
	public JSONObject getWXShare(int gid, int type, String platform) {
		
		String sql="select title,text,url,imagePath from za_shareinfo where gid=? and type=? and platform=?";
		//return DBUtil.getObjectBySQL(sql, new Object[]{gid, type, platform});
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{gid, type, platform}));
	}

	@Override
	public JSONArray getUserGameLogList(long uid, int gid) {
		
		String sql="select zu.id,zu.gid,zu.user_id,zu.gamelog_id,zu.result,zu.createtime,zg.user_id0,zus.name,zu.room_no,concat_ws('-',zu.room_no,zu.game_index) as roomindex " +
				" from za_usergamelogs zu " +
				" LEFT JOIN za_gamerooms zg on zg.id=zu.room_id " +
				" LEFT JOIN za_users zus  on zus.id=zg.user_id0 "+
				" where zu.gid=? and zu.user_id=? and zg.roomtype=0 GROUP BY zu.createtime order by zu.id desc LIMIT 20";
		//return DBUtil.getObjectListBySQL(sql, new Object[]{gid, uid});
		PageUtil pageUtil = null;
		return TimeUtil.listToJSONArray(JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{gid, uid}, pageUtil)));
	}

	@Override
	public JSONArray getUserGameList(long uid, int gid) {		
		String sql="SELECT id as roomindex,room_no,user_name0 as name,game_count,createtime  FROM za_gamerooms where game_id=? and (roomtype=0 or roomtype=2 ) and  (user_id0=? or user_id1=? or user_id2=? or user_id3=? or user_id4=? or user_id5=? or user_id6=? or user_id7=? or user_id8=? or user_id9=?) order by id desc LIMIT 20";
		Object[] objects={gid,uid,uid,uid,uid,uid,uid,uid,uid,uid,uid};
		//JSONArray list =DBUtil.getObjectListBySQL(sql, objects);
		PageUtil pageUtil = null;
		JSONArray list = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects, pageUtil),TimeUtil.Timestampzh());
		String id="";
		for (int i = 0; i < list.size(); i++) {
			id+=""+list.getJSONObject(i).getString("roomindex")+",";
		}
//		JSONArray data=new JSONArray();
//		if(!Dto.stringIsNULL(id)){
//			String sql2="select * from room_zzj where roomindex in ("+id.substring(0, id.length()-1)+") and result <> '[]' ORDER BY roomindex DESC";
//			 data=DBUtil.getObjectListBySQL(sql2, new Object[]{});
//		}
		
		
		
		
		
		list=hqzzj(list,id);
		return list;
		
	}

	@Override
	public JSONArray getActivityList() {
		
		String sql="select id,title from za_activity order by createTime desc";
		//return DBUtil.getObjectListBySQL(sql, new Object[]{});
		PageUtil pageUtil = null;
		return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{}, pageUtil));
	}

	@Override
	public JSONObject getActivityDetail(long id) {
		
		String sql="select title,con from za_activity where id=?";
		//return DBUtil.getObjectBySQL(sql, new Object[]{id});
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{id}));
	}

	@Override
	public JSONObject getGameLog(int gid, String visitcode) {
		
		String sql="select action_records,base_info,game_index,result from za_gamelogs where gid=? and visitcode=?";
		//return DBUtil.getObjectBySQL(sql, new Object[]{gid, visitcode});
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{gid, visitcode}));
	}

	@Override
	public JSONObject getGameLogById(long glog_id) {
		
		String sql="select room_no,gid,base_info,game_index,result,action_records from za_gamelogs where id=?";
		//return DBUtil.getObjectBySQL(sql, new Object[]{glog_id});
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{glog_id}));
	}

	@Override
	public JSONObject getGlobalInfo(String url) {
		
		String sql="select version,roomcard,coins,yuanbao,platform from za_globalinfo where platform=?";
		Object[] objects={url};
		//JSONObject data=DBUtil.getObjectBySQL(sql, objects);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		if(Dto.isNull(data)){
			 sql="select version,roomcard,coins,yuanbao,platform from za_globalinfo ";
			 Object[] objects2={};
			 //data=DBUtil.getObjectBySQL(sql, objects2);
			 data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects2));
		}
		return data;
	}

	@Override
	public JSONObject getVersionInfo(String version) {
		
		String sql="select version,isshop,isvisiter,ismemu,url,platform,mch,`key`,ThirdPay,veUrl,memo from za_version where version=?";
		//return DBUtil.getObjectBySQL(sql, new Object[]{version});
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{version}));
	}
	
	@Override
	public JSONObject getVersionInfo_new(String platform) {
		
		String sql="select version,isshop,isvisiter,ismemu,url,platform,memo from za_version where platform=?";
		//return DBUtil.getObjectBySQL(sql, new Object[]{version});
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{platform}));
	}

	@Override
	public JSONObject getuserlogsInfo(long id) {
		
		String sql="select gamelog_id,result from za_usergamelogs where id=?";
		//return DBUtil.getObjectBySQL(sql, new Object[]{id});
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{id}));
	}
	@Override
	public JSONObject getgamelogsInfo(long id) {
		
		String sql="select visitcode,room_no from za_gamelogs where id=?";
		//return DBUtil.getObjectBySQL(sql, new Object[]{id});
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{id}));
	}

	@Override
	public JSONObject getGlobalVersion(String platform) {
		String sql="select version from za_version where platform = ? order by id desc ";
		//return DBUtil.getObjectBySQL(sql, new Object[]{platform});
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{platform}));
	}

	@Override
	public JSONArray getzausergamelogs(String roonid) {
		// TODO Auto-generated method stub
		String sql="SELECT id,gamelog_id,room_no,result,createtime,game_index from za_usergamelogs zu where room_id=?  GROUP BY zu.game_index";//GROUP BY zu.gamelog_id
		//return DBUtil.getObjectListBySQL(sql, new Object[]{roonid});
		PageUtil pageUtil = null;
		JSONArray array = TimeUtil.listToJSONArray(JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{roonid}, pageUtil)));
		for (int i = 0; i < array.size(); i++) {
			array.getJSONObject(i).put("gamelog_id", String.valueOf(array.getJSONObject(i).getLong("gamelog_id")));
		}
		return array;
	}

	@Override
	public JSONObject getuserlogs2(long id) {
		// TODO Auto-generated method stub
		String sql="SELECT id as roomindex,room_no,createtime  FROM za_gamerooms where id=?";
		Object[] objects={id};
		//JSONArray list =DBUtil.getObjectListBySQL(sql, objects);
		PageUtil pageUtil = null;
		JSONArray list = TimeUtil.listToJSONArray(JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects, pageUtil)));
		String id2="";
		for (int i = 0; i < list.size(); i++) {
			id2+=" zg.room_id="+list.getJSONObject(i).getString("roomindex")+" or";
		}
		list=hqzzj(list, id2);
		return list.getJSONObject(0);
	}
	
	/**
	 * 计算总战绩
	 * @param list
	 * @param id
	 * @return
	 */
	public JSONArray hqzzj(JSONArray list,String id){
		if(list.size()>0){
			String sql2="SELECT gamelog_id,room_id,result FROM za_usergamelogs zu where room_id in  ("+id.substring(0, id.length()-1)+")  GROUP BY gamelog_id ORDER BY id DESC ";
			//JSONArray list2=DBUtil.getObjectListBySQL(sql2, new Object[]{});
			PageUtil pageUtil = null;
			JSONArray list2 = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql2, new Object[]{}, pageUtil));
				for (int i = 0; i < list.size(); i++) {
					JSONArray data=new JSONArray();
					if(list2.size()!=0){
					list.getJSONObject(i).element("gamelog_id", list2.getJSONObject(0).getString("gamelog_id"));
					for (int j = 0; j < list2.size(); j++) {
						if(list2.getJSONObject(j).getInt("room_id")==list.getJSONObject(i).getInt("roomindex")){
							if(data.size()==0){
								data=JSONArray.fromObject(list2.getJSONObject(j).getString("result"));
							}else{
								JSONArray data2=JSONArray.fromObject(list2.getJSONObject(j).getString("result"));
									for (int k = 0; k < data.size(); k++) {
										for (int k2 = 0; k2 < data2.size(); k2++) {
											if(data.getJSONObject(k).getString("player").equals(data2.getJSONObject(k2).getString("player"))){
												//System.out.println("名"+data.getJSONObject(k).getString("player")+"第一次"+data.getJSONObject(k).getInt("score")+"第二次"+data2.getJSONObject(k2).getInt("score"));
												int s=data.getJSONObject(k).getInt("score")+data2.getJSONObject(k2).getInt("score");
												//System.out.println("总的"+s);
												data.getJSONObject(k).element("score", s);
												data2.remove(k2);
												k2--;
										}
										}
									}	
									}
								list2.remove(j);
								j--;
						}
					}
						list.getJSONObject(i).element("result", data);
					}else{
						list.remove(i);
						i--;
					}
				}
				
				}
		return list;
	}

	@Override
	public JSONObject getJesuanSSS(long id) {
		
		//获取房间号信息
		String rommsql = " select room_no,game_id from za_gamerooms where id=? ";
		//JSONObject roomdata = DBUtil.getObjectBySQL(rommsql, new Object[]{id});
		JSONObject roomdata = JSONObject.fromObject(sshUtilDao.getObjectBySQL(rommsql, new Object[]{id}));
		
		// 获取十三水战绩 
		String sql = " select jiesuan,finishtime,game_index from za_gamelogs where room_id=? ORDER BY id DESC LIMIT 1 ";
		//JSONObject jiesuan = DBUtil.getObjectBySQL(sql, new Object[]{id});
		JSONObject jiesuan = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{id}));
		jiesuan=TimeUtil.transTimeStamp(jiesuan, "yyyy-MM-dd HH:mm:SS", "finishtime");
		JSONObject obj = new JSONObject();
		obj.element("roomdata", roomdata);
		obj.element("jiesuan", jiesuan);
		
		return obj;
	}

	@Override
	public JSONArray getUserGameLogList3(long uid, int gid, String type) {
		// TODO Auto-generated method stub
		String sql=" select id,gid,user_id,gamelog_id,result,createtime,room_no from za_usergamelogs where gid=? and user_id=? GROUP BY gamelog_id  order by id desc LIMIT 20";
		return TimeUtil.listToJSONArray(sshUtilDao.getObjectListBySQL(sql, new Object[]{gid, uid}, new PageUtil()));
		
	}

	@Override
	public JSONArray getUserGameList3(long uid, int gid,String type) {
		// TODO Auto-generated method stub
		System.out.println("进入查询战绩"+TimeUtil.getNowDate());
		String sql="SELECT id as roomindex,room_no,user_name0 as name,game_count,createtime  FROM za_gamerooms where game_id=?   ";
		if("1".equals(type)){
			sql=sql+" and (roomtype=0 or roomtype=2) and  (user_id0=? or user_id1=? or user_id2=? or user_id3=? or user_id4=? or user_id5=? or user_id6=? or user_id7=? or user_id8=? or user_id9=?)";
		}else if("2".equals(type)){
			sql=sql+" and roomtype=3 ";
		}else{
			sql=sql+" and (roomtype=3 or roomtype=0)";
		}
		sql=sql+"  order by id desc LIMIT 20";
		Object[] objects={gid,uid,uid,uid,uid,uid,uid,uid,uid,uid,uid};
		//JSONArray list =DBUtil.getObjectListBySQL(sql, objects);
		PageUtil pageUtil = null;
		JSONArray list = TimeUtil.listToJSONArray(JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects, pageUtil)));
		System.out.println("进入查询战绩-第一阶段查询完整"+TimeUtil.getNowDate());
		String id="";
		for (int i = 0; i < list.size(); i++) {
			id+=""+list.getJSONObject(i).getString("roomindex")+",";
		}
		list=hqzzj(list,id);
		return list;
	}

	@Override
	public String getzagame(String type, String val,String gid,String platform) {
		// TODO Auto-generated method stub
		String sql="SELECT opt_key,opt_val FROM za_gamesetting where game_id=? and memo=?";
		Object[] objects={gid,platform};
		//JSONArray data=DBUtil.getObjectListBySQL(sql, objects);
		PageUtil pageUtil = null;
		JSONArray data = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects, pageUtil));
		String text="";
		for (int i = 0; i < data.size(); i++) {
			JSONObject obj=data.getJSONObject(i);
			if(type.equals(obj.getString("opt_key"))){
				JSONArray list=JSONArray.fromObject(obj.getString("opt_val"));
				for (int j = 0; j < list.size(); j++) {
					if(val.equals(list.getJSONObject(j).getString("val"))){
						text=list.getJSONObject(j).getString("name");
					}
				}
			}
		}
		return text;
	}

	@Override
	public JSONObject getGlodSysSetting() {
		String sql="select ipRegisterLimit,onlinePlyaerData from glod_sys_setting where id=?";
		//return DBUtil.getObjectBySQL(sql, new Object[]{1L});
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{1L}));
	}

	@Override
	public JSONArray getgames() {//没有找到应用的
		// TODO Auto-generated method stub
		String sql="select id from za_games ";
		//return DBUtil.getObjectListBySQL(sql, new Object[]{});
		PageUtil pageUtil = null;
		return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[]{}, pageUtil)); 
	}


}
