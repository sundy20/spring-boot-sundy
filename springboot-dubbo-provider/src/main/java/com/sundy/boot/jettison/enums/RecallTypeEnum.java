package com.sundy.boot.jettison.enums;

public enum RecallTypeEnum {
    FIXED("FIXED", "固定"),
    TRANSMIT("TRANSMIT", "透传");

    private String code;
    private String description;

    RecallTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RecallTypeEnum of(String code) {
        for (RecallTypeEnum value : RecallTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
