package com.box.common.web.interceptor;

import com.box.common.web.context.RequestContextHolder;
import com.box.common.web.context.TraceContext;
import com.box.common.web.properties.WebProperties;
import com.box.common.web.util.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 轻量请求日志拦截器。
 */
public class RequestLogInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLogInterceptor.class);

    private final WebProperties webProperties;

    public RequestLogInterceptor(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!webProperties.isRequestLoggingEnabled()) {
            return true;
        }
        RequestContextHolder.set(new RequestContextHolder.RequestContext(
                TraceContext.getTraceId(),
                request.getRequestURI(),
                ServletUtils.getClientIp(request),
                System.currentTimeMillis()
        ));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!webProperties.isRequestLoggingEnabled()) {
            return;
        }
        RequestContextHolder.RequestContext context = RequestContextHolder.get();
        if (context == null) {
            return;
        }
        long cost = System.currentTimeMillis() - context.startTime();
        if (cost >= 1000L) {
            log.warn("traceId={}, method={}, uri={}, status={}, cost={}ms, ip={}",
                    context.traceId(), request.getMethod(), request.getRequestURI(), response.getStatus(), cost, context.clientIp());
        } else {
            log.info("traceId={}, method={}, uri={}, status={}, cost={}ms, ip={}",
                    context.traceId(), request.getMethod(), request.getRequestURI(), response.getStatus(), cost, context.clientIp());
        }
    }
}
