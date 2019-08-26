package com.sundy.boot.web.config;

import com.sundy.boot.web.aop.CacheInterceptor;
import com.sundy.boot.web.filter.CommonFilter;
import com.sundy.boot.web.filter.JvmTPSLimiterFilter;
import com.sundy.boot.web.filter.RestInterceptor;
import com.sundy.boot.web.filter.XssFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc web 配置
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Value("${application.restFilterUrl}")
    private String restFilterPattern;

    @Value("${application.rate:5}")
    private String rate;

    /**
     * @param jvmTPSLimiterFilter 限流
     */
    @Bean
    public FilterRegistrationBean limitFilterRegistration(JvmTPSLimiterFilter jvmTPSLimiterFilter) {

        jvmTPSLimiterFilter.setRate(Integer.valueOf(rate));

        if (log.isInfoEnabled()) {

            log.info("server injvm limiter allow {} requests per second", rate);
        }

        FilterRegistrationBean registration = new FilterRegistrationBean(jvmTPSLimiterFilter);

        registration.addUrlPatterns("/*");

        return registration;
    }

    /**
     * @param commonFilter 拦截所有请求
     */
    @Bean
    public FilterRegistrationBean commonFilterRegistration(CommonFilter commonFilter) {

        FilterRegistrationBean registration = new FilterRegistrationBean(commonFilter);

        registration.addUrlPatterns("/*");

        return registration;
    }

    /**
     * @param xssFilter 拦截所有请求
     */
    @Bean
    public FilterRegistrationBean xssFilterRegistration(XssFilter xssFilter) {

        FilterRegistrationBean registration = new FilterRegistrationBean(xssFilter);

        registration.addUrlPatterns("/*");

        return registration;
    }

    @Bean
    public RestInterceptor initRestInterceptor() {

        return new RestInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 多个拦截器组成一个拦截器链 addPathPatterns 用于添加拦截规则 excludePathPatterns 用户排除拦截
        String[] urls = restFilterPattern.split(",");

        for (String url : urls) {

            registry.addInterceptor(initRestInterceptor()).addPathPatterns(url + "*");
        }

        super.addInterceptors(registry);
    }

    @Bean
    public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {

        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

        List<MediaType> mediaTypes = new ArrayList<>();

        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);

        mediaTypes.add(MediaType.TEXT_HTML);

        jsonConverter.setSupportedMediaTypes(mediaTypes);

        return jsonConverter;
    }
}
