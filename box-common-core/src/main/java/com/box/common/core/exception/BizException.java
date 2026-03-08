package com.box.common.core.exception;

import com.box.common.core.enums.ErrorCode;

/**
 * 业务异常。
 */
public class BizException extends BaseException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    public BizException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public BizException(ErrorCode errorCode, String message) {
        super(errorCode, message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public static BizException of(ErrorCode errorCode) {
        return new BizException(errorCode);
    }

    public static BizException of(ErrorCode errorCode, String message) {
        return new BizException(errorCode, message);
    }
}
