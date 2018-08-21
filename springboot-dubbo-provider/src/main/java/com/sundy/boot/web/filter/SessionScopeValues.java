package com.sundy.boot.web.filter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author plus.wang
 * @description 服务端会话信息
 * @date 2018/5/2
 */
public class SessionScopeValues implements Serializable {

    private static final long serialVersionUID = -2634810462430718262L;

    private String token;

    private String userAgent;

    private String ip;

    private Long userId;

    private String juuid;

    private long created;

    private long lasted;

    private Map<String, Object> stringObjectMap = new HashMap<>();

    public SessionScopeValues() {


    }

    public SessionScopeValues(String token, String userAgent, String ip, Long userId, String juuid) {

        this.token = token;

        this.userAgent = userAgent;

        this.ip = ip;

        this.userId = userId;

        this.juuid = juuid;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getJuuid() {
        return juuid;
    }

    public void setJuuid(String juuid) {
        this.juuid = juuid;
    }

    public Object get(String key) {

        return stringObjectMap.get(key);
    }

    public void put(String key, Object value) {

        stringObjectMap.put(key, value);
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getLasted() {
        return lasted;
    }

    public void setLasted(long lasted) {
        this.lasted = lasted;
    }
}
