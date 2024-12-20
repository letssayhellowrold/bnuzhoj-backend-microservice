package com.bnuzhoj.bnuzhojbackendjudgeservice;

import com.bnuzhoj.bnuzhojbackendjudgeservice.rabbitmq.InitRabbitMq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.bnuzhoj")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.bnuzhoj.bnuzhojbackendserviceclient.service"})
public class BnuzhojBackendJudgeServiceApplication {

    public static void main(String[] args) {
//         初始化消息队列，先注释掉，改用 Bean 的方式初始化消息队列（InitRabbitMqBean.java）
        InitRabbitMq.doInit();
        SpringApplication.run(BnuzhojBackendJudgeServiceApplication.class, args);
    }
}
