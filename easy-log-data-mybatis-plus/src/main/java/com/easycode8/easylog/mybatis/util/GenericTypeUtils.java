package com.easycode8.easylog.mybatis.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericTypeUtils {
    /**
     * 获取指定类实现接口的泛型类型
     *
     * @param clazz           指定的类
     * @param interfaceClass  实现的接口的Class对象
     * @return 该类实现接口的泛型类型
     */
    public static Class<?> getGenericParameterType(Class<?> clazz, Class<?> interfaceClass) {
        Type[] interfaces = clazz.getGenericInterfaces();
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;

                if (interfaceClass.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                        Type typeArgument = actualTypeArguments[0];
                        if (typeArgument instanceof Class) {
                            return (Class<?>) typeArgument;
                        } else if (typeArgument instanceof ParameterizedType) {
                            Type rawType = ((ParameterizedType) typeArgument).getRawType();
                            if (rawType instanceof Class && ((Class<?>) rawType).isAssignableFrom(interfaceClass)) {
                                return getGenericParameterType(clazz, (Class<?>) rawType);
                            }
                        }
                    }
                } else if (type instanceof Class && interfaceClass.isAssignableFrom((Class<?>) type)) {
                    return getGenericParameterType(clazz, (Class<?>) type);
                }
            }
        }
        return null;
    }
}
