package com.sundy.boot.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * Created by plus
 */
@Configuration
public class I18nConfig extends WebMvcConfigurerAdapter {

    @Bean
    public LocaleResolver localeResolver() {

        CookieLocaleResolver slr = new CookieLocaleResolver();

        //设置默认区域,
        slr.setDefaultLocale(Locale.CHINA);

        slr.setCookieMaxAge(Integer.MAX_VALUE);

        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {

        return new LocaleChangeInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(localeChangeInterceptor());
    }

}
