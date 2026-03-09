package com.box.common.security.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全组件配置。
 *
 * <p>统一管理接口鉴权场景下的基础安全配置、JWT 配置、Claims 配置。</p>
 * <p>该模块不提供默认登录页，登录应由业务系统自行实现接口，例如 `/auth/login`。</p>
 * box:
 *   security:
 *     enabled: true
 *     authenticate-all-paths: true
 *
 *     permit-paths:
 *       - /error
 *       - /favicon.ico
 *       - /auth/login
 *       - /auth/refresh
 *       - /swagger-ui/**
 *       - /v3/api-docs/**
 *       - /actuator/health
 *       - /public/**
 *
 *     token-header: Authorization
 *     token-prefix: "Bearer "
 *
 *     claims:
 *       user-id: userId
 *       user-name: userName
 *       tenant-id: tenantId
 *       permissions: permissions
 *
 *     jwt:
 *       secret: ${BOX_SECURITY_JWT_SECRET:12345678901234567890123456789012}
 *       issuer: auth-center
 *       audience:
 *         - demo-app
 *       access-ttl-seconds: 1800
 *       refresh-enabled: true
 *       refresh-ttl-seconds: 604800
 *       clock-skew-seconds: 30
 */
@Validated
@ConfigurationProperties(prefix = "box.security")
public class SecurityProperties {

    /**
     * 是否启用接口鉴权能力。
     *
     * <p>false 时注册全放行过滤链，但仍关闭 Spring Security 默认登录页。</p>
     */
    private boolean enabled = true;

    /**
     * 是否默认要求所有路径都鉴权。
     *
     * <p>true：除 permitPaths 外，其余路径默认都需要鉴权</p>
     * <p>false：由业务方自行决定拦截范围</p>
     */
    private boolean authenticateAllPaths = true;

    /**
     * 放行路径（白名单）。
     */
    private List<String> permitPaths = new ArrayList<>(List.of(
            "/error",
            "/favicon.ico",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    ));

    /**
     * Token 所在请求头。
     */
    @NotBlank
    private String tokenHeader = HttpHeaders.AUTHORIZATION;

    /**
     * Token 前缀。
     *
     * <p>例如：Bearer </p>
     */
    @NotBlank
    private String tokenPrefix = "Bearer ";

    /**
     * 自定义 Claims 配置。
     */
    @Valid
    private Claims claims = new Claims();

    /**
     * JWT 配置。
     */
    @Valid
    private Jwt jwt = new Jwt();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAuthenticateAllPaths() {
        return authenticateAllPaths;
    }

    public void setAuthenticateAllPaths(boolean authenticateAllPaths) {
        this.authenticateAllPaths = authenticateAllPaths;
    }

    public List<String> getPermitPaths() {
        return permitPaths;
    }

    public void setPermitPaths(List<String> permitPaths) {
        this.permitPaths = permitPaths;
    }

    public String getTokenHeader() {
        return tokenHeader;
    }

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public Claims getClaims() {
        return claims;
    }

    public void setClaims(Claims claims) {
        this.claims = claims;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    /**
     * JWT Claims 字段配置。
     */
    public static class Claims {

        /**
         * userId claim 名称。
         */
        @NotBlank
        private String userId = "userId";

        /**
         * userName claim 名称。
         */
        @NotBlank
        private String userName = "userName";

        /**
         * tenantId claim 名称。
         */
        @NotBlank
        private String tenantId = "tenantId";

        /**
         * permissions claim 名称。
         */
        @NotBlank
        private String permissions = "permissions";

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }

        public String getPermissions() {
            return permissions;
        }

        public void setPermissions(String permissions) {
            this.permissions = permissions;
        }
    }

    /**
     * JWT 配置。
     */
    public static class Jwt {

        /**
         * JWT 签名密钥（HS256）。
         *
         * <p>要求：UTF-8 编码后长度建议 >= 32 bytes。</p>
         */
        @NotBlank
        private String secret;

        /**
         * 签发者。
         */
        @NotBlank
        private String issuer = "auth-center";

        /**
         * 接收方。
         */
        private List<String> audience = new ArrayList<>(List.of("demo-app"));

        /**
         * Access Token 有效期（秒）。
         */
        @Min(1)
        private long accessTtlSeconds = 1800;

        /**
         * 是否启用 Refresh Token。
         */
        private boolean refreshEnabled = true;

        /**
         * Refresh Token 有效期（秒）。
         */
        @Min(1)
        private long refreshTtlSeconds = 604800;

        /**
         * 时钟偏移容忍（秒）。
         */
        @Min(0)
        private long clockSkewSeconds = 30;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public List<String> getAudience() {
            return audience;
        }

        public void setAudience(List<String> audience) {
            this.audience = audience;
        }

        public long getAccessTtlSeconds() {
            return accessTtlSeconds;
        }

        public void setAccessTtlSeconds(long accessTtlSeconds) {
            this.accessTtlSeconds = accessTtlSeconds;
        }

        public boolean isRefreshEnabled() {
            return refreshEnabled;
        }

        public void setRefreshEnabled(boolean refreshEnabled) {
            this.refreshEnabled = refreshEnabled;
        }

        public long getRefreshTtlSeconds() {
            return refreshTtlSeconds;
        }

        public void setRefreshTtlSeconds(long refreshTtlSeconds) {
            this.refreshTtlSeconds = refreshTtlSeconds;
        }

        public long getClockSkewSeconds() {
            return clockSkewSeconds;
        }

        public void setClockSkewSeconds(long clockSkewSeconds) {
            this.clockSkewSeconds = clockSkewSeconds;
        }
    }
}
