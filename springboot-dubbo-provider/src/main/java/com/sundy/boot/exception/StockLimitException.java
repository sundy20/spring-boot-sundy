package com.sundy.boot.exception;

public class StockLimitException extends RuntimeException {

    public StockLimitException(String message) {
        super(message);
    }

    public StockLimitException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
