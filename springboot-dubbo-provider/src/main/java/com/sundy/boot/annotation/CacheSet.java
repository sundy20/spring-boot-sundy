package com.sundy.boot.annotation;

import java.lang.annotation.*;

/**
 * @author zeng.wang
 * @description 缓存set注解
 * @date 2019-08-26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheSet {
    String key() default "";

    String prefix() default "";

    int ttl() default 60;

    String cache() default "";
}
