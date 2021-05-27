package com.qy;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.jms.annotation.EnableJms;

@EnableDiscoveryClient
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
@EnableFeignClients
public class GameSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameSocketApplication.class,args);
    }
}
