package com.sundy.nettysocketio.web.rest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sundy.share.service.DemoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author plus.wang
 * @description
 * @date 2018/5/3
 */
@RestController
public class DemoConsumerController {

    @Reference(version = "1.0", check = false, timeout = 3000)
    private DemoService demoService;

    @RequestMapping("/sayHello")
    public String sayHello(@RequestParam String name) {
        return demoService.sayHello(name);
    }
}
