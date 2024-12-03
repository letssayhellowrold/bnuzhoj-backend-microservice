package com.bnuzhoj.bnuzhojbackendpostservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.bnuzhoj.bnuzhojbackendpostservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.bnuzhoj")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.bnuzhoj.bnuzhojbackendserviceclient.service"})
public class BnuzhojBackendPostServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BnuzhojBackendPostServiceApplication.class, args);
    }

}
