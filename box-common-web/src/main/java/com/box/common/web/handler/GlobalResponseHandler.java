package com.box.common.web.handler;

import com.box.common.core.response.Result;
import com.box.common.core.util.JsonUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * 统一响应处理工具。
 */
public final class GlobalResponseHandler {

    private GlobalResponseHandler() {
    }

    public static boolean shouldWrap(MethodParameter returnType) {
        Class<?> parameterType = returnType.getParameterType();
        return !Result.class.isAssignableFrom(parameterType)
                && !ResponseEntity.class.isAssignableFrom(parameterType)
                && !Resource.class.isAssignableFrom(parameterType)
                && !byte[].class.equals(parameterType)
                && !StreamingResponseBody.class.isAssignableFrom(parameterType);
    }

    public static Object wrapBody(Object body, MethodParameter returnType) {
        if (body instanceof Result<?>) {
            return body;
        }
        Result<Object> result = Result.ok(body);
        if (String.class.equals(returnType.getParameterType())) {
            return JsonUtils.toJson(result);
        }
        return result;
    }
}
