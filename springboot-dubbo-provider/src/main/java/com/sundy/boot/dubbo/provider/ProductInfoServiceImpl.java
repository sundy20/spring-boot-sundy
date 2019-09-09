package com.sundy.boot.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.sundy.share.dto.ProductInfoDTO;
import com.sundy.share.service.ProductInfoService;

/**
 * @author plus.wang
 * @description 商品信息服务默认实现
 * @date 2019-07-01
 */
@Service(version = "1.0")
public class ProductInfoServiceImpl implements ProductInfoService {
    /**
     * 根据商品id获取商品信息
     *
     * @param productId 商品id
     */
    @HystrixCommand(fallbackMethod = "fallback", groupKey = "ProductInfoService", commandKey = "GetProductInfoCommand",
            commandProperties = {
                    //滑动窗口中，最少有20个请求，才可能触发断路
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "20"),
                    //异常比例达到多少，才触发断路，默认50%
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "40"),
                    //断路后多少时间内直接reject请求，之后进入half-open状态，默认5000ms
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "3000"),
                    //设置超时时间800ms（超过800ms走fallback） 默认1秒
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "800"),
                    //设置fallback最大请求并发数
                    @HystrixProperty(name = "fallback.isolation.semaphore.maxConcurrentRequests", value = "50")

            },
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "16"),
                    @HystrixProperty(name = "maximumSize", value = "16"),
                    @HystrixProperty(name = "maxQueueSize", value = "512"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "512"),
            })
    @Override
    public ProductInfoDTO getProductInfo(Long productId) {
        ProductInfoDTO productInfoDTO = new ProductInfoDTO();
        productInfoDTO.setProductId(productId);
        productInfoDTO.setProductName("商品" + productId + "信息");
        return productInfoDTO;
    }
}
