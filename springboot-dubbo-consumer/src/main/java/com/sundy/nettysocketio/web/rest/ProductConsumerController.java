package com.sundy.nettysocketio.web.rest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.sundy.share.dto.ProductInfoDTO;
import com.sundy.share.service.ProductInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author plus.wang
 * @description
 * @date 2018/5/3
 */
@RestController
public class ProductConsumerController {

    @Reference(version = "1.0", check = false, timeout = 3000)
    private ProductInfoService productInfoService;

    @HystrixCommand(fallbackMethod = "fallback", groupKey = "ProductInfoService", commandKey = "GetProductInfoCommand",
            commandProperties = {
                    // 滑动窗口中，最少有20个请求，才可能触发断路
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "20"),
                    // 异常比例达到多少，才触发断路，默认50%
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "40"),
                    // 断路后多少时间内直接reject请求，之后进入half-open状态，默认5000ms
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "3000")

            })
    @RequestMapping("/getProductInfo")
    public ProductInfoDTO getProductInfo(@RequestParam Long productId) {
        return productInfoService.getProductInfo(productId);
    }

    public ProductInfoDTO fallback(Long productId) {
        ProductInfoDTO productInfo = new ProductInfoDTO();
        productInfo.setProductId(productId);
        productInfo.setProductName("降级商品");
        return productInfo;
    }
}
