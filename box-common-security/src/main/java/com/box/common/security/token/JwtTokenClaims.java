package com.box.common.security.token;

import com.box.common.security.context.SecurityUser;

import java.time.Instant;
import java.util.*;

/**
 * JWT 标准化 Claims。
 */
public class JwtTokenClaims {

    private String subject;
    private String userId;
    private String username;
    private String tenantId;
    private Set<String> permissions = Collections.emptySet();
    private Instant issuedAt;
    private Instant expiresAt;
    private String jti;
    private String sessionJti;
    private Map<String, Object> claims = Collections.emptyMap();

    public SecurityUser toSecurityUser() {
        return new SecurityUser(userId, username, tenantId, permissions, claims);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = permissions == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(new LinkedHashSet<>(permissions));
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Map<String, Object> getClaims() {
        return claims;
    }


    public void setClaims(Map<String, Object> claims) {
        this.claims = claims == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(claims));
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getSessionJti() {
        return sessionJti;
    }

    public void setSessionJti(String sessionJti) {
        this.sessionJti = sessionJti;
    }
}
