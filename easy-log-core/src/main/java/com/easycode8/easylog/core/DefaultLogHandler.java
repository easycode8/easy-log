package com.easycode8.easylog.core;

import com.alibaba.fastjson.JSON;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import com.easycode8.easylog.core.util.LogUtils;
import com.easycode8.easylog.core.util.SpringSpelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

public class DefaultLogHandler implements LogDataHandler<LogInfo> {

    @Autowired(required = false)
    private HttpServletRequest request;

    @Override
    public LogInfo init(LogAttribute logAttribute, Method method, Object[] args, Class<?> targetClass) {
        LogInfo info = new LogInfo();
        info.setTitle(logAttribute.title());

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

    @Override
    public void before(LogInfo info, Method method, Class<?> targetClass) {
        Logger logger =  LoggerFactory.getLogger(targetClass);

        logger.info("[easy-log][{}]--开始 content:{}", info.getTitle(), JSON.toJSONString(info));

    }

    @Override
    public void after(LogInfo info, Method method, Class<?> targetClass) {
        Logger logger =  LoggerFactory.getLogger(targetClass);
        logger.info("[easy-log][{}]--结束 timeout:{}", info.getTitle(), info.getTimeout());

    }
}
