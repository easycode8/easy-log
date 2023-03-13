package com.easycode8.easylog.core.annotation;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EasyLogConfiguration.class)
public @interface EnableEasyLog {
}
