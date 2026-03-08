package com.box.common.core.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Bean 拷贝工具。
 */
public final class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    public static <T> T copy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T target = BeanUtils.newInstance(targetClass);
        BeanUtils.copyProperties(source, target);
        return target;
    }

    public static void copyIgnoreNull(Object source, Object target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtils.copyProperties(source, target, BeanUtils.getNullPropertyNames(source));
    }

    public static <S, T> List<T> copyList(Collection<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceList.stream()
                .map(source -> copy(source, targetClass))
                .toList();
    }
}
