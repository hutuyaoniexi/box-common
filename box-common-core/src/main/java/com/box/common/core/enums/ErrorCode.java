package com.box.common.core.enums;

/**
 * 统一错误码。
 */
public enum ErrorCode {

    INVALID_PARAM(1001, "参数不合法"),
    MISSING_PARAM(1002, "缺少必要参数"),
    UNAUTHORIZED(1003, "未授权访问"),
    FORBIDDEN(1004, "无权限执行该操作"),

    DUPLICATE_OPERATION(2001, "重复操作，请勿重复提交"),
    NOT_FOUND(2002, "资源不存在"),
    STATUS_NOT_ALLOWED(2003, "当前状态不允许此操作"),


    CODE_FORBIDDEN(40301,"权限不足"),



    INTERNAL_ERROR(9000, "系统繁忙，请稍后再试");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public boolean isSameCode(int value) {
        return this.code == value;
    }
}
