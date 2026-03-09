package com.box.common.log.aspect;

import com.box.common.core.constant.HeaderConstants;
import com.box.common.core.util.DateUtils;
import com.box.common.core.util.JsonUtils;
import com.box.common.log.annotation.OperateLog;
import com.box.common.log.context.LogContext;
import com.box.common.log.model.OperateLogRecord;
import com.box.common.log.properties.LogProperties;
import com.box.common.log.publisher.OperateLogPublisher;
import com.box.common.log.support.LogExpressionEvaluator;
import com.box.common.log.support.LogMaskUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 操作日志切面。
 */
@Aspect
public class OperateLogAspect {

    private final OperateLogPublisher publisher;
    private final LogExpressionEvaluator evaluator;
    private final LogProperties properties;

    public OperateLogAspect(OperateLogPublisher publisher, LogExpressionEvaluator evaluator, LogProperties properties) {
        this.publisher = publisher;
        this.evaluator = evaluator;
        this.properties = properties;
    }

    @Around("@annotation(com.box.common.log.annotation.OperateLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!properties.isEnabled() || !properties.isOperateEnabled()) {
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
            publish(joinPoint, result, error, System.currentTimeMillis() - start);
        }
    }

    private void publish(ProceedingJoinPoint joinPoint, Object result, Throwable error, long durationMs) {
        Method method = resolveMethod(joinPoint);
        OperateLog operateLog = AnnotationUtils.findAnnotation(method, OperateLog.class);
        if (operateLog == null) {
            return;
        }
        HttpServletRequest request = currentRequest();
        OperateLogRecord record = new OperateLogRecord();
        record.setTraceId(request == null ? null : request.getHeader(HeaderConstants.TRACE_ID));
        record.setModule(blankToNull(operateLog.module()));
        record.setAction(operateLog.action());
        record.setBizNo(resolveValue(operateLog.bizNo(), method, joinPoint, result, error));
        record.setOperatorId(resolveOperatorId(operateLog, method, joinPoint, result, error));
        record.setOperatorName(resolveOperatorName(operateLog, method, joinPoint, result, error));
        record.setRequestUri(request == null ? null : request.getRequestURI());
        //record.setClientIp(IpUtils.getClientIp(request));
        record.setSuccess(resolveSuccess(operateLog, method, joinPoint, result, error));
        record.setDurationMs(durationMs);
        record.setRequestData(operateLog.logArgs() ? truncateJson(joinPoint.getArgs()) : null);
        record.setResponseData(operateLog.logResult() ? truncateJson(result) : null);
        record.setErrorMessage(error == null ? null : LogMaskUtils.truncate(error.getMessage(), properties.getMaxBodyLength()));
        record.setExtra(resolveValue(operateLog.extra(), method, joinPoint, result, error));
        record.setOperateTime(DateUtils.now());
        publisher.publish(record);
    }

    private String resolveOperatorId(OperateLog operateLog, Method method, ProceedingJoinPoint joinPoint, Object result, Throwable error) {
        String operatorId = resolveValue(operateLog.operatorId(), method, joinPoint, result, error);
        return operatorId != null ? operatorId : blankToNull(LogContext.getOperatorId());
    }

    private String resolveOperatorName(OperateLog operateLog, Method method, ProceedingJoinPoint joinPoint, Object result, Throwable error) {
        String operatorName = resolveValue(operateLog.operatorName(), method, joinPoint, result, error);
        return operatorName != null ? operatorName : blankToNull(LogContext.getOperatorName());
    }

    private boolean resolveSuccess(OperateLog operateLog, Method method, ProceedingJoinPoint joinPoint, Object result, Throwable error) {
        if (error != null) {
            return false;
        }
        String expression = operateLog.success();
        if (expression == null || expression.isBlank()) {
            return true;
        }
        String value = resolveValue(expression, method, joinPoint, result, error);
        return Boolean.parseBoolean(value);
    }

    private String resolveValue(String expression, Method method, ProceedingJoinPoint joinPoint, Object result, Throwable error) {
        String value = evaluator.evaluate(expression, method, joinPoint.getTarget(), joinPoint.getArgs(), result, error);
        return blankToNull(value);
    }

    private String truncateJson(Object value) {
        return LogMaskUtils.truncate(JsonUtils.toJson(value), properties.getMaxBodyLength());
    }

    private Method resolveMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return AopUtils.getMostSpecificMethod(signature.getMethod(), joinPoint.getTarget().getClass());
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}
