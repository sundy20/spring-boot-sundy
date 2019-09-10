package com.sundy.share.dto;

import java.io.Serializable;

/**
 * @author plus.wang
 * @description 商品信息
 * @date 2019-07-01
 */
public class ProductInfoDTO implements Serializable {
    private Long productId;
    private String productName;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
