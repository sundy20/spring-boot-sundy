package com.sundy.nettysocketio.web.rest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sundy.share.service.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author plus.wang
 * @description
 * @date 2018/5/3
 */
@Slf4j
@RestController
public class DemoConsumerController {

    @Reference(version = "1.0", check = false, timeout = 3000)
    private DemoService demoService;

    @Retryable(value = Exception.class, maxAttempts = 5, backoff = @Backoff(delay = 2000L, maxDelay = 600000,
            multiplier = 2))
    @RequestMapping("/sayHello")
    public String sayHello(@RequestParam String name) {
        return demoService.sayHello(name);
    }

    @Recover
    public String recover(Exception e) {
        log.warn("操作失败", e);
        return "downgraded";
    }
}
