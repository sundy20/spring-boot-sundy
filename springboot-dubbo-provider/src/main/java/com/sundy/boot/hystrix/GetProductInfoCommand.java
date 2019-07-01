package com.sundy.boot.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.sundy.share.dto.ProductInfoDTO;
import com.sundy.share.service.ProductInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zeng.wang
 * @description 获取产品信息HystrixCommand
 * @date 2019-07-01
 */
@Slf4j
@Component
public class GetProductInfoCommand extends HystrixCommand<ProductInfoDTO> {

    @Autowired
    private ProductInfoService productInfoService;

    private Long productId;

    private static final HystrixCommandKey KEY = HystrixCommandKey.Factory.asKey("GetProductInfoCommand");

    public GetProductInfoCommand(Long productId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                .andCommandKey(KEY)
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        // 是否允许断路器工作
                        .withCircuitBreakerEnabled(true)
                        // 滑动窗口中，最少有多少个请求，才可能触发断路
                        .withCircuitBreakerRequestVolumeThreshold(20)
                        // 异常比例达到多少，才触发断路，默认50%
                        .withCircuitBreakerErrorThresholdPercentage(40)
                        // 断路后多少时间内直接reject请求，之后进入half-open状态，默认5000ms
                        .withCircuitBreakerSleepWindowInMilliseconds(3000)));
        this.productId = productId;
    }

    @Override
    protected ProductInfoDTO run() throws Exception {
        log.info("调用接口查询商品数据，productId={}", productId);
        if (productId == -1L) {
            throw new Exception();
        }
        return productInfoService.getProductInfo(productId);
    }

    @Override
    protected ProductInfoDTO getFallback() {
        ProductInfoDTO productInfo = new ProductInfoDTO();
        productInfo.setProductId(this.productId);
        productInfo.setProductName("降级商品");
        return productInfo;
    }
}
