package com.sundy.boot.exception;

/**
 * @author plus.wang
 * @description 系统异常
 * @date 2019-09-17
 */
public class SystemException extends RuntimeException {

    private String code;

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String code, String message) {
        this(message);
        this.setCode(code);
    }

    public SystemException(String code, String message, Throwable cause) {
        super(message, cause);
        this.setCode(code);
    }

    public SystemException(String code, Throwable cause) {
        super(cause);
        this.setCode(code);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
