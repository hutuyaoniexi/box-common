package com.box.common.core.util;

/**
 * 敏感信息脱敏工具。
 */
public final class MaskUtils {

    private MaskUtils() {
    }

    public static String mask(String value, int prefixLength, int suffixLength) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (value.length() <= prefixLength + suffixLength) {
            return "*".repeat(value.length());
        }
        int maskLength = value.length() - prefixLength - suffixLength;
        return value.substring(0, prefixLength) + "*".repeat(maskLength) + value.substring(value.length() - suffixLength);
    }

    public static String maskPhone(String phone) {
        return phone == null || phone.length() < 7 ? phone : mask(phone, 3, 4);
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int index = email.indexOf('@');
        String prefix = email.substring(0, index);
        String domain = email.substring(index);
        return mask(prefix, 1, 1) + domain;
    }

    public static String maskIdCard(String idCard) {
        return idCard == null || idCard.length() < 8 ? idCard : mask(idCard, 4, 4);
    }

    public static String maskName(String name) {
        if (name == null || name.isBlank()) {
            return name;
        }
        return name.length() == 1 ? "*" : name.charAt(0) + "*".repeat(name.length() - 1);
    }
}
