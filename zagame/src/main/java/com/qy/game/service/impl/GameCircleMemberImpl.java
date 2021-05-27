package com.qy.game.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qy.game.model.GameCircleMember;
import com.qy.game.service.GameCircleMemberService;
import com.qy.game.ssh.dao.SSHUtilDao;

import net.sf.json.JSONObject;

@Transactional
@Component
@Service
public class GameCircleMemberImpl implements GameCircleMemberService{

	@Resource
	SSHUtilDao sshUtilDao;
	
	@Override
	public long insertRoBotInfo(GameCircleMember gm) {
		String sql = "insert into game_circle_member(circle_id,user_id,user_code,user_hp,superior_user_code,user_role,platform,create_user,modify_user,gmt_create,gmt_modified) values(?,?,?,?,?,?,?,?,?,?,?)";
		Object[] obj = new Object[] { gm.getCircleId(),gm.getUserID(),gm.getUserCode(),gm.getUserHp(),gm.getSuperUserCode(),
				gm.getUserRole(),gm.getPlatform(),gm.getCreateUser(),gm.getModifyUser(),gm.getGmtCreate(),gm.getGmtModified()};
		boolean result = sshUtilDao.updObjectBySQL(sql, obj);
		return result?1:0;
	}

}
