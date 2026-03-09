package com.box.common.security.annotation;

import java.lang.annotation.*;

/**
 * 声明式权限控制。
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {

    String[] value();

    boolean requireAll() default false;
}
