package com.sundy.boot.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.sundy.share.service.DemoService;

/**
 * @author plus.wang
 * @description
 * @date 2018/5/3
 */
@Service(version = "1.0")
public class DemoServiceImpl implements DemoService {

    @Override
    public String sayHello(String name) {
        return "Hello, " + name + " (from Spring Boot dubbo provider)";
    }
}