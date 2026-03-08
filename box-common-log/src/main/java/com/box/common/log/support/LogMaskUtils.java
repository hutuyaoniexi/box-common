package com.box.common.log.support;

import com.box.common.core.util.MaskUtils;

/**
 * 日志脱敏工具。
 */
public final class LogMaskUtils {

    private LogMaskUtils() {
    }

    public static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }

    public static String maskSensitive(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (value.matches("^1\\d{10}$")) {
            return MaskUtils.maskPhone(value);
        }
        if (value.contains("@")) {
            return MaskUtils.maskEmail(value);
        }
        if (value.matches("^\\d{15,18}[0-9Xx]?$")) {
            return MaskUtils.maskIdCard(value);
        }
        return value;
    }
}
