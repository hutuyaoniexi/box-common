package com.box.common.core.constant;

import java.nio.charset.StandardCharsets;

/**
 * 通用常量定义。
 */
public final class CommonConstants {

    public static final int SUCCESS_CODE = 200;
    public static final String SUCCESS_MESSAGE = "success";
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String UNKNOWN = "unknown";
    public static final String UTF_8 = StandardCharsets.UTF_8.name();

    private CommonConstants() {
    }
}
