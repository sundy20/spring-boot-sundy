package com.sundy.boot.jettison.check;

public enum CheckCodeEnum {
    RISK_CHECK("RISK_CHECK", "风控校验"),
    FREQ_CHECK("FREQ_CHECK", "频次库存校验");

    private String code;
    private String description;

    CheckCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static CheckCodeEnum of(String code) {
        for (CheckCodeEnum value : CheckCodeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
