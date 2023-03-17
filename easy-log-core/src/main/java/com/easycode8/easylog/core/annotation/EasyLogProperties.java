package com.easycode8.easylog.core.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.easy-log")
public class EasyLogProperties {
    /** 是否开启调试模式,自动打印service接口方法的日志*/
    private Boolean serviceDebug = false;
    /** 获取操作人信息的表达式 支持从session或请求头获取 默认为空示例:
     * <li>spring.easy-log.operator=session.account.username:通过session获取属性为account中对象的username字段作为操作人</li>
     * <li>spring.easy-log.operator=header.x-username:代表从请求头中获取x-username作为操作人信息 </li>
     * */
    private String operator = "";
    public Boolean getServiceDebug() {
        return serviceDebug;
    }

    public void setServiceDebug(Boolean serviceDebug) {
        this.serviceDebug = serviceDebug;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
