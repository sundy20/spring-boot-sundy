package com.sundy.boot.utils;

/**
 * Created on 2018/1/17
 *
 * @author plus.wang
 * @description 公共常量
 */
public interface Constants {

    /**
     * 编码
     */
    String CHARSET = "UTF-8";

    /**
     * redis key 前缀
     */
    String REDIS_PREFIX = "spring-boot-sundy:";

    /**
     * redis 分布式锁 前缀
     */
    String REDIS_LOCK_PREFIX = "spring-boot-sundy";

    /**
     * redis分布式锁5秒后自动释放
     */
    long REDIS_LOCK_EXPIRE_MS = 5000L;
}
