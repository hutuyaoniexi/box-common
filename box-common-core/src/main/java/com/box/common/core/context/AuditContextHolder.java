package com.box.common.core.context;

public final class AuditContextHolder {

    private static final ThreadLocal<AuditContext> HOLDER = new ThreadLocal<>();

    private AuditContextHolder() {
    }

    public static void set(AuditContext context) {
        HOLDER.set(context);
    }

    public static AuditContext get() {
        return HOLDER.get();
    }

    public static String getOperatorOrDefault(String defaultOperator) {
        AuditContext context = HOLDER.get();
        if (context == null || context.operator() == null || context.operator().isBlank()) {
            return defaultOperator;
        }
        return context.operator();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
