package com.easycode8.easylog.web.model.param;

import java.io.Serializable;

public class LogAttributeParam implements Serializable {
    /** 日志标题*/
    private String title;
    /** 日志处理器*/
    private String handler;
    /** 日志spel模板*/
    private String template;
    /** 日志操作人*/
    private String operator;
    /** 是否异步处理日志*/
    private Boolean async;
    /**是否活跃:非活跃的忽略增强处理*/
    Boolean active = true;
    /**增强的方法*/
    String method;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
