package com.easycode8.easylog.core.aop.interceptor;


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


    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        String title;
        String handler;
        String template;
        String operator;
        boolean async;

        private Builder() {
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

        public Builder async(Boolean async) {
            this.async = async;
            return this;
        }

        public DefaultLogAttribute build() {
            DefaultLogAttribute defaultLogAttribute = new DefaultLogAttribute();
            defaultLogAttribute.template = this.template;
            defaultLogAttribute.operator = this.operator;
            defaultLogAttribute.handler = this.handler;
            defaultLogAttribute.title = this.title;
            defaultLogAttribute.async = this.async;
            return defaultLogAttribute;
        }
    }
}
