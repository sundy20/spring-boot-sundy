package com.sundy.boot.jettison.query;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 资源位参数
 */
@Data
public class ResParam implements Serializable {

    private static final long serialVersionUID = 8042118440786362610L;

    /**
     * 资源位code
     */
    private String resCode;

    /**
     * 额外参数
     */
    private Map<String, Object> extra;
}
