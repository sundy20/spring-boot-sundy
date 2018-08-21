package com.sundy.boot.limiter;

public interface TPSLimiter {

    /**
     * 根据 tps 限流规则判断是否限制此次调用.
     *
     * @param sessionIdAndReqURI 当前请求
     * @return true 则允许调用，否则不允许
     */
    boolean isAllowable(String sessionIdAndReqURI);

}
