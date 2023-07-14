package com.easycode8.easylog.mybatis.util;

import com.easycode8.easylog.mybatis.CompareResult;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Function;

public abstract class MybatisUtils {
    public static void showSql(Configuration configuration, BoundSql boundSql, String sqlId) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        //替换空格、换行、tab缩进等
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        System.out.println(sql);
    }

    /***
     * sql 参数替换
     * @param configuration
     * @param boundSql
     * @return
     */
    public static String showSql(Configuration configuration, BoundSql boundSql) {
        //获取参数对象
        Object parameterObject = boundSql.getParameterObject();
        //获取当前的sql语句有绑定的所有parameterMapping属性
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        //去除空格
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            /* 如果参数满足：org.apache.ibatis.type.TypeHandlerRegistry#hasTypeHandler(java.lang.Class<?>)
            org.apache.ibatis.type.TypeHandlerRegistry#TYPE_HANDLER_MAP
            * 即是不是属于注册类型(TYPE_HANDLER_MAP...等/有没有相应的类型处理器)
             * */
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                //装饰器，可直接操作属性值 ---》 以parameterObject创建装饰器
                //MetaObject 是 Mybatis 反射工具类，通过 MetaObject 获取和设置对象的属性值
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                //循环 parameterMappings 所有属性
                for (ParameterMapping parameterMapping : parameterMappings) {
                    //获取property属性
                    String propertyName = parameterMapping.getProperty();
                    //是否声明了propertyName的属性和get方法
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        //判断是不是sql的附加参数
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }

    private static String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value.replace("$", "\\$");
    }

    /**
     * 根据mapper的sqlId获取 mapper的class及执行的方法
     * @param sqlId
     * @return
     * @throws ClassNotFoundException
     */
    public static Method getMethodBySqlId(String sqlId) throws ClassNotFoundException {
        String mapperClassName = sqlId.substring(0, sqlId.lastIndexOf("."));
        Class<?> mapperClass = Class.forName(mapperClassName);

        // 判断是否为代理类
        if (mapperClass.getSuperclass() != null && Proxy.isProxyClass(mapperClass)) {
            mapperClass = mapperClass.getInterfaces()[0];
        }
        String methodName = sqlId.substring(sqlId.lastIndexOf(".") + 1);
        Method[] methods = mapperClass.getMethods();
        Method method = null;
        for (Method m : methods) {
            if (m.getName().equals(methodName)) {
                method = m;
                break;
            }
        }
        // 打印 Mapper 接口及方法
//        System.out.println("Mapper Class: " + mapperClass.getName());
//        System.out.println("Method Name: " + method.getName());
        return method;
    }

    /**
     * 根据mappedStatement获取实体对象
     * @param mappedStatement
     * @param boundSql
     * @return
     */
    public static Class getEntityClassByMapper(MappedStatement mappedStatement, BoundSql boundSql) {
        ParameterMap parameterMap = mappedStatement.getParameterMap();
        Object parameterObject = boundSql.getParameterObject();

        Class<?> entityClass;
        if (parameterMap != null && parameterMap.getType() != null) {
            entityClass = parameterMap.getType();
        } else if (parameterObject != null) {
            entityClass = parameterObject.getClass();
        } else {
            throw new RuntimeException("无法获取实体类型");
        }
        return entityClass;
    }

    /**
     * 根据mapper获取即将更新的实体对象
     * @param mappedStatement
     * @param boundSql
     * @return
     */
    public static Object getEntityObjectByMapper(MappedStatement mappedStatement, BoundSql boundSql) {
        Class entityClass = getEntityClassByMapper(mappedStatement, boundSql);
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject == null) {
            return null;
        }

        Object entity;
        if (parameterObject instanceof Map) {
            // 如果参数是一个 Map，尝试从中获取实体对象
            Map<?, ?> paramMap = (Map<?, ?>) parameterObject;
            if (paramMap.containsKey("et")) {
                entity = paramMap.get("et");
            } else {
                // 如果没有 et 参数，则直接返回 null
                return null;
            }
        } else {
            // 如果参数不是一个 Map，则直接使用参数对象作为实体对象
            entity = parameterObject;
        }

        if (!entityClass.isInstance(entity)) {
            return null;
        }

        return entity;

    }


    public static List<CompareResult> compareTowObject(Object oldObj, Object newObj, Function<Field, String> function) throws IllegalAccessException {
        List<CompareResult> list = new ArrayList<>();
        //获取对象的class
        Class<?> clazz1 = oldObj.getClass();
        Class<?> clazz2 = newObj.getClass();
        //获取对象的属性列表
        Field[] field1 = clazz1.getDeclaredFields();
        Field[] field2 = clazz2.getDeclaredFields();
        //遍历属性列表field1
        for (int i = 0; i < field1.length; i++) {
            //遍历属性列表field2
            for (int j = 0; j < field2.length; j++) {
                //如果field1[i]属性名与field2[j]属性名内容相同
                if (field1[i].getName().equals(field2[j].getName())) {
                    field1[i].setAccessible(true);
                    field2[j].setAccessible(true);
                    if (field2[j].get(newObj) == null) {
                        continue;
                    }
                    //如果field1[i]属性值与field2[j]属性值内容不相同
                    if (!compareTwo(field1[i].get(oldObj), field2[j].get(newObj))) {
                        CompareResult r = new CompareResult();
                        r.setFieldName(field1[i].getName());
                        r.setOldValue(field1[i].get(oldObj));
                        r.setNewValue(field2[j].get(newObj));
                        // 获取属性名称能力暴露出去,由外部决定怎么实现
                        if (function != null) {
                            r.setFieldComment(function.apply(field1[i]));
                        }

                        list.add(r);
                    }
                    break;
                }
            }
        }
        return list;
    }

    /**
     * 对比两个对象
     *
     * @param oldObj 旧对象
     * @param newObj 新对象
     */
    public static List<CompareResult> compareTowObject(Object oldObj, Object newObj) throws IllegalAccessException {
        if (oldObj == null || newObj == null) {
            return new ArrayList<>();
        }
        return compareTowObject(oldObj, newObj, null);
    }


    /**
     * 对比两个数据是否内容相同
     *
     * @param object1,object2
     * @return boolean类型
     */
    private static boolean compareTwo(Object object1, Object object2) {

        if (object1 == null && object2 == null) {
            return true;
        }
        if (object1 == null && object2 != null) {
            return false;
        }
        if (object1.equals(object2)) {
            return true;
        }
        return false;
    }
}
