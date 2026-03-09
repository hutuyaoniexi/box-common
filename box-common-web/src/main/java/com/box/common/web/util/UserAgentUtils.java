package com.box.common.web.util;

/**
 * User-Agent 解析工具。
 */
public final class UserAgentUtils {

    private UserAgentUtils() {
    }

    public static boolean isMobile(String userAgent) {
        if (userAgent == null) {
            return false;
        }
        String value = userAgent.toLowerCase();
        return value.contains("android") || value.contains("iphone") || value.contains("mobile");
    }

    public static String resolveOs(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        String value = userAgent.toLowerCase();
        if (value.contains("windows")) {
            return "Windows";
        }
        if (value.contains("mac os")) {
            return "macOS";
        }
        if (value.contains("android")) {
            return "Android";
        }
        if (value.contains("iphone") || value.contains("ipad")) {
            return "iOS";
        }
        if (value.contains("linux")) {
            return "Linux";
        }
        return "Unknown";
    }

    public static String resolveBrowser(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        String value = userAgent.toLowerCase();
        if (value.contains("edg/")) {
            return "Edge";
        }
        if (value.contains("chrome/")) {
            return "Chrome";
        }
        if (value.contains("firefox/")) {
            return "Firefox";
        }
        if (value.contains("safari/") && !value.contains("chrome/")) {
            return "Safari";
        }
        return "Unknown";
    }
}
