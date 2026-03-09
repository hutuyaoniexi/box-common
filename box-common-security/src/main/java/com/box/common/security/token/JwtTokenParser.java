package com.box.common.security.token;

import com.box.common.core.enums.ErrorCode;
import com.box.common.core.exception.BaseException;
import com.box.common.security.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

/**
 * JWT 解析器。
 *
 * <p>职责：</p>
 * <ul>
 *     <li>调用 JwtTokenProvider 完成解析与基础校验</li>
 *     <li>提取统一的 JwtTokenClaims</li>
 *     <li>按 access / refresh 场景做类型校验</li>
 * </ul>
 */
public class JwtTokenParser {

    /**
     * 兼容旧 token 的 claim 名。
     */
    private static final String LEGACY_CLAIM_USERNAME = "username";
    private static final String LEGACY_CLAIM_ROLES = "roles";

    private final SecurityProperties securityProperties;
    private final SecurityProperties.Jwt jwtProperties;
    private final SecurityProperties.Claims claimsProperties;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenParser(SecurityProperties securityProperties,
                          JwtTokenProvider jwtTokenProvider) {
        this.securityProperties = securityProperties;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = securityProperties.getJwt();
        this.claimsProperties = securityProperties.getClaims();
    }

    /**
     * 按 access token 规则解析。
     */
    public JwtTokenClaims parseAccessToken(String token) {
        return parse(token, JwtTokenProvider.TYP_ACCESS);
    }

    /**
     * 按 refresh token 规则解析。
     */
    public JwtTokenClaims parseRefreshToken(String token) {
        return parse(token, JwtTokenProvider.TYP_REFRESH);
    }

    /**
     * 通用解析。
     *
     * @param token JWT
     * @param expectedType 期望类型，可为 access / refresh；为空则不校验 typ
     */
    public JwtTokenClaims parse(String token, String expectedType) {
        if (!StringUtils.hasText(token)) {
            throw unauthorized("JWT 不能为空");
        }

        try {
            Claims claims = jwtTokenProvider.parseAndValidate(token);
            validateClaims(claims, expectedType);
            return buildClaims(claims);
        } catch (ExpiredJwtException exception) {
            throw unauthorized("登录状态已过期", exception);
        } catch (JwtException exception) {
            throw unauthorized("JWT 非法", exception);
        } catch (IllegalArgumentException exception) {
            throw unauthorized(
                    exception.getMessage() == null ? "JWT 非法" : exception.getMessage(),
                    exception
            );
        } catch (BaseException exception) {
            throw exception;
        } catch (Exception exception) {
            throw unauthorized("JWT 非法", exception);
        }
    }

    /**
     * 兼容旧调用：默认按 access token 解析。
     */
    public JwtTokenClaims parse(String token) {
        return parseAccessToken(token);
    }

    private void validateClaims(Claims claims, String expectedType) {
        validateTimeClaims(claims);
        validateAudience(claims);
        validateType(claims, expectedType);

        String userId = resolveUserId(claims);
        if (!StringUtils.hasText(userId)) {
            throw unauthorized("JWT 缺少用户标识");
        }
    }

    private void validateTimeClaims(Claims claims) {
        Instant now = Instant.now();
        long clockSkewSeconds = jwtProperties == null
                ? 0L
                : Math.max(jwtProperties.getClockSkewSeconds(), 0L);

        Instant issuedAt = toInstant(claims.getIssuedAt());
        Instant expiresAt = toInstant(claims.getExpiration());
        Instant notBefore = toInstant(claims.getNotBefore());

        if (expiresAt != null && expiresAt.plusSeconds(clockSkewSeconds).isBefore(now)) {
            throw unauthorized("登录状态已过期");
        }

        if (notBefore != null && notBefore.minusSeconds(clockSkewSeconds).isAfter(now)) {
            throw unauthorized("JWT 尚未生效");
        }

        if (issuedAt != null && expiresAt != null && issuedAt.isAfter(expiresAt)) {
            throw unauthorized("JWT 时间声明不合法");
        }
    }

    private void validateAudience(Claims claims) {
        try {
            jwtTokenProvider.validateAudience(claims);
        } catch (IllegalArgumentException exception) {
            throw unauthorized("JWT 接收方不匹配", exception);
        }
    }

    private void validateType(Claims claims, String expectedType) {
        if (!StringUtils.hasText(expectedType)) {
            return;
        }

        try {
            jwtTokenProvider.validateType(claims, expectedType);
        } catch (IllegalArgumentException exception) {
            throw unauthorized("JWT 类型不正确", exception);
        }
    }

