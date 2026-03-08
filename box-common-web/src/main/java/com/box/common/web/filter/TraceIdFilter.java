package com.box.common.web.filter;

import com.box.common.web.context.TraceContext;
import com.box.common.web.properties.WebProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * TraceId 过滤器。
 */
public class TraceIdFilter extends OncePerRequestFilter {

    private final WebProperties webProperties;

    public TraceIdFilter(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceIdHeader = webProperties.getTraceIdHeader();
        String traceId = request.getHeader(traceIdHeader);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        TraceContext.setTraceId(traceId);
        response.setHeader(traceIdHeader, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TraceContext.clear();
            com.box.common.web.context.RequestContextHolder.clear();
        }
    }
}
