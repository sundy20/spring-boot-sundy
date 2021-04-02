package com.sundy.boot.jettison.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 下游请求参数基类
 */
@Data
public abstract class AbstractParam implements Serializable {

    private static final long serialVersionUID = 4319547484743530095L;

    /**
     * 偏移量
     */
    private Integer offset = 0;

    /**
     * 单页大小
     */
    private Integer limit = 10;

    /**
     * 扩展参数
     */
    private Map<String, Object> extra;
}
