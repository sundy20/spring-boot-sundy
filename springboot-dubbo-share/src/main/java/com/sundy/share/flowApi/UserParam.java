package com.sundy.share.flowApi;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 用户信息相关参数
 */
public class UserParam implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * ua
     */
    private String userAgent;

    /**
     * 渠道
     */
    private String channel;

    /**
     * 用户信息额外透传字段
     */
    private Map<String, Object> extra;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
