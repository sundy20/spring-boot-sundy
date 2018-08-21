package com.sundy.boot.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Created by plus
 */
@RestController
public class LanguageController {

    private static final Logger log = LoggerFactory.getLogger(LanguageController.class);

    @RequestMapping(value = "/rest/changeSessionLanauage", method = RequestMethod.GET)
    public String changeSessionLanauage(HttpServletRequest request, HttpServletResponse response, String lang) {

        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

        log.info("LanguageController.changeSessionLanauage 已切换语言为：{}", lang);

        if ("zh".equals(lang)) {

            localeResolver.setLocale(request, response, new Locale("zh", "CN"));

        } else if ("en".equals(lang)) {

            localeResolver.setLocale(request, response, new Locale("en", "US"));

        }

        return "redirect:/" + request.getHeader("Referer");
    }

}
