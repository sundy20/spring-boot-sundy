package com.sundy.boot.jettison.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 召回模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecallBO extends BaseBO {

    private static final long serialVersionUID = 6540092474865052770L;

    /**
     * 召回code
     */
    private String recallCode;

    /**
     * 是否有下一页
     */
    private boolean hasNext;

    private String nextPageId = "";

    /**
     * 召回的key列表
     */
    private List<String> recallIds;

    /**
     * 召回的复合数据
     */
    private List<Map<String, Object>> recallData;

    /**
     * 业务请求扩展参数
     */
    private Map<String, Object> queryExtra;
}
