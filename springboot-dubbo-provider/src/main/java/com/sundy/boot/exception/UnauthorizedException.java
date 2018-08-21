package com.sundy.boot.exception;

/**
 * @author plus.wang
 * @description 未验证权限异常
 * @date 2018/5/7
 */
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 8460881761376399537L;

    public UnauthorizedException() {

        super();
    }

    public UnauthorizedException(String msg) {

        super(msg);
    }
}
