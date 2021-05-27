package com.qy.openfeign.service;

import com.alibaba.fastjson.JSONArray;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient("game-dao")
public interface RedisInfoFeignClientService {

    @GetMapping("/game-dao/getSocketInfo")
    Map<String,String> getScoketInfor();

    @GetMapping("/game-dao/delRedisSystem")
    void delRedisSystem();


    @RequestMapping("/other/getObjectListBySQL")
    JSONArray getObjectListBySQL(@RequestParam("sql") String sql, @RequestParam(value = "objs",required = false) Object[] objs);

    @RequestMapping("/other/executeUpdateGetKeyBySQL")
    Long executeUpdateGetKeyBySQL(@RequestParam("sql")String sql,@RequestParam(value = "objs",required = false)Object[] objs);

    @RequestMapping("/other/executeUpdateBySQL")
    int executeUpdateBySQL(@RequestParam("sql")String sql,@RequestParam(value = "objs",required = false)Object[] objs);

}
