package com.box.common.log.aspect;

import com.box.common.core.constant.HeaderConstants;
import com.box.common.core.util.DateUtils;
import com.box.common.core.util.IpUtils;
import com.box.common.core.util.JsonUtils;
import com.box.common.log.model.RequestLogRecord;
import com.box.common.log.properties.LogProperties;
import com.box.common.log.support.LogMaskUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 请求日志切面。
 */
@Aspect
public class RequestLogAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestLogAspect.class);

    private final LogProperties properties;

    public RequestLogAspect(LogProperties properties) {
        this.properties = properties;
    }

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled() || !properties.isRequestEnabled()) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = currentRequest();
        if (request == null || shouldIgnore(request.getRequestURI())) {
            return joinPoint.proceed();
        }
        long start = System.currentTimeMillis();
        Object result = null;
        Throwable error = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            error = throwable;
            throw throwable;
        } finally {
            writeLog(request, currentResponse(), result, error, System.currentTimeMillis() - start);
        }
    }

    private void writeLog(HttpServletRequest request, HttpServletResponse response, Object result, Throwable error, long durationMs) {
        RequestLogRecord record = new RequestLogRecord();
        record.setTraceId(request.getHeader(HeaderConstants.TRACE_ID));
        record.setMethod(request.getMethod());
        record.setRequestUri(request.getRequestURI());
        record.setQueryString(request.getQueryString());
        record.setClientIp(IpUtils.getClientIp(request));
        record.setUserAgent(request.getHeader(HeaderConstants.USER_AGENT));
        record.setStatus(response == null ? 200 : response.getStatus());
        record.setDurationMs(durationMs);
        record.setSuccess(error == null && (response == null || response.getStatus() < 500));
        record.setRequestBody(LogMaskUtils.truncate(JsonUtils.toJson(sanitizeArgs(request)), properties.getMaxBodyLength()));
        record.setResponseBody(result == null ? null : LogMaskUtils.truncate(JsonUtils.toJson(result), properties.getMaxBodyLength()));
        record.setRequestTime(DateUtils.now());
        if (durationMs >= properties.getSlowRequestThresholdMs()) {
            log.warn("slow-request={}", JsonUtils.toJson(record));
            return;
        }
        log.info("request-log={}", JsonUtils.toJson(record));
    }

    private Object[] sanitizeArgs(HttpServletRequest request) {
        return new Object[]{request.getMethod(), request.getRequestURI(), request.getParameterMap()};
    }

    private boolean shouldIgnore(String requestUri) {
        return properties.getIgnoreUrls().stream().anyMatch(requestUri::startsWith);
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private HttpServletResponse currentResponse() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getResponse();
        }
        return null;
    }
}
