package com.easycode8.easylog.core.aop.interceptor;


import java.io.Serializable;
import java.util.Map;

public class DefaultLogAttribute implements LogAttribute, Serializable {
    /**
     * 日志标题
     */
    String title;
    /**
     * 日志处理器
     */
    String handler;
    /**
     * 日志spel模板
     */
    String template;
    /**
     * 日志操作人
     */
    String operator;
    /**
     * 是否异步处理日志
     */
    Boolean async;
    /**
     * 日志标签用于使用着扩展属性
     */
    Map<String, String> tags;
    /**
     * 是否活跃:非活跃的忽略增强处理
     */
    Boolean active = true;


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

    @Override
    public Boolean active() {
        return this.active;
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

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }


}
