package com.sundy.boot.annotation;

import java.lang.annotation.*;

/**
 * @author plus.wang
 * @description 缓存get注解
 * @date 2019-08-26
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    String key() default "";

    String prefix() default "";

    int ttl() default 60;

    String cache() default "";

    boolean cacheNull() default false;

    long waitTimeout() default 0L;

    String asyncExecutor() default "";
}
