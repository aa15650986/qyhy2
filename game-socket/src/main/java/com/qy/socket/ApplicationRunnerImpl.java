package com.qy.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class ApplicationRunnerImpl implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(ApplicationRunnerImpl.class);
    @Autowired
    private GameMain gameMain;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("自动启动 SocketIO服务"+System.currentTimeMillis());
       // gameMain.startServer(true);
    }
}
