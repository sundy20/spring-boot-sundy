package com.sundy.boot;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableDubbo(scanBasePackages = "com.sundy.boot.dubbo.provider")
@MapperScan("com.sundy.boot.dao")
@EnableTransactionManagement(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = "com.sundy.boot")
@EnableHystrix
public class SpringBootSundyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSundyApplication.class, args);
    }
}
