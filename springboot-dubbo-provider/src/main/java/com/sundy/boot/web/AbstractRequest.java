package com.sundy.boot.web;

import java.io.Serializable;

/**
 * @author plus.wang
 * @description 抽象请求基础类
 * @date 2018/5/14
 */
public abstract class AbstractRequest implements Serializable {

    private static final long serialVersionUID = 5051234786740848723L;
    /**
     * 请求号
     */
    private String reqNo;

    /**
     * 当前请求的时间戳
     */
    private long timeStamp;


    public AbstractRequest() {
        this.timeStamp = System.currentTimeMillis();
    }

    public String getReqNo() {
        return reqNo;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}