package com.box.common.datasource.aspect;

import com.box.common.datasource.annotation.DS;
import com.box.common.datasource.context.DynamicDataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * 基于注解的数据源切换切面。
 */
@Aspect
public class DynamicDatasourceAspect implements Ordered {

    @Around("@within(com.box.common.datasource.annotation.DS) || @annotation(com.box.common.datasource.annotation.DS)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String dataSourceKey = resolveDataSourceKey(joinPoint);
        if (dataSourceKey != null && !dataSourceKey.isBlank()) {
            DynamicDataSourceContextHolder.push(dataSourceKey);
        }
        try {
            return joinPoint.proceed();
        } finally {
            if (dataSourceKey != null && !dataSourceKey.isBlank()) {
                DynamicDataSourceContextHolder.poll();
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private String resolveDataSourceKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget() != null ? joinPoint.getTarget().getClass() : signature.getDeclaringType();
        Method method = AopUtils.getMostSpecificMethod(signature.getMethod(), targetClass);
        DS methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, DS.class);
        if (methodAnnotation != null) {
            return methodAnnotation.value();
        }
        DS typeAnnotation = AnnotatedElementUtils.findMergedAnnotation(targetClass, DS.class);
        return typeAnnotation != null ? typeAnnotation.value() : null;
    }
}
