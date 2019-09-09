package com.sundy.share.service;

import com.sundy.share.dto.ProductInfoDTO;

/**
 * @author plus.wang
 * @description 商品信息服务
 * @date 2019-07-01
 */
public interface ProductInfoService {

    /**
     * 根据商品id获取商品信息
     *
     * @param productId 商品id
     */
    ProductInfoDTO getProductInfo(Long productId);
}
