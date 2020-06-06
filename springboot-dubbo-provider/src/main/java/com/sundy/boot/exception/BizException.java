package com.sundy.boot.exception;

/**
 * Created on 2017/12/7
 *
 * @author plus.wang
 * @description 业务通用异常
 */
public class BizException extends RuntimeException {

    private String errorCode;

    public BizException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public BizException(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum.getCode(), errorCodeEnum.getMsg());
    }

    public String getErrorCode() {
        return errorCode;
    }
}
