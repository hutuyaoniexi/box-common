package com.box.common.core.util;

import com.box.common.core.constant.CommonConstants;
import com.box.common.core.constant.HeaderConstants;
import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 工具。
 */
public final class IpUtils {

    private static final String UNKNOWN = "unknown";

    private IpUtils() {
    }

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return CommonConstants.UNKNOWN;
        }
        String[] headerNames = {
                HeaderConstants.FORWARDED_FOR,
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                HeaderConstants.REAL_IP,
                "HTTP_X_FORWARDED_FOR",
                "HTTP_CLIENT_IP"
        };
        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (hasIp(ip)) {
                return normalizeIp(ip);
            }
        }
        return normalizeIp(request.getRemoteAddr());
    }

    public static String normalizeIp(String ip) {
        if (!hasIp(ip)) {
            return CommonConstants.UNKNOWN;
        }
        String clientIp = ip.split(",")[0].trim();
        return "0:0:0:0:0:0:0:1".equals(clientIp) ? "127.0.0.1" : clientIp;
    }

    public static boolean isLoopback(String ip) {
        String normalized = normalizeIp(ip);
        return "127.0.0.1".equals(normalized) || "localhost".equalsIgnoreCase(normalized);
    }

    private static boolean hasIp(String ip) {
        return ip != null && !ip.isBlank() && !UNKNOWN.equalsIgnoreCase(ip);
    }
}
