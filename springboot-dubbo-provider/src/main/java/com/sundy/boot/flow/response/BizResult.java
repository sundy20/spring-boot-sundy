package com.sundy.boot.flow.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zeng.wang
 * @description 通用业务结果对象
 */
@Data
public class BizResult<T> implements Serializable {

    private static final long serialVersionUID = -176984059971187440L;

    //是否成功
    private boolean success;

    //业务错误code
    private String errorCode;

    //错误消息
    private String errorMsg;

    //数据
    private T data;

    private BizResult() {
    }

    public static <T> BizResult<T> success(T data) {
        return new BizResult<T>().success(true).data(data);
    }

    public static <T> BizResult<T> bizFailure(String errorMsg) {
        return new BizResult<T>().success(false).errorMsg(errorMsg);
    }

    public static <T> BizResult<T> bizFailure(String errorCode, String errorMsg) {
        return new BizResult<T>().success(false).errorCode(errorCode).errorMsg(errorMsg);
    }

    public static <T> BizResult<T> bizFailure(String errorCode, String errorMsg, T data) {
        return new BizResult<T>().success(false).errorCode(errorCode).errorMsg(errorMsg).data(data);
    }

    public BizResult<T> data(T data) {
        this.data = data;
        return this;
    }

    public BizResult<T> errorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public BizResult<T> errorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public BizResult<T> success(boolean success) {
        this.success = success;
        return this;
    }
}
