package com.box.common.web.context;

import org.slf4j.MDC;

/**
 * Trace 上下文。
 */
public final class TraceContext {

    private static final String MDC_TRACE_ID = "traceId";
    private static final ThreadLocal<String> TRACE_ID_HOLDER = new ThreadLocal<>();

    private TraceContext() {
    }

    public static void setTraceId(String traceId) {
        TRACE_ID_HOLDER.set(traceId);
        if (traceId != null && !traceId.isBlank()) {
            MDC.put(MDC_TRACE_ID, traceId);
        }
    }

    public static String getTraceId() {
        return TRACE_ID_HOLDER.get();
    }

    public static void clear() {
        TRACE_ID_HOLDER.remove();
        MDC.remove(MDC_TRACE_ID);
    }
}
