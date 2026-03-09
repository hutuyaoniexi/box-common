package com.box.common.security.token;

import com.box.common.security.properties.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

/**
 * JWT 提供者：负责签发、解析、基础校验。
 */
public class JwtTokenProvider {

    public static final String CLAIM_TYP = "typ";
    public static final String CLAIM_AUD = "aud";

    public static final String TYP_ACCESS = "access";
    public static final String TYP_REFRESH = "refresh";

    private final SecurityProperties securityProperties;
    private final SecurityProperties.Jwt jwtProps;
    private final SecurityProperties.Claims claimsProps;
    private final SecretKey secretKey;

    public JwtTokenProvider(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.jwtProps = securityProperties.getJwt();
        this.claimsProps = securityProperties.getClaims();

        if (this.jwtProps == null) {
            throw new IllegalArgumentException("box.security.jwt config must not be null");
        }

        if (!StringUtils.hasText(this.jwtProps.getSecret())) {
            throw new IllegalArgumentException("box.security.jwt.secret must not be blank");
        }

        byte[] secretBytes = this.jwtProps.getSecret().getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalArgumentException("box.security.jwt.secret must be at least 32 bytes for HS256");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * 生成 Access Token。
     *
     * @param userId 用户ID
     * @param userName 用户名
     * @param permissions 权限集合
     * @return access token
     */
    public String generateAccessToken(Long userId, String userName, Collection<String> permissions) {
        return buildToken(userId, userName, permissions, TYP_ACCESS, jwtProps.getAccessTtlSeconds(),null);
    }

    /**
     * 生成 Refresh Token。
     *
     * @param userId 用户ID
     * @return refresh token
     */
    public String generateRefreshToken(Long userId,String sessionJti) {
        if (!jwtProps.isRefreshEnabled()) {
            throw new IllegalStateException("refresh token is disabled");
        }
        return buildToken(userId, null, null, TYP_REFRESH, jwtProps.getRefreshTtlSeconds(),sessionJti);
    }

    private String buildToken(Long userId,
                              String userName,
                              Collection<String> permissions,
                              String typ,
                              long ttlSeconds,
                              String sessionJti) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (!StringUtils.hasText(typ)) {
            throw new IllegalArgumentException("typ must not be blank");
        }
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("ttlSeconds must be > 0");
        }

        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();

        List<String> audList = jwtProps.getAudience() == null
                ? List.of()
                : new ArrayList<>(jwtProps.getAudience());

        var builder = Jwts.builder()
                .id(jti)
                .issuer(jwtProps.getIssuer())
                .subject(String.valueOf(userId))
                .claim(CLAIM_TYP, typ)
                .claim("session_jti", sessionJti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(secretKey);

        if (!audList.isEmpty()) {
            builder.claim(CLAIM_AUD, audList);
        }

        if (TYP_ACCESS.equals(typ)) {
            builder.claim(claimsProps.getUserId(), String.valueOf(userId));

            if (StringUtils.hasText(userName)) {
                builder.claim(claimsProps.getUserName(), userName);
            }

            if (permissions != null && !permissions.isEmpty()) {
                builder.claim(claimsProps.getPermissions(), new ArrayList<>(permissions));
            }
        }

        return builder.compact();
    }

    /**
     * 解析并校验签名、iss、exp 等基础字段。
     */
    public Claims parseAndValidate(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("token must not be blank");
        }

        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(jwtProps.getIssuer())
                    .clockSkewSeconds(jwtProps.getClockSkewSeconds())
                    .build()
                    .parseSignedClaims(token);

            return jws.getPayload();
        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid JWT", e);
        }
    }

    /**
     * 校验 audience。
     */
    public void validateAudience(Claims claims) {
        List<String> allowed = jwtProps.getAudience();
        if (allowed == null || allowed.isEmpty()) {
            return;
        }

        List<String> tokenAudience = extractAudience(claims);
        boolean matched = tokenAudience.stream().anyMatch(allowed::contains);
        if (!matched) {
            throw new IllegalArgumentException("invalid audience");
        }
    }

    /**
     * 校验 token 类型。
     */
    public void validateType(Claims claims, String expectedType) {
        if (!StringUtils.hasText(expectedType)) {
            return;
        }

        String actualType = claims.get(CLAIM_TYP, String.class);
        if (!expectedType.equals(actualType)) {
            throw new IllegalArgumentException("token type is not " + expectedType);
        }
    }

    /**
     * 从 claims 中提取 audience。
     */
    private List<String> extractAudience(Claims claims) {
        Object audObj;
        try {
            audObj = claims.getAudience();
        } catch (Exception e) {
            audObj = null;
        }

        List<String> standard = normalizeAudienceObject(audObj);
        if (!standard.isEmpty()) {
            return standard;
        }

        return normalizeAudienceObject(claims.get(CLAIM_AUD));
    }

    private List<String> normalizeAudienceObject(Object audObj) {
        if (audObj == null) {
            return List.of();
        }

        if (audObj instanceof String text) {
            return StringUtils.hasText(text) ? List.of(text) : List.of();
        }

        if (audObj instanceof Collection<?> collection) {
            List<String> list = new ArrayList<>();
            for (Object item : collection) {
                if (item == null) {
                    continue;
                }
                String value = String.valueOf(item);
                if (StringUtils.hasText(value)) {
                    list.add(value);
                }
            }
            return list;
        }

        String value = String.valueOf(audObj);
        return StringUtils.hasText(value) ? List.of(value) : List.of();
    }

    /**
     * 从请求头中解析 token。
     */
    public static String resolveToken(String headerValue, String tokenPrefix) {
        if (!StringUtils.hasText(headerValue)) {
            return null;
        }

        String value = headerValue.trim();
        if (!StringUtils.hasText(tokenPrefix)) {
            return value.isEmpty() ? null : value;
        }

        String prefix = tokenPrefix.trim();
        if (!value.regionMatches(true, 0, prefix, 0, prefix.length())) {
            return null;
        }

        String token = value.substring(prefix.length()).trim();
        if (token.startsWith(":")) {
            token = token.substring(1).trim();
        }

        return token.isEmpty() ? null : token;
    }

    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }
}
