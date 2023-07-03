package com.easycode8.easylog.mybatis.adapter;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easycode8.easylog.core.handler.DefaultLogHandler;
import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import com.easycode8.easylog.core.util.LogUtils;
import com.easycode8.easylog.mybatis.util.GenericTypeUtils;

import java.lang.reflect.Method;

public class MybatisPlusAdapter extends DefaultLogHandler implements MybatisLogAttributeMappingAdapter {
    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {

        if (BaseMapper.class.isAssignableFrom(targetClass)) {
            // 如果是对象LogInfo及其子类忽略操作,避免日志持久化操作本身记录日志
            if (LogInfo.class.isAssignableFrom(GenericTypeUtils.getGenericParameterType(((Class)targetClass.getGenericInterfaces()[0]), BaseMapper.class))) {
                return null;
            }

            String title = LogUtils.createDefaultTitle(method, ((Class)targetClass.getGenericInterfaces()[0]));
            return DefaultLogAttribute.builder()
                    .title(title)
                    .handler("mybatisPlusLogDataHandler")
                    .build();

        }
        return null;

    }

    @Override
    public void before(LogInfo info, Method method, Object[] args, Class<?> targetClass, Object targetObject) {

//        MybatisPlusLogUtils.recordLog(info, targetObject, targetClass, method, args);
        String methodName = ((Class)targetClass.getGenericInterfaces()[0]).getSimpleName() + "." + method.getName();
        info.setMethod(methodName);
        super.before(info, method, args, targetClass, targetObject);
    }
}
