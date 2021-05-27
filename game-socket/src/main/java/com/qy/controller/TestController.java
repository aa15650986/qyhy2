package com.qy.controller;

import com.qy.openfeign.service.RedisInfoFeignClientService;
import com.qy.socket.GameMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("aa")
public class TestController {

    @Autowired
    private RedisInfoFeignClientService service;

    @Autowired
    private GameMain gameMain;
    @GetMapping("bb")
    public void b(){
        gameMain.startServer(true);
    }
}
