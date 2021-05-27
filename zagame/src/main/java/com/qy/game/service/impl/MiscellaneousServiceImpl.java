package com.qy.game.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qy.game.service.MiscellService;
import com.qy.game.utils.Dto;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.bean.PageUtil;
import com.qy.game.ssh.dao.SSHUtilDao;
@Component
@Service
@Transactional
public class MiscellaneousServiceImpl implements MiscellService{

	@Resource
	SSHUtilDao sshUtilDao;
	@Override
	public JSONObject systurntab(String id,String platform) {
		// TODO Auto-generated method stub
		String sql="select zpgs,mtmfcs,dycsmomey,zfcs,momy,zdgl,zggl,zfcs,decsmomey from sys_turntable";
		Object[] objects={};
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		//获取转盘中的数据
		JSONArray list=zaturntab(data);
		data.element("list", list);
	
		String s=TimeUtil.getNowDate("yyyy-MM-dd");
		String sql2="select cs from user_turntable where userID=? and creatTime>='"+s+" 00:00:00"+"' and creatTime<='"+s+" 23:59:59"+"'";
		Object[] objects2={id};
		JSONObject data2=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects2));
		
		if(Dto.stringIsNULL(platform)){//明明
			if(Dto.isNull(data2)){//代表今天第一次进
				data.element("cs",0);
				if(data.getInt("mtmfcs")>0)
					data.element("price", 0);
				else
					data.element("price", data.getInt("dycsmomey"));
			}
			else{
				data.element("cs", data2.getInt("cs")-data.getInt("mtmfcs")<0?0:data2.getInt("cs")-data.getInt("mtmfcs"));
				if(data2.getInt("cs")-data.getInt("mtmfcs")<0)//免费次数没用完
					data.element("price", 0);
				else{
					if(data2.getInt("cs")-data.getInt("mtmfcs")==0)//用玩免费次数，要进行第一次花钱
						data.element("price", data.getInt("dycsmomey"));
					else {
						data.element("price", data.getInt("decsmomey"));
					}
				}
			}
		}else if("JHG".equals(platform)){//黄金冠
			String sql3="select Losevalue from za_users where id=?";
			JSONObject data3=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql3, objects2));
			if(Dto.isNull(data2)){//代表今天第一次进
				data.element("cs",0)
					.element("zfcs",data.getInt("zfcs"));//每天可用次数
			}else{
				data.element("cs", data2.getInt("cs"))
				.element("zfcs",data.getInt("zfcs")-data2.getInt("cs"));
			}
			data.element("price", data.getInt("dycsmomey"))
				.element("Losevalue", data3.getInt("Losevalue"));
		}
		
		
		return data;
	}

	
	public JSONArray zaturntab(JSONObject data2) {
		// TODO Auto-generated method stub
		PageUtil pageUtil=null;
		String sql="select id,name,img,type,memy,gl from za_turntable where sta=1  LIMIT ?";
		Object[] objects={data2.getInt("zpgs")};
		JSONArray data=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, objects,pageUtil));
		return data;
	}
	
	public JSONObject zaturntabgl(JSONArray data,JSONObject con,String platform){
		
		JSONObject obj=new JSONObject();
		int random = new Random().nextInt(10000);
		String sql="select id,name,img,type,memy,gl,mome,sta from za_turntable where sta=1  ORDER BY gl DESC";
		if("JHG".equals(platform)){
			sql="select id,name,img,type,memy,gl,mome,sta from za_turntable where sta=1 and Number>0 ORDER BY gl DESC";
		}
		Object[] objects={};
		JSONObject shu=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		if(!Dto.isNull(shu)){
			System.out.println("random:"+random);
			double perB =0;
			double perA = 0;
			int xz=0;
			for (int i = 0; i < data.size(); i++) {
				
				if(i > 0){
					perA += data.getJSONObject(i-1).getDouble("gl");
				}
				 perB = data.getJSONObject(i).getDouble("gl")+perA;
				
				double min = 10000 * perA;
				double max = 10000 * perB;
				//System.out.println( data.getJSONObject(i).getString("id")+"区间："+min+"~"+max +"  百分比："+perA+"~"+perB);
				if(random>min && random<=max){
					obj=data.getJSONObject(i);
					obj.element("xuanzhong", i+1);
				}
				if(shu.getString("id").equals(data.getJSONObject(i).getString("id")))
					xz=i+1;
				
			}
			if(!obj.containsKey("id")){
				obj=shu;
				obj.element("xuanzhong", xz);
			}
		}else{
			obj=null;
		}
		
		return obj;
	}


	@Override
	public JSONObject delturntabmeny(String id,String platform) {
		// TODO Auto-generated method stub
		String s=TimeUtil.getNowDate("yyyy-MM-dd");
		String sql="select id,cs,userID from user_turntable where userID=? and creatTime>='"+s+" 00:00:01"+"' and creatTime<='"+s+" 23:59:59"+"'";
		Object[] objects={id};
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		
		//用户信息
		String sql2="select roomcard,Losevalue from za_users where id=?";
		Object[] objects2={id};
		JSONObject user=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects2));
		//转盘基础数据
		String sql3="select id,zpgs,zfcs,dycsmomey,mtmfcs from sys_turntable";
		Object[] objects3={};
		JSONObject zp=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql3, objects3));
		//转盘商品的数据
		PageUtil pageUtil=null;
		String sql8="select gl,id from za_turntable LIMIT ?";
		if("JHG".equals(platform)){
			sql8="select gl,id from za_turntable where Number>0  LIMIT ?";
		}
		Object[] objects8={zp.getInt("zpgs")};
		JSONArray data8=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql8, objects8,pageUtil));
		//获取选中的产品
		JSONObject sp=zaturntabgl(data8,zp,platform);
		
		JSONObject msg=new JSONObject();
		if(!Dto.isNull(sp)){
			try {
				if(Dto.stringIsNULL(platform)){//明明
					if(Dto.isNull(data)){//今天第一次
						if(user.getInt("roomcard")<zp.getInt("dycsmomey")&&zp.getInt("mtmfcs")==0){
							msg.element("code", "0")
								.element("msg", "钱不够");
						}else{
							String sql6="INSERT INTO user_turntable (userID,cs,creatTime) VALUES (?,?,?)";
							Object[] objects6={id,0,Timestamp.valueOf(TimeUtil.getNowDate())};
							sshUtilDao.updObjectBySQL(sql6, objects6);
							msg=koqian(id,sp,zp,0,zp.getInt("dycsmomey"),platform);
						}
					}else{
						if(data.getInt("cs")<zp.getInt("zfcs")){
							if(data.getInt("cs")-zp.getInt("mtmfcs")==0){//已有的次数减去每天免费的次数==0代表这一次是第一次用钱的
								if(user.getInt("roomcard")<zp.getInt("dycsmomey")){
									msg.element("code", "0")
									.element("msg", "钱不够");
								}else{
									msg=koqian(id,sp,zp,data.getInt("cs"),zp.getInt("dycsmomey"),platform);
								}
							}
							else{
								if(user.getInt("roomcard")<zp.getInt("decsmomey")){
									msg.element("code", "0")
										.element("msg", "钱不够");
								}else{
									msg=koqian(id,sp,zp,data.getInt("cs"),zp.getInt("decsmomey"),platform);
								}
							}
						}else{
							msg.element("code", "0")
							.element("msg", "今天次数已用完");
						}
					}
				}else if("JHG".equals(platform)){//金黄冠
					if(Dto.isNull(data)){
						if(user.getInt("Losevalue")<zp.getInt("dycsmomey")){
							msg.element("code", "0")
								.element("msg", "幸运值不够");
						}else{
							String sql6="INSERT INTO user_turntable (userID,cs,creatTime) VALUES (?,?,?)";
							Object[] objects6={id,0,Timestamp.valueOf(TimeUtil.getNowDate())};
							sshUtilDao.updObjectBySQL(sql6, objects6);
							msg=koqian(id,sp,zp,0,zp.getInt("dycsmomey"),platform);
						}
					}else{
						if(data.getInt("cs")<zp.getInt("zfcs")){
							if(user.getInt("Losevalue")<zp.getInt("dycsmomey")){
								msg.element("code", "0")
									.element("msg", "幸运值不够");
							}else{
								msg=koqian(id,sp,zp,data.getInt("cs"),zp.getInt("dycsmomey"),platform);
							}
						}else{
							msg.element("code", "0")
							.element("msg", "今天次数已用完");
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				msg.element("code", "0")
				.element("msg", "程序错误,扣前之前错");
			}
		}else{
			msg.element("code", "0")
			.element("msg", "商品数量为0");
		}
		
		
		return msg;
	} 
	
	/**
	 * 转盘的扣，加
	 * @param id
	 * @param zaid
	 * @param sp
	 * @return
	 */
	public JSONObject koqian(String id,JSONObject sp,JSONObject zp,int cs,int price,String platform){
		JSONObject msg=new JSONObject();
		msg.element("text", "获得"+sp.getString("name"));
		String sql6="select Losevalue from za_users where id=?";
		Object[] objects6={id};
		JSONObject user=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql6, objects6));
		
		//扣开始转盘的钱
		try {
			if("JHG".equals(platform)){
				String sql="UPDATE  za_users SET Losevalue=Losevalue-? where id=? ";
				Object[] object={price,id};
				sshUtilDao.updObjectBySQL(sql, object);
			}else{
				if(cs>=zp.getInt("mtmfcs")){
					String sql="UPDATE  za_users SET roomcard=roomcard-? where id=? ";
					Object[] object={price,id};
					sshUtilDao.updObjectBySQL(sql, object);
				}
			}
			try {
				//加用户的钱
				String type="";
				if(sp.getInt("type")==1)
					type="roomcard";
				else if(sp.getInt("type")==2)
					type="coins";
				else if(sp.getInt("type")==3)
					type="score";
				else if(sp.getInt("type")==4)
					type="yuanbao";
				
				String sql2="UPDATE  za_users SET "+type+"="+type+"+? where id=? ";
				Object[] objects2={sp.getInt("memy"),id};
				sshUtilDao.updObjectBySQL(sql2, objects2);
			} catch (Exception e) {
				// TODO: handle exception
				msg.element("code", "0")
				.element("msg", "程序错误，//加用户的钱");
			}
		
			
			//更新用户转盘数据
			try {
				String sql3="UPDATE user_turntable set cs=cs+1 where userID=? ";
				Object[] objects3={id};
				sshUtilDao.updObjectBySQL(sql3, objects3);
				msg.element("code", "1")
					.element("msg", "成功")
					.element("xuan", sp.getInt("xuanzhong"));
				if("JHG".equals(platform)){//金黄冠
					msg.element("cs",cs+1)
						.element("price", user.getInt("Losevalue")-Integer.valueOf(price));
					String sql4="INSERT INTO za_userdeduction (userid,type,sum,doType,creataTime,memo) VALUES(?,?,?,4,?,?)";
					Object[] objects4={id,sp.getInt("type"),sp.getInt("memy"),new Date(),"来自转盘"};
					sshUtilDao.updObjectBySQL(sql4, objects4);
					//扣库存
					String sql5="update za_turntable set Number=Number-1 where id=?";
					Object[] objects5={sp.getLong("id")};
					sshUtilDao.updObjectBySQL(sql5, objects5);
				}else{//明明
					msg.element("cs", cs-zp.getInt("mtmfcs")<0?0:cs-zp.getInt("mtmfcs"));
					if(cs-zp.getInt("mtmfcs")<0)//用玩免费次数，要进行第一次花钱
						msg.element("price", 0);
					else {
						msg.element("price", zp.getInt("decsmomey"));
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				msg.element("code", "0")
				.element("msg", "程序错误，//更新用户转盘数据");
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			msg.element("code", "0")
			.element("msg", "程序错误，扣钱的时候");
		}
		return msg;
	}


	@Override
	public void JHGfd(String id,String type) {
		// TODO Auto-generated method stub
		//查询是否分享过
		String sql4="SELECT id FROM share_rec where userId=? AND createTime>'"+TimeUtil.getNowDate("yyyy-MM-dd")+" 00:00:01' and  createTime<='"+TimeUtil.getNowDate("yyyy-MM-dd")+" 23:59:59'  ";
		Object[] objects={id};
		JSONObject data2=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql4, objects));
		if(Dto.isNull(data2)){
			
			//用户信息
			String sql="select lv from za_users where id=?";
			JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
			//获取能拿的福袋
			String sql2="SELECT SUM(denomination) as denomination  FROM za_mall where type=5 and viprank<=?";
			Object[] objects2={data.getString("lv")};
			JSONObject list=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects2));
			System.out.println("进入福袋插入数据-福袋的钱"+String.valueOf(list)+"登陆用户"+id);
			if(list.getDouble("denomination")>0.00){
				System.out.println("进入福袋插入数据-福袋的钱1");
				Double price=list.getDouble("denomination");
				String sql3="UPDATE za_users set coins=coins+? where id=?";
				Object[] objects3={price,id};
				sshUtilDao.updObjectBySQL(sql3, objects3);
				String sql5="INSERT INTO share_rec (userId,resource,type,obj,count,createTime) VALUES ("+Long.valueOf(id)+",1,"+Integer.valueOf(type)+",2,'"+price+"','"+Timestamp.valueOf(TimeUtil.getNowDate())+"')";
				//Object[] objects5={Long.valueOf(id),1,Integer.valueOf(type),2,price,Timestamp.valueOf(TimeUtil.getNowDate())};
				System.out.println("进入福袋插入数据-福袋的钱3sql"+sql5);
				sshUtilDao.updObjectBySQL(sql5, null);
				
				String sql6="INSERT INTO za_userdeduction (userid,type,sum,doType,creataTime,memo) VALUES(?,?,?,?,?,?)";
				Object[] objects6={Long.valueOf(id),Dto.ZaUserdeduction_type_conin,price,Dto.ZAMALL_TYPE_FUDAI,Timestamp.valueOf(TimeUtil.getNowDate()),"来自福袋"};
				sshUtilDao.updObjectBySQL(sql6, objects6);
				System.out.println("进入福袋插入数据-结束");
			}
		}
	}


	@Override
	public JSONObject JHGcosin(String id) {
		// TODO Auto-generated method stub
		JSONObject msg=new JSONObject();
		String sql="SELECT count FROM share_rec where userId=? AND createTime>'"+TimeUtil.getNowDate("yyyy-MM-dd")+" 00:00:00' and  createTime<='"+TimeUtil.getNowDate("yyyy-MM-dd")+" 23:59:59'  ";
		Object[] objects={id};
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		if(Dto.isNull(data)){
			String sql3="select lv from za_users where id=?";
			JSONObject data3=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql3, objects));
			//获取能拿的福袋
			String sql4="SELECT SUM(denomination) as denomination  FROM za_mall where type=5 and viprank<=?";
			Object[] objects2={data3.getString("lv")};
			JSONObject list=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql4, objects2));
			msg.element("newprice", list.getDouble("denomination"));
		}
		else
			msg.element("newprice", data.getString("count"));
		String sql2="SELECT SUM(count) as count FROM share_rec where userId=?";
		JSONObject data2=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects));
		if (Dto.stringIsNULL(data2.getString("count"))) 
			msg.element("countprice", 0);
		else
			msg.element("countprice", data2.getString("count"));
		return msg;
		
	}

	
	@Override
	public JSONObject zdlwase(String url) {
		// TODO Auto-generated method stub
		String tj=" platform,appId,appSecret,merchantCode,merchantSecret,wechatCode,wechatName,welcomeMsg,wechatTel,proxyMsg,serviceMsg";
		String sql="select "+tj+" from operator_wechatinfo where platform=?";
		Object[] objects={url};
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		if(Dto.isNull(data)){
			 sql="select "+tj+" from operator_wechatinfo where ISNULL(platform)";
			 data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{}));
		}
		return data;
	}


	@Override
	public JSONObject gouserimg() {
		// TODO Auto-generated method stub
		PageUtil pageUtil=null;
		JSONObject data=new JSONObject();
		String tj=" id,img";
		String sql="select "+tj+" from user_img where sex=0";
		JSONArray con=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, null,pageUtil));
		String sql2="select "+tj+" from user_img where sex=1";
		JSONArray con2=JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql2, null,pageUtil));
		data.element("nan", con2)
			.element("nv", con);
		return data;
	}


	@Override
	public void JHGupuserimg(String id, String img) {
		// TODO Auto-generated method stub
		String sql="update za_users set headimg=? where id=?";
		Object[] objects={img,id};
		sshUtilDao.updObjectBySQL(sql, objects);
	}


	@Override
	public JSONObject getmessage9() {
		// TODO Auto-generated method stub
		String sql="select id,title,con,image,showType,strcreateTime,endcreateTime,status from za_message where type=9";
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[]{}));
		String time=TimeUtil.getNowDate("yyyy-MM-dd HH:mm:ss");
		Integer istype=1;
		if(data.containsKey("endcreateTime")){
			TimeUtil.transTimeStamp(data, "yyyy-MM-dd HH:mm:ss", "endcreateTime");
			if(TimeUtil.isLatter(data.getString("endcreateTime"), time)){
				istype=1;
			}else{
				istype=0;
			}
		}
		if(data.containsKey("strcreateTime")){
			TimeUtil.transTimeStamp(data, "yyyy-MM-dd HH:mm:ss", "strcreateTime");
			if(!TimeUtil.isLatter(data.getString("strcreateTime"), time)){
				istype=1;
			}else{
				istype=0;
			}
			
		}
		if(data.containsKey("strcreateTime")&&data.containsKey("endcreateTime")){
			Boolean str=TimeUtil.isLatter(data.getString("strcreateTime"), time);
			Boolean end=TimeUtil.isLatter(data.getString("endcreateTime"), time);
			if(!str&&end){
				istype=1;
			}else{
				istype=0;
			}
		}
		
		data.element("istype", istype);
		return data;
	}


	@Override
	public void upMachinenull(String id) {
		// TODO Auto-generated method stub
		String sql2="select Machine from za_users where id=?";
		Object[] objects={id};
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects));
		String sql="update za_users set Machine=?  where id=?";
		Object[] objects2={data.getString("Machine")+"$",id};
		sshUtilDao.updObjectBySQL(sql, objects2);
	}


	@Override
	public String czJHGfd(String id) {
		// TODO Auto-generated method stub
		String sql4="SELECT id FROM share_rec where userId=? AND createTime>'"+TimeUtil.getNowDate("yyyy-MM-dd")+" 00:00:01' and  createTime<='"+TimeUtil.getNowDate("yyyy-MM-dd")+" 23:59:59'  ";
		Object[] objects={id};
		JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql4, objects));
		if(Dto.isNull(data)){
			return "0";
		}else{
			return "1";
		}
	}
}
