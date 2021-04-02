package com.sundy.boot.jettison.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zeng.wang
 * @description 资源位货品数据聚合
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResGoodsDTO extends BaseDTO {

    private static final long serialVersionUID = 98875031482037616L;

    /**
     * 资源位code
     */
    private String resCode;

    /**
     * 资源位名称
     */
    private String resName;

    /**
     * 资源位类型
     * aggr：聚合资源位  res：资源
     */
    private String resType;

    /**
     * 资源位描述
     */
    private String resDesc;

    /**
     * 数据项列表
     */
    private List<DataItemDTO> data;
}
