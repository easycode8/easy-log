package com.easycode8.easylog.core.provider;

import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.util.SpringSpelUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 根据session提取操作人信息
 */
public class SessionOperatorProvider implements OperatorProvider{
    private static final String PREFIX_SESSION = "SESSION.";
    private static final String PREFIX_HEADER = "HEADER.";
    private final EasyLogProperties easyLogProperties;


    public SessionOperatorProvider(EasyLogProperties easyLogProperties) {
        this.easyLogProperties = easyLogProperties;
    }

    @Override
    public String currentOperator() {
        String operator = "";
        if (StringUtils.isEmpty(easyLogProperties.getOperator())) {
            return operator;
        }
        if (easyLogProperties.getOperator().startsWith(PREFIX_SESSION)) {
            String[] info = easyLogProperties.getOperator().replace(PREFIX_SESSION, "").split("\\.", 2);
            operator =  (String) SpringSpelUtils.getSessionAttribute(info[0], info[1]);
        }

        if (easyLogProperties.getOperator().startsWith(PREFIX_HEADER)) {
            String expression = easyLogProperties.getOperator().replace(PREFIX_HEADER, "");
            // 项目启动后立马执行业务处理,可能是非web请求这里做下兼容
            if (RequestContextHolder.getRequestAttributes() == null) {
                return operator;
            }
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            operator =  request.getHeader(expression);
        }
        return operator;
    }
}
