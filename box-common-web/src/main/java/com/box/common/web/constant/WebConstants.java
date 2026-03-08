package com.box.common.web.constant;

import com.box.common.core.constant.HeaderConstants;

/**
 * Web 常量。
 */
public final class WebConstants {

    public static final String TRACE_ID_HEADER = HeaderConstants.TRACE_ID;
    public static final String REQUEST_START_TIME = "BOX_REQUEST_START_TIME";
    public static final String REQUEST_CONTEXT = "BOX_REQUEST_CONTEXT";

    private WebConstants() {
    }
}
