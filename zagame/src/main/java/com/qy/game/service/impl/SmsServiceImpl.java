package com.qy.game.service.impl;

import com.qy.game.service.SmsService;
import com.qy.game.utils.ihuyi.IhuyiSendSms;
import com.qy.game.ssh.dao.SSHUtilDao;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Random;

/**
 * 短信接口
 */
@Transactional
@Component
@Service
public class SmsServiceImpl implements SmsService {
    @Resource
    private SSHUtilDao sshUtilDao;

    /**
     * 根据短信类型获取短信信息
     *
     * @param type 1 互亿
     * @return
     */
    @Override
    public JSONObject getSysSmsByType(int type) {
        return JSONObject.fromObject(sshUtilDao.getObjectBySQL("SELECT * FROM sys_sms s WHERE s.`sms_type`= ? AND s.`is_deleted` = ? "
                , new Object[]{type, 0}));
    }

    /**
     * 互亿短信发送
     *
     * @param mobile 手机号码
     * @return
     */
    @Override
    public String sendMsgIhuyi(String mobile) {
        JSONObject object = getSysSmsByType(1);
        Random rd = new Random();
        int mobileCode = rd.nextInt(899999) + 100000;
        if (null != object && object.containsKey("api_id") && object.containsKey("api_key") && object.containsKey("sms_url")) {
            IhuyiSendSms.sendMsg(mobileCode, mobile
                    , String.valueOf(object.get("api_id")), String.valueOf(object.get("api_key")), String.valueOf(object.get("sms_url")));
        }
        return String.valueOf(mobileCode);
    }
    
    public static void main(String[] args) {
		SmsServiceImpl impl = new SmsServiceImpl();
		String a = impl.sendMsgIhuyi("17744602860");
		System.out.println(a);
	}
}
