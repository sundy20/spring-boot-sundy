package com.sundy.boot.freq.BO;

import lombok.Data;

@Data
public class ItemBO {
    private String type;
    private String unit;
    private Integer interval;
    private Long amount;
    private Long endTime; // 随排期有效
}
