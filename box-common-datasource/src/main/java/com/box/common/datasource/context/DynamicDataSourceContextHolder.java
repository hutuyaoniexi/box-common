package com.box.common.datasource.context;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 线程级数据源上下文，支持嵌套切换。
 */
public final class DynamicDataSourceContextHolder {

    private static final ThreadLocal<Deque<String>> HOLDER = ThreadLocal.withInitial(ArrayDeque::new);

    private DynamicDataSourceContextHolder() {
    }

    public static void push(String dataSourceKey) {
        HOLDER.get().push(dataSourceKey);
    }

    public static String peek() {
        return HOLDER.get().peek();
    }

    public static void poll() {
        Deque<String> deque = HOLDER.get();
        if (!deque.isEmpty()) {
            deque.pop();
        }
        if (deque.isEmpty()) {
            HOLDER.remove();
        }
    }

    public static void clear() {
        HOLDER.remove();
    }
}
