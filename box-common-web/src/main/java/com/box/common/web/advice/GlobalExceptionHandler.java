package com.box.common.web.advice;

import com.box.common.core.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Web 全局异常处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Result<Void> handle(Exception exception, HttpServletRequest request) {
        Result<Void> result = com.box.common.web.handler.GlobalExceptionHandler.toResult(exception);
        if (result.getCode() >= 9000) {
            log.error("Unhandled exception, uri={}", request.getRequestURI(), exception);
        } else {
            log.warn("Business exception, uri={}, message={}", request.getRequestURI(), exception.getMessage());
        }
        return result;
    }
}
