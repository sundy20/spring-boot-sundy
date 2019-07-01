package com.sundy.share.service;

import com.sundy.share.dto.ProductInfoDTO;

/**
 * @author zeng.wang
 * @description 产品信息服务
 * @date 2019-07-01
 */
public interface ProductInfoService {

    /**
     * 根据产品id获取产品信息
     *
     * @param productId 产品id
     */
    ProductInfoDTO getProductInfo(Long productId);
}
