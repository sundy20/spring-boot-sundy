package com.sundy.boot.utils;

/**
 * @author plus.wang
 * @description 日期格式化枚举
 * @date 2018/5/10
 */
public enum DatePattern {
    yyyyMMddHHmmssSSS("yyyy-MM-dd HH:mm:ss.SSS"),
    yyyyMMddHHmmss("yyyy-MM-dd HH:mm:ss"),
    yyyyMMddHHmmWithOut("yyyyMMddHHmm"),
    yyyyMMddHHmm("yyyy-MM-dd HH:mm"),
    T("yyyy-MM-dd'T'HH:mm:ss"),
    DAYOF("dd' of 'MM"),
    yyyyMMdd("yyyy-MM-dd"),
    yyyyMM("yyyyMM"),
    yyyyMMddWithoutHypen("yyyyMMdd"),
    yyyy("yyyy"),
    yyMM("yyMM"),
    MMdd("MMdd"),
    MM("MM"),
    HHmm("HH:mm"),
    dd("dd");

    DatePattern(String pattern) {
        this.pattern = pattern;
    }

    private String pattern;

    public String getPattern() {
        return pattern;
    }
}

