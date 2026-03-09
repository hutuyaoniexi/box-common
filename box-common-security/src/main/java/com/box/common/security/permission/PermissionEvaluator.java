package com.box.common.security.permission;

import com.box.common.core.enums.ErrorCode;
import com.box.common.core.exception.BaseException;
import com.box.common.security.context.SecurityContextHolder;
import com.box.common.security.context.SecurityUser;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 默认权限判定器。
 */
public class PermissionEvaluator implements PermissionService {

    @Override
    public boolean hasPermission(String permission) {
        SecurityUser user = SecurityContextHolder.get();
        return user != null && user.hasPermission(permission);
    }

    @Override
    public boolean hasAnyPermission(Collection<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return true;
        }
        return permissions.stream().anyMatch(this::hasPermission);
    }

    @Override
    public boolean hasAllPermissions(Collection<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return true;
        }
        return permissions.stream().allMatch(this::hasPermission);
    }

    @Override
    public void checkPermissions(String[] permissions, boolean requireAll) {
        List<String> requiredPermissions = permissions == null ? List.of() : Arrays.stream(permissions)
                .filter(permission -> permission != null && !permission.isBlank())
                .toList();
        if (requiredPermissions.isEmpty()) {
            return;
        }
        boolean granted = requireAll ? hasAllPermissions(requiredPermissions) : hasAnyPermission(requiredPermissions);
        if (!granted) {
            throw new BaseException(ErrorCode.FORBIDDEN, "权限不足");
        }
    }
}
