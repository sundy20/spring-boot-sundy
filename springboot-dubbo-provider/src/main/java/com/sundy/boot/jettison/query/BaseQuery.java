package com.sundy.boot.jettison.query;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class BaseQuery implements Serializable {

    private static final long serialVersionUID = -8929063495693916517L;

    /**
     * 请求来源
     */
    private String callSource;

    /**
     * 业务code
     */
    private String bizCode;

    /**
     * 偏移量
     */
    private Integer offset = 0;

    /**
     * 单页大小
     */
    private Integer limit = 10;

    /**
     * 客户端额外透传字段
     */
    private Map<String, Object> extra;
}
