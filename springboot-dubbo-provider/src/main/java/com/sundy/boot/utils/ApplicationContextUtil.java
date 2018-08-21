package com.sundy.boot.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author plus.wang
 * @description 应用上下文
 * @date 2018/4/25
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {

        applicationContext = ctx;
    }

    public static ApplicationContext getApplicationContext() {

        return applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {

        return applicationContext.getBean(clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {

        return (T) applicationContext.getBean(name);
    }
}
