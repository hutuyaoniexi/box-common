package com.box.common.log.support;

import org.slf4j.MDC;

import java.util.Map;

/**
 * MDC 工具。
 */
public final class MdcUtils {

    private MdcUtils() {
    }

    public static void put(String key, String value) {
        if (key != null && value != null && !value.isBlank()) {
            MDC.put(key, value);
        }
    }

    public static String get(String key) {
        return MDC.get(key);
    }

    public static void remove(String key) {
        MDC.remove(key);
    }

    public static void putAll(Map<String, String> contextMap) {
        if (contextMap != null && !contextMap.isEmpty()) {
            contextMap.forEach(MdcUtils::put);
        }
    }

    public static void clear() {
        MDC.clear();
    }
}
