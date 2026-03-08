package com.box.common.core.constant;

/**
 * 常用请求头定义。
 */
public final class HeaderConstants {

    public static final String TRACE_ID = "X-Trace-Id";
    public static final String REQUEST_ID = "X-Request-Id";
    public static final String USER_ID = "X-User-Id";
    public static final String USER_NAME = "X-User-Name";
    public static final String FORWARDED_FOR = "X-Forwarded-For";
    public static final String REAL_IP = "X-Real-IP";
    public static final String USER_AGENT = "User-Agent";

    private HeaderConstants() {
    }
}
