package com.box.common.web.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Web 通用配置。
 */
@ConfigurationProperties(prefix = "box.web")
public class WebProperties {

    private boolean responseWrapEnabled = true;
    private boolean requestCachingEnabled = true;
    private boolean requestLoggingEnabled = true;
    private String traceIdHeader = "X-Trace-Id";
    private int maxPayloadLength = 2048;
    private List<String> excludePaths = new ArrayList<>(List.of("/error", "/favicon.ico", "/actuator"));

    public boolean isResponseWrapEnabled() {
        return responseWrapEnabled;
    }

    public void setResponseWrapEnabled(boolean responseWrapEnabled) {
        this.responseWrapEnabled = responseWrapEnabled;
    }

    public boolean isRequestCachingEnabled() {
        return requestCachingEnabled;
    }

    public void setRequestCachingEnabled(boolean requestCachingEnabled) {
        this.requestCachingEnabled = requestCachingEnabled;
    }

    public boolean isRequestLoggingEnabled() {
        return requestLoggingEnabled;
    }

    public void setRequestLoggingEnabled(boolean requestLoggingEnabled) {
        this.requestLoggingEnabled = requestLoggingEnabled;
    }

    public String getTraceIdHeader() {
        return traceIdHeader;
    }

    public void setTraceIdHeader(String traceIdHeader) {
        this.traceIdHeader = traceIdHeader;
    }

    public int getMaxPayloadLength() {
        return maxPayloadLength;
    }

    public void setMaxPayloadLength(int maxPayloadLength) {
        this.maxPayloadLength = maxPayloadLength;
    }

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }
}
