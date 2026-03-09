package com.box.common.datasource.config;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * 仅在显式配置动态数据源时启用自动配置。
 */
public class DynamicDataSourceCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Binder binder = Binder.get(context.getEnvironment());
        Map<String, Object> datasources = binder.bind("box.datasource.datasources", Bindable.mapOf(String.class, Object.class))
                .orElse(null);
        return datasources != null && !datasources.isEmpty();
    }
}
