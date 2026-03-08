package com.box.common.core.exception;

import com.box.common.core.enums.ErrorCode;
import com.box.common.core.response.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Iterator;

/**
 * 异常结果转换工具。
 */
public final class GlobalExceptionHandler {

    private GlobalExceptionHandler() {
    }

    public static Result<Void> toResult(Throwable throwable) {
        if (throwable instanceof BizException bizException) {
            return Result.fail(bizException.getCode(), bizException.getMessage());
        }
        if (throwable instanceof BaseException baseException) {
            return Result.fail(baseException.getCode(), baseException.getMessage());
        }
        if (throwable instanceof MethodArgumentNotValidException exception) {
            return Result.fail(ErrorCode.INVALID_PARAM, resolveBindingMessage(exception.getBindingResult()));
        }
        if (throwable instanceof BindException exception) {
            return Result.fail(ErrorCode.INVALID_PARAM, resolveBindingMessage(exception.getBindingResult()));
        }
        if (throwable instanceof ConstraintViolationException exception) {
            return Result.fail(ErrorCode.INVALID_PARAM, resolveViolationMessage(exception));
        }
        if (throwable instanceof MissingServletRequestParameterException exception) {
            return Result.fail(ErrorCode.MISSING_PARAM, exception.getParameterName() + " 不能为空");
        }
        if (throwable instanceof MethodArgumentTypeMismatchException exception) {
            return Result.fail(ErrorCode.INVALID_PARAM, exception.getName() + " 参数类型不正确");
        }
        if (throwable instanceof HttpMessageNotReadableException) {
            return Result.fail(ErrorCode.INVALID_PARAM);
        }
        if (throwable instanceof IllegalArgumentException exception) {
            return Result.fail(ErrorCode.INVALID_PARAM, exception.getMessage());
        }
        return Result.fail(ErrorCode.INTERNAL_ERROR);
    }

    public static String resolveBindingMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse(ErrorCode.INVALID_PARAM.message());
    }

    public static String resolveViolationMessage(ConstraintViolationException exception) {
        Iterator<ConstraintViolation<?>> iterator = exception.getConstraintViolations().iterator();
        return iterator.hasNext() ? iterator.next().getMessage() : ErrorCode.INVALID_PARAM.message();
    }
}
