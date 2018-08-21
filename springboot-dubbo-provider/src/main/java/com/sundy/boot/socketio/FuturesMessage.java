package com.sundy.boot.socketio;

import java.io.Serializable;

/**
 * @author plus.wang
 * @description 行情-合约对应数据
 * @date 2018/4/18
 */
public class FuturesMessage implements Serializable {

    private static final long serialVersionUID = -3875316257368526889L;

    //合约英文
    private String contract;

    private String nameZh;

    //最新报价
    private String lastPrice;

    //涨跌
    private String updown;

    private String updownPercent;

    //关联的合约id
    private Long contractId;

    //排序
    private Integer sort;

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getUpdown() {
        return updown;
    }

    public void setUpdown(String updown) {
        this.updown = updown;
    }

    public String getUpdownPercent() {
        return updownPercent;
    }

    public void setUpdownPercent(String updownPercent) {
        this.updownPercent = updownPercent;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
