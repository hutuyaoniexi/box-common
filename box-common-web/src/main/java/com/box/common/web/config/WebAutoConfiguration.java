package com.box.common.web.config;

import com.box.common.web.advice.GlobalExceptionHandler;
import com.box.common.web.advice.ResponseBodyWrapAdvice;
import com.box.common.web.filter.RequestCachingFilter;
import com.box.common.web.filter.TraceIdFilter;
import com.box.common.web.interceptor.RequestLogInterceptor;
import com.box.common.web.properties.WebProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Web 自动配置。
 */
@AutoConfiguration
@Import(JacksonConfig.class)
@EnableConfigurationProperties(WebProperties.class)
public class WebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TraceIdFilter traceIdFilter(WebProperties webProperties) {
        return new TraceIdFilter(webProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistrationBean(TraceIdFilter traceIdFilter) {
        FilterRegistrationBean<TraceIdFilter> registrationBean = new FilterRegistrationBean<>(traceIdFilter);
        registrationBean.setOrder(-200);
        return registrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestCachingFilter requestCachingFilter(WebProperties webProperties) {
        return new RequestCachingFilter(webProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<RequestCachingFilter> requestCachingFilterRegistrationBean(RequestCachingFilter requestCachingFilter) {
        FilterRegistrationBean<RequestCachingFilter> registrationBean = new FilterRegistrationBean<>(requestCachingFilter);
        registrationBean.setOrder(-100);
        return registrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestLogInterceptor requestLogInterceptor(WebProperties webProperties) {
        return new RequestLogInterceptor(webProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebMvcConfig webMvcConfig(RequestLogInterceptor requestLogInterceptor, WebProperties webProperties) {
        return new WebMvcConfig(requestLogInterceptor, webProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ResponseBodyWrapAdvice responseBodyWrapAdvice(WebProperties webProperties) {
        return new ResponseBodyWrapAdvice(webProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
