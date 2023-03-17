package com.easycode8.easylog.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasyLog {
    /** 日志标题 title别名*/
    String value() default "";
    /** 日志标题*/
    String title() default "";
    /** 日志模板 使用spring的spel表达式 用于从请求参数中提取日志描述*/
    String template() default "";
    /** 日志处理器*/
    String handler() default "";
    /** 日志操作人 使用spring的spel表达式 用于从请求参数中提取日志操作人*/
    String operator() default "";
}
