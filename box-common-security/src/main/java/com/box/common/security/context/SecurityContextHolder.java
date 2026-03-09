package com.box.common.security.context;

import com.box.common.core.enums.ErrorCode;
import com.box.common.core.exception.BaseException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

/**
 * 安全上下文持有器。
 */
public final class SecurityContextHolder {

    private static final ThreadLocal<SecurityUser> USER_HOLDER = new ThreadLocal<>();

    private SecurityContextHolder() {
    }

    public static void set(SecurityUser user) {
        if (user == null) {
            USER_HOLDER.remove();
            return;
        }
        USER_HOLDER.set(user);
    }

    public static SecurityUser get() {
        SecurityUser user = USER_HOLDER.get();
        if (user != null) {
            return user;
        }
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        return principal instanceof SecurityUser securityUser ? securityUser : null;
    }

    public static Optional<SecurityUser> optional() {
        return Optional.ofNullable(get());
    }

    public static SecurityUser required() {
        SecurityUser user = get();
        if (user == null || !user.isAuthenticated()) {
            throw new BaseException(ErrorCode.UNAUTHORIZED);
        }
        return user;
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}
