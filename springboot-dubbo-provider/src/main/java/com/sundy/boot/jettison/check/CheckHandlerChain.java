package com.sundy.boot.jettison.check;

import com.sundy.boot.jettison.query.GoodsTradeQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 校验处理器链
 */
public class CheckHandlerChain {

    private List<CheckHandler> checkHandlers = new ArrayList<>();

    public void addHandler(CheckHandler handler) {
        this.checkHandlers.add(handler);
    }

    public void addHandlers(List<CheckHandler> handlers) {
        this.checkHandlers.addAll(handlers);
    }

    public CheckResult check(GoodsTradeQuery goodsTradeQuery, Map<String, Object> extra) {
        // 依次调用每个Handler:
        CheckResult checkResult = null;
        for (CheckHandler handler : checkHandlers) {
            checkResult = handler.handleCheck(goodsTradeQuery, extra);
            if (null != checkResult && !checkResult.isCheckPass()) {
                return checkResult;
            }
        }
        return checkResult;
    }
}
