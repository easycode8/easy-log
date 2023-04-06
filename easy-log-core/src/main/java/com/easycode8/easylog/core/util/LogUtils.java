package com.easycode8.easylog.core.util;

import com.alibaba.fastjson.JSON;
import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public abstract class LogUtils {


    public static void initLog(LogInfo info, LogAttribute logAttribute, Method method, Object[] args, Class<?> targetClass) {
        info.setLogId(UUID.randomUUID().toString().replace("-", ""));
        info.setTitle(logAttribute.title());
        // 定义了操作人spel表达式则使用尝试解析
        info.setOperator(SpringSpelUtils.parse(method, args, logAttribute.operator()));
        info.setOperateDate(new Date());


        if (RequestContextHolder.getRequestAttributes() != null && targetClass.getAnnotation(Controller.class) != null || targetClass.getAnnotation(RestController.class) != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            info.setRequestUri( request.getRequestURI() + " [" + request.getMethod() + "]");
            info.setIp(request.getRemoteAddr());
            if (StringUtils.isEmpty(info.getType())) {
                info.setType("web");
            }
            info.setParams(LogUtils.buildRequestParams(request.getParameterMap(), args));
        } else {
            info.setParams(LogUtils.buildRequestParams(null, args));
        }

        if (targetClass.getAnnotation(Service.class) != null && StringUtils.isEmpty(info.getType())) {
            info.setType("service");
        }
        // 处理tags信息
        if (!CollectionUtils.isEmpty(logAttribute.tags())) {
            for (Map.Entry<String, String> item: logAttribute.tags().entrySet()) {
                item.setValue(SpringSpelUtils.parse(method, args, item.getValue()));
            }
            info.setTags(JSON.toJSONString(logAttribute.tags()));
        }


        info.setMethod(LogUtils.createDefaultTitle(method, targetClass));
        String spelDescription = SpringSpelUtils.parse(method, args, logAttribute.template());
        info.setDescription(spelDescription);

    }

    public static String buildRequestParams(Map<String, String[]> paramMap, Object[] args) {

        // post/put 请求体json参数
        if (CollectionUtils.isEmpty(paramMap)) {
            List<String> paramList = new ArrayList<>();
            for (Object obj : args) {
                try {
                    paramList.add(JSON.toJSONString(obj));
                } catch (Exception e) {
                    // 不是所有对象都支持序列化比如 HttpServletRequest
                    paramList.add("notSerializableParam");
                }

            }
            return Strings.join(paramList,',');
            // get 请求参数
        } else {
            StringBuilder params = new StringBuilder();
            for (Map.Entry<String, String[]> param : ((Map<String, String[]>) paramMap).entrySet()) {
                params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
                String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
                params.append(paramValue);
            }
            return params.toString();
        }

    }

    public static String createDefaultTitle(Method method, Class<?> targetClass) {
        return createDefaultTitle(method, targetClass, true);
    }

    public static String createDefaultTitle(Method method, Class<?> targetClass, boolean isSimpleName) {
        StringBuilder title = new StringBuilder();
        if (isSimpleName) {
            title.append(targetClass.getSimpleName());
        } else {
            title.append(targetClass.getName());
        }
        title.append("." + method.getName());
        if (method.getParameters() != null) {
            List<String> paramNames = Arrays.stream(method.getParameters()).map(item -> item.getName()).collect(Collectors.toList());
            title.append("(" + Strings.join(paramNames, ',') + ")");
        } else {
            title.append("()");
        }
        return title.toString();
    }
}
