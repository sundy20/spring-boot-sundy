package com.sundy.nettysocketio;

import com.sundy.nettysocketio.web.rest.ProductConsumerController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author zeng.wang
 * @description hystrix熔断测试
 * @date 2019-07-01
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SocketioServerApplication.class)
public class CircuitBreakerTest {

    @Autowired
    private ProductConsumerController productConsumerController;

    @Test
    public void testCircuitBreaker() throws InterruptedException {
        for (int i = 0; i < 30; ++i) {
            // 传入-1，会抛出异常，然后走降级逻辑
            try {
                productConsumerController.getProductInfo(-1L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Thread.sleep(3000);
        System.out.println("After sleeping...");

        for (int i = 31; i < 100; ++i) {
            // 传入1，走服务正常调用
            try {
                productConsumerController.getProductInfo(1L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
