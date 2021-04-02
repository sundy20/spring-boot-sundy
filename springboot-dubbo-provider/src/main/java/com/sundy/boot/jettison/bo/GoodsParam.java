package com.sundy.boot.jettison.bo;

import com.sundy.boot.jettison.query.UserParam;
import com.sundy.boot.jettison.config.GoodsConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zeng.wang
 * @description 货品请求参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsParam extends AbstractParam {

    private static final long serialVersionUID = 5019805154981755538L;

    /**
     * 货品类型
     */
    private String goodsType;

    /**
     * 货品key
     */
    private String goodsKey;

    /**
     * 用户信息
     */
    private UserParam userParam;

    /**
     * 召回的数据
     */
    private RecallBO recallBO;

    /**
     * 货品配置列表
     */
    private List<GoodsConfigDTO> goodsConfigs;
}
