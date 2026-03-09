package com.box.common.security.annotation;

import java.lang.annotation.*;

/**
 * 注入当前登录用户。
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}
