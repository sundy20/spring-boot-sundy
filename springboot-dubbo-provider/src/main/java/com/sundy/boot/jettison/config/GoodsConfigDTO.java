package com.sundy.boot.jettison.config;

import com.sundy.boot.jettison.dto.BaseDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 货品配置
 */
@Data
public class GoodsConfigDTO extends BaseDTO {
    private static final long serialVersionUID = -2512158761865470472L;
    private String goodsCode;
    private String goodsType;
    private boolean pageable;
    private Map<String, Object> freqMap;
    private Map<String, Object> bizParams;
    private Map<String, Object> recallStrategy;
    private List<Map<String, Object>> skuList;
}