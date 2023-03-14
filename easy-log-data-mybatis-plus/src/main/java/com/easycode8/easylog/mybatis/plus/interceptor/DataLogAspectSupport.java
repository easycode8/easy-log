package com.easycode8.easylog.mybatis.plus.interceptor;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.mybatis.plus.CompareResult;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DataLogAspectSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLogAspectSupport.class);

    public Object invoke(Object target, Class<?> targetClass, Method method, Object[] args,
                            final InvocationCallback invocation) throws Throwable {
        LogInfo logInfo = this.recordLog(target, targetClass, method, args);
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            result = invocation.proceedWithLog();
        } catch (Throwable t) {
            if (logInfo != null) {
                logInfo.setException(ExceptionUtils.getRootCause(t).getMessage());
            }
            throw t;
        } finally {
            long timeout = System.currentTimeMillis() - startTime;
            logInfo.setTimeout(timeout);
        }
        LOGGER.debug("[easy-log]{}:\n{}", logInfo.getMethod(), JSON.toJSONString(logInfo, true));
        return result;
    }

    private LogInfo recordLog(Object target, Class<?> targetClass, Method method, Object[] args) {
        LogInfo logInfo = new LogInfo();
        BaseMapper baseMapper = (BaseMapper) target;
        String methodName = ((Class)targetClass.getGenericInterfaces()[0]).getSimpleName() + "." + method.getName();
        logInfo.setMethod(methodName);
        // TODO 后续支持更多方法
        if (methodName.contains("updateById") || methodName.contains("deleteById")) {
            LOGGER.debug("[easy-log][{}] 执行数据变化分析--开始", methodName);
            Serializable primaryKey = this.getPrimaryKey(args[0]);
            LOGGER.debug("[easy-log][{}] key:[{}] current:{}", methodName, primaryKey, JSON.toJSONString(args[0]));
            Object result = baseMapper.selectById(primaryKey);
            LOGGER.debug("[easy-log][{}] key:[{}] history:{}", methodName, primaryKey, JSON.toJSONString(result));
            if (methodName.contains("updateById")){
                try {
                    List<CompareResult> compareResultList = this.compareTowObject(result, args[0]);
                    logInfo.setDataSnapshot(JSON.toJSONString(compareResultList));
                    LOGGER.debug("[easy-log][{}] key:[{}] compareResult:" + JSON.toJSONString(compareResultList), methodName, primaryKey);
                    for (CompareResult compareResult : compareResultList) {
                        String report = compareResult.getFieldName() + "【" + compareResult.getFieldComment() + "】值:" + compareResult.getOldValue() + " => " + compareResult.getNewValue();
                        LOGGER.debug(report);
                    }
                    LOGGER.debug("[easy-log][{}] 执行数据变化分析--结束", methodName);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                logInfo.setDataSnapshot(JSON.toJSONString(result));
            }

        }
        return logInfo;
    }


    /**
     * 对比两个对象
     *
     * @param oldObj 旧对象
     * @param newObj 新对象
     */
    protected List<CompareResult> compareTowObject(Object oldObj, Object newObj) throws IllegalAccessException {
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
                        // TODO 获取属性名称功能暴露出去
//                        ApiModelProperty apiModelProperty = field1[i].getAnnotation(ApiModelProperty.class);
//                        if (apiModelProperty != null) {
//                            r.setFieldComment(apiModelProperty.value());
//                        }

                        list.add(r);
                    }
                    break;
                }
            }
        }
        return list;
    }

    @FunctionalInterface
    protected interface InvocationCallback {

        @Nullable
        Object proceedWithLog() throws Throwable;
    }

    private Serializable getPrimaryKey(Object et) {
        // 反射获取实体类
        Class<?> clazz = et.getClass();
        // 不含有表名的实体就默认通过
        if (!clazz.isAnnotationPresent(TableName.class)) {
            return (Serializable) et;
        }
        // 获取表名
        TableName tableName = clazz.getAnnotation(TableName.class);
        String tbName = tableName.value();
        if (StringUtils.isBlank(tbName)) {
            return null;
        }
        String pkName = null;
        String pkValue = null;
        // 获取实体所有字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 设置些属性是可以访问的
            field.setAccessible(true);
            if (field.isAnnotationPresent(TableId.class)) {
                // 获取主键
                pkName = field.getName();
                try {
                    // 获取主键值
                    pkValue = field.get(et).toString();
                } catch (Exception e) {
                    pkValue = null;
                }

            }
        }
        return pkValue;

    }

    /**
     * 对比两个数据是否内容相同
     *
     * @param object1,object2
     * @return boolean类型
     */
    private boolean compareTwo(Object object1, Object object2) {

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
