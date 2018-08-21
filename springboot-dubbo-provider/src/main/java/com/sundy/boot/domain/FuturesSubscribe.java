/**
 * Copyright (c) 2005-2016, cnfuelteam (fuelteam@163.com)"
 * <p>
 * Licensed under The MIT License (MIT)
 */
package com.sundy.boot.domain;

import java.util.Date;

/**
 * 描述： （futures_subscribe）
 * <p>
 * 作者： plus
 * 时间： 2017-11-28 16:40:34
 */
public class FuturesSubscribe {

    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订阅的合约号
     */
    private Long contractId;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 创建日期
     */
    private Date createAt;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 更新日期
     */
    private Date updateAt;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 是否删除  Y N
     */
    private String deleted;

    /**
     * 备注
     */
    private String remark;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
}