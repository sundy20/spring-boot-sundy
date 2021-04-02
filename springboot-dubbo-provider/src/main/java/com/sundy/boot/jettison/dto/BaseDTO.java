package com.sundy.boot.jettison.dto;

import java.io.Serializable;
import java.util.Map;

public abstract class BaseDTO implements Serializable {

    protected Map<String, Object> attribute;

    public Map<String, Object> getAttribute() {
        return this.attribute;
    }

    public void setAttribute(Map<String, Object> attribute) {
        this.attribute = attribute;
    }
}