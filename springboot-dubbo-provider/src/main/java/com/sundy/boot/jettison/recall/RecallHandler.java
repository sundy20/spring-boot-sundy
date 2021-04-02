package com.sundy.boot.jettison.recall;

import com.sundy.boot.jettison.bo.RecallParam;

/**
 * 召回处理器
 */
public interface RecallHandler<T> {

    /**
     * 获得召回code
     */
    String getRecallCode();

    /**
     * 召回处理
     */
    T handleRecall(RecallParam recallParam);
}
