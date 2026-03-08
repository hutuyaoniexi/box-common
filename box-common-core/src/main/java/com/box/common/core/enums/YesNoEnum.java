package com.box.common.core.enums;

import java.util.Arrays;

/**
 * 通用是/否枚举。
 */
public enum YesNoEnum {

    NO(0, "否"),
    YES(1, "是");

    private final int code;
    private final String description;

    YesNoEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public boolean isYes() {
        return this == YES;
    }

    public boolean isNo() {
        return this == NO;
    }

    public static YesNoEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported yes/no code: " + code));
    }
}
