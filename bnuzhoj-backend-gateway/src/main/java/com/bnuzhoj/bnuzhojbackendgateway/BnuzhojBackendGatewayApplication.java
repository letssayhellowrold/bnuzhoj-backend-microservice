package com.bnuzhoj.bnuzhojbackendgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// 不检测数据库
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class BnuzhojBackendGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BnuzhojBackendGatewayApplication.class, args);
    }

}
