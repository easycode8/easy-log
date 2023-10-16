package com.easycode8.easylog.core.adapter;

import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import com.easycode8.easylog.core.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;

/**
 * 从controller bean日志属性提取
 */
public class ControllerLogAttributeMapping implements LogAttributeMappingAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerLogAttributeMapping.class);
    private final EasyLogProperties easyLogProperties;

    public ControllerLogAttributeMapping(EasyLogProperties easyLogProperties) {
        LOGGER.info("[easy-log]启动controller bean日志增强");
        this.easyLogProperties = easyLogProperties;
    }

    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {
        if (easyLogProperties.getScanController().getEnabled() && isControllerPublicMethod(method, targetClass)) {
            String title = LogUtils.createDefaultTitle(method, targetClass);

            return DefaultLogAttribute.builder()
                    .title(title)
                    .async(easyLogProperties.getAsync())
                    .build();
        }
        return null;
    }


    private boolean isControllerPublicMethod(Method method, Class<?> targetClass) {
        // 如果方法不是controller自己的而是继承来的则忽略
        if (!method.getDeclaringClass().equals(targetClass)) {
            return false;
        }
//        return (targetClass.getAnnotation(Controller.class) != null && (method.getAnnotation(ResponseBody.class) != null || method.getReturnType() == ResponseEntity.class))
//                ||
//                (targetClass.getAnnotation(RestController.class) != null && !Modifier.isStatic(method.getModifiers())
//                        && Modifier.isPublic(method.getModifiers()));

        // 判断是否是Controller的接口方法
        return method.isAnnotationPresent(RequestMapping.class) ||
                method.isAnnotationPresent(GetMapping.class) ||
                method.isAnnotationPresent(PostMapping.class) ||
                method.isAnnotationPresent(PutMapping.class) ||
                method.isAnnotationPresent(DeleteMapping.class);
    }
}