    private JwtTokenClaims buildClaims(Claims claims) {
        JwtTokenClaims tokenClaims = new JwtTokenClaims();
        tokenClaims.setSubject(claims.getSubject());
        tokenClaims.setUserId(resolveUserId(claims));
        tokenClaims.setUsername(resolveUsername(claims));
        tokenClaims.setTenantId(resolveTenantId(claims));
        tokenClaims.setPermissions(resolvePermissions(claims));
        tokenClaims.setIssuedAt(toInstant(claims.getIssuedAt()));
        tokenClaims.setExpiresAt(toInstant(claims.getExpiration()));
        tokenClaims.setJti(claims.getId());
        tokenClaims.setSessionJti(claims.get("session_jti", String.class));
        tokenClaims.setClaims(claims);
        return tokenClaims;
    }

    private String resolveUserId(Claims claims) {
        if (claimsProperties != null && StringUtils.hasText(claimsProperties.getUserId())) {
            String userId = stringValue(claims.get(claimsProperties.getUserId()));
            if (StringUtils.hasText(userId)) {
                return userId;
            }
        }

        String subject = claims.getSubject();
        return StringUtils.hasText(subject) ? subject : null;
    }

    private String resolveUsername(Claims claims) {
        if (claimsProperties != null && StringUtils.hasText(claimsProperties.getUserName())) {
            String username = stringValue(claims.get(claimsProperties.getUserName()));
            if (StringUtils.hasText(username)) {
                return username;
            }
        }

        String legacyUsername = stringValue(claims.get(LEGACY_CLAIM_USERNAME));
        return StringUtils.hasText(legacyUsername) ? legacyUsername : null;
    }

    private String resolveTenantId(Claims claims) {
        if (claimsProperties == null || !StringUtils.hasText(claimsProperties.getTenantId())) {
            return null;
        }

        String tenantId = stringValue(claims.get(claimsProperties.getTenantId()));
        return StringUtils.hasText(tenantId) ? tenantId : null;
    }

    /**
     * 兼容 permissions claim / 旧 roles claim。
     */
    private List<String> resolvePermissions(Claims claims) {
        Set<String> result = new LinkedHashSet<>();

        if (claimsProperties != null && StringUtils.hasText(claimsProperties.getPermissions())) {
            result.addAll(toStringList(claims.get(claimsProperties.getPermissions())));
        }

        result.addAll(toStringList(claims.get(LEGACY_CLAIM_ROLES)));

        return List.copyOf(result);
    }

    private List<String> toStringList(Object value) {
        if (value == null) {
            return List.of();
        }

        if (value instanceof String text) {
            if (!StringUtils.hasText(text)) {
                return List.of();
            }

            String[] arr = text.split(",");
            List<String> list = new ArrayList<>(arr.length);
            for (String item : arr) {
                if (StringUtils.hasText(item)) {
                    list.add(item.trim());
                }
            }
            return list.stream().distinct().toList();
        }

        if (value instanceof Collection<?> collection) {
            List<String> list = new ArrayList<>(collection.size());
            for (Object item : collection) {
                String str = stringValue(item);
                if (StringUtils.hasText(str)) {
                    list.add(str.trim());
                }
            }
            return list.stream().distinct().toList();
        }

        String text = stringValue(value);
        return StringUtils.hasText(text) ? List.of(text.trim()) : List.of();
    }

    private Instant toInstant(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Instant instant) {
            return instant;
        }

        if (value instanceof java.util.Date date) {
            return date.toInstant();
        }

        if (value instanceof Number number) {
            long ts = number.longValue();
            if (String.valueOf(Math.abs(ts)).length() >= 13) {
                return Instant.ofEpochMilli(ts);
            }
            return Instant.ofEpochSecond(ts);
        }

        if (value instanceof String text && StringUtils.hasText(text)) {
            String trimmed = text.trim();

            try {
                long ts = Long.parseLong(trimmed);
                if (trimmed.length() >= 13) {
                    return Instant.ofEpochMilli(ts);
                }
                return Instant.ofEpochSecond(ts);
            } catch (NumberFormatException ignored) {
                try {
                    return Instant.parse(trimmed);
                } catch (Exception exception) {
                    throw unauthorized("JWT 时间声明格式错误", exception);
                }
            }
        }

        throw unauthorized("JWT 时间声明类型不支持");
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private BaseException unauthorized(String message) {
        return new BaseException(ErrorCode.UNAUTHORIZED, message);
    }

    private BaseException unauthorized(String message, Throwable cause) {
        return new BaseException(ErrorCode.UNAUTHORIZED.code(), message, cause);
    }
}
