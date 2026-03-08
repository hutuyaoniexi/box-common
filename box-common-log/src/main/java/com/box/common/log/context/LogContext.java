package com.box.common.log.context;

/**
 * 日志上下文。
 */
public final class LogContext {

    private static final ThreadLocal<Operator> OPERATOR_HOLDER = new ThreadLocal<>();

    private LogContext() {
    }

    public static void setOperator(String operatorId, String operatorName) {
        OPERATOR_HOLDER.set(new Operator(operatorId, operatorName));
    }

    public static String getOperatorId() {
        Operator operator = OPERATOR_HOLDER.get();
        return operator == null ? null : operator.operatorId();
    }

    public static String getOperatorName() {
        Operator operator = OPERATOR_HOLDER.get();
        return operator == null ? null : operator.operatorName();
    }

    public static void clear() {
        OPERATOR_HOLDER.remove();
    }

    private record Operator(String operatorId, String operatorName) {
    }
}
