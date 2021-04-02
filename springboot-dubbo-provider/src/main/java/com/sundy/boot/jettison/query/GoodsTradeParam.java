package com.sundy.boot.jettison.query;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 货品交易参数
 */
@Data
public class GoodsTradeParam implements Serializable {

    private static final long serialVersionUID = 8042118440786362610L;

    /**
     * 资源位code
     */
    private String resCode;

    /**
     * 货品实例标识
     */
    private String goodsInst;

    /**
     * 额外参数
     */
    private Map<String, Object> extra;
}
