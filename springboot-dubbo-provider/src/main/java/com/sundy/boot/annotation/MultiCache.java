package com.sundy.boot.annotation;

import java.lang.annotation.*;

/**
 * @author zeng.wang
 * @description 批量缓存注解(针对于入参list返回map)
 * @date 2020/4/24
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultiCache {

    String prefix() default "";

    String configKey() default "";

    /**
     * 默认秒
     */
    int defaultTtl() default 60;

    String cache() default "";

    /**
     * 默认ms
     */
    long waitTimeout() default 0;

    String asyncExecutor() default "";

    Class modelType() default Object.class;

    String property() default "";
}
