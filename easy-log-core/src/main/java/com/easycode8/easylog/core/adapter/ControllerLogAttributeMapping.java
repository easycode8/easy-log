package com.easycode8.easylog.core.adapter;

import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import com.easycode8.easylog.core.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 从controller bean日志属性提取
 */
public class ControllerLogAttributeMapping implements LogAttributeMappingAdapter{

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerLogAttributeMapping.class);
    private final EasyLogProperties easyLogProperties;

    public ControllerLogAttributeMapping(EasyLogProperties easyLogProperties) {
        LOGGER.info("[easy-log]启动controller bean日志增强");
        this.easyLogProperties = easyLogProperties;
    }

    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {
        if (isControllerPublicMethod(method, targetClass)) {
            String title = LogUtils.createDefaultTitle(method, targetClass);

            return DefaultLogAttribute.builder()
                    .title(title)
                    .async(easyLogProperties.getAsync())
                    .build();
        }
        return null;
    }


    private boolean isControllerPublicMethod(Method method, Class<?> targetClass) {
        return easyLogProperties.getScanController().getEnabled()
                && (targetClass.getAnnotation(Controller.class) != null || targetClass.getAnnotation(RestController.class) != null)
                && !Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers());
    }
}
