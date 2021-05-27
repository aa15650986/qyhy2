package com.qy.game.service.impl;

import java.sql.Timestamp;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qy.game.service.dating.FundRechargeServerBiz;
import com.qy.game.utils.Dto;
import com.qy.game.utils.TimeUtil;
import com.qy.game.ssh.dao.SSHUtilDao;

@Service
@TransactionAttribute
@Transactional
public class FundRechargeServiceImpl implements FundRechargeServerBiz{

	@Resource
	SSHUtilDao sshUtilDao;
	
	@Override
	public void addsysbalreccz(String id, String price) {
		// TODO Auto-generated method stub
		String sql="INSERT INTO user_bal_rec (userID,money,type,des,createTime) values (?,?,?,?,?)";
		Object[] objects={id,price,Dto.USERBALREC_TYPE_RECHARGE,"用户进行充值",Timestamp.valueOf(TimeUtil.getNowDate())};
		//int s=DBUtil.executeUpdateBySQL(sql, objects);
		//System.out.println(s);
		sshUtilDao.updObjectBySQL(sql, objects);
	}

	@Override
	public JSONObject getsysbalrec(String id) {
		// TODO Auto-generated method stub
		
		String sql=" select SUM(money) as money from sys_bal_rec where userID=?";
		Object[] objects={id};
		//JSONObject data=DBUtil.getObjectBySQL(sql, objects);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		if(!data.containsKey("money"))
			data.element("money", 0);
		return data;
	}

	@Override
	public String addsysbalreczf(String id, String price,String type,Long tyID) {
		// TODO Auto-generated method stub
		/*扣钱*/
		String sql="INSERT INTO user_bal_rec (userID,money,type,createTime) values (?,?,?,?)";
		Object[] objects={id,price,Dto.USERBALREC_TYPE_RECHARGE,Timestamp.valueOf(TimeUtil.getNowDate())};
		
		//DBUtil.executeUpdateBySQL(sql, objects);
		sshUtilDao.updObjectBySQL(sql, objects);
	
		if("za".equals(type))//商城购买加用户
			addzausers(id,tyID);
		else if("vip".equals(type))
			upbasevip(id,tyID);//更新用户vip
		String sql3="select id from  user_bal_rec where userID=? order by createTime desc";
		Object[] objects2={id};
		//JSONObject data=DBUtil.getObjectBySQL(sql3, objects2);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql3, objects2));
		
		return data.getString("id");
	}
	
