package com.sundy.boot.jettison.recall.impl;

import com.sundy.boot.jettison.enums.RecallTypeEnum;
import com.sundy.boot.jettison.recall.RecallHandler;
import com.sundy.boot.jettison.bo.RecallParam;
import com.sundy.boot.jettison.bo.RecallBO;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author zeng.wang
 * @description 固定召回
 */
@Component
public class FixedRecallHandler implements RecallHandler<RecallBO> {

    /**
     * 获得召回code
     */
    @Override
    public String getRecallCode() {
        return RecallTypeEnum.FIXED.getCode();
    }

    /**
     * 召回处理
     */
    @Override
    public RecallBO handleRecall(RecallParam recallParam) {
        RecallBO recallBO = RecallBO.builder().recallCode(getRecallCode()).build();
        recallBO.setStatus(true);
        recallBO.setRecallIds(Collections.emptyList());
        recallBO.setRecallData(Collections.emptyList());
        recallBO.setAttribute(recallParam.getExtra());
        recallBO.setQueryExtra(recallParam.getQueryExtra());
        return recallBO;
    }
}
