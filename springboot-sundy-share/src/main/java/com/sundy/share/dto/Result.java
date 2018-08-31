package com.sundy.share.dto;

import com.sundy.share.enums.ResultCode;

import java.io.Serializable;

/**
 * 响应对象
 *
 * @author plus
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 9131899105712058126L;

    private String message;

    private String code;

    private T data;

    private Result() {

    }

    public static <T> Result<T> success(T data) {

        return new Result<T>().code(ResultCode.SUCCESS.getCode()).data(data).message(ResultCode.SUCCESS.getMessage());
    }

    public static <T> Result<T> success(String code, String message) {

        return new Result<T>().code(code).message(message);
    }

    public static <T> Result<T> failure() {

        return new Result<T>().code(ResultCode.SERVER_ERROR.getCode()).message(ResultCode.SERVER_ERROR.getMessage());
    }

    public static <T> Result<T> failure(String message) {

        return new Result<T>().code(ResultCode.SERVER_ERROR.getCode()).message(message);
    }

    public static <T> Result<T> failure(String code, String message) {

        return new Result<T>().code(code).message(message);
    }

    public boolean succeed() {

        return this.code.equals(ResultCode.SUCCESS.getCode());
    }

    public T getData() {
        return data;
    }

    public Result<T> data(T data) {

        this.data = data;

        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> message(String message) {

        this.message = message;

        return this;
    }

    public String getCode() {
        return code;
    }

    public Result<T> code(String code) {

        this.code = code;

        return this;
    }

}
