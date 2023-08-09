package com.easycode8.easylog.mybatis.adapter;

import com.easycode8.easylog.core.handler.DefaultLogHandler;
import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import com.easycode8.easylog.core.util.LogUtils;

import java.lang.reflect.Method;

public class MybatisAdapter extends DefaultLogHandler implements MybatisLogAttributeMappingAdapter {

    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {

        if (targetClass.getGenericInterfaces() != null && targetClass.getGenericInterfaces().length == 1) {
            String mapperName = targetClass.getGenericInterfaces()[0].getTypeName().toLowerCase();
            if (mapperName.endsWith("dao") || mapperName.endsWith("mapper") ) {

                String title = LogUtils.createDefaultTitle(method, (Class)targetClass.getGenericInterfaces()[0]);
                return DefaultLogAttribute.builder()
                        .title(title)
                        .handler("mybatisLogDataHandler")
                        .build();

            }

        }
        return null;

    }

    @Override
    public void before(LogInfo info, Method method, Object[] args, Class<?> targetClass, Object targetObject) {
        info.setType(LogInfo.TYPE_DAO);
        super.before(info, method, args, targetClass, targetObject);
    }
}
