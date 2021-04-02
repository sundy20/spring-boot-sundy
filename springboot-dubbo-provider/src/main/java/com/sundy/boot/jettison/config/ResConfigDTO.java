package com.sundy.boot.jettison.config;

import com.sundy.boot.jettison.dto.BaseDTO;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 资源位配置
 */
@Data
public class ResConfigDTO extends BaseDTO {

    private static final long serialVersionUID = -2512158761865470472L;

    private String resCode;
    private String resName;
    private String resDesc;
    private String resType;
    private boolean pageable;
    private List<ShelfConfigDTO> shelfConfigs;
    /**
     * {type:topN,count:20}
     */
    private Map<String, Object> sortStrategy;
    private List<Map<String, Object>> freqKeys;
    private Map<String, GoodsConfigDTO> goodsConfigMap = new HashMap<>();
}
