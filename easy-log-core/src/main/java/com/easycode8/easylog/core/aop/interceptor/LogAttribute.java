package com.easycode8.easylog.core.aop.interceptor;


import java.util.Map;

public interface LogAttribute {
    /** 日志标题*/
    String title();
    /** 日志处理器*/
    String handler();
    /** 日志spel模板*/
    String template();
    /** 日志操作人*/
    String operator();
    /** 是否异步处理日志*/
    boolean async();
    /** 日志标签用于使用着扩展属性*/
    Map<String, String> tags();

}
