package com.easycode8.easylog.core.util;

import com.alibaba.fastjson.JSON;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Map;

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
}
