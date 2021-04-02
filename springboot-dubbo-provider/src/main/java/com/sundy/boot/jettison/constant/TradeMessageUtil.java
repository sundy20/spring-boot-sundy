package com.sundy.boot.jettison.constant;

public class TradeMessageUtil {

    private static final String GOODS_IS_OVER = "货品已被抢光，请稍后再试";

    public static String getTradeMessage() {
        return GOODS_IS_OVER;
    }
}
