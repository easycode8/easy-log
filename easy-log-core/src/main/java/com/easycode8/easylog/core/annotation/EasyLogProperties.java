package com.easycode8.easylog.core.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.easy-log")
public class EasyLogProperties {
    /** 是否开启调试模式,自动打印service接口方法的日志*/
    private Boolean serviceDebug = false;

    public Boolean getServiceDebug() {
        return serviceDebug;
    }

    public void setServiceDebug(Boolean serviceDebug) {
        this.serviceDebug = serviceDebug;
    }
}
