package com.qy.game.task;

import com.qy.game.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TaskJob {
    @Resource
    private UserService userService;

    @Scheduled(cron = "20 20 * * * ? ") // 一小时执行一次
    public void taskCycle() {
        userService.getMySql();
        LocalDateTime date = LocalDateTime.now();
        String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("zagame一小时一次的定时任务 : " + dateStr);
    }
}
