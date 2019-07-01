package com.sundy.boot.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.sundy.share.dto.ProductInfoDTO;
import com.sundy.share.service.ProductInfoService;

/**
 * @author zeng.wang
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
    @Override
    public ProductInfoDTO getProductInfo(Long productId) {
        ProductInfoDTO productInfoDTO = new ProductInfoDTO();
        productInfoDTO.setProductId(productId);
        productInfoDTO.setProductName("商品" + productId + "信息");
        return productInfoDTO;
    }
}
