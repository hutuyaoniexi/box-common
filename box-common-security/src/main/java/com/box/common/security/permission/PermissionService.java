package com.box.common.security.permission;

import java.util.Collection;

/**
 * 权限服务接口。
 */
public interface PermissionService {

    boolean hasPermission(String permission);

    boolean hasAnyPermission(Collection<String> permissions);

    boolean hasAllPermissions(Collection<String> permissions);

    void checkPermissions(String[] permissions, boolean requireAll);
}
