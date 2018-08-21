package com.sundy.boot.annotation;


import java.lang.annotation.*;

/**
 * @author plus.wang
 * @description http 防重注解
 * @date 2018/5/14
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckReqNo {

    /**
     * 防重http接口描述
     */
    String desc() default "";
}
