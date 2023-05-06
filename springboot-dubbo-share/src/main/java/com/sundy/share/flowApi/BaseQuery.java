package com.sundy.share.flowApi;

import java.io.Serializable;
import java.util.Map;

public class BaseQuery implements Serializable {

    private Map<String, Object> extra;

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }
}
