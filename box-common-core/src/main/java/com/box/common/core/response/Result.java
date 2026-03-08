package com.box.common.core.response;

import com.box.common.core.constant.CommonConstants;
import com.box.common.core.enums.ErrorCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应体。
 */
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(CommonConstants.SUCCESS_CODE, CommonConstants.SUCCESS_MESSAGE, data);
    }

    public static <T> Result<T> ok(String message, T data) {
        return new Result<>(CommonConstants.SUCCESS_CODE, message, data);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(ErrorCode errorCode) {
        return fail(errorCode.code(), errorCode.message());
    }

    public static <T> Result<T> fail(ErrorCode errorCode, String message) {
        return fail(errorCode.code(), message);
    }

    public boolean isSuccess() {
        return code == CommonConstants.SUCCESS_CODE;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
