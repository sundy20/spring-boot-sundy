package com.sundy.boot.jettison.bo;


import java.io.Serializable;
import java.util.Map;

public abstract class BaseBO implements Serializable {

    private static final long serialVersionUID = -3603819692886256573L;

    private boolean status;

    protected Map<String, Object> attribute;

    public Map<String, Object> getAttribute() {
        return attribute;
    }

    public void setAttribute(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
