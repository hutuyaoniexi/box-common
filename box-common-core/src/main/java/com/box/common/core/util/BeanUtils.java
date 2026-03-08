package com.box.common.core.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Bean 工具。
 */
public final class BeanUtils {

    private BeanUtils() {
    }

    public static <T> T newInstance(Class<T> targetClass) {
        try {
            return targetClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to instantiate " + targetClass.getName(), exception);
        }
    }

    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    public static String[] getNullPropertyNames(Object source) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        return java.util.Arrays.stream(beanWrapper.getPropertyDescriptors())
                .map(descriptor -> descriptor.getName())
                .filter(name -> beanWrapper.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }
}
