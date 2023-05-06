package com.sundy.boot.jettison.bo;

import com.sundy.share.flowApi.UserParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author zeng.wang
 * @description 召回参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecallParam extends AbstractParam {

    private static final long serialVersionUID = 5019805154981755538L;

    /**
     * 召回code
     */
    private String recallCode;

    /**
     * 透传召回
     */
    private List<String> goodsKeys;

    /**
     * 用户信息
     */
    private UserParam userParam;

    /**
     * 业务请求扩展参数
     */
    private Map<String, Object> queryExtra;
}
