package com.qy.Controller;

import com.qy.redis.RedisInfoService;
import com.qy.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("game-dao")
public class RedisServerController {

    private static final Logger log = LoggerFactory.getLogger(RedisServerController.class);
    @Autowired
    private RedisInfoService redisInfoService;
    @Autowired
    private RedisService redisService;

    @GetMapping("getSocketInfo")
    public Map<String, String> getSocketInfo() {
        log.info("执行method--getSocketInfo");
        Map<String, String> map = redisInfoService.getSocketInfo();
        return map;
    }
    @GetMapping("delRedisSystem")
    public void delRedisSystem(){
        log.info("执行method--delRedisSystem");
        this.redisInfoService.delGameSettingAll();
        this.redisInfoService.delGameInfoAll();
        this.redisInfoService.delSummaryAll();
        this.redisService.deleteByLikeKey("startTimes_*");
        this.redisInfoService.delAppTimeSettingAll();
        this.redisInfoService.delTeaInfoAllCode();
        this.redisInfoService.delTeaRoomOpen((String)null);
        this.redisInfoService.delExistCreateRoom((String)null);
        this.redisInfoService.delTeaSys();
        this.redisInfoService.delGameSssSpecialSetting();
        this.redisInfoService.delAllCreateCircle();
        this.redisInfoService.delAllReadMessageRepeatCheck();
        this.redisInfoService.delHasControl();
        this.redisInfoService.delSysGlobal();
        this.redisInfoService.delSokcetInfo();
    }


}
