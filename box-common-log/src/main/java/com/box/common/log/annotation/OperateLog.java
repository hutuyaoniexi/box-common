package com.box.common.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解。
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperateLog {

    String module() default "";

    String action();

    String bizNo() default "";

    String operatorId() default "";

    String operatorName() default "";

    String success() default "";

    String extra() default "";

    boolean logArgs() default false;

    boolean logResult() default false;
}
