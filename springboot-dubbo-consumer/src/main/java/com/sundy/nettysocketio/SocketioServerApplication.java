package com.sundy.nettysocketio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication(scanBasePackages = "com.sundy.nettysocketio")
@EnableHystrix
public class SocketioServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketioServerApplication.class, args);
    }
}
