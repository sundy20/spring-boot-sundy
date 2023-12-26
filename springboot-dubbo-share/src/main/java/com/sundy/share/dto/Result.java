package com.sundy.share.dto;

import com.sundy.share.enums.ResultCode;

import java.io.Serializable;
import java.util.Map;

/**
 * @author plus.wang
 * @description 通用响应对象
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 9131899105712058126L;

    private boolean success;

    private String msgCode;

    private String msgInfo;

    private T data;

    private Map<String, Object> bizExtMap;

    private Result() {

    }

    public static <T> Result<T> success(T data) {
        return new Result<T>().success(true).code(ResultCode.SUCCESS.getCode()).data(data).message(ResultCode.SUCCESS.getMessage());
    }

    public static <T> Result<T> success(String code, String message) {
        return new Result<T>().success(true).code(code).message(message);
    }

    public static <T> Result<T> failure() {
        return new Result<T>().success(false).code(ResultCode.SERVER_ERROR.getCode()).message(ResultCode.SERVER_ERROR.getMessage());
    }

    public static <T> Result<T> failure(String message) {
        return new Result<T>().success(false).code(ResultCode.SERVER_ERROR.getCode()).message(message);
    }

    public static <T> Result<T> failure(String code, String message) {
        return new Result<T>().success(false).code(code).message(message);
    }

    public static <T> Result<T> clientFailure() {
        return new Result<T>().success(false).code(ResultCode.CLIENT_ERROR.getCode()).message(ResultCode.CLIENT_ERROR.getMessage());
    }

    public static <T> Result<T> clientFailure(String message) {
        return new Result<T>().success(false).code(ResultCode.CLIENT_ERROR.getCode()).message(message);
    }

    public static <T> Result<T> clientFailure(String code, String message) {
        return new Result<T>().success(false).code(code).message(message);
    }

    public boolean isSucceed() {
        return success;
    }

    public T getData() {
        return data;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }

    public String getMsgInfo() {
        return msgInfo;
    }

    public Result<T> message(String message) {
        this.msgInfo = message;
        return this;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public Result<T> code(String code) {
        this.msgCode = code;
        return this;
    }

    public Result<T> success(boolean success) {
        this.success = success;
        return this;
    }

    public Map<String, Object> getBizExtMap() {
        return bizExtMap;
    }

    public void setBizExtMap(Map<String, Object> bizExtMap) {
        this.bizExtMap = bizExtMap;
    }
}
