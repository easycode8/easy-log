package com.easycode8.easylog.core;

import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import com.easycode8.easylog.core.provider.OperatorProvider;
import com.easycode8.easylog.core.util.LogUtils;
import com.easycode8.easylog.core.util.SpringSpelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

public abstract class AbstractLogDataHandler implements LogDataHandler<LogInfo> {

    @Autowired(required = false)
    private HttpServletRequest request;

    final private OperatorProvider operatorProvider;

    public AbstractLogDataHandler(OperatorProvider operatorProvider) {
        this.operatorProvider = operatorProvider;
    }

    @Override
    public LogInfo init(LogAttribute logAttribute, Method method, Object[] args, Class<?> targetClass) {
        LogInfo info = new LogInfo();
        info.setTitle(logAttribute.title());
        if (StringUtils.isEmpty(logAttribute.operator())) {
            // 默认通过操作人上下文获取
            info.setLoginName(operatorProvider.currentOperator());
        } else {
            // 定义了操作人spel表达式则使用尝试解析
            info.setLoginName(SpringSpelUtils.parse(method, args, logAttribute.operator()));
        }
        info.setOperateDate(new Date());
        if (targetClass.getAnnotation(Controller.class) != null || targetClass.getAnnotation(RestController.class) != null) {
            info.setRequestUri(request.getMethod() + ":" + request.getRequestURI());
            info.setIp(request.getRemoteAddr());
        }
        info.setParams(LogUtils.buildRequestParams(request.getParameterMap(), args));
        info.setMethod(method.getName());

        String spelDescription = SpringSpelUtils.parse(method, args, logAttribute.template());
        info.setDescription(spelDescription);
        return info;
    }
}
