package com.sundy.boot.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created on 2017/11/17
 *
 * @author plus.wang
 * @description
 */
public class CookieUtil {

    public static void create(HttpServletResponse httpServletResponse, String name, String value, Boolean httponly, Boolean secure, Integer maxAge, String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(secure);
        cookie.setHttpOnly(httponly);
        cookie.setMaxAge(maxAge);
        cookie.setDomain(domain);
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }

    public static String getOrAddJuuid(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Integer maxAge, String domain) {

        String juuid = getValue(httpServletRequest, "juuid");

        if (StringUtils.isEmpty(juuid)) {
            juuid = CodecUtil.createUUID();
            Cookie cookie = new Cookie("juuid", juuid);
            cookie.setSecure(false);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(maxAge);
            cookie.setDomain(domain);
            cookie.setPath("/");
            httpServletResponse.addCookie(cookie);
        }
        return juuid;
    }

    public static void clear(HttpServletResponse httpServletResponse, String name, String domain) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setDomain(domain);
        httpServletResponse.addCookie(cookie);
    }

    public static String getValue(HttpServletRequest httpServletRequest, String name) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, name);
        return cookie != null ? cookie.getValue() : "";
    }

    public static String getAjaxHeaderValue(HttpServletRequest httpServletRequest, String headerName) {
        return httpServletRequest.getHeader(headerName);
    }
}