package com.box.common.web.util;

import com.box.common.security.constant.SecurityConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public final class CookieUtils {


    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request == null || cookieName == null) {
            return null;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }


    public static void writeCookie(HttpServletResponse response, String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void writeTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        writeCookie(response, SecurityConstants.ACCESS_TOKEN_COOKIE, accessToken);
        writeCookie(response, SecurityConstants.REFRESH_TOKEN_COOKIE, refreshToken);
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setMaxAge(0); // 关键：立即删除
        response.addCookie(cookie);
    }

    public static void clearTokenCookies(HttpServletResponse response) {
        deleteCookie(response, SecurityConstants.ACCESS_TOKEN_COOKIE);
        deleteCookie(response, SecurityConstants.REFRESH_TOKEN_COOKIE);
    }
}
