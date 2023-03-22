package com.easycode8.easylog.core.annotation;

import com.easycode8.easylog.core.constants.HandleMode;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasyLog {
    int MODE_DEFAULT = 0;
    int MODE_ASYNC = 1;
    int MODE_SYNC = 2;

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
    /** 处理日志的模式 GLOBAL:使用全局默认值 ASYNC:使用异步 SYNC:使用同步*/
    HandleMode handleMode() default HandleMode.GLOBAL;
}
