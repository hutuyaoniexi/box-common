package com.box.common.security.context;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * 当前登录用户上下文。
 */
public class SecurityUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String userId;
    private final String username;
    private final String tenantId;
    private final Set<String> permissions;
    private final Map<String, Object> attributes;

    public SecurityUser(String userId,
                        String username,
                        String tenantId,
                        Collection<String> permissions,
                        Map<String, Object> attributes) {
        this.userId = userId;
        this.username = username;
        this.tenantId = tenantId;
        this.permissions = permissions == null
                ? Collections.emptySet()
                : Collections.unmodifiableSet(new LinkedHashSet<>(permissions));
        this.attributes = attributes == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getTenantId() {
        return tenantId;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public boolean hasPermission(String permission) {
        return permission != null && permissions.contains(permission);
    }

    public boolean isAuthenticated() {
        return userId != null && !userId.isBlank();
    }
}
