package com.qy.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.qy.dao.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequestMapping("other")
public class OtherDBController {

    private static final Logger log = LoggerFactory.getLogger(OtherDBController.class);



    @RequestMapping("getObjectListBySQL")
    public JSONArray getObjectListBySQL(@RequestParam("sql") String sql,@RequestParam(value = "objs",required = false) Object[] objs) throws UnsupportedEncodingException {
        log.info("执行方法------------getObjectListBySQL");
        sql = URLDecoder.decode(sql,"utf-8");
        return DBUtil.getObjectListBySQL(sql,objs);
    }

    @RequestMapping("executeUpdateGetKeyBySQL")
    public Long executeUpdateGetKeyBySQL(@RequestParam("sql") String sql,@RequestParam(value = "objs",required = false) Object[] objs){
        return DBUtil.executeUpdateGetKeyBySQL(sql,objs);
    }
    @RequestMapping("executeUpdateBySQL")
    public int executeUpdateBySQL(@RequestParam("sql") String sql,@RequestParam(value = "objs",required = false) Object[] objs){
        return  DBUtil.executeUpdateBySQL(sql,objs);
    }
}
