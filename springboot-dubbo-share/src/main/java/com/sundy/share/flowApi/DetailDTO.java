package com.sundy.share.flowApi;

import java.time.LocalDateTime;
import java.util.Map;

public class DetailDTO extends BaseDTO {

    /**
     * 序列id
     */
    private Long seqId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 业务结果 0：业务成功 1：业务失败
     */
    private Integer bizResult;

    /**
     * 业务结果关联key
     */
    private String outKey;

    /**
     * 流明细
     */
    private Map<String, Object> detail;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    private LocalDateTime gmtModified;

    /**
     * 业务code
     */
    private String bizCode;

    /**
     * 业务动作标识
     */
    private String code;

    public Long getSeqId() {
        return seqId;
    }

    public void setSeqId(Long seqId) {
        this.seqId = seqId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getBizResult() {
        return bizResult;
    }

    public void setBizResult(Integer bizResult) {
        this.bizResult = bizResult;
    }

    public String getOutKey() {
        return outKey;
    }

    public void setOutKey(String outKey) {
        this.outKey = outKey;
    }

    public Map<String, Object> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
