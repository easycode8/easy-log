package com.easycode8.easylog.core.aop.interceptor;


public interface LogAttribute {
    /** 日志标题*/
    String title();
    /** 日志处理器*/
    String handler();
    /** 日志spel模板*/
    String template();

}
