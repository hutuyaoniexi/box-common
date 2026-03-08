package com.box.common.core.exception;

import com.box.common.core.enums.ErrorCode;

/**
 * 业务基础异常。
 */
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int code;

    public BaseException(ErrorCode errorCode) {
        this(errorCode.code(), errorCode.message());
    }

    public BaseException(ErrorCode errorCode, String message) {
        this(errorCode.code(), message);
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