	public void addzausers(String id,Long tyID){
		
		String sql2="select denomination,type from za_mall where id=?";
		Object[] objects={tyID};
		//JSONObject data=DBUtil.getObjectBySQL(sql2, objects);
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects));
		if(data.getInt("type")==1){
			String sql="update za_users set roomcard=roomcard+?";
			Object[] objects2={data.getString("denomination")};
			//DBUtil.executeUpdateBySQL(sql, objects2);
			sshUtilDao.updObjectBySQL(sql, objects2);
		}
		else if(data.getInt("type")==2){
			String sql="update za_users set coins=coins+?";
			Object[] objects2={data.getString("denomination")};
			//DBUtil.executeUpdateBySQL(sql, objects2);
			sshUtilDao.updObjectBySQL(sql, objects2);
		}
	}

	@Override
	public String upbasezf(JSONObject con) {
		// TODO Auto-generated method stub
		String sj="";
		String sql="select roomcard,coins,score,tel,unionid,yuanbao from za_users where id=?";
		Object[] objects={con.getString("id")};
		//JSONObject zu=DBUtil.getObjectBySQL(sql, objects);
		JSONObject zu = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		if (zu.containsKey("unionid")&&!Dto.stringIsNULL(zu.getString("unionid"))) {
			Object[] params={zu.getString("unionid")};
			//JSONObject bu=DBUtil.getObjectBySQL("select id FROM base_users WHERE unionID=?", params);
			JSONObject bu = JSONObject.fromObject(sshUtilDao.getObjectBySQL("select id FROM base_users WHERE unionID=?", params));
			if (!Dto.isObjNull(bu)) {
				if (zu.containsKey("tel")&&!Dto.stringIsNULL(zu.getString("tel"))) {
					String sql2="select type,srcDenomination,id,title,denomination from za_exchange where id=?";
					Object[] objects2={con.getString("zeid")};
					//JSONObject ze=DBUtil.getObjectBySQL(sql2, objects2);
					JSONObject ze = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects2));
					
					Boolean f=false;
					if(ze.getInt("type")==1){//房卡
						if(zu.getDouble("roomcard")-ze.getDouble("srcDenomination")>=0){
							String sql3="update za_users set roomcard=roomcard-? where id=?";
							Object[] objects3={ze.getDouble("srcDenomination"),con.getString("id")};
							//DBUtil.executeUpdateBySQL(sql3, objects3);
							sshUtilDao.updObjectBySQL(sql3, objects3);
							f=true;
						}
						else
							sj="房卡不足请先充值";
					}else if (ze.getInt("type")==2) {//金币
						if(zu.getDouble("coins")-ze.getDouble("srcDenomination")>=0){
							String sql3="update za_users set coins=coins-? where id=?";
							Object[] objects3={ze.getDouble("srcDenomination"),con.getString("id")};
							//DBUtil.executeUpdateBySQL(sql3, objects3);
							sshUtilDao.updObjectBySQL(sql3, objects3);
							f=true;
						}else
							sj="金币不足请先充值";
						
					}
					else if (ze.getInt("type")==3) {//积分
						if(zu.getDouble("score")-ze.getDouble("srcDenomination")>=0){
							String sql3="update za_users set score=score-? where id=?";
							Object[] objects3={ze.getDouble("srcDenomination"),con.getString("id")};
							//DBUtil.executeUpdateBySQL(sql3, objects3);
							sshUtilDao.updObjectBySQL(sql3, objects3);
							f=true;
						}else
							sj="积分不足请先充值";
						
					}
					else if (ze.getInt("type")==4) {//元宝
						if(zu.getDouble("yuanbao")-ze.getDouble("srcDenomination")>=0){
							String sql3="update za_users set yuanbao=yuanbao-? where id=?";
							Object[] objects3={ze.getDouble("srcDenomination"),con.getString("id")};
							//DBUtil.executeUpdateBySQL(sql3, objects3);
							sshUtilDao.updObjectBySQL(sql3, objects3);
							f=true;
						}else
							sj="积分不足请先充值";
						
					}
					if(f){
						String sql5="INSERT INTO za_mall_exchange_rec (orderCode,objId,userId,titile,pay,text,type,status,createTime,money) values(?,?,?,?,?,?,?,?,?,?)";
						Object[] objects3={Dto.getorderCode(),ze.getString("id"),con.getString("id"),ze.getString("title"),ze.getString("srcDenomination"),con.getString("text"),Dto.ZAMALLEXCHANGEREC_TYPE_EXCHANGE,"0",Timestamp.valueOf(TimeUtil.getNowDate()),ze.getString("denomination")};
						//DBUtil.executeUpdateBySQL(sql5, objects3);
						sshUtilDao.updObjectBySQL(sql5, objects3);
						sj="兑换成功";
					}
				}else {
					sj="请先绑定手机号";
				}
			}else {
				sj="请先关注微信公众号,进入玩家个人中心";
			}
		}else{
			sj="请先关注微信公众号";
		}
		
		return sj;
	}
	
	
	public void upbasevip(String id,Long tyID){
		String sql="update za_users set vip=? where id=?";
		Object[] objects={tyID,id};
		//DBUtil.executeUpdateBySQL(sql, objects);
		sshUtilDao.updObjectBySQL(sql, objects);
	}
	
	/**
	 * 创建订单
	 * @param obj
	 * @return
	 */
	@Override
	public Boolean creataOrder(JSONObject obj) {
		String sql="INSERT INTO za_mall_exchange_rec (orderCode,objId,userId,titile,money,sum,type,status,createTime,memo,platform) values(?,?,?,?,?,?,?,?,?,?,?)";
		Object[] object={obj.getString("code"),obj.getString("objId"),obj.getString("userid"),obj.getString("title"),obj.getDouble("money"),obj.getDouble("sum"),obj.getString("type"),"0",Timestamp.valueOf(TimeUtil.getNowDate()),obj.getString("memo"),obj.getString("platform")};
		//int i=DBUtil.executeUpdateBySQL(sql, object);
		boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, object);
		boolean is=false;
		//if (i>0) {
		if (updObjectBySQL) {
			is=true;
		}
		
		return is;
		
	}
	
	/**
	 * 创建订单
	 * @param obj
	 * @return
	 */
	@Override
