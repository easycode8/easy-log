package com.easycode8.easylog.core;




import java.util.Date;
import java.util.Map;

public class LogInfo implements LogDefinition {
    public static final Integer STATUS_INIT = 1;
    public static final Integer STATUS_BEFORE = 2;
    public static final Integer STATUS_FINISH = 3;

    /** 日志主键*/
//    @TableId(type = IdType.UUID)
    private String logId;

    /** 日志类型*/
    private String type;

    /** 日志标题*/
    private String title;

    /** 日志摘要*/
    private String description;

    /** 请求IP*/
    private String ip;

    /** URI*/
    private String requestUri;

    /** 请求方式*/
    private String method;

    /** 提交参数*/
    private String params;

    /** 异常*/
    private String exception;

    /** 操作时间*/
    private Date operateDate;

    /** 请求时长*/
    private Long timeout;

    /** 用户登入名*/
    private String loginName;

    /** requestID*/
    private String requestId;

    /** 历史数据*/
    private String dataSnapshot;

    /** 日志状态*/
    private Integer status = 0;


    private Map<String, Object> custom;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public Date getOperateDate() {
        return operateDate;
    }

    public void setOperateDate(Date operateDate) {
        this.operateDate = operateDate;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDataSnapshot() {
        return dataSnapshot;
    }

    public void setDataSnapshot(String dataSnapshot) {
        this.dataSnapshot = dataSnapshot;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Map<String, Object> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, Object> custom) {
        this.custom = custom;
    }
}
