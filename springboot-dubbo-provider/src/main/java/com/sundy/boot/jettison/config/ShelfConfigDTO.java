package com.sundy.boot.jettison.config;

import com.sundy.boot.jettison.dto.BaseDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 货架配置
 */
@Data
public class ShelfConfigDTO extends BaseDTO {
    private static final long serialVersionUID = -2512158761865470472L;
    private Long priority;
    private Map<String, Object> target;
    private Map<String, Object> channel;
    private List<GoodsConfigDTO> goodsConfigs;
}
