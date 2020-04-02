package com.sundy.boot.annotation;

import java.lang.annotation.*;

/**
 * @author plus.wang
 * @description lock注解
 * @date 2020/4/2
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {
    String key() default "";

    String prefix() default "";

    int ttl() default 3;

    String cache() default "";
}
