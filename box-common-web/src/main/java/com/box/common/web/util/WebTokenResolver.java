package com.box.common.web.util;

import com.box.common.core.util.StringUtils;
import com.box.common.security.constant.SecurityConstants;
import com.box.common.security.token.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;

public class WebTokenResolver {

    /**
     * 解析 token：优先 Authorization，再 Cookie
     */
    public static String resolveToken(HttpServletRequest request, String cookieName) {

        String authorization = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);

        String token = JwtTokenProvider.resolveToken(authorization, SecurityConstants.BEARER_PREFIX);
        if (StringUtils.hasText(token)) {
            return token;
        }

        return CookieUtils.getCookieValue(request, cookieName);
    }
}
