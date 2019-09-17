package com.sundy.boot.exception;

/**
 * @author plus.wang
 * @description 未验证权限异常
 * @date 2018/5/7
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String msg) {
        super(msg);
    }
}
