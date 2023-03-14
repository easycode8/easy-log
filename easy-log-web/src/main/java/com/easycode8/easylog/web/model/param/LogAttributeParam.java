package com.easycode8.easylog.web.model.param;

import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;

public class LogAttributeParam extends DefaultLogAttribute {
    String method;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
