package com.easycode8.easylog.core.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EasyLog {
    /** 日志标题 title别名*/
    String value();
    /** 日志模板*/
    String template() default "";
    /** 日志处理器*/
    String handler() default "";
}
