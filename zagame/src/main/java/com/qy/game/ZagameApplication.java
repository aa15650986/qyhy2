package com.qy.game;



import org.hibernate.SessionFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@EnableFeignClients
@EnableDiscoveryClient
@ImportResource(locations = { "classpath:spring.xml","classpath:springMVC-servlet.xml" })
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
public class ZagameApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZagameApplication.class,args);
    }
/*




        _   ______     __  ___                          _   ______     _______      ________     _                __
   / | / / __ \   /  |/  /___  ____  ___  __  __   / | / / __ \   / ____(_)____/ / ____/____(_)__  ____  ____/ /
  /  |/ / / / /  / /|_/ / __ \/ __ \/ _ \/ / / /  /  |/ / / / /  / / __/ / ___/ / /_  / ___/ / _ \/ __ \/ __  /
 / /|  / /_/ /  / /  / / /_/ / / / /  __/ /_/ /  / /|  / /_/ /  / /_/ / / /  / / __/ / /  / /  __/ / / / /_/ /
/_/ |_/\____/  /_/  /_/\____/_/ /_/\___/\__, /  /_/ |_/\____/   \____/_/_/  /_/_/   /_/  /_/\___/_/ /_/\__,_/
                                       /____/






    */









}
