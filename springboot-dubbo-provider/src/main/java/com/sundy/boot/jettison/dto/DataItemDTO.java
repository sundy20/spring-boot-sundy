package com.sundy.boot.jettison.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zeng.wang
 * @description 数据项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataItemDTO extends BaseDTO {

    private static final long serialVersionUID = 98875031482037616L;

    /**
     * 货品实例标识
     */
    private String goodsInst;

    /**
     * 货品类型
     */
    private String goodsType;

    /**
     * 货品code
     */
    private String goodsCode;
}
