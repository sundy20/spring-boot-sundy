package com.sundy.boot.exception;

/**
 * @author plus.wang
 * @description 异常code枚举
 * @date 2019-09-17
 */
public enum ErrorCodeEnum {

    /**
     * 系统异常
     */
    SYSTEM_ERROR("system_error", "系统异常"),
    /**
     * 无法识别的业务类型
     */
    UNKNOW_BIZ_TYPE("unknow_biz_type", "无法识别的业务类型");

    private String code;

    private String msg;

    ErrorCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ErrorCodeEnum ofCode(String code) {
        ErrorCodeEnum[] errorCodeEnums = ErrorCodeEnum.values();
        for (ErrorCodeEnum errorCode : errorCodeEnums) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return UNKNOW_BIZ_TYPE;
    }
}
