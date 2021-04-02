package com.sundy.boot.jettison.enums;

public enum ResTypeEnum {
    AGGR("aggr", "聚合"),
    RES("res", "资源");

    private String type;
    private String description;

    ResTypeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static ResTypeEnum of(String type) {
        for (ResTypeEnum value : ResTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
