package com.box.common.web.advice;

import com.box.common.web.handler.GlobalResponseHandler;
import com.box.common.web.properties.WebProperties;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一响应包装。
 */
@RestControllerAdvice
public class ResponseBodyWrapAdvice implements ResponseBodyAdvice<Object> {

    private final WebProperties webProperties;

    public ResponseBodyWrapAdvice(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return webProperties.isResponseWrapEnabled() && GlobalResponseHandler.shouldWrap(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        return GlobalResponseHandler.wrapBody(body, returnType);
    }
}
