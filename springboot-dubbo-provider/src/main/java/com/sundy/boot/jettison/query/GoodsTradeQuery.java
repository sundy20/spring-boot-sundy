package com.sundy.boot.jettison.query;

import lombok.Data;

import java.util.List;

/**
 * @author zeng.wang
 * @description 货品交易请求
 */
@Data
public class GoodsTradeQuery extends BaseQuery {

    private static final long serialVersionUID = -4130690616694574593L;

    /**
     * 货品交易参数列表
     */
    private List<GoodsTradeParam> goodsTradeParams;

    /**
     * 用户信息
     */
    private UserParam userParam;
}
