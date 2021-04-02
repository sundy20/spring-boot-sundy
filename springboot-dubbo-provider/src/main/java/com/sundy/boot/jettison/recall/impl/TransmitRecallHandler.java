package com.sundy.boot.jettison.recall.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.sundy.boot.jettison.enums.RecallTypeEnum;
import com.sundy.boot.jettison.constant.GoodsConstant;
import com.sundy.boot.jettison.recall.RecallHandler;
import com.sundy.boot.jettison.bo.RecallParam;
import com.sundy.boot.jettison.bo.RecallBO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author zeng.wang
 * @description 透传召回
 */
@Component
public class TransmitRecallHandler implements RecallHandler<RecallBO> {

    /**
     * 获得召回code
     */
    @Override
    public String getRecallCode() {
        return RecallTypeEnum.TRANSMIT.getCode();
    }

    /**
     * 召回处理
     */
    @Override
    public RecallBO handleRecall(RecallParam recallParam) {
        Map<String, Object> queryExtra = recallParam.getQueryExtra();
        RecallBO recallBO = RecallBO.builder().recallCode(getRecallCode()).build();
        recallBO.setRecallIds(Collections.emptyList());
        recallBO.setRecallData(Collections.emptyList());
        recallBO.setAttribute(recallParam.getExtra());
        recallBO.setQueryExtra(queryExtra);
        if (!CollectionUtils.isEmpty(queryExtra)) {
            Object o = queryExtra.get(GoodsConstant.GOODS_KEYS);
            if (Objects.nonNull(o)) {
                List<String> goodsKeys = JSONObject.parseObject(o.toString(), new TypeReference<List<String>>() {
                });
                recallBO.setStatus(true);
                recallBO.setRecallIds(goodsKeys);
            }
        }
        return recallBO;
    }
}
