package com.easycode8.easylog.mybatis.plus.util;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.mybatis.CompareResult;
import com.easycode8.easylog.mybatis.util.MybatisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public abstract class MybatisPlusLogUtils {
    public static final Logger LOGGER = LoggerFactory.getLogger(MybatisPlusLogUtils.class);
    public static final String LOG_DATA_HANDLER = "mybatisPlusLogDataHandler";

    public static LogInfo recordLog(LogInfo logInfo, Object target, Class<?> targetClass, Method method, Object[] args) {

        BaseMapper baseMapper = (BaseMapper) target;
        String methodName = ((Class)targetClass.getGenericInterfaces()[0]).getSimpleName() + "." + method.getName();
        logInfo.setMethod(methodName);
        // TODO 后续支持更多方法
//        if (methodName.contains("updateById") || methodName.contains("deleteById")) {
//            LOGGER.debug("[easy-log][{}] 执行数据变化分析--开始", methodName);
//            Serializable primaryKey = getPrimaryKey(args[0]);
//            LOGGER.debug("[easy-log][{}] key:[{}] current:{}", methodName, primaryKey, JSON.toJSONString(args[0]));
//            Object result = baseMapper.selectById(primaryKey);
//            LOGGER.debug("[easy-log][{}] key:[{}] history:{}", methodName, primaryKey, JSON.toJSONString(result));
//            if (methodName.contains("updateById")){
//                try {
//                    List<CompareResult> compareResultList = MybatisUtils.compareTowObject(result, args[0]);
//                    logInfo.setDataSnapshot(JSON.toJSONString(compareResultList));
//                    LOGGER.debug("[easy-log][{}] key:[{}] compareResult:" + JSON.toJSONString(compareResultList), methodName, primaryKey);
//                    for (CompareResult compareResult : compareResultList) {
//                        String report = compareResult.getFieldName() + "【" + compareResult.getFieldComment() + "】值:" + compareResult.getOldValue() + " => " + compareResult.getNewValue();
//                        LOGGER.debug(report);
//                    }
//                    LOGGER.debug("[easy-log][{}] 执行数据变化分析--结束", methodName);
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                logInfo.setDataSnapshot(JSON.toJSONString(result));
//            }
//
//        }
        return logInfo;
    }





    private static Serializable getPrimaryKey(Object et) {
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
}
