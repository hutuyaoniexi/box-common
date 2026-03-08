package com.box.common.web.support;

import com.box.common.core.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servlet 请求工具。
 */
public final class ServletRequestUtils {

    private ServletRequestUtils() {
    }

    public static String getClientIp(HttpServletRequest request) {
        return IpUtils.getClientIp(request);
    }

    public static Map<String, String> getHeaders(HttpServletRequest request) {
        if (request == null) {
            return Collections.emptyMap();
        }
        Map<String, String> headers = new LinkedHashMap<>();
        java.util.Collections.list(request.getHeaderNames()).forEach(name -> headers.put(name, request.getHeader(name)));
        return headers;
    }

    public static String getCachedRequestBody(HttpServletRequest request, int maxLength) {
        if (!(request instanceof ContentCachingRequestWrapper wrapper)) {
            return null;
        }
        return toText(wrapper.getContentAsByteArray(), maxLength);
    }

    public static String getCachedResponseBody(ContentCachingResponseWrapper response, int maxLength) {
        if (response == null) {
            return null;
        }
        return toText(response.getContentAsByteArray(), maxLength);
    }

    private static String toText(byte[] content, int maxLength) {
        if (content == null || content.length == 0) {
            return null;
        }
        String value = new String(content, StandardCharsets.UTF_8);
        return value.length() <= maxLength ? value : value.substring(0, maxLength) + "...";
    }
}
