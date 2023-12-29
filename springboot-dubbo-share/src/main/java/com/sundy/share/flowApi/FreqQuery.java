package com.sundy.share.flowApi;

import java.util.List;

public class FreqQuery extends BaseQuery {

    private List<String> bizIds;

    private List<String> bizKeys;

    /**
     * 是否需要频次库存分离，忽略库存，只回滚频次
     */
    private Boolean ignoreReverseStock;

    public List<String> getBizIds() {
        return bizIds;
    }

    public void setBizIds(List<String> bizIds) {
        this.bizIds = bizIds;
    }

    public List<String> getBizKeys() {
        return bizKeys;
    }

    public void setBizKeys(List<String> bizKeys) {
        this.bizKeys = bizKeys;
    }

    public Boolean getIgnoreReverseStock() {
        return ignoreReverseStock;
    }

    public void setIgnoreReverseStock(Boolean ignoreReverseStock) {
        this.ignoreReverseStock = ignoreReverseStock;
    }
}