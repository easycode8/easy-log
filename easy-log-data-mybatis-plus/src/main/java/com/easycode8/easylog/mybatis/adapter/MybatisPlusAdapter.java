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
            // 忽略Mapper属于Object的方法, 如toString等不记录
            if (method.getDeclaringClass().equals(Object.class)) {
                return null;
            }

            // 如果是对象LogInfo及其子类忽略操作,避免日志持久化操作本身记录日志
            Class<?> clazz = GenericTypeUtils.getGenericParameterType(((Class)targetClass.getGenericInterfaces()[0]), BaseMapper.class);
            // 非空判断防止用户继承泛型接口又不定义泛型
            if ( clazz != null && LogInfo.class.isAssignableFrom(clazz)) {
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

        String methodName = LogUtils.createDefaultTitle(method, ((Class)targetClass.getGenericInterfaces()[0]));
        info.setMethod(methodName);
        info.setType(LogInfo.TYPE_DAO);
        super.before(info, method, args, targetClass, targetObject);
    }
}
