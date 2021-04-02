package com.sundy.boot.jettison.enums;

public enum GoodsTypeEnum {
    TEST("TEST", 0, "测试");

    private String type;
    private int activityType;
    private String description;

    GoodsTypeEnum(String type, int activityType, String description) {
        this.type = type;
        this.activityType = activityType;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public int getActivityType() {
        return activityType;
    }

    public String getDescription() {
        return description;
    }

    public static GoodsTypeEnum of(String type) {
        for (GoodsTypeEnum value : GoodsTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }
}
