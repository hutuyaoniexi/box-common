package com.box.common.log.config;

import com.box.common.log.aspect.OperateLogAspect;
import com.box.common.log.aspect.RequestLogAspect;
import com.box.common.log.properties.LogProperties;
import com.box.common.log.publisher.NoopOperateLogPublisher;
import com.box.common.log.publisher.OperateLogPublisher;
import com.box.common.log.support.LogExpressionEvaluator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 日志自动配置。
 */
@AutoConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ConditionalOnClass(name = "org.aspectj.lang.annotation.Aspect")
@EnableConfigurationProperties(LogProperties.class)
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LogExpressionEvaluator logExpressionEvaluator() {
        return new LogExpressionEvaluator();
    }

    @Bean
    @ConditionalOnMissingBean(OperateLogPublisher.class)
    public OperateLogPublisher operateLogPublisher() {
        return new NoopOperateLogPublisher();
    }

    @Bean
    @ConditionalOnProperty(prefix = "box.log", name = {"enabled", "operate-enabled"}, havingValue = "true", matchIfMissing = true)
    public OperateLogAspect operateLogAspect(OperateLogPublisher publisher,
                                             LogExpressionEvaluator evaluator,
                                             LogProperties properties) {
        return new OperateLogAspect(publisher, evaluator, properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = "box.log", name = {"enabled", "request-enabled"}, havingValue = "true", matchIfMissing = true)
    public RequestLogAspect requestLogAspect(LogProperties properties) {
        return new RequestLogAspect(properties);
    }
}
