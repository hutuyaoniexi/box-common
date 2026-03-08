package com.box.common.web.config;

import com.box.common.web.interceptor.RequestLogInterceptor;
import com.box.common.web.properties.WebProperties;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 配置。
 */
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestLogInterceptor requestLogInterceptor;
    private final WebProperties webProperties;

    public WebMvcConfig(RequestLogInterceptor requestLogInterceptor, WebProperties webProperties) {
        this.requestLogInterceptor = requestLogInterceptor;
        this.webProperties = webProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(webProperties.getExcludePaths());
    }
}
