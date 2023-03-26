package com.easycode8.easylog.core.aop.interceptor;


import java.util.Map;

public class DefaultLogAttribute implements LogAttribute{
    /** 日志标题*/
    String title;
    /** 日志处理器*/
    String handler;
    /** 日志spel模板*/
    String template;
    /** 日志操作人*/
    String operator;
    /** 是否异步处理日志*/
    boolean async;
    /** 日志标签用于使用着扩展属性*/
    Map<String, String> tags;

    @Override
    public String title() {
        return this.title;
    }

    @Override
    public String handler() {
        return this.handler;
    }

    @Override
    public String template() {
        return this.template;
    }

    @Override
    public String operator() {
        return this.operator;
    }

    @Override
    public boolean async() {
        return this.async;
    }

    @Override
    public Map<String, String> tags() {
        return this.tags;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        String title;
        String handler;
        String template;
        String operator;
        boolean async;
        Map<String, String> tags;

        private Builder() {
        }

        public static Builder aDefaultLogAttribute() {
            return new Builder();
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder handler(String handler) {
            this.handler = handler;
            return this;
        }

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder operator(String operator) {
            this.operator = operator;
            return this;
        }

        public Builder async(boolean async) {
            this.async = async;
            return this;
        }

        public Builder tags(Map<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public DefaultLogAttribute build() {
            DefaultLogAttribute defaultLogAttribute = new DefaultLogAttribute();
            defaultLogAttribute.handler = this.handler;
            defaultLogAttribute.template = this.template;
            defaultLogAttribute.title = this.title;
            defaultLogAttribute.tags = this.tags;
            defaultLogAttribute.async = this.async;
            defaultLogAttribute.operator = this.operator;
            return defaultLogAttribute;
        }
    }
}
