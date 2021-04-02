package com.sundy.boot.jettison.query;

import lombok.Data;

import java.util.List;

/**
 * @author zeng.wang
 * @description 流量供给查询
 */
@Data
public class ResQuery extends BaseQuery {

    private static final long serialVersionUID = -4130690616694574593L;

    /**
     * 标签参数列表
     */
    private List<ResParam> resParams;

    /**
     * 用户信息
     */
    private UserParam userParam;
}
