package com.sundy.boot.jettison.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zeng.wang
 * @description 货品交易结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsTradeDTO extends BaseDTO {

    private static final long serialVersionUID = 98875031482037616L;

    /**
     * 资源位code
     */
    private String resCode;

    /**
     * 货品实例标识
     */
    private String goodsInst;

    /**
     * 1: 成功  非1: 失败
     */
    private int tradeStatus;

    /**
     * tradeStatus失败时的提示
     */
    private String tradeMessage;
}
