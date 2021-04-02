package com.sundy.boot.jettison.goods;

import com.sundy.boot.jettison.bo.GoodsBO;
import com.sundy.boot.jettison.bo.GoodsParam;
import com.sundy.boot.jettison.bo.GoodsTradeBO;

/**
 * 货品处理器
 */
public interface GoodsHandler {

    /**
     * 获得货品类型
     */
    String getGoodsType();

    /**
     * 货品查询处理
     */
    GoodsBO handleQueryGoods(GoodsParam goodsParam);

    /**
     * 货品交易处理
     */
    GoodsTradeBO handleTradeGoods(GoodsParam goodsParam);
}
