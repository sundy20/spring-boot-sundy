package com.sundy.boot.web;

import com.sundy.boot.exception.BizException;
import com.sundy.boot.exception.UnauthorizedException;
import com.sundy.share.dto.Result;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionController {

    /**
     * 捕捉shiro的异常
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public Result<String> handle401(ShiroException e) {

        return Result.failure("401", e.getMessage());
    }

    /**
     * 捕捉UnauthorizedException
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Result<String> handle401() {

        return Result.failure("401", "Unauthorized");
    }

    /**
     * 业务异常处理
     */
    @ExceptionHandler(value = BizException.class)
    public Result<String> defaultErrorHandler(BizException e) {

        return Result.failure(e.getErrorCode(), e.getMessage());
    }

    /**
     * 捕捉其他所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> globalException(HttpServletRequest request, Throwable ex) {

        return Result.failure(getStatus(request).toString(), ex.getMessage());
    }

    private HttpStatus getStatus(HttpServletRequest request) {

        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (statusCode == null) {

            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return HttpStatus.valueOf(statusCode);
    }
}

