package com.box.common.web.filter;

import com.box.common.web.properties.WebProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * 请求缓存过滤器。
 */
public class RequestCachingFilter extends OncePerRequestFilter {

    private final WebProperties webProperties;

    public RequestCachingFilter(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!webProperties.isRequestCachingEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper requestWrapper = request instanceof ContentCachingRequestWrapper wrapper
                ? wrapper : new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = response instanceof ContentCachingResponseWrapper wrapper
                ? wrapper : new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }
}
