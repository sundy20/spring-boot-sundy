package com.sundy.boot.web.filter;

import com.sundy.boot.utils.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 通用拦截  编码处理  跨域处理 log 处理
 */
@Component
public class CommonFilter implements Filter {

    private static final String DOMAIN = "testdomain.com";

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) req;

        HttpServletResponse httpServletResponse = (HttpServletResponse) resp;

        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));

        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");

        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));

        httpServletResponse.setHeader("Access-Control-Expose-Headers", "Set-Cookie");

        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {

            httpServletResponse.setStatus(HttpStatus.OK.value());

            return;
        }

        encodeFilter(httpServletRequest, httpServletResponse, chain);

    }

    private void encodeFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {

        req.setCharacterEncoding("UTF-8");

        resp.setCharacterEncoding("UTF-8");

        CookieUtil.getOrAddJuuid(req, resp, Integer.MAX_VALUE, DOMAIN);

        chain.doFilter(req, resp);
    }

}