//	public long creataOrder2(JSONObject obj) {
	public boolean creataOrder2(JSONObject obj) {
		
		String text = "";
		if(obj.containsKey("text") &&!Dto.stringIsNULL(obj.getString("text"))){
			text = obj.getString("text");
		}
		
		String sql="INSERT INTO za_mall_exchange_rec (orderCode,objId,userId,titile,money,sum,type,status,createTime,memo,text) values(?,?,?,?,?,?,?,?,?,?,?)";
		Object[] object={obj.getString("code"),obj.getString("objId"),obj.getString("userid"),obj.getString("title"),obj.getDouble("money"),obj.getDouble("sum"),obj.getString("type"),"0",Timestamp.valueOf(TimeUtil.getNowDate()),obj.getString("memo"),text};
		//int i=DBUtil.executeUpdateBySQL(sql, object);
		//return i;
		boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, object);
		return updObjectBySQL;
		
	}

	/**
	 * 获取查看有无重复订单
	 * @param obj
	 * @return
	 */
	@Override
	public JSONObject creataOrderText(String text) {
		
		String sql="select id from za_mall_exchange_rec where text=?";
		Object[] objects={text};
		//return DBUtil.getObjectBySQL(sql, objects);
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		
	}
	@Override
	public JSONObject getExchangeRec(String order) {
		String sql="select objId,userId,money,status,type,sum,id,platform from za_mall_exchange_rec where orderCode=?";
		Object[] objects={order};
		//return DBUtil.getObjectBySQL(sql, objects);
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		
	
	}
	@Override
	public void updExchangeRec(Long id) {
		String sql="update za_mall_exchange_rec set status=1 where id=?";
		Object[] objects={id};
		//DBUtil.executeUpdateBySQL(sql, objects);
		sshUtilDao.updObjectBySQL(sql, objects);
		
	}
	
	@Override
	public void updExchangePayc(Double money,Long id) {
		String sql="UPDATE za_mall_exchange_rec SET pay=money ,money=? WHERE id=?";
		Object[] objects={money,id};
		//DBUtil.executeUpdateBySQL(sql, objects);
		sshUtilDao.updObjectBySQL(sql, objects);
		
	}
	
	@Override
	public void updZaUser(String id,double sum,String zz) {
		String sql="";
		if (zz.equals("roomcard")) {
			sql="update za_users set roomcard=roomcard+? where id=?";
			Object[] objects1={sum,id};
			//DBUtil.executeUpdateBySQL(sql, objects1);
			sshUtilDao.updObjectBySQL(sql, objects1);
			
			sql="update za_users za,base_users ba set ba.roomCards=ba.roomCards+? where za.unionid=ba.unionID and za.id=?";
			Object[] objects2={sum,id};
			//DBUtil.executeUpdateBySQL(sql, objects2);
			sshUtilDao.updObjectBySQL(sql, objects2);
		}else if (zz.equals("coins")){
			sql="update za_users set coins=coins+? where id=?";
			Object[] objects1={sum,id};
			//DBUtil.executeUpdateBySQL(sql, objects1);
			sshUtilDao.updObjectBySQL(sql, objects1);
			
			sql="update za_users za,base_users ba set ba.coins=ba.coins+? where za.unionid=ba.unionID and za.id=?";
			Object[] objects2={sum,id};
			//DBUtil.executeUpdateBySQL(sql, objects2);
			sshUtilDao.updObjectBySQL(sql, objects2);
		}else if (zz.equals("score")){
			sql="update za_users set score=score+? where id=?";
			Object[] objects1={sum,id};
			//DBUtil.executeUpdateBySQL(sql, objects1);
			sshUtilDao.updObjectBySQL(sql, objects1);
			
			sql="update za_users za,base_users ba set ba.score=ba.score+? where za.unionid=ba.unionID and za.id=?";
			Object[] objects2={sum,id};
			//DBUtil.executeUpdateBySQL(sql, objects2);
			sshUtilDao.updObjectBySQL(sql, objects2);
		}else if(zz.equals("yuanbao")){
			System.err.println("抽水扣元宝："+sum);
			sql="update za_users set yuanbao=yuanbao+? where id=?";
			Object[] objects1={sum,id};
			//DBUtil.executeUpdateBySQL(sql, objects1);
			sshUtilDao.updObjectBySQL(sql, objects1);
			
			sql="update za_users za,base_users ba set ba.yuanbao=ba.yuanbao+? where za.unionid=ba.unionID and za.id=?";
			Object[] objects2={sum,id};
			//DBUtil.executeUpdateBySQL(sql, objects2);
			sshUtilDao.updObjectBySQL(sql, objects2);
			
			String sql2="select account from za_users where id=?";
			Object[] objects={id};
			JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects));
			 sql2="insert into za_yb_update (account,yuanbao,status) values (?,?,?)";
			Object[] objects3={data.getString("account"),sum,0};
			sshUtilDao.updObjectBySQL(sql2, objects3);
		}
	}

	@Override
	public JSONObject getZaMallById(long id) {
		String sql="select type,coins,roomcard, from za_mall where id=?";
		Object[] objects={id};
		//return DBUtil.getObjectBySQL(sql, objects);
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
	}

	@Override
	public boolean updateUserVip(long id, int type) {
		String sql="update za_users set vip=? where id=?";
		Object[] objects={type,id};
		//int line=DBUtil.executeUpdateBySQL(sql, objects);
		boolean updObjectBySQL = sshUtilDao.updObjectBySQL(sql, objects);
		//if (line>0) {
		if (updObjectBySQL) {
			return true;
		}
		
		return false;
	}

	@Override
	public String upbasezf2(String id, String zaid, String text, String platform,String uuid) {
		// TODO Auto-generated method stub
		String sj="";
		String sql2="select type,srcDenomination,id,title,denomination from za_exchange where id=?";
		Object[] objects2={zaid};
		//JSONObject ze=DBUtil.getObjectBySQL(sql2, objects2);
		JSONObject ze = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects2));
		String sql="select roomcard,coins,score,yuanbao,tel,unionid from za_users where id=? and uuid=?";
		Object[] objects={id,uuid};
		//JSONObject zu=DBUtil.getObjectBySQL(sql, objects);
		JSONObject zu = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		Boolean f=false;
		if (!Dto.isObjNull(zu)) {
			if(ze.getInt("type")==1){//房卡
				if(zu.getDouble("roomcard")-ze.getDouble("srcDenomination")>=0){
					String sql3="update za_users set roomcard=roomcard-? where id=?";
					Object[] objects3={ze.getDouble("srcDenomination"),id};
					//DBUtil.executeUpdateBySQL(sql3, objects3);
					sshUtilDao.updObjectBySQL(sql3, objects3);
					f=true;
				}
				else
					sj="房卡不足请先充值";
			}else if (ze.getInt("type")==2) {//金币
				if(zu.getDouble("coins")-ze.getDouble("srcDenomination")>=0){
					String sql3="update za_users set coins=coins-? where id=?";
					Object[] objects3={ze.getDouble("srcDenomination"),id};
					//DBUtil.executeUpdateBySQL(sql3, objects3);
					sshUtilDao.updObjectBySQL(sql3, objects3);
					f=true;
				}else
					sj="金币不足请先充值";
				
			}
			else if (ze.getInt("type")==3) {//积分
				if(zu.getDouble("score")-ze.getDouble("srcDenomination")>=0){
					String sql3="update za_users set score=score-? where id=?";
					Object[] objects3={ze.getDouble("srcDenomination"),id};
					//DBUtil.executeUpdateBySQL(sql3, objects3);
					sshUtilDao.updObjectBySQL(sql3, objects3);
					f=true;
				}else
					sj="积分不足请先充值";
				
			}
			else if (ze.getInt("type")==4) {//元宝
				if(zu.getDouble("yuanbao")-ze.getDouble("srcDenomination")>=0){
					String sql3="update za_users set yuanbao=yuanbao-? where id=?";
					Object[] objects3={ze.getDouble("srcDenomination"),id};
					//DBUtil.executeUpdateBySQL(sql3, objects3);
					sshUtilDao.updObjectBySQL(sql3, objects3);
					f=true;
				}else
					sj="积分不足请先充值";
				
			}
		}
		if(f){
			String sql5="INSERT INTO za_mall_exchange_rec (orderCode,objId,userId,titile,pay,text,type,status,createTime,money) values(?,?,?,?,?,?,?,?,?,?)";
			Object[] objects3={Dto.getorderCode(),ze.getString("id"),id,ze.getString("title"),ze.getString("srcDenomination"),text,Dto.ZAMALLEXCHANGEREC_TYPE_EXCHANGE,"0",Timestamp.valueOf(TimeUtil.getNowDate()),ze.getString("denomination")};
			//DBUtil.executeUpdateBySQL(sql5, objects3);
			sshUtilDao.updObjectBySQL(sql5, objects3);
			sj="兑换成功";
		}
		return sj;
	}

	@Override
	public JSONObject getWechatSetting(long id) {
		String sql="SELECT appId,merchantCode,appSecret  FROM operator_wechatinfo WHERE id=?";
		Object[] objects={id};
		//return DBUtil.getObjectBySQL(sql, objects);
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
	}

	@Override
	public JSONObject getExchangeRecById(String id) {
		String sql="select userId,status,id,orderCode,titile,money,ip,tradeType,openid, from za_mall_exchange_rec where id=?";
		Object[] objects={id};
		//return DBUtil.getObjectBySQL(sql, objects);
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
	}

}
