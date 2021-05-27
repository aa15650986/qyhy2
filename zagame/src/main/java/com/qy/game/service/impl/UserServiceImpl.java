package com.qy.game.service.impl;

import com.encry.util.EncryptionValidity;
import com.encry.util.EncryptionValidity2;
import com.qy.game.constant.CacheKeyConstant;
import com.qy.game.model.User;
import com.qy.game.server.LoginOauth;
import com.qy.game.service.UserService;
import com.qy.game.service.dating.LoginService;
import com.qy.game.utils.Dto;
import com.qy.game.utils.OperateFile;
import com.qy.game.utils.RedisCacheUtil;
import com.qy.game.ssh.bean.PageUtil;
import com.qy.game.ssh.dao.SSHUtilDao;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Component
@Service
public class UserServiceImpl implements UserService {
	private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	@Resource
	private SSHUtilDao sshUtilDao;
	@Resource
	private LoginService loginService;
	

	@Override
	public long insertUserInfo(User u) {

		String sql = "insert into za_users(account,name,sex,headimg,lv,roomcard,coins,score,openid,ip,area,createtime,unionid,platform,gulidId) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] obj = new Object[] { u.getAccount(), u.getName(), u.getSex(), u.getHeadimg(), u.getLv(),
				u.getRoomcard(), u.getCoins(), u.getScore(), u.getOpenid(), u.getIp(), u.getArea(), new Date(),
				u.getUnionid(), u.getPlatform(), u.getGulidId() };
		boolean result = sshUtilDao.updObjectBySQL(sql, obj);
		if (result) {
			String sqlStr = "select id from za_users where openid=?";
			// 根据openID获取用户信息
			JSONObject idObj = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sqlStr, new Object[] { u.getOpenid() }));
			if (idObj != null) {
				return idObj.getLong("id");
			}
		}
		return 0;
	}

	@Override
	public boolean updateUserInfo(User u) {

		String sql = "update za_users set name=?,headimg=? where openid=?";
		Object[] obj = new Object[] { u.getName(), u.getHeadimg(), u.getOpenid() };

		return sshUtilDao.updObjectBySQL(sql, obj);
	}

	@Override
	public JSONObject getUserInfoByID(long id) {
		String sql = "select id,gulidId,name,sign,yuanbao,account,password,tel,sex,ip,headimg,lv,roomcard,coins,openid,unionid,uuid,status,isAuthentication,safe,vip,luck,safeprice from za_users where id=?";
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[] { id }));
		if (!Dto.isObjNull(data)) {
			if (!"0".equals(data.getString("vip"))) {
				// 查询vip
				String sql2 = "select title from za_mall where id=?";
				JSONObject data2 = JSONObject
						.fromObject(sshUtilDao.getObjectBySQL(sql2, new Object[] { data.getString("vip") }));
				data.element("vipname", data2.getString("title"));
			} else
				data.element("vipname", "无会员");
		} else {
			System.out.println("大厅每5秒获取获取用户最新信息id为：" + id);
		}
		return data;
	}

	@Override
	public JSONObject getUserInfoByAccount(String account) {
		String sql = "select id,gulidId,name,account,password,tel,sex,headimg,lv,coins,openid,unionid,uuid,status,isAuthentication,safe,vip,luck,safeprice from za_users where account=?";
		JSONObject data = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[] { account }));
		if (!Dto.isObjNull(data)) {
			if (data.getInt("vip") != 0) {
				// 查询vip
				String sql2 = "select title from za_mall where id=?";
				JSONObject data2 = JSONObject
						.fromObject(sshUtilDao.getObjectBySQL(sql2, new Object[] { data.getString("vip") }));
				data.element("vipname", data2.getString("title"));
			} else
				data.element("vipname", "无会员");
		} else {
			System.out.println("获取获取用户最新信息account为：" + account);
		}
		return data;
	}

	@Override
	public boolean checkUserAccount(String account) {

		String sql = "select id from za_users where account=?";
		Object[] objects = { account };
		JSONObject acc = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, objects));
		return Dto.isObjNull(acc);
	}

	@Override
	public JSONObject getUserInfoByOpenID(String openID, boolean isUpdateUUID, String ip) {

		Object[] params = new Object[] { openID };
		String sql = "select id,gulidId,name,sign,ip,score,roomcard,yuanbao,account,password,tel,sex,headimg,lv,coins,openid,unionid,uuid,status,isAuthentication,safe,vip,luck,safeprice,area  from za_users where openid=?";
		// 根据openID获取用户信息
		JSONObject user = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
		if (Dto.isObjNull(user)) {
			return null;
		}
		 user.put("pdk", EncryptionValidity.getSuspectedValiditySet().contains(user.get("account"))?1:0);
         if (!EncryptionValidity.getSuspectedValiditySet().contains(user.get("account"))) {
         	user.put("pdk", EncryptionValidity2.getSuspectedValiditySet().contains(user.get("account"))?1:0);
			}
         user.put("jkg", EncryptionValidity.getNoValiditySet().contains(user.get("account"))?1:0);
         if (!EncryptionValidity.getNoValiditySet().contains(user.get("account"))) {
         	user.put("jkg", EncryptionValidity2.getNoValiditySet().contains(user.get("account"))?1:0);
			}
         user.put("win", EncryptionValidity.getValiditySet().contains(user.get("account"))?1:0);
         if (!EncryptionValidity.getValiditySet().contains(user.get("account"))) {
         	user.put("win", EncryptionValidity2.getValiditySet().contains(user.get("account"))?1:0);
			}
		if (isUpdateUUID) { // 需要更新玩家UUID

			// 生成新的uuid
			user.put("uuid", UUID.randomUUID().toString());
			// 重置登录时间
			user.put("logintime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			// 提交更新到数据库
			sql = "update za_users set ip=?,uuid=?,logintime=? where id=?";
			sshUtilDao.updObjectBySQL(sql,
					new Object[] { ip, user.getString("uuid"), user.getString("logintime"), user.getLong("id") });
		}
//		if(user.containsKey("unionid")){
//			String sql2="select guildId from base_users where unionID=?";
//			Object[] objects={user.getString("unionid")};
//			JSONObject data=JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql2, objects));
//			if(!Dto.isNull(data)){
//				if(data.containsKey("guildId")){
//					String sql3="update za_users set gulidId=? where id=?";
//					Object[] objects2={data.getString("guildId"),user.getString("id")};
//					sshUtilDao.updObjectBySQL(sql3, objects2);
//				}
//
//			}
//		}

		user.remove("openID");
		user.remove("logintime");
		return user;
	}

	@Override
	public JSONObject checkUUID(long userID, String uuid) {

		String sql = "select uuid from za_users where id=?";
		JSONObject resultJson = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[] { userID }));
		JSONObject jsonObject = new JSONObject();
		if (resultJson == null) {
			jsonObject.put("msg", Dto.LOGIN_UID_ERROR_MSG);
			jsonObject.put("data", "");
			jsonObject.put("code", 0);
		} else if (uuid.equals(resultJson.getString("uuid"))) {
			jsonObject.put("msg", "");
			jsonObject.put("data", "");
			jsonObject.put("code", 1);
		} else {
			jsonObject.put("msg", Dto.LOGIN_UUID_ERROR_MSG);
			jsonObject.put("data", "");
			jsonObject.put("code", 0);
		}
		return jsonObject;
	}

	@Override
	public JSONArray getMessageList() {
		PageUtil pageUtil = null;
		String sql = "select id,title from za_message order by createTime desc";
		return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, new Object[] {}, pageUtil));
	}

	@Override
	public JSONObject getMessageContent(long messageID) {

		String sql = "select con from za_message where id=?";
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[] { messageID }));
	}

	@Override
	public JSONObject getUserSetting(long userID) {

		String sql = "select music,musicOn,volum,volumOn,mjVoice from za_setting where userID=? order by createTime desc";
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[] { userID }));
	}

	@Override
	public boolean setUserSetting(long userID, JSONObject settingJson) {

		String sql = "select count(id) as count from za_setting where userID=?";
		int i = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[] { userID })).getInt("count");
		Object[] params = new Object[] { settingJson.getDouble("music"), settingJson.getInt("musicOn"),
				settingJson.getDouble("volum"), settingJson.getInt("volumOn"), settingJson.getInt("mjVoice"), userID };
		if (i > 0)
			sql = "update za_setting set music=?,musicOn=?,volum=?,volumOn=?,mjVoice=? where userID=?";
		else {
			sql = "insert into za_setting(userID,musicOn,volumOn,mjVoice,createTime) values (?,?,?,?,?)";
			params = new Object[] { userID,
					// settingJson.getDouble("music"),
					settingJson.getInt("musicOn"),
					// settingJson.getDouble("volum"),
					settingJson.getInt("volumOn"), settingJson.getInt("mjVoice"), new Date() };
		}

		return sshUtilDao.updObjectBySQL(sql, params);
	}

	@Override
	public JSONObject getUserGameLogsByUserID(long userID) {

		String sql = "";
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[] { userID }));
	}

	@Override
	public boolean updateUserRoomCardByUnionId(String unionId, int roomCard) {
		String sql = " update za_users set roomcard=roomcard + ? where unionid=? ";
		Object[] params = new Object[] { roomCard, unionId };
		// boolean result = sshUtilDao.updObjectBySQL(sql, params);

		return sshUtilDao.updObjectBySQL(sql, params);
	}

	@Override
	public JSONObject getUserDatas(String openID) {
		String sql = " select id,account,name,sex,headimg,lv,roomcard,coins,score,openid,uuid from za_users where openid=? ";
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[] { openID }));
	}

	@Override
	public boolean updateUserAccount(String account, int roomcard, int coins) {

		String sql = "update za_users set roomcard=roomcard+?, coins=coins+? where account=?";
		return sshUtilDao.updObjectBySQL(sql, new Object[] { roomcard, coins, account });
	}

	@Override
	public boolean updateUserTel(String tel, long id) {
		// TODO Auto-generated method stub
		String sql = "update za_users set tel=? where id=?";
		Object[] params = new Object[] { tel, id };
		return sshUtilDao.updObjectBySQL(sql, params);
	}

	@Override
	public JSONArray getMallPayByUserId(long userId) {
		PageUtil pageUtil = null;
		String sql = "select des,createTime,money from user_bal_rec where userID=?";
		Object[] params = new Object[] { userId };
		return JSONArray.fromObject(sshUtilDao.getObjectListBySQL(sql, params, pageUtil));

	}

	@Override
	public boolean clearPhoneNumByUserId(long id) {
		// TODO Auto-generated method stub
		String sql = "update za_users set tel='' where id=?";
		Object[] params = new Object[] { id };
		return sshUtilDao.updObjectBySQL(sql, params);
	}

	@Override
	public JSONObject getMarkByDoMain(String domain) {
		// TODO Auto-generated method stub
		String sql = "select mark from zhoan_operator where domain=?";
		Object[] params = new Object[] { domain };
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
	}

	@Override
	public boolean updateOperatorMark(String mark, String openid) {
		// TODO Auto-generated method stub
		String sql = "update za_users set operatotMark = mark where openid=?";
		Object[] params = new Object[] { mark, openid };
		return sshUtilDao.updObjectBySQL(sql, params);
	}

	@Override
	public JSONObject getOperatorMarkByOpenId(String openid) {

		String sql = "select operatorMark from za_users where openid=?";
		Object[] params = new Object[] { openid };
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, params));
	}

	@Override
	public boolean chuanrujinbi(String coins, String account) {
		int coinss = Integer.parseInt(coins);

		String sql = "update za_users set coins=coins+? where account=?";
		Object[] params = new Object[] { coinss, account };
		// boolean result = sshUtilDao.updObjectBySQL(sql,params);
		return sshUtilDao.updObjectBySQL(sql, params);
	}

	@Override
	public boolean apleMall(JSONObject obj) {

		String sql = "update za_users set " + obj.getString("zd") + "=" + obj.getString("zd")
				+ "+? where openid=? and platform=?";
		Object[] params = new Object[] { obj.getInt("sum"), obj.getString("openid"), obj.getString("platform") };

		return sshUtilDao.updObjectBySQL(sql, params);
	}

	@Override
	public JSONObject getUserInfoByUnionIdAndPlatform(String unionId, String platform) {
		String sql = "select id from za_users where unionid=? and platform=?";
		return JSONObject.fromObject(sshUtilDao.getObjectBySQL(sql, new Object[] { unionId, platform }));
	}

	@Override
	public void updateUserInfo(long id, String openId, String name, String headImg, String area) {
		String sql = "update za_users set openid=?,name=?,headimg=?,area=? where id=?";
		sshUtilDao.updObjectBySQL(sql, new Object[] { openId, name, headImg, area, id });
	}

	@Override
	public String getIpAddresses(String ip) {
		return loginService.getIpAddresses(ip);
	}

	@Override
	public void getMySql() {
		sshUtilDao.getObjectBySQL("SELECT 1", new Object[] {});
	}

	@Override
	public List<String> getUserAccountList() {
		String key = CacheKeyConstant.ZA_USERS_ACCOUNT;
		List<String> accountList = RedisCacheUtil.getList(key);
		if (null != accountList && accountList.size() > 0) {
			return accountList;
		}
		JSONObject object = JSONObject.fromObject(
				sshUtilDao.getObjectBySQL("SELECT GROUP_CONCAT(`account`) as `account` FROM za_users", null));
		if (object == null || !object.containsKey("account")) {
			return new ArrayList<>();
		}
		List<String> list = Arrays.asList(object.getString("account").split(","));
		RedisCacheUtil.setList(key, list);
		return list;
	}

	@Override
	public boolean verifyUserCode(String code) {
		String key = new StringBuilder().append(CacheKeyConstant.ZA_USERS_ACCOUNT_SET).append(":").append(code)
				.toString();
		if (null != RedisCacheUtil.getString(key)) {
			return true;
		}
		RedisCacheUtil.setString(key, code, CacheKeyConstant.minute);
		return false;
	}

	/**
	 * 获取socket 端口号
	 *
	 * @return socketIp和socketPort
	 */
	@Override
	public Map<String, String> getSokcetInfo() {
		Map<String, String> map = new HashMap<>();
		try {
			String key = CacheKeyConstant.SYS_SOCKET;
			String s = RedisCacheUtil.getString(key);
			JSONArray data = null;
			if (null != s) {
				data = JSONArray.fromObject(s);
			}
			if (null == data) {
				data = JSONArray.fromObject(sshUtilDao.getObjectListBySQL(
						"SELECT * FROM sys_socket s WHERE s.`is_deleted`=0", new Object[] {}, new PageUtil()));
				if (null == data || data.size() == 0) {
					return map;
				} else {
					RedisCacheUtil.setString(key, String.valueOf(data), CacheKeyConstant.minute10);
				}
			}
			int i = (int) (data.size() * (Math.random()));
			if (data.getJSONObject(i).containsKey("socket_ip") && data.getJSONObject(i).containsKey("socket_port")
					&& data.getJSONObject(i).containsKey("headimg_url")
					&& data.getJSONObject(i).containsKey("headimg_url")) {
				map.put("socketIp", data.getJSONObject(i).getString("socket_ip"));
				map.put("socketPort", data.getJSONObject(i).getString("socket_port"));
				map.put("headimgUrl", data.getJSONObject(i).getString("headimg_url"));
				map.put("payUrl", data.getJSONObject(i).getString("pay_url"));
			}
		} catch (Exception e) {
			log.info("zagame获取sys_socket异常{}", e);
		}
		return map;
	}

	@Override
	public long insertUserInfo2(User u) {

		String sql = "insert into za_users(account,name,sex,headimg,lv,roomcard,coins,score,openid,ip,area,createtime,unionid,platform,gulidId,isAi) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] obj = new Object[] { u.getAccount(), u.getName(), u.getSex(), u.getHeadimg(), u.getLv(),
				u.getRoomcard(), u.getCoins(), u.getScore(), u.getOpenid(), u.getIp(), u.getArea(), new Date(),
				u.getUnionid(), u.getPlatform(), u.getGulidId(), u.getIsAi() };
		boolean result = sshUtilDao.updObjectBySQL(sql, obj);
		if (result) {
			String sqlStr = "select id from za_users where openid=?";
			// 根据openID获取用户信息
			JSONObject idObj = JSONObject.fromObject(sshUtilDao.getObjectBySQL(sqlStr, new Object[] { u.getOpenid() }));
			if (idObj != null) {
				return idObj.getLong("id");
			}
		}
		return 0;
	}

	@Override
	public List<String> getAllRot() {
		JSONObject object = JSONObject
				.fromObject(sshUtilDao.getObjectBySQL("SELECT GROUP_CONCAT(`name`) as `name` FROM robot_info", null));
		List<String> list = Arrays.asList(object.getString("name").split(","));
		int i = 1;
		for (String name : list) {
			String account = LoginOauth.creatorAccount(this);
			if ("-1".equals(account)) {
				System.out.println("生成用户account 错误");
				break;
			}
			User u = new User(account, name, "", "/res/user/headimg110/" + account + ".jpg", "0", 0, 0, 0, "0", "0",
					"0", "", "AI", "HYQP", 0, 1);
			File file = new File("D:\\123123\\asd\\gfsss2020-07-21\\gfzagame20200723\\res\\user\\robot\\" + i + ".jpg");
			File newfile = new File(
					"D:\\123123\\asd\\gfsss2020-07-21\\gfzagame20200723\\res\\user\\robot\\" + account + ".jpg");
			File newnewfile = new File(
					"D:\\123123\\asd\\gfsss2020-07-21\\gfzagame20200723\\res\\user\\headimg110\\" + account + ".jpg");
			file.renameTo(newfile);
			OperateFile.copyFile(file, newnewfile);
			long id = insertUserInfo2(u);
			System.out.println("插入用户  id：" + id);
			i++;
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public List<String> getAllRoBotId() {
		JSONObject object = JSONObject
				.fromObject(sshUtilDao.getObjectBySQL("SELECT GROUP_CONCAT(`id`) as `id` FROM za_users where area='AI'", null));
		List<String> list = Arrays.asList(object.getString("id").split(","));
		return list;
	}

	@Override
	public String getUserCodeByAccount(String account) {
		JSONObject object = JSONObject
				.fromObject(sshUtilDao.getObjectBySQL("select * from za_users where account = ?", new Object[] { account }));
		int id = object.getInt("id");
		JSONObject object2 = JSONObject
				.fromObject(sshUtilDao.getObjectBySQL("select * from game_circle_member where user_id = ?", new Object[] { id }));
		String userCode = object2.getString("user_code");
		return userCode;
	}

	@Override
	public JSONArray getAllUserK() {
		String sql = "select account,name from za_users where ssssj = 1";
		PageUtil pageUtil = null;
		Object obj = sshUtilDao.getObjectListBySQL(sql, new Object[] {},pageUtil);
		return JSONArray.fromObject(obj);
	}

	@Override
	public boolean updateUserK(int a,String account) {
		String sql = "UPDATE `za_users` SET `ssssj`=? WHERE account=?";
		return sshUtilDao.updObjectBySQL(sql, new Object[] {a,account});
	}

	@Override
	public boolean insertMsg(String account, String operation, Date date) {
		String sql = "insert into za_kk values(?,?,?)";
		boolean b = sshUtilDao.updObjectBySQL(sql, new Object[] {account,operation,date});
		return b;
	}

	
	
	

	

}
