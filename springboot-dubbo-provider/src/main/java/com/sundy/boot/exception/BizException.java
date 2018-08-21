package com.sundy.boot.exception;

/**
 * Created on 2017/12/7
 *
 * @author plus.wang
 * @description 业务通用异常
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = -9079010183705787652L;

    private String errorCode;

    public BizException() {

    }

    public BizException(String message) {

        super(message);
    }

    public BizException(String errorCode, String message) {

        this(errorCode, message, null);
    }

    public BizException(String errorCode, String message, Exception exception) {

        super(message, exception);

        this.errorCode = errorCode;
    }

    public String getErrorCode() {

        return errorCode;
    }
}
