package com.sundy.boot.jettison.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 货品模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsBO extends BaseBO {

    private static final long serialVersionUID = 6540092474865052770L;

    private List<Map<String, Object>> goodsData;

    /**
     * 召回的数据
     */
    private RecallBO recallBO;
}
