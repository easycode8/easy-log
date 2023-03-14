package com.easycode8.easylog.web.model.vo;

import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;

public class LogAttributeVO extends DefaultLogAttribute {
    String method;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
