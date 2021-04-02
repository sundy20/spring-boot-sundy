package com.sundy.boot.jettison.check;

import com.sundy.boot.jettison.query.GoodsTradeQuery;

import java.util.Map;

/**
 * @author zeng.wang
 * @description 校验处理接口
 */
public interface CheckHandler {

    String getCheckCode();

    CheckResult handleCheck(GoodsTradeQuery goodsTradeQuery, Map<String, Object> extra);
}
