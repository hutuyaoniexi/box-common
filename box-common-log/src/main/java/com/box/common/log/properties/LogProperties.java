package com.box.common.log.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志配置。
 */
@ConfigurationProperties(prefix = "box.log")
public class LogProperties {

    private boolean enabled = true;
    private boolean requestEnabled = true;
    private boolean operateEnabled = true;
    private int maxBodyLength = 2048;
    private long slowRequestThresholdMs = 3000L;
    private List<String> ignoreUrls = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRequestEnabled() {
        return requestEnabled;
    }

    public void setRequestEnabled(boolean requestEnabled) {
        this.requestEnabled = requestEnabled;
    }

    public boolean isOperateEnabled() {
        return operateEnabled;
    }

    public void setOperateEnabled(boolean operateEnabled) {
        this.operateEnabled = operateEnabled;
    }

    public int getMaxBodyLength() {
        return maxBodyLength;
    }

    public void setMaxBodyLength(int maxBodyLength) {
        this.maxBodyLength = maxBodyLength;
    }

    public long getSlowRequestThresholdMs() {
        return slowRequestThresholdMs;
    }

    public void setSlowRequestThresholdMs(long slowRequestThresholdMs) {
        this.slowRequestThresholdMs = slowRequestThresholdMs;
    }

    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }

    public void setIgnoreUrls(List<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls;
    }
}
