package com.sundy.boot.flow.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author zeng.wang
 * @description 领域抽象模型
 */
public abstract class AbstractDomain implements Serializable {

    private static final long serialVersionUID = 2965335091616379435L;

    protected String domainInfo;

    protected List<String> outKeys;

    protected AbstractDomain() {
        setDomainInfo(this.initDomainInfo());
    }

    /**
     * 领域信息
     */
    protected abstract String initDomainInfo();

    public String getDomainInfo() {
        return domainInfo;
    }

    public void setDomainInfo(String domainInfo) {
        this.domainInfo = domainInfo;
    }

    public List<String> getOutKeys() {
        return outKeys;
    }

    public void setOutKeys(List<String> outKeys) {
        this.outKeys = outKeys;
    }
}
