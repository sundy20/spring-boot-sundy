package com.sundy.boot.inventory.domain;


public class Item {
    /**
     * 类型 freq/stock
     */
    private String type;
    private String unit;
    private Integer interval;
    private Long amount;
    /**
     * 结束时间
     */
    private Long endTime;

    public Item(String type, String unit, Integer interval, Long amount, Long endTime) {
        this.type = type;
        this.unit = unit;
        this.interval = interval;
        this.amount = amount;
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
