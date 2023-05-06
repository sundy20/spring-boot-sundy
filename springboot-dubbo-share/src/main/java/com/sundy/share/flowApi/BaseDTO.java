package com.sundy.share.flowApi;


import java.io.Serializable;
import java.util.Map;

public abstract class BaseDTO implements Serializable {

    protected Map<String, Object> attribute;

    public Map<String, Object> getAttribute() {
        return attribute;
    }

    public void setAttribute(Map<String, Object> attribute) {
        this.attribute = attribute;
    }
}
