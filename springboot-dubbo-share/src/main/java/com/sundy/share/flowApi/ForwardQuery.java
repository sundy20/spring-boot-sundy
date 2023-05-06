package com.sundy.share.flowApi;

import java.util.List;

/**
 * @author zeng.wang
 * @description 服务流交互动作请求参数
 */
public class ForwardQuery extends BaseQuery {

    private static final long serialVersionUID = 5581512162504388388L;

    /**
     * 业务code
     */
    private String bizCode;

    /**
     * 服务流code
     */
    private String flowCode;

    /**
     * ele用户信息
     */
    private UserParam userParam;

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 频次信息
     */
    private List<String> freqKey;

    /**
     * 频次Id
     */
    private List<String> freqId;

    /**
     * 启用用户颗粒度锁 默认true：启用 false：不启用
     */
    private Boolean userLockFlag = true;

    /**
     * 业务场景自定义细颗粒度锁bizId  userLockFlag=false的情况下生效
     */
    private String lockBizId;

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public UserParam getUserParam() {
        return userParam;
    }

    public void setUserParam(UserParam userParam) {
        this.userParam = userParam;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<String> getFreqKey() {
        return freqKey;
    }

    public void setFreqKey(List<String> freqKey) {
        this.freqKey = freqKey;
    }

    public List<String> getFreqId() {
        return freqId;
    }

    public void setFreqId(List<String> freqId) {
        this.freqId = freqId;
    }

    public Boolean getUserLockFlag() {
        return userLockFlag;
    }

    public void setUserLockFlag(Boolean userLockFlag) {
        this.userLockFlag = userLockFlag;
    }

    public String getLockBizId() {
        return lockBizId;
    }

    public void setLockBizId(String lockBizId) {
        this.lockBizId = lockBizId;
    }
}
