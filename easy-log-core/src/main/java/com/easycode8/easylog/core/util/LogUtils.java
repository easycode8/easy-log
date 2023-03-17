package com.easycode8.easylog.core.util;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogUtils {

    public static String buildRequestParams(Map<String, String[]> paramMap, Object[] args) {
        StringBuilder params = new StringBuilder();
        // post/put 请求体json参数
        if (CollectionUtils.isEmpty(paramMap)) {
            for (Object obj : args) {
                if (!(obj instanceof Serializable)) {
                    continue;
                }
                params.append(JSON.toJSONString(obj));
            }
            // get 请求参数
        } else {
            for (Map.Entry<String, String[]> param : ((Map<String, String[]>) paramMap).entrySet()) {
                params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
                String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
                params.append(paramValue);
            }
        }
        return params.toString();
    }

    public static String createDefaultTitle(Method method, Class<?> targetClass) {
        String title = targetClass.getSimpleName() + "." + method.getName();
        if (method.getParameters() != null) {
            List<String> paramNames = Arrays.stream(method.getParameters()).map(item -> item.getName()).collect(Collectors.toList());
            Strings.join(paramNames, ',');
            title = title + "(" + Strings.join(paramNames, ',') + ")";
        } else {
            title = title + "()";
        }
        return title;
    }
}
