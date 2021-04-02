package com.sundy.boot.jettison.query;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 用户信息相关参数
 */
@Data
public class UserParam implements Serializable {

    private static final long serialVersionUID = -5241854989095738756L;

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
     * 客户端额外透传字段
     */
    private Map<String, Object> extra;
}
