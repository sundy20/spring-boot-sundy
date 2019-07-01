package com.sundy.boot;

import com.sundy.boot.utils.HttpClientUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zeng.wang
 * @description hystrix熔断测试
 * @date 2019-07-01
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CircuitBreakerTest {
    @Test
    public void testCircuitBreaker() throws InterruptedException {
        String baseURL = "http://localhost:8080/getProductInfo?productId=";

        for (int i = 0; i < 30; ++i) {
            // 传入-1，会抛出异常，然后走降级逻辑
            try {
                HttpClientUtil.getInstance().doGetForString(baseURL + "-1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Thread.sleep(3000);
        System.out.println("After sleeping...");

        for (int i = 31; i < 100; ++i) {
            // 传入1，走服务正常调用
            try {
                HttpClientUtil.getInstance().doGetForString(baseURL + "1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
