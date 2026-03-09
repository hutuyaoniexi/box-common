package com.box.common.datasource.annotation;

import java.lang.annotation.*;

/**
 * 声明当前方法或类型使用的数据源名称。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DS {

    /**
     * 数据源标识。
     */
    String value();
}
