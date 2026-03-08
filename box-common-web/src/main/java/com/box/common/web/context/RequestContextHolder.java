package com.box.common.web.context;

/**
 * 请求上下文。
 */
public final class RequestContextHolder {

    private static final ThreadLocal<RequestContext> CONTEXT_HOLDER = new ThreadLocal<>();

    private RequestContextHolder() {
    }

    public static void set(RequestContext context) {
        CONTEXT_HOLDER.set(context);
    }

    public static RequestContext get() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }

    public record RequestContext(String traceId, String requestUri, String clientIp, long startTime) {
    }
}
