package com.sundy.boot.web.filter;

import com.sundy.boot.utils.WebUtil;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created on 2017/12/28
 *
 * @author plus.wang
 * @description
 */
public class RestInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        if (WebUtil.isIE(request)) {

            response.setHeader("Content-Type", "text/html;charset=UTF-8");

        } else {

            response.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        }


        super.postHandle(request, response, handler, modelAndView);
    }

}
